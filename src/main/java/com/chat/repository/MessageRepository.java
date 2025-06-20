
package com.chat.repository;

import com.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Existing method
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    
    // Additional methods for better functionality
    List<Message> findBySenderId(Long senderId);
    
    List<Message> findByReceiverId(Long receiverId);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT m FROM Message m WHERE m.sentAt < :cutoffDate")
    List<Message> findMessagesOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}