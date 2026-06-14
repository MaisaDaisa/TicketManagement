# TicketManagement

TicketManagement is a modern Android application designed for efficient ticket administration and validation. It features role-based access control, real-time updates via Firebase, and a seamless QR code scanning experience for ticket verification.

## 🚀 Features

- **Role-Based Access Control**: Different permissions and views for Admins and Helpers.
- **Secure Authentication**: Integrated with Firebase Authentication (Email/Password).
- **Ticket Creation**: Admins can generate new tickets with unique IDs and user details.
- **Real-time Synchronization**: Live ticket lists and status updates powered by Firebase Firestore.
- **Advanced QR Code Scanning**: High-performance ticket validation using Google's GMS Barcode Scanning API.
- **Validation Logic**: Automatic check for duplicate scans and database verification.
- **Modern UI/UX**: Built entirely with Jetpack Compose and Material 3, following modern Android design principles.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Backend**: Firebase (Auth, Firestore)
- **Scanning**: Google Code Scanner
- **Build System**: Gradle Kotlin DSL (KTS)

## 📦 Getting Started

### Prerequisites

- **Android Studio** (Latest Stable Version)
- **JDK 17+**
- A **Firebase Project** with:
    - Android app registered (matching `com.example.ticketmanagement` package).
    - Email/Password authentication enabled.
    - Cloud Firestore instance configured.
    - `google-services.json` placed in the `app/` folder.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/MaisaDaisa/TicketManagement.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and build the project.
4. Run on a physical device or emulator.

## 📱 Usage

- **Sign In**: Access the app using your registered account.
- **Admin**: Create tickets by filling out the user details (First Name, Last Name, etc.).
- **Scan**: Tap the scanner icon to open the QR scanner. Aim at a ticket's QR code to validate it.
- **List**: View all tickets and their scanning status in real-time.
