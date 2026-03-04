# 🏨 Aqua Hotel System

<div align="center">

![Java](https://img.shields.io/badge/Java-13+-orange?style=flat-square&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.6+-red?style=flat-square&logo=apachemaven)
![MongoDB](https://img.shields.io/badge/MongoDB-6.x-green?style=flat-square&logo=mongodb)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-brightgreen?style=flat-square)

A full-stack desktop hotel management application built with Java Swing and MongoDB.  
Covers the complete guest lifecycle — from registration and room browsing through booking, receipt generation, and check-out.

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Running the App](#-running-the-app)
- [Contributing](#-contributing)
- [Roadmap](#-roadmap)
- [License](#-license)

---

## ✨ Features

| Category | Details |
|---|---|
| **Authentication** | Registration, login, SHA-256 hashed passwords, brute-force lockout |
| **Room Browsing** | Image gallery, search by type, price cap, and guest count |
| **Booking** | Date range picker, booking summary, MongoDB persistence |
| **Receipts** | Auto-generated PDF receipts via iTextPDF, opens on confirmation |
| **Notifications** | In-app observer pattern — booking, check-in, check-out, cancellation |
| **Inventory** | Room status tracking, maintenance and cleaning task management |
| **Reporting** | Occupancy rate, revenue by room type, average stay duration |

---

## 🛠 Tech Stack

- **Language:** Java 13
- **UI:** Java Swing (desktop GUI)
- **Build:** Apache Maven 3.6+
- **Database:** MongoDB 6.x
- **DB Driver:** MongoDB Java Driver 4.x
- **PDF:** iTextPDF 5.x
- **Auth:** `java.security` — SHA-256 + per-user random salt

---

## ✅ Prerequisites

Before you begin, ensure you have the following installed:

- [JDK 13+](https://adoptium.net/)
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [MongoDB 6.x](https://www.mongodb.com/try/download/community) running on `localhost:27017`


---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/aqua-hotel-system.git
cd aqua-hotel-system
```

### 2. Start MongoDB

```bash
# macOS / Linux
mongod

# Windows (if installed as a service)
net start MongoDB
```

### 3. Add image assets

Create an `images/` directory in the project root and populate it:

```
images/
├── 1.jpg                   # Hero / background image
├── logo.png                # Hotel logo (header)
├── hotel_icon.png          # Application window icon
├── deluxe-double.jpg       # Room images
├── superior-suite.jpg
├── family-room.jpg
├── executive-suite.jpg
├── standard-twin.jpg
├── spa&welness.JPEG         # Amenity cards
├── finedining.JPEG
├── conference rooms.JPEG
└── pool&fitness.JPEG
```

> **Tip:** The app gracefully falls back to placeholder images if any file is missing, so you can run it without all assets during development.

### 4. Build the project

```bash
mvn clean install
```

A successful build ends with `BUILD SUCCESS`.

---

## 📁 Project Structure

```
aqua-hotel-system/
├── src/
│   └── main/
│       └── java/
│           └── com/hotel/
│               ├── App.java                  # Entry point, Swing UI & navigation
│               ├── Room.java                 # Room domain model
│               ├── User.java                 # User domain model
│               ├── Booking.java              # Booking domain model
│               ├── Payment.java              # Payment record model
│               ├── BookingStatus.java        # Enum: PENDING → COMPLETED
│               ├── UserRole.java             # Enum: GUEST | STAFF | ADMIN
│               ├── RoomManager.java          # Room lifecycle & availability logic
│               ├── SecurityManager.java      # Auth, hashing, lockout policy
│               ├── NotificationManager.java  # Observer-based notification hub
│               ├── ReportingManager.java     # Occupancy & revenue analytics
│               ├── InventoryManager.java     # Stock and task management
│               ├── LoginDialog.java          # Login UI dialog
│               ├── RegisterDialog.java       # Registration UI dialog
│               ├── DateRangeDialog.java      # Check-in/out date picker
│               ├── db/
│               │   ├── DatabaseConfig.java   # MongoDB singleton connection
│               │   ├── MongoDBConfig.java    # POJO codec & index setup
│               │   ├── UserDAO.java          # User data access object
│               │   ├── RoomDAO.java          # Room data access object
│               │   └── BookingDAO.java       # Booking data access object
│               └── utils/
│                   └── ReceiptGenerator.java # iTextPDF receipt builder
├── images/                                   # UI and room image assets
├── pom.xml                                   # Maven build & dependencies
└── README.md
```

---

## ⚙️ Configuration

The MongoDB connection string is set in `DatabaseConfig.java`:

```java
private static final String CONNECTION_STRING = "mongodb://localhost:27017";
private static final String DATABASE_NAME     = "hoteldb";

## ▶️ Running the App

**Option A — Maven exec (recommended)**

```bash
# Windows PowerShell
mvn exec:java "-Dexec.mainClass=com.hotel.App"

# macOS / Linux
mvn exec:java -Dexec.mainClass="com.hotel.App"
```

**Option B — JAR file**

```bash
java -jar target/hotel-management-1.0-SNAPSHOT.jar
```

**Option C — With classpath (if JAR has no manifest)**

```bash
java -cp target/hotel-management-1.0-SNAPSHOT.jar com.hotel.App
```

---

## 🤝 Contributing

Contributions are welcome! Please follow the workflow below.

### Branch naming

```
feature/<short-description>     # New functionality
fix/<short-description>         # Bug fixes
refactor/<short-description>    # Code improvements without behaviour change
docs/<short-description>        # Documentation only
```

### Commit messages — [Conventional Commits](https://www.conventionalcommits.org/)

```
feat: add admin dashboard with occupancy chart
fix: correct BookingStatus COMPLETED enum value
refactor: extract booking logic from App.java into BookingService
docs: update README with Atlas connection instructions
chore: bump iTextPDF to 5.5.14
```

### Pull request process

```bash
# 1. Branch off develop
git checkout develop && git pull
git checkout -b feature/your-feature

# 2. Make changes and commit
git add .
git commit -m "feat: your feature description"

# 3. Push and open a PR → develop
git push origin feature/your-feature
```

- PRs must target `develop`, never `main` directly.
- At least one review approval is required before merging.
- `main` is the production-ready branch and is protected.

---

## 🗺 Roadmap

- [x] Core booking workflow with MongoDB persistence
- [x] PDF receipt generation
- [x] SHA-256 password hashing with brute-force lockout
- [x] Room inventory and maintenance task management
- [ ] Admin dashboard UI (occupancy & revenue reports)
- [ ] JavaMail integration for real email confirmations
- [ ] Payment gateway (Stripe / Paystack)
- [ ] REST API layer (Spring Boot)
- [ ] Web / mobile client

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">
  Built with ☕ by the Database Group 6 Team 
</div>