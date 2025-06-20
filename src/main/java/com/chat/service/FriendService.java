
package com.chat.service;

import com.chat.entity.Friend;
import com.chat.entity.User;
import com.chat.repository.FriendRepository;
import com.chat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    /**
     * Add a friend relationship (bidirectional)
     * THIS IS WHERE THE INSERTION SHOULD HAPPEN
     */
    public boolean addFriend(String currentUsername, String friendUsername) {
        try {
            // Find both users
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
            Optional<User> friendUserOpt = userRepository.findByUsername(friendUsername);

            if (currentUserOpt.isEmpty() || friendUserOpt.isEmpty()) {
                return false;
            }

            User currentUser = currentUserOpt.get();
            User friendUser = friendUserOpt.get();

            // Check if already friends
            if (areFriends(currentUser.getUserId(), friendUser.getUserId())) {
                return false;
            }

            // 🔥 ACTUAL INSERTION HAPPENS HERE 🔥
            
            // Create bidirectional friendship
            Friend friendship1 = new Friend();
            friendship1.setUserId(currentUser.getUserId());
            friendship1.setFriendId(friendUser.getUserId());
            friendship1.setCreatedAt(LocalDateTime.now());
            
            Friend friendship2 = new Friend();
            friendship2.setUserId(friendUser.getUserId());
            friendship2.setFriendId(currentUser.getUserId());
            friendship2.setCreatedAt(LocalDateTime.now());

            // Save both relationships
            friendRepository.save(friendship1);
            friendRepository.save(friendship2);

            System.out.println("✅ Successfully added friend relationship:");
            System.out.println("   " + currentUsername + " (ID: " + currentUser.getUserId() + ") ↔ " + friendUsername + " (ID: " + friendUser.getUserId() + ")");

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error adding friend: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if two users are already friends
     */
    public boolean areFriends(Long userId1, Long userId2) {
        List<Long> user1Friends = friendRepository.findFriendIdsByUserId(userId1);
        return user1Friends.contains(userId2);
    }

    /**
     * Get all friends for a user
     */
    public List<User> getFriends(Long userId) {
        List<Long> friendIds = friendRepository.findFriendIdsByUserId(userId);
        return friendIds.stream()
                .map(friendId -> userRepository.findById(friendId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Remove friend relationship (bidirectional)
     */
    public boolean removeFriend(Long userId, Long friendId) {
        try {
            // Remove both directions of friendship
            friendRepository.deleteById(new com.chat.entity.FriendId(userId, friendId));
            friendRepository.deleteById(new com.chat.entity.FriendId(friendId, userId));
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error removing friend: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find user by username
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}