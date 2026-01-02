# LendWise Nepal
**AI-Driven Micro-Lending Platform for Unbanked Merchants**

---

## Project Overview

**LendWise Nepal** is a microservices-based, AI-enabled digital lending platform designed to support unbanked and underbanked merchants in Nepal.  
The system leverages **alternative digital transaction data** (QR payments, wallet transactions, and utility payments) to assess creditworthiness and provide instant micro-loans without traditional collateral.

The platform is built using **modern distributed system principles**, integrating secure identity management, transaction analytics, AI-based credit scoring, and automated loan lifecycle management.

---

## System Architecture Overview

LendWise follows a **microservices architecture**, where each core business capability is implemented as an independent service.  
Services communicate via REST APIs and asynchronous messaging, enabling scalability, fault isolation, and maintainability.

---

## 🔧 Microservices Description

### 1. Middleware Service
- **Port:** `9004`
- **Context Path:** `/iam`

**Purpose:**  
Acts as an API gateway / request orchestrator for external clients. It validates requests, enforces security policies, and routes traffic to appropriate backend services.

**Responsibilities:**
- Request validation and forwarding
- Centralized authentication enforcement
- Cross-service communication control
- Rate limiting and request filtering

---

### 2. IAM Service (Identity and Access Management)
- **Port:** `9001`
- **Context Path:** `/iam`

**Purpose:**  
Manages authentication, authorization, and user identity across the platform.

**Responsibilities:**
- Merchant and admin authentication
- Role-based access control (RBAC)
- Token generation and validation (JWT)
- Secure session management

---

### 3. Loan Service
- **Port:** `9002`
- **Context Path:** `/loan`

**Purpose:**  
Handles the complete **loan lifecycle**, from application to repayment.

**Responsibilities:**
- Loan application processing
- Credit score integration
- Loan approval and rejection logic
- Repayment tracking and interest calculation
- Loan status management

---

### 4. Merchant Service
- **Port:** `9003`
- **Context Path:** `/lendwisemw`

**Purpose:**  
Manages merchant profiles and onboarding workflows.

**Responsibilities:**
- Merchant registration and profile management
- KYC document handling
- Business information management
- Merchant eligibility validation

---

### 5. Notification Service
- **Port:** `9005`

**Purpose:**  
Delivers asynchronous notifications related to loan and account events.

**Responsibilities:**
- Loan approval/rejection notifications
- Repayment reminders
- System alerts and updates
- Email/SMS simulation support

---

### 6. Transaction Service
- **Port:** `9006`
- **Context Path:** `/txn`

**Purpose:**  
Aggregates and analyzes merchant transaction data used for credit assessment.

**Responsibilities:**
- Transaction ingestion and storage
- Cash flow analysis
- Transaction pattern evaluation
- Feature extraction for AI credit scoring

---

## AI Credit Scoring (Conceptual)

The platform integrates an AI-based credit scoring engine that evaluates merchant creditworthiness using:
- Transaction frequency
- Average transaction value
- Cash flow stability
- Payment consistency

The generated credit score is used by the **Loan Service** to determine loan eligibility and limits.

---

## Data Storage Strategy

- **PostgreSQL:**
    - Loans, repayments, ledgers
    - User accounts and transactional integrity

- **MongoDB:**
    - KYC documents
    - Semi-structured transaction logs

- **Redis (Optional):**
    - Caching credit scores
    - Rate limiting and performance optimization

---

## 🛠️ Technology Stack

| Layer            | Technology |
|------------------|------------|
| Frontend         | React.js, Tailwind CSS |
| Backend          | Spring Boot (Java) |
| AI/ML            | Python, FastAPI, Scikit-learn |
| Databases        | PostgreSQL, MongoDB |
| Messaging        | RabbitMQ |
| Authentication  | JWT, Role-Based Access |
| Deployment       | Docker, Docker Compose |

---

## Getting Started (High-Level)

1. Clone the repository
2. Configure service ports and database connections
3. Start services individually or via Docker Compose
4. Access APIs via the Middleware or service-specific endpoints

---

## Academic Relevance

This project demonstrates:
- Microservices architecture
- Secure IAM implementation
- AI-based decision systems
- Financial technology (FinTech) application
- Real-world problem solving in the Nepali digital ecosystem

---

## License

This project is developed for **academic and research purposes** as part of a Master’s level project.

---

## Author

**Biplaw Chaudhary**  
Master’s Project – LendWise Nepal  
