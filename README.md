## **Real-Time Auction System**

**Description:**

A backend system for live auctions where users can bid on items in real-time. The system updates the highest bid instantly, tracks user activity and maintains historical bidding data. Designed to demonstrate **event-driven microservices architecture, real-time updates and caching**.

**Core Features:**

- User registration & authentication using JWT
- CRUD operations for auction items
- Real-time bidding via **Kafka**
- Redis caching for highest bid via Redis
- PostgreSQL for persistent auction and bid history
- Microservices:
    - **User Service** – Auth, profile
    - **Auction Service** – Auction & bids
    - **Notification Service** – Alerts on new highest bid
- gRPC for Auction ↔ Notification service communication
- Dockerized services within an internal cluster for local

**Tech Stack:**

- Java + Spring Boot 
- PostgreSQL 
- Redis 
- Apache Kafka 
- JWT  
- Docker
- gRPC
- JUnit5 / Mockito / REST-Assured
- AWS(LocalStack)

**Future Development:**

- Inter service communication using gRPC
- Swagger Documentation
- Testing Code
- Production Grade Cloud Native Deployment Setup
- LeaderBoard for Auction
- Send Results Notification to Users

## Architecture

### **Microservices & Responsibilities**

| Service | Responsibility |
| --- | --- |
| User Service | Auth, profile management, JWT/OAuth |
| Auction Service | Manage items, bids, current highest bid |
| Notification Service | Real-time alerts for highest bid updates |
| Optional API Gateway | Route requests, handle JWT verification |

---

### **PostgreSQL Schema**

- `auctions`: id, title, description, start_time, end_time, status, createdAt
- `users`: id, name, email, password, role, createdAt
- `bids`: id, user_id, auction_id, amount, timestamp

---

### **Redis Usage**

- Cache **active auctions** with highest bid
---

### **Kafka Topics**

- `auction-bids` → Auction Service publishes new bids
