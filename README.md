# Payment System

## Description
Payment System is a microservice-based application to handle payments, order updates, and notifications.  
It consists of 3 services:
1. **Order Service** – receives order requests and initiates payments.  
2. **Payment Service** – processes payments, handles callbacks from the payment gateway, and updates payment status.  
3. **Notification Service** – sends notifications to customers after payment is processed.

Features:
- Idempotent payment callback handling  
- Accurate payment status (`PENDING`, `COMPLETED`, `FAILED`)  
- Automatic notifications to order service and customer email  

---

## Setup & Run

### Clone the repository
```bash
git clone <REPO_URL>
cd payment-system
```

### Database Configuration
Use PostgreSQL / MySQL / SQLite according to your `application.properties`.  

Example PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/paymentdb
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
```
> All data is stored in the database; H2 in-memory is not used.

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

---

## API Flow

### 1. Create Payment
**Endpoint:** `POST /payments`  
**Request Payload:**
```json
{
  "orderId": "UUID_ORDER",
  "amount": 100000,
  "accountNumber": "1234567890",
  "currency": "IDR"
}
```
**Response:** Payment status `PENDING` with `transactionId`.

### 2. Payment Callback (External)
**Endpoint:** `POST /payments/callback/{transactionId}`  
**Example Payload:**
```json
{
  "status": "COMPLETED",
  "paymentMethod": "BANK_TRANSFER",
  "paidAt": "2026-03-27T22:30:00",
  "referenceNumber": "REF123456",
  "amount": 100000,
  "currency": "IDR"
}
```
- `status` can be `PENDING`, `COMPLETED`, or `FAILED`  
- Callback is **idempotent**, multiple calls will not result in double charges

### 3. Notifications
After a successful callback, the system automatically calls:  
- **Order Service:** sends `orderId` + status  
- **Notification Service:** sends `orderId`, `amount`, `accountNumber`, `status`, `email`  

---

## Testing Flow
1. Call Order Service to create an order:
```json
{
  "amount": 100000,
  "accountNumber": "1234567890"
}
```
2. Payment Service generates a `transactionId` and sets status to `PENDING`.  
3. Trigger Payment Callback externally with payload:
```json
{
  "status": "COMPLETED",
  "paymentMethod": "BANK_TRANSFER",
  "paidAt": "2026-03-27T22:30:00",
  "referenceNumber": "REF123456",
  "amount": 100000,
  "currency": "IDR"
}
```
4. Payment Service:  
   - Saves the callback  
   - Updates payment status  
   - Notifies Order Service & Notification Service