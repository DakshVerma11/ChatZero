
package com.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friends")
@IdClass(FriendId.class)
public class Friend {
    
    @Id 
    @Column(name = "user_id")
    private Long userId;
    
    @Id 
    @Column(name = "friend_id")
    private Long friendId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "category")
    private String category;

    // Default constructor
    public Friend() {}

    // Constructor with required fields
    public Friend(Long userId, Long friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.createdAt = LocalDateTime.now();
    }

    // Getter and Setter for userId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter and Setter for friendId
    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    // Getter and Setter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and Setter for category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                ", createdAt=" + createdAt +
                ", category='" + category + '\'' +
                '}';
    }
}