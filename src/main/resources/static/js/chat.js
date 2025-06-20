/*
 * ChatZero - Chat Page JavaScript
 * Handles real-time messaging functionality
 */

(function() {
  'use strict';
  
  // Only run on chat page
  if (!document.body.classList.contains('chat-page')) {
    console.log('🚫 Not on chat page, skipping chat initialization');
    return;
  }

  console.log('💬 Initializing ChatZero Chat functionality');

  // Global variables (scoped to this IIFE)
  let stomp;
  let selectedFriendId = null;
  let selectedFriendName = null;
  let currentUserId = null;
  let isConnected = false;

  // ===== INITIALIZATION =====
  function initializeChatPage() {
    currentUserId = window.currentUserId || parseInt(document.querySelector('[data-user-id]')?.getAttribute('data-user-id'));
    
    console.log('🚀 Initializing ChatZero with user ID:', currentUserId);
    
    if (currentUserId) {
      connect();
      initializeUI();
    } else {
      console.error('❌ Current user ID not found');
    }
  }

  function initializeUI() {
    // Set up enter key for message input
    const msgInput = document.getElementById('msgInput');
    if (msgInput) {
      msgInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
          e.preventDefault();
          sendMessage();
        }
      });
    }
  }

  // ===== WEBSOCKET CONNECTION =====
  function connect() {
    const socket = new SockJS('/ws');
    stomp = Stomp.over(socket);
    
    stomp.debug = null;
    
    stomp.connect({}, function(frame) {
      console.log('✅ WebSocket Connected: ' + frame);
      isConnected = true;
      
      stomp.subscribe('/topic/messages', function(messageOutput) {
        try {
          const message = JSON.parse(messageOutput.body);
          handleIncomingMessage(message);
        } catch (error) {
          console.error('❌ Error parsing message:', error);
        }
      });
      
      updateConnectionStatus(true);
      
    }, function(error) {
      console.error('❌ WebSocket connection error:', error);
      isConnected = false;
      updateConnectionStatus(false);
      setTimeout(connect, 5000);
    });
  }

  function handleIncomingMessage(message) {
    const isRelevantMessage = (message.receiverId === currentUserId && message.senderId === selectedFriendId) ||
                             (message.senderId === currentUserId && message.receiverId === selectedFriendId);
    
    if (isRelevantMessage && selectedFriendId) {
      console.log('📨 Incoming message:', message);
      displayMessage(message, false);
      scrollToBottom();
    }
  }

  // ===== MESSAGE HANDLING =====
  function sendMessage() {
    const msgInput = document.getElementById('msgInput');
    const content = msgInput.value.trim();

    if (!content) {
      showNotification("Please enter a message", "error");
      return;
    }
    
    if (!currentUserId || !selectedFriendId) {
      showNotification("Please select a friend to chat with", "error");
      return;
    }

    if (!isConnected) {
      showNotification("Not connected to server. Please wait...", "error");
      return;
    }

    const messageData = {
      senderId: currentUserId,
      receiverId: selectedFriendId,
      content: content
    };

    try {
      stomp.send('/app/chat', {}, JSON.stringify(messageData));
      msgInput.value = '';
      console.log('📤 Message sent:', messageData);
      scrollToBottom();
      
    } catch (error) {
      console.error('❌ Error sending message:', error);
      showNotification("Failed to send message. Please try again.", "error");
    }
  }

  function displayMessage(message, clearChat = false) {
    const chatWindow = document.getElementById('chatWindow');
    
    if (clearChat) {
      chatWindow.innerHTML = '';
    }
    
    // Remove welcome/loading messages
    const welcomeMsg = chatWindow.querySelector('.welcome-message, .loading-messages, .no-messages');
    if (welcomeMsg) {
      welcomeMsg.remove();
    }
    
    const messageDiv = document.createElement('div');
    const isCurrentUser = message.senderId === currentUserId;
    
    let timestamp;
    if (message.sentAt) {
      try {
        const date = new Date(message.sentAt);
        timestamp = isNaN(date.getTime()) ? new Date().toLocaleTimeString() : date.toLocaleTimeString();
      } catch (e) {
        timestamp = new Date().toLocaleTimeString();
      }
    } else {
      timestamp = new Date().toLocaleTimeString();
    }
    
    messageDiv.className = `message-bubble ${isCurrentUser ? 'sent' : 'received'}`;
    messageDiv.innerHTML = `
      <div class="message-content">${escapeHtml(message.content)}</div>
      <div class="message-time">${timestamp}</div>
    `;
    
    chatWindow.appendChild(messageDiv);
  }

  function loadMessageHistory(friendId) {
    if (!friendId) {
      console.error('❌ No friend ID provided for message history');
      return;
    }
    
    console.log('📚 Loading message history for friend ID:', friendId);
    
    const chatWindow = document.getElementById('chatWindow');
    chatWindow.innerHTML = '<div class="loading-messages">Loading messages...</div>';
    
    fetch(`/api/messages/${friendId}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin'
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(messages => {
      console.log('📨 Loaded messages:', messages.length, 'messages');
      
      chatWindow.innerHTML = '';
      
      if (messages.length === 0) {
        chatWindow.innerHTML = `
          <div class="no-messages">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <p>No messages yet</p>
            <span>Start the conversation with ${selectedFriendName}!</span>
          </div>
        `;
        return;
      }
      
      messages.forEach(message => {
        displayMessage(message, false);
      });
      
      scrollToBottom();
      console.log('✅ Message history loaded successfully');
      
    })
    .catch(error => {
      console.error('❌ Error loading message history:', error);
      chatWindow.innerHTML = `
        <div class="error-messages">
          <p>Error loading messages</p>
          <button onclick="loadMessageHistory(${friendId})">Try Again</button>
        </div>
      `;
    });
  }

  // ===== FRIEND MANAGEMENT =====
  function selectFriend(elem) {
    const friendId = parseInt(elem.getAttribute('data-id'));
    const friendName = elem.querySelector('.friend-name').textContent.trim();
    
    if (!friendId) {
      console.error('❌ Invalid friend ID');
      return;
    }
    
    selectedFriendId = friendId;
    selectedFriendName = friendName;
    
    console.log('👤 Selected friend:', friendName, 'ID:', friendId);
    
    // Update UI
    highlightSelectedFriend(elem);
    updateChatHeader(friendName);
    enableChatInput();
    
    // Load message history
    loadMessageHistory(friendId);
  }

  function highlightSelectedFriend(selectedElem) {
    document.querySelectorAll('.friend-item').forEach(item => {
      item.classList.remove('selected');
    });
    
    selectedElem.classList.add('selected');
  }

  function updateChatHeader(friendName) {
    const chatFriendName = document.getElementById('chatFriendName');
    const chatFriendStatus = document.getElementById('chatFriendStatus');
    const chatFriendInitial = document.getElementById('chatFriendInitial');
    
    if (chatFriendName) chatFriendName.textContent = friendName;
    if (chatFriendStatus) chatFriendStatus.textContent = 'Online';
    if (chatFriendInitial) chatFriendInitial.textContent = friendName.charAt(0).toUpperCase();
  }

  function enableChatInput() {
    const msgInput = document.getElementById('msgInput');
    const sendButton = document.getElementById('sendButton');
    
    if (msgInput) {
      msgInput.disabled = false;
      msgInput.placeholder = `Message ${selectedFriendName}...`;
      msgInput.focus();
    }
    
    if (sendButton) {
      sendButton.disabled = false;
    }
  }

  function addFriend(event) {
    event.preventDefault();
    const username = document.getElementById('addFriendInput').value.trim();
    const resultSpan = document.getElementById('addFriendResult');
    
    resultSpan.textContent = '';
    resultSpan.className = 'add-friend-result';
    
    if (!username) {
      showAddFriendResult('Please enter a username', 'error');
      return false;
    }
    
    fetch('/add-friend', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'username=' + encodeURIComponent(username)
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        showAddFriendResult('Friend added successfully!', 'success');
        document.getElementById('addFriendInput').value = '';
        setTimeout(() => window.location.reload(), 1500);
      } else {
        showAddFriendResult(data.message || 'Error adding friend.', 'error');
      }
    })
    .catch(() => {
      showAddFriendResult('Server error. Please try again.', 'error');
    });
    
    return false;
  }

  function showAddFriendResult(message, type) {
    const resultSpan = document.getElementById('addFriendResult');
    resultSpan.textContent = message;
    resultSpan.style.color = type === 'success' ? '#4ade80' : '#ef4444';
    
    setTimeout(() => {
      resultSpan.textContent = '';
    }, 3000);
  }

  // ===== UI UTILITIES =====
  function scrollToBottom() {
    const chatWindow = document.getElementById('chatWindow');
    if (chatWindow) {
      chatWindow.scrollTop = chatWindow.scrollHeight;
    }
  }

  function updateConnectionStatus(connected) {
    const statusDot = document.querySelector('.status-dot');
    const statusText = document.querySelector('.status-text');
    
    if (statusDot) {
      statusDot.className = connected ? 'status-dot' : 'status-dot disconnected';
    }
    
    if (statusText) {
      statusText.textContent = connected ? 'Connected' : 'Disconnected';
    }
  }

  function showNotification(message, type = 'info') {
    let notification = document.getElementById('chat-notification');
    if (!notification) {
      notification = document.createElement('div');
      notification.id = 'chat-notification';
      notification.style.cssText = `
        position: fixed;
        top: 90px;
        right: 20px;
        padding: 12px 16px;
        border-radius: 6px;
        color: white;
        font-size: 14px;
        z-index: 1000;
        transform: translateX(100%);
        transition: transform 0.3s ease;
      `;
      document.body.appendChild(notification);
    }
    
    const colors = {
      success: '#4ade80',
      error: '#ef4444',
      info: '#3b82f6'
    };
    
    notification.style.background = colors[type] || colors.info;
    notification.textContent = message;
    
    setTimeout(() => {
      notification.style.transform = 'translateX(0)';
    }, 100);
    
    setTimeout(() => {
      notification.style.transform = 'translateX(100%)';
    }, 3000);
  }

  function escapeHtml(text) {
    const map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
  }

  // ===== GLOBAL FUNCTIONS =====
  window.selectFriend = selectFriend;
  window.addFriend = addFriend;
  window.sendMessage = sendMessage;
  window.loadMessageHistory = loadMessageHistory;

  // ===== EVENT LISTENERS =====
  document.addEventListener('visibilitychange', function() {
    if (!document.hidden && !isConnected) {
      connect();
    }
  });

  window.addEventListener('beforeunload', function() {
    if (stomp && isConnected) {
      stomp.disconnect();
    }
  });

  // ===== INITIALIZE =====
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeChatPage);
  } else {
    initializeChatPage();
  }

})();