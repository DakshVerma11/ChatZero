
package com.chat.service;

import com.chat.entity.Message;
import com.chat.repository.MessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    
    private final MessageRepository msgRepo;
    
    public MessageService(MessageRepository msgRepo) { 
        this.msgRepo = msgRepo; 
    }
    
    /**
     * Send and save a message
     * @param message - Message to send
     * @return Saved message with ID
     */
    public Message send(Message message) {
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now());
        }
        message.setDelivered(true);
        
        Message savedMessage = msgRepo.save(message);
        
        System.out.println("✅ Message saved: ID=" + savedMessage.getMessageId() + 
                          ", From=" + savedMessage.getSenderId() + 
                          ", To=" + savedMessage.getReceiverId() + 
                          ", Content='" + savedMessage.getContent() + "'");
        
        return savedMessage;
    }
    
    /**
     * Get messages from sender to receiver (one direction)
     * @param senderId - Sender user ID
     * @param receiverId - Receiver user ID
     * @return List of messages
     */
    public List<Message> history(Long senderId, Long receiverId) {
        return msgRepo.findBySenderIdAndReceiverId(senderId, receiverId);
    }
    
    /**
     * Get complete conversation history between two users (bidirectional)
     * @param userId1 - First user ID
     * @param userId2 - Second user ID
     * @return List of messages sorted by timestamp
     */
    public List<Message> getConversationHistory(Long userId1, Long userId2) {
        System.out.println("🔍 Getting conversation history between users " + userId1 + " and " + userId2);
        
        // Get messages sent from user1 to user2
        List<Message> messages1to2 = msgRepo.findBySenderIdAndReceiverId(userId1, userId2);
        System.out.println("   Messages from " + userId1 + " to " + userId2 + ": " + messages1to2.size());
        
        // Get messages sent from user2 to user1
        List<Message> messages2to1 = msgRepo.findBySenderIdAndReceiverId(userId2, userId1);
        System.out.println("   Messages from " + userId2 + " to " + userId1 + ": " + messages2to1.size());
        
        // Combine and sort by timestamp
        List<Message> allMessages = new java.util.ArrayList<>();
        allMessages.addAll(messages1to2);
        allMessages.addAll(messages2to1);
        
        // Sort by sentAt timestamp (chronological order)
        List<Message> sortedMessages = allMessages.stream()
                .sorted((m1, m2) -> {
                    if (m1.getSentAt() == null && m2.getSentAt() == null) return 0;
                    if (m1.getSentAt() == null) return 1;
                    if (m2.getSentAt() == null) return -1;
                    return m1.getSentAt().compareTo(m2.getSentAt());
                })
                .collect(Collectors.toList());
        
        System.out.println("✅ Total conversation messages: " + sortedMessages.size());
        return sortedMessages;
    }
    
    /**
     * Get all messages for a user (sent and received)
     * @param userId - User ID
     * @return List of all messages involving this user
     */
    public List<Message> getAllMessagesForUser(Long userId) {
        List<Message> sent = msgRepo.findBySenderId(userId);
        List<Message> received = msgRepo.findByReceiverId(userId);
        
        List<Message> allMessages = new java.util.ArrayList<>();
        allMessages.addAll(sent);
        allMessages.addAll(received);
        
        return allMessages.stream()
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Delete old messages (scheduled task)
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void purgeOld() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        
        List<Message> oldMessages = msgRepo.findAll().stream()
            .filter(m -> m.getSentAt() != null && m.getSentAt().isBefore(cutoff))
            .collect(Collectors.toList());
        
        if (!oldMessages.isEmpty()) {
            System.out.println("🗑️ Purging " + oldMessages.size() + " old messages");
            oldMessages.forEach(m -> msgRepo.deleteById(m.getMessageId()));
        }
    }
    
    /**
     * Mark messages as delivered
     * @param messageIds - List of message IDs to mark as delivered
     */
    public void markAsDelivered(List<Long> messageIds) {
        messageIds.forEach(id -> {
            msgRepo.findById(id).ifPresent(message -> {
                message.setDelivered(true);
                msgRepo.save(message);
            });
        });
    }
}