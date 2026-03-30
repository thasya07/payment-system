# Payment System Microservices

Sistem ini terdiri dari **3 microservices**:

1. **Order Service** – Mengelola pembuatan dan status order.  
2. **Payment Service** – Mengelola pembayaran, callback, dan status pembayaran.  
3. **Notification Service** – Mengirim notifikasi ke user ketika pembayaran selesai.

---

## Alur Sistem

1. Client membuat order dengan payload ke **Order Service**:

```json
{
  "amount": 100000,
  "accountNumber": "1234567890"
}
