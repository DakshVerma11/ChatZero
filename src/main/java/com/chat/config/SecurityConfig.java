package com.chat.config;

  import com.chat.service.ChatUserDetailsService;
  import com.chat.service.CaptchaService;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
  import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.security.web.SecurityFilterChain;
  import org.springframework.security.web.authentication.AuthenticationFailureHandler;
  import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
  import org.springframework.security.config.annotation.web.builders.HttpSecurity;

  import jakarta.servlet.http.HttpServletRequest;
  import jakarta.servlet.http.HttpServletResponse;
  import org.springframework.security.core.AuthenticationException;
  import java.io.IOException;

  @Configuration
  @EnableWebSecurity
  public class SecurityConfig {
      private final ChatUserDetailsService chatUserDetailsService;
      private final CaptchaService captchaService;

      public SecurityConfig(ChatUserDetailsService chatUserDetailsService, CaptchaService captchaService) {
          this.chatUserDetailsService = chatUserDetailsService;
          this.captchaService = captchaService;
      }

      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",                // home/index
                    "/login",           // login form
                    "/register",        // registration form
                    "/forgot-password", // forgot-password form
                    "/css/**", "/js/**" // static assets
                ).permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(chatUserDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username") 
                .passwordParameter("password") 
                .defaultSuccessUrl("/chat", true)
                .failureHandler(customAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );

          return http.build();
      }
      
      @Bean
      public AuthenticationFailureHandler customAuthenticationFailureHandler() {
          return new SimpleUrlAuthenticationFailureHandler() {
              @Override
              public void onAuthenticationFailure(HttpServletRequest request, 
                                                HttpServletResponse response, 
                                                AuthenticationException exception) throws IOException {
                  
                  String captchaResponse = request.getParameter("g-recaptcha-response");
                  
                  if (!captchaService.verifyCaptcha(captchaResponse)) {
                      response.sendRedirect("/login?captcha");
                  } else {
                      response.sendRedirect("/login?error");
                  }
              }
          };
      }
      
      @Bean
      public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
      }
  }