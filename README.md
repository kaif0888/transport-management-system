# 🚚 Transport Management System (TMS)

This is a **full-stack Transport Management System (TMS)** project built to manage transport business operations such as **master/catalog data**, **orders/consignments**, **tracking**, **vehicle management**, **expenses**, and **document handling**.

The project includes:
- ✅ **Frontend Dashboard** (Next.js + React)
- ✅ **Backend REST APIs** (Spring Boot + JPA + JWT)
- ✅ File upload/document storage support
- ✅ Invoice configuration support
- ✅ Email (SMTP) and SMS (Twilio) integration (optional)

---

## ✅ Key Features

### 📌 Master / Catalog Module
All master data is managed in one place, such as:
- Customer
- Branch
- Location
- Vehicle Type
- Expense Type

### 🚛 Transport Operations
- Consignment / order handling
- Tracking status flow
- Expense and fuel receipt management
- Vehicle and driver related management (based on module)

### 🔐 Authentication & Security
- Spring Security based authentication
- JWT Token support

### 📁 Document Management
- Upload and store documents (receipts, invoices, etc.)
- Access files using a base URL

---

## 🧱 Tech Stack

### Frontend
- Next.js
- React.js
- Tailwind CSS
- Ant Design
- React Icons
- Recharts

### Backend
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- MySQL
- Email (Java Mail Sender)
- Twilio (SMS - optional)

---

## 📂 Project Structure
<img width="974" height="374" alt="image" src="https://github.com/user-attachments/assets/f1d9a26f-056a-4f58-b6cc-ad20ff429d17" />

## ✅ Prerequisites

### Backend Requirements
- Java 17+
- Maven
- MySQL

### Frontend Requirements
- Node.js 18+
- npm (or yarn)

---

# ⚙️ Backend Setup (Spring Boot)

## ✅ 1) Create MySQL Database
Run this in MySQL:

```sql
CREATE DATABASE transportms;


<img width="1919" height="934" alt="image" src="https://github.com/user-attachments/assets/b5a01ea5-266a-43b2-8c1c-ec39ea6e817b" />

