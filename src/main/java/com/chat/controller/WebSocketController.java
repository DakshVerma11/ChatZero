package com.chat.controller;

import com.chat.dto.MessageDto;
import com.chat.entity.Message;
import com.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WebSocketController {
    
    private final MessageService messageService;
    
    public WebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageDto broadcast(MessageDto msg) {
        try {
            // Save message to database
            Message message = new Message();
            message.setSenderId(msg.senderId);
            message.setReceiverId(msg.receiverId);
            message.setContent(msg.content);
            message.setSentAt(LocalDateTime.now());
            message.setDelivered(true);
            
            Message savedMessage = messageService.send(message);
            
            // Create response DTO with saved message data
            MessageDto response = new MessageDto();
            response.senderId = savedMessage.getSenderId();
            response.receiverId = savedMessage.getReceiverId();
            response.content = savedMessage.getContent();
            
            //System.out.println("✅ Message saved and broadcast: " + savedMessage.getMessageId());
            
            return response;
            
        } catch (Exception e) {
            //System.err.println("❌ Error handling WebSocket message: " + e.getMessage());
            e.printStackTrace();
            return msg; // Return original message if save fails
        }
    }
}