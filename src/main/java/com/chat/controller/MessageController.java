package com.chat.controller;

import com.chat.entity.Message;
import com.chat.entity.User;
import com.chat.service.MessageService;
import com.chat.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    /**
     * Get conversation history between current user and a friend
     * @param principal - Current authenticated user
     * @param friendId - ID of the friend to get conversation with
     * @return List of messages in chronological order
     */
    @GetMapping("/{friendId}")
    public ResponseEntity<List<Message>> getMessageHistory(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendId
    ) {
        if (principal == null) {
            System.err.println("❌ No authenticated user found");
            return ResponseEntity.status(401).build();
        }

        try {
            // Get current user ID from authenticated principal
            Long currentUserId = getCurrentUserIdFromPrincipal(principal);
            
            if (currentUserId == null) {
                System.err.println("❌ Could not resolve current user ID for: " + principal.getUsername());
                return ResponseEntity.status(404).build();
            }
            /*
            System.out.println("=== MESSAGE HISTORY REQUEST ===");
            System.out.println("Current User: " + principal.getUsername() + " (ID: " + currentUserId + ")");
            System.out.println("Friend ID: " + friendId);
            System.out.println("Timestamp: " + java.time.LocalDateTime.now());
            */
            // Validate that friend exists
            if (!userService.userExistsById(friendId)) {
                //System.err.println("❌ Friend with ID " + friendId + " does not exist");
                return ResponseEntity.status(404).build();
            }
            
            // Get conversation history
            List<Message> messages = messageService.getConversationHistory(currentUserId, friendId);
            
            //System.out.println("✅ Found " + messages.size() + " messages in conversation");
            
            // Debug: Print first few messages
            for (int i = 0; i < Math.min(3, messages.size()); i++) {
                Message msg = messages.get(i);
                System.out.println("  Message " + (i+1) + ": " + msg.getSenderId() + " -> " + msg.getReceiverId() + ": " + msg.getContent().substring(0, Math.min(50, msg.getContent().length())));
            }
            
            //System.out.println("==============================");
            
            return ResponseEntity.ok(messages);
            
        } catch (Exception e) {
            //System.err.println("❌ Error loading message history: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Get user ID from authenticated principal
     * @param principal - The authenticated user details
     * @return User ID or null if not found
     */
    private Long getCurrentUserIdFromPrincipal(UserDetails principal) {
        try {
            String username = principal.getUsername();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isPresent()) {
                Long userId = userOpt.get().getUserId();
                //System.out.println("✅ Resolved user ID: " + username + " -> " + userId);
                return userId;
            } else {
                //System.err.println("❌ User not found in database: " + username);
                return null;
            }
        } catch (Exception e) {
            //System.err.println("❌ Error resolving user ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Send a message (alternative to WebSocket for testing)
     * @param principal - Current authenticated user
     * @param friendId - ID of the friend to send message to
     * @param content - Message content
     * @return Success/failure response
     */
    @PostMapping("/send/{friendId}")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendId,
            @RequestParam String content
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        try {
            Long currentUserId = getCurrentUserIdFromPrincipal(principal);
            
            if (currentUserId == null) {
                return ResponseEntity.status(404).body("Current user not found");
            }
            
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.status(400).body("Message content cannot be empty");
            }
            
            // Create and save message
            Message message = new Message();
            message.setSenderId(currentUserId);
            message.setReceiverId(friendId);
            message.setContent(content.trim());
            
            Message savedMessage = messageService.send(message);
            
            //System.out.println("✅ Message sent via API: " + savedMessage.getMessageId());
            
            return ResponseEntity.ok(savedMessage);
            
        } catch (Exception e) {
            //System.err.println("❌ Error sending message via API: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}