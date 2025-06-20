/**
   * ChatZero Application JavaScript
   * Handles all general page functionality (index, login, register, forgot-password, logout)
   */

  (function() {
    'use strict';

    // Page detection
    const isIndexPage = document.body.classList.contains('index-page') || window.location.pathname === '/';
    const isLoginPage = window.location.pathname.includes('/login');
    const isRegisterPage = window.location.pathname.includes('/register');
    const isForgotPasswordPage = window.location.pathname.includes('/forgot-password');
    const isChatPage = document.body.classList.contains('chat-page');

    // Don't run on chat page
    if (isChatPage) {
      return;
    }

    console.log('🚀 Initializing ChatZero App.js');

    // ===== CAPTCHA FUNCTIONALITY =====
    function validateCaptcha(formType) {    
      if (typeof grecaptcha === 'undefined') {
        console.log('⚠️ reCAPTCHA not loaded, skipping validation');
        return true; // Allow form submission if CAPTCHA is not available
      }

      const captchaResponse = grecaptcha.getResponse();
      const errorDiv = document.getElementById('captcha-error-' + formType);
      
      console.log('🔍 CAPTCHA validation for form:', formType);
      console.log('📝 CAPTCHA response:', captchaResponse ? 'Present' : 'Missing');
      
      if (!captchaResponse || captchaResponse.length === 0) {
        console.log('❌ CAPTCHA validation failed - no response');
        if (errorDiv) {
          errorDiv.style.display = 'block';
          errorDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        return false;
      }
      
      console.log('✅ CAPTCHA validation passed');
      if (errorDiv) {
        errorDiv.style.display = 'none';
      }
      return true;
    }

    function captchaCallback() {
      console.log('✅ CAPTCHA completed successfully');
      // Find any captcha error div and hide it
      const errorDivs = document.querySelectorAll('[id^="captcha-error-"]');
      errorDivs.forEach(div => {
        div.style.display = 'none';
      });
    }

    function initializeCaptcha() {
      if (typeof grecaptcha === 'undefined') {
        console.log('⚠️ reCAPTCHA library not available');
        return;
      }

      // Find and render any reCAPTCHA elements
      document.querySelectorAll('[data-sitekey]').forEach(element => {
        const sitekey = element.getAttribute('data-sitekey');
        if (sitekey && element.id) {
          try {
            grecaptcha.render(element.id, {
              'sitekey': sitekey,
              'callback': captchaCallback
            });
            console.log('✅ reCAPTCHA initialized for', element.id);
          } catch (error) {
            console.error('❌ reCAPTCHA initialization failed:', error);
          }
        }
      });
    }

    function setupFormValidation() {
      // Add any additional form validation logic here
      console.log('📋 Setting up form validation');
    }

    function initializeHomePage() {
      if (isIndexPage) {
        console.log('🏠 Initializing home page');
      }
    }

    function initializeAuthPages() {
      if (isLoginPage || isRegisterPage || isForgotPasswordPage) {
        console.log('🔐 Initializing auth page');
      }
    }

    function showNotification(message, type = 'info') {
      console.log(`📢 Notification (${type}): ${message}`);
      // Add notification display logic here if needed
    }
    
    // ===== GLOBAL FUNCTIONS (for inline onclick handlers) =====
    window.validateCaptcha = validateCaptcha;
    window.showNotification = showNotification;

    // ===== INITIALIZATION =====
    function initialize() {
      console.log('📱 App.js initialization started');
      
      // Setup form validation for all pages
      setupFormValidation();
      
      // Page-specific initialization
      initializeHomePage();
      initializeAuthPages();
      
      console.log('✅ App.js initialization completed');
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', initialize);
    } else {
      initialize();
    }

    // Initialize CAPTCHA when window loads (for external script compatibility)
    window.addEventListener('load', function() {
      console.log('🔄 Window loaded, initializing CAPTCHA');
      initializeCaptcha();
    });

  })();