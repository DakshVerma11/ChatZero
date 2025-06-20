package com.chat.service;

  import com.chat.entity.User;
  import com.chat.repository.UserRepository;
  import com.chat.repository.FriendRepository;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  import java.time.LocalDateTime;
  import java.util.List;
  import java.util.Optional;
  import java.util.stream.Collectors;

  @Service
  @Transactional
  public class UserService {
      private final UserRepository userRepo;
      private final FriendRepository friendRepo;
      private final PasswordEncoder passwordEncoder;

      public UserService(UserRepository userRepo, FriendRepository friendRepo, PasswordEncoder passwordEncoder) {
          this.userRepo = userRepo;
          this.friendRepo = friendRepo;
          this.passwordEncoder = passwordEncoder;
      }

      @Transactional
      public User register(User u) {
          try {
              u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
              u.setLastSeen(LocalDateTime.now());
              User savedUser = userRepo.save(u);
              System.out.println("✅ User registered successfully: ID=" + savedUser.getUserId() + ", Username=" + savedUser.getUsername());
              return savedUser;
          } catch (Exception e) {
              System.err.println("❌ Error registering user: " + e.getMessage());
              e.printStackTrace();
              throw e;
          }
      }


 // Compare raw password at login
    public Optional<User> authenticate(String usernameOrEmail, String rawPw) {
        Optional<User> u = userRepo.findByUsername(usernameOrEmail)
            .or(() -> userRepo.findByEmail(usernameOrEmail));
        return u.filter(user -> passwordEncoder.matches(rawPw, user.getPasswordHash()));
    }

    public void updateStatus(Long id, boolean online) {
        userRepo.findById(id).ifPresent(u -> {
            u.setOnline(online);
            u.setLastSeen(LocalDateTime.now());
            userRepo.save(u);
        });
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    
    /**
     * Check if user exists by ID
     * @param userId - User ID to check
     * @return true if user exists, false otherwise
     */
    public boolean userExistsById(Long userId) {
        if (userId == null) {
            return false;
        }
        return userRepo.existsById(userId);
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userRepo.findById(userId);
    }

    /**
     * Get username by user ID
     */
    public String getUsernameById(Long userId) {
        return findById(userId)
                .map(User::getUsername)
                .orElse(null);
    }

    
    /*
    public List<User> getFriends(Long userId) {
        List<Long> friendIds = friendRepo.findFriendIdsByUserId(userId);
        return friendIds.stream()
                .map(friendId -> userRepo.findById(friendId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    */
}