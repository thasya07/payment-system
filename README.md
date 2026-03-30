# Payment System

Payment System is a microservice-based application to handle payments, order updates, and notifications. It consists of 3 services: Order Service (receives order requests and initiates payments), Payment Service (processes payments, handles callbacks from the payment gateway, and updates payment status), Notification Service (sends notifications to customers after payment is processed).

Features: Idempotent payment callback handling, Accurate payment status (PENDING, COMPLETED, FAILED), Automatic notifications to order service and customer email.

Setup & Run:

Clone the repository:
```bash
git clone <REPO_URL>
cd payment-system

Database: Use PostgreSQL / MySQL / SQLite according to your application.properties. Example PostgreSQL configuration:

spring.datasource.url=jdbc:postgresql://localhost:5432/paymentdb
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update

All data is stored in the database; H2 in-memory is not used.

Build & Run:

mvn clean install
mvn spring-boot:run

API Flow:

Create Payment
Endpoint: POST /payments
Request Payload:
{
  "orderId": "UUID_ORDER",
  "amount": 100000,
  "accountNumber": "1234567890",
  "currency": "IDR"
}

Response: Payment status PENDING with transactionId.

Payment Callback (External)
Endpoint: POST /payments/callback/{transactionId}
Example Payload:
{
  "status": "COMPLETED",
  "paymentMethod": "BANK_TRANSFER",
  "paidAt": "2026-03-27T22:30:00",
  "referenceNumber": "REF123456",
  "amount": 100000,
  "currency": "IDR"
}

Status can be PENDING, COMPLETED, or FAILED. Callback is idempotent, so multiple calls will not result in double charges.

Notifications: After a successful callback, the system automatically calls Order Service (orderId + status) and Notification Service (orderId, amount, accountNumber, status, email).

Testing Flow: Call Order Service to create an order:

{
  "amount": 100000,
  "accountNumber": "1234567890"
}

Payment Service generates a transactionId and sets status to PENDING. Trigger Payment Callback externally with payload:

{
  "status": "COMPLETED",
  "paymentMethod": "BANK_TRANSFER",
  "paidAt": "2026-03-27T22:30:00",
  "referenceNumber": "REF123456",
  "amount": 100000,
  "currency": "IDR"
}

Payment Service saves the callback, updates payment status, and notifies Order Service & Notification Service.