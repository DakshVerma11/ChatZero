package com.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CaptchaService {
    
    private static final String SECRET_KEY = "6LcT5l0rAAAAAG8_vy9qx_z9pM2Rkx4KZak81Vg-";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public CaptchaService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public boolean verifyCaptcha(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.trim().isEmpty()) {
            System.out.println("❌ CAPTCHA response is null or empty");
            return false;
        }
        
        try {
            System.out.println("🔍 Verifying CAPTCHA response: " + captchaResponse.substring(0, Math.min(20, captchaResponse.length())) + "...");
            
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // Prepare request body
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("secret", SECRET_KEY);
            map.add("response", captchaResponse);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            // Make request to Google
            ResponseEntity<String> response = restTemplate.postForEntity(VERIFY_URL, request, String.class);
            
            System.out.println("📡 Google reCAPTCHA API Response: " + response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful()) {
                CaptchaResponse captchaResp = objectMapper.readValue(response.getBody(), CaptchaResponse.class);
                System.out.println("✅ CAPTCHA verification result: " + captchaResp.isSuccess());
                return captchaResp.isSuccess();
            } else {
                System.err.println("❌ HTTP error from Google reCAPTCHA API: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error verifying CAPTCHA: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Updated inner class with @JsonIgnoreProperties to handle unknown fields
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CaptchaResponse {
        @JsonProperty("success")
        private boolean success;
        
        @JsonProperty("error-codes")
        private String[] errorCodes;
        
        @JsonProperty("challenge_ts")
        private String challengeTimestamp;
        
        @JsonProperty("hostname")
        private String hostname;
        
        @JsonProperty("action")
        private String action;
        
        @JsonProperty("score")
        private Double score;
        
        // Getters and setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String[] getErrorCodes() {
            return errorCodes;
        }
        
        public void setErrorCodes(String[] errorCodes) {
            this.errorCodes = errorCodes;
        }
        
        public String getChallengeTimestamp() {
            return challengeTimestamp;
        }
        
        public void setChallengeTimestamp(String challengeTimestamp) {
            this.challengeTimestamp = challengeTimestamp;
        }
        
        public String getHostname() {
            return hostname;
        }
        
        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public Double getScore() {
            return score;
        }
        
        public void setScore(Double score) {
            this.score = score;
        }
    }
}