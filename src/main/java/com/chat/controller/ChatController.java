package com.chat.controller;

import com.chat.entity.User;
import com.chat.service.FriendService;  // Add this import
import com.chat.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    private final UserService userService;
    private final FriendService friendService;  // Add this

    public ChatController(UserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;  // Inject FriendService
    }

    @GetMapping("/chat")
    public String chatPage(@AuthenticationPrincipal org.springframework.security.core.userdetails.User u, Model m) {
        User user = userService.findByUsername(u.getUsername()).orElseThrow();
        m.addAttribute("username", user.getUsername());
        m.addAttribute("userId", user.getUserId());
        m.addAttribute("friends", friendService.getFriends(user.getUserId())); // Use FriendService
        return "chat";
    }
}