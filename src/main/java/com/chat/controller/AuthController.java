package com.chat.controller;

import com.chat.entity.User;
import com.chat.service.UserService;
import com.chat.service.CaptchaService;
import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    public AuthController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    /**
     * Landing page.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Show login form.  If user just registered, we can display a flash message.
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) boolean registered,
            Model model
    ) {
        if (registered) {
            model.addAttribute("msg", "Registration successful! Please log in.");
        }
        return "login";
    }
    
    /**
     * Display the registration form.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Process the registration submission.
     * Performs basic validation and checks for duplicate usernames/emails.
     * Includes CAPTCHA verification.
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(name = "g-recaptcha-response", required = false) String captchaResponse,
            RedirectAttributes redirectAttrs,
            Model model
    ) {
        
        System.out.println("🚀 Registration attempt for user: " + user.getUsername());
        System.out.println("📝 CAPTCHA response received: " + (captchaResponse != null ? "Yes" : "No"));
        
        try {
            // 1) CAPTCHA verification
            if (!captchaService.verifyCaptcha(captchaResponse)) {
                System.out.println("❌ CAPTCHA verification failed");
                model.addAttribute("captchaError", "Please complete the CAPTCHA verification.");
                return "register";
            }
            System.out.println("✅ CAPTCHA verification passed");
            
            // 2) Bean-level validation errors?
            if (bindingResult.hasErrors()) {
                System.out.println("❌ Validation errors: " + bindingResult.getAllErrors());
                return "register";
            }

            // 3) Duplicate username/email?
            if (userService.existsByUsername(user.getUsername())) {
                System.out.println("❌ Username already exists: " + user.getUsername());
                model.addAttribute("usernameError", "Username already taken");
                return "register";
            }
            if (userService.existsByEmail(user.getEmail())) {
                System.out.println("❌ Email already exists: " + user.getEmail());
                model.addAttribute("emailError", "Email already registered");
                return "register";
            }
            
            // 4) All good → save user
            User savedUser = userService.register(user);
            System.out.println("✅ User registered successfully: ID=" + savedUser.getUserId() + ", Username=" + savedUser.getUsername());

            // 5) Redirect to login with a flag so we can show a success msg
            redirectAttrs.addAttribute("registered", true);
            return "redirect:/login";
            
        } catch (Exception e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    /**
     * Show forgot‐password form.
     * 
     * 
     * 
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }
       

    /**
     * Handle forgot‐password submission.
     * 
     * 
     * 
     * IDK how to implement
     */
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("username") String username,
            RedirectAttributes redirectAttrs
    ) {
    	redirectAttrs.addFlashAttribute("msg","Send a mail titles 'Forgot Password -“" + username + "”' to test@example.com , a reset link has been sent.");
    	/*
        if (userService.existsByUsername(username) || userService.existsByEmail(username)) {
            // TODO: send reset link via email/SMS
            redirectAttrs.addFlashAttribute("msg",
                "If an account exists for “" + username + "”, a reset link has been sent.");
        } else {
            redirectAttrs.addFlashAttribute("msg",
                "If an account exists for “" + username + "”, a reset link has been sent.");
        }
        */
        return "redirect:/forgot-password";
    }
    
    
    
}
