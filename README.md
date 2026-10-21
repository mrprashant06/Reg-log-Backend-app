# 🛡️ AURA LUXE — Spring Boot Authentication & OTP Backend

Production-grade, high-performance authentication service built with **Java 21**, **Spring Boot 3.3.4**, **Spring Security (JJWT Stateless)**, **Spring Data JPA**, and **MySQL 8.0**.

---

## ✨ Features

- **Relational MySQL 8.0 Schema**: Fully mapped JPA entities with indexes and constraint checks.
- **Dual Authentication Modes**:
  - Secure **Password Mode** with BCrypt password hashing.
  - Time-bound **6-Digit OTP Mode** with auto-expiration (5 min), attempts throttling, and single-use consumption.
- **Identifier Agnostic**: Supports both **Email Address** and **Phone Number** registration and login.
- **OpenAPI 3 / Swagger Documentation**: Preconfigured with Swagger UI at `/swagger-ui.html`.
- **CORS Configured**: Seamless communication with Vite frontend at `http://localhost:5173`.

---

## 🗄️ Database Schema (`ecommerce_auth_db`)

```sql
-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NULL UNIQUE,
    phone VARCHAR(20) NULL UNIQUE,
    password_hash VARCHAR(255) NULL,
    auth_preference ENUM('PASSWORD', 'OTP') NOT NULL DEFAULT 'PASSWORD',
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_phone (phone)
);

-- OTP Verifications Table
CREATE TABLE otp_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(150) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    purpose VARCHAR(30) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_consumed BOOLEAN NOT NULL DEFAULT FALSE,
    attempts_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_identifier (identifier, purpose, is_consumed)
);
```

---

## 📡 REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/send-otp` | Generates & logs 6-digit OTP code, stores in `otp_verifications`. |
| `POST` | `/api/auth/verify-otp` | Verifies code against MySQL record and marks consumed. |
| `POST` | `/api/auth/register` | Registers user into `users` table via Password or OTP. |
| `POST` | `/api/auth/login` | Authenticates credentials and returns JWT Bearer token. |
| `GET` | `/api/auth/me` | Returns authenticated user profile. |
| `POST` | `/api/auth/logout` | Safely invalidates/terminates session. |

---

## 🚀 Getting Started

### Prerequisites
- **Java 21**
- **MySQL 8.0** running on `localhost:3306`

### Run with Gradle
```bash
./gradlew bootRun
```
Service runs on port `8080`.
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
