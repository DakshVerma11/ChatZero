
package com.chat.controller;

import com.chat.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add-friend")
    public ResponseEntity<?> addFriend(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String username
    ) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("success", false);
            response.put("message", "Not authenticated.");
            return ResponseEntity.status(401).body(response);
        }

        // Validation
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username cannot be empty.");
            return ResponseEntity.badRequest().body(response);
        }

        if (principal.getUsername().equals(username)) {
            response.put("success", false);
            response.put("message", "Cannot add yourself as friend.");
            return ResponseEntity.badRequest().body(response);
        }

        // 🔥 CALL SERVICE METHOD (WHERE INSERTION ACTUALLY HAPPENS)
        boolean success = friendService.addFriend(principal.getUsername(), username);

        if (success) {
            response.put("success", true);
            response.put("message", "Successfully added " + username + " as friend!");
        } else {
            response.put("success", false);
            response.put("message", "Failed to add friend. User may not exist or already friends.");
        }

        return ResponseEntity.ok(response);
    }
}