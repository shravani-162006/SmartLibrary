# QR Smart Library Management System (Java + XML)

A complete, modern, and production-ready **QR Smart Library Management System** for Android built strictly with **Java, XML Layouts, Material Components 3, ZXing Barcode Engine, Google Maps SDK, and Firebase**.

---

## 📱 Features

- **Splash Screen**: Initial session initialization, auth routing, and brand animation.
- **Authentication**:
  - Email & Password Login with field validations.
  - User Registration (Full Name, Email, Mobile, Roll / Student ID, Password).
  - Forgot Password flow with a simulated 60-second OTP timer and password reset.
- **Dashboard & Navigation**:
  - **Navigation Drawer**: Professional drawer header with user profile photo, name, email, roll number, and items:
    - Home Dashboard
    - Profile
    - Available Books
    - Issued Books
    - Pending Requests
    - History
    - Learning Points
    - Library Locations
    - Quiz
    - Feedback
    - Notifications
    - Settings
    - About Us
    - Contact Us
    - Rate Us
    - Share App
    - Logout Dialog
  - **Bottom Navigation**: Home, Books, Issued, History, Profile tabs.
- **Book Catalog & Search**:
  - Real-time SearchView for searching books by title, author, category, or ISBN.
  - Horizontal category filter chips (Personal Development, Business, Psychology, Fiction, etc.).
  - Detailed view showing cover image, description, ISBN, publisher, publication year, total & available copies, and location.
- **Book Request & QR Code System**:
  - Book borrowing request submission with confirmation dialog (`dialog_confirm.xml`).
  - Request Statuses: `PENDING`, `APPROVED`, `REJECTED`, `COMPLETED`, `CANCELLED`.
  - **QR Code Pass Generation**: Generates high-resolution QR matrix using ZXing containing temporary secure token payload, expiration timer, and notice.
  - **QR Code Scanner**: Live camera scanning using ZXing `DecoratedBarcodeView` with camera permission handling and verification against the database to complete book checkout/return transactions.
- **Issued Books & Learning Points**:
  - Track active borrowed books with due date countdowns and automated fine calculations ($1.50/day overdue).
  - Record key takeaways and ratings per book in **Learning Points**.
- **Library Branch Finder & Google Maps**:
  - List of library branches with opening hours, address, and phone dialer.
  - Google Maps integration with custom branch markers and native directions intent launcher.
- **Interactive Quiz & Feedback**:
  - Knowledge quiz with progress bar, multiple choice options, score calculation, and result analysis.
  - RatingBar feedback form with comments submission and custom dialog feedback response.
- **Settings & Dark Mode**:
  - Real-time Light/Dark theme switching via `AppCompatDelegate`.
  - Push Notification preferences via `MyFirebaseMessagingService`.
- **Offline First**:
  - SQLite database (`DatabaseHelper`) pre-populated with sample books (*Think Like a Monk*, *Ikigai*, *The Power of Moments*, *Atomic Habits*, *Deep Work*, etc.), library locations, notifications, and quiz questions.

---

## 🛠️ Technology Stack

- **Language**: Java 17 (Strictly 100% Java source code)
- **UI Framework**: XML Layouts & ViewBinding
- **Design**: Material Components 3 & Custom HSL Theme Palettes
- **Database & Auth**: Firebase Auth, Firestore, and SQLite local fallback
- **Barcodes**: ZXing Core & ZXing Android Embedded (`com.journeyapps:zxing-android-embedded`)
- **Maps & Location**: Google Maps SDK for Android (`play-services-maps`)
- **Push Notifications**: Firebase Cloud Messaging (`firebase-messaging`)
- **Image Loading**: Glide (`com.github.bumptech.glide:glide:4.16.0`)

---

## 🚀 Setup & Installation

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Android SDK 36 (Min SDK 24)

### Steps
1. Clone or copy the project into your Android Studio workspace:
   ```bash
   cd SmartLibrary
   ```
2. Open the project in Android Studio and sync Gradle files.

---

## 🔥 Firebase Setup Guide

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new Firebase project named **SmartLibrary**.
3. Register an Android App with package name: `com.example.smartlibrary`.
4. Download `google-services.json` and place it in the `app/` folder directory (`SmartLibrary/app/google-services.json`).
5. Enable **Authentication** (Email/Password method).
6. Enable **Cloud Firestore** and **Realtime Database**.
7. Enable **Firebase Cloud Messaging**.

---

## 🗺️ Google Maps Setup Guide

1. Obtain a Google Maps Android API Key from the [Google Cloud Console](https://console.cloud.google.com/).
2. Open `app/src/main/AndroidManifest.xml`.
3. Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your API key:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_ACTUAL_GOOGLE_MAPS_API_KEY" />
   ```

---

## 📦 Build & Release

### Build Debug APK
```bash
./gradlew assembleDebug
```
The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

### Build Release Bundle / APK
```bash
./gradlew assembleRelease
```

---

## 📋 Features Verification Checklist

- [x] Splash Screen with Auth Routing
- [x] Login & Registration with ViewBinding
- [x] Forgot Password & OTP Flow
- [x] Navigation Drawer & Header
- [x] Bottom Navigation Bar
- [x] Home Dashboard & Stats Cards
- [x] Book Catalog, SearchView & Category Chips
- [x] Book Details & Request Confirmation
- [x] Pending & Approved Requests
- [x] ZXing QR Code Pass Generation
- [x] ZXing Camera QR Code Scanner
- [x] Issued Books & Fine Calculation
- [x] Learning Points / Notes System
- [x] Borrowing History with Filter Tabs
- [x] Library Locations & Google Maps
- [x] Interactive Quiz & Score Screen
- [x] User Profile & Profile Editing
- [x] RatingBar Feedback Submission
- [x] Push Notifications Handler
- [x] Dark Mode Switch
- [x] About Us, Contact Us (Phone/Email/Maps intents)
- [x] Rate Us & Share App Intents
- [x] Clean Logout & Session Clearing
