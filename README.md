# ChatZero - Real-time Chat Application

![ChatZero Logo](https://github.com/DakshVerma11/ChatZero/blob/main/src/main/resources/static/ChatZeroLogo.png)

## Overview

ChatZero is a real-time chat application built with Spring Boot that enables secure user authentication, friend management, and instant messaging. This project was developed during my internship at CDAC Noida as my first major project. Through this project, I learned about socket programming, the Spring Framework, Spring Boot, and much more.

### GIF Demonstrations

- **Registration and Adding Friends:**  
  ![registration_friends.gif](https://drive.google.com/uc?export=view&id=1dslr_3olPf0CUlQMlgKUI8A1T-FZf530)

- **Real-time Chat Between Multiple Users:**  
  ![real_time_chat.gif](https://drive.google.com/uc?export=view&id=1_q1OCp8HXuNU2i7uCqu067gKRX29-RtD)

  
## 🚀 Features

- **User Authentication**
  - Secure registration and login with email/username
  - Password encryption with BCrypt
  - Google reCAPTCHA integration for bot prevention
  - Password recovery flow

- **Real-time Messaging**
  - Instant message delivery using WebSockets
  - Message history and persistence
  - Read receipts and typing indicators

- **Friend Management**
  - Add friends by username
  - Bidirectional friendship model
  - Friend online status tracking

- **Modern UI**
  - Responsive design with dark theme and glassmorphism effects
  - Clean and intuitive chat interface

## 🛠️ Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3.5.0**
- **Spring WebSocket**: Provides real-time bidirectional communication via STOMP over WebSocket.
- **Spring Security**: Manages authentication and authorization, password encryption with BCrypt, and reCAPTCHA verification.
- **Spring Data JPA**: Simplifies database access by mapping Java objects (entities) to database tables.
- **MySQL**: Stores user, friend, and message data.

### Frontend
- **Thymeleaf**: A server-side template engine to render HTML pages dynamically.
- **HTML5/CSS3**: Structures and styles the web pages.
- **JavaScript**: Implements client-side interactivity, WebSocket communication, and form validation.
- **SockJS & STOMP.js**: Provide WebSocket client support and fallback options.

## 📂 Project Structure

```
chatv2/chat/
├── src/
│   ├── main/
│   │   ├── java/com/chat/          # Java source code
│   │   │   ├── config/             # Application configuration (SecurityConfig, WebSocketConfig)
│   │   │   ├── controller/         # HTTP and WebSocket controllers (AuthController, ChatController, FriendController, MessageController, WebSocketController)
│   │   │   ├── dto/                # Data Transfer Objects (e.g., MessageDto)
│   │   │   ├── entity/             # JPA entities representing the database schema (User, Message, Friend, FriendId)
│   │   │   ├── repository/         # Data access interfaces (UserRepository, FriendRepository, MessageRepository)
│   │   │   ├── service/            # Business logic (UserService, MessageService, FriendService, CaptchaService, ChatUserDetailsService)
│   │   │   └── ChatApplication.java # Main entry point of the Spring Boot application
│   │   └── resources/
│   │       ├── static/             # Static assets (CSS, JavaScript, images)
│   │       ├── templates/          # Thymeleaf HTML templates (login.html, register.html, chat.html, etc.)
│   │       └── application.properties # Application configuration properties
│   └── test/                      # Test classes
└── pom.xml                        # Maven project configuration and dependencies
```

## 🏗️ Architecture

### Key Components

1. **Authentication System**
   - **AuthController**: Handles registration, login, and password recovery.
   - **SecurityConfig**: Configures Spring Security for URL protection, form login, and logout behavior.
   - **CaptchaService**: Integrates Google reCAPTCHA on login and registration forms.

2. **Chat Engine**
   - **WebSocketController**: Facilitates the real-time messaging connection through WebSockets.
   - **MessageController**: Offers REST API endpoints for fetching conversation history and sending messages for testing.
   - **WebSocketConfig**: Sets up STOMP endpoints and in-memory message broker.

3. **Friend Management**
   - **FriendController**: Manages adding and listing friends.
   - **FriendService**: Implements business logic for bidirectional friend relationships.
   
4. **Data Models**
   - **User**: Represents a user account.
   - **Message**: Contains the details of a chat message.
   - **Friend & FriendId**: Represent a friendship between two users.

### Data Flow

1. **Registration & Adding Friends**
   - Users register via the `/register` page (with reCAPTCHA for bot prevention).
   - After registration, users log in and can add friends using the FriendController & FriendService.

2. **Chat Flow**
   - Authenticated users access the `/chat` page.
   - The chat interface uses WebSockets for real-time messaging.
   - Messages are saved to the database and broadcast to connected clients.
   - Clients can also retrieve their conversation history via REST APIs.

## 🚀 Installation and Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Database Setup
Create the database and user using the following commands:
```sql
CREATE DATABASE NITReport;
CREATE USER 'CDAC'@'localhost' IDENTIFIED BY 'nitdelhi';
GRANT ALL PRIVILEGES ON NITReport.* TO 'CDAC'@'localhost';
FLUSH PRIVILEGES;
```

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DakshVerma11/ChatAppJava.git
   cd ChatAppJava/chatv2/chat
   ```

2. **Build the project:**
   ```bash
   ./mvnw clean package
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application:**  
   Open [http://localhost:8081](http://localhost:8081) in your browser.

## 💻 Usage

1. **Registration:**
   - Visit the `/register` page.
   - Fill in your username, email, and password.
   - Complete the reCAPTCHA challenge.
   - Submit the form.

2. **Login:**
   - Navigate to the `/login` page.
   - Enter your username/email and password.
   - Complete reCAPTCHA if prompted.
   - Log in to the chat application.

3. **Adding Friends:**
   - On the chat page, use the "Add Friend" field in the sidebar.
   - Enter the username of the friend you want to add and click the add button.
   - The friend will appear in your friends list.

4. **Messaging:**
   - Select a friend from the sidebar to open a conversation.
   - Type your message in the input field and press Enter or click the send button.
   - Messages are sent and received in real time using WebSockets.

## 🔒 Security Features

- **Password Encryption:**  
  User passwords are securely hashed using BCrypt before storage.

- **CAPTCHA Protection:**  
  Google reCAPTCHA is integrated into registration and login forms to prevent automated bots.

- **Spring Security:**  
  Protects endpoints and handles session-based authentication.

- **Input Validation:**  
  All user inputs are validated on the server side to ensure data integrity.

## 🎓 What I Learned

This project was a significant milestone during my internship at CDAC Noida. I gained hands-on experience with:

- **WebSocket Communication:**  
  Implementing real-time, bidirectional communication between users.

- **Spring Framework Fundamentals:**  
  Learning dependency injection, transaction management, and the overall architecture of a Spring Boot application.

- **Spring Boot:**  
  Quickly building production-ready applications with minimal configuration using auto-configuration and starter dependencies.

- **Spring Security:**  
  Integrating robust authentication and authorization mechanisms.

- **JPA/Hibernate:**  
  Simplifying database interactions and mapping Java classes to relational database tables.

- **Thymeleaf:**  
  Rendering dynamic HTML content with server-side templates.

- **Front-End Integration:**  
  Connecting JavaScript functionalities with server-side operations to create a seamless and interactive user experience.

## 🚧 Future Improvements

- Add group chat functionality.
- Integrate media file sharing.
- Implement end-to-end encryption.
- Enhance mobile responsiveness.
- Allow user profile customization.
- Add message search capabilities.
- Incorporate push notifications for new messages.

## 📚 References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

## 👤 Author

**Daksh Verma**  
Intern at CDAC Noida (2025)  
GitHub: [DakshVerma11](https://github.com/DakshVerma11)

## 📜 License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

---

*This project was developed as part of my internship at CDAC Noida in 2025.*
