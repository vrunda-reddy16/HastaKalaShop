# Hasta-Kala Shop 🛍️

A micro-sales analytics Android application built for rural artisans 
who sell handmade crafts such as banana fiber bags, keychains, and 
pouches at local markets.

---

## 📱 About the App

Hasta-Kala Shop solves a real problem — rural artisans have no digital 
tool to track their daily sales, identify best-selling products, monitor 
stock levels, or make informed production decisions.

The app provides:
- 3-tap sale logging
- Live Pie and Bar chart analytics
- Stock alerts for low inventory
- Income log with time-range filtering
- AI-powered production suggestions using Gemini API
- Complete product catalog management
- Dark Mode and Light Mode toggle

---

## 🖥️ Screenshots

| Home Screen | Quick Bill | Best Seller |
|---|---|---|
| Dashboard with revenue and charts | Tap product → set qty → save | Pie + Bar chart analytics |

| Income Log | AI Coach | Manage Products |
|---|---|---|
| Sales history with filters | Gemini AI suggestions | Add, Edit, Stock, Delete |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Modern declarative UI |
| Firebase Realtime Database | Cloud data storage and sync |
| Firebase Authentication | Anonymous per-device identity |
| Gemini API | AI-powered production suggestions |
| MPAndroidChart | Pie and Bar chart visualization |
| Hilt | Dependency injection |
| Navigation Compose | Screen routing |
| MVVM Architecture | Clean code separation |
| Material 3 | Dynamic theming |

---

## 🏗️ Architecture
UI Layer (Jetpack Compose Screens)
↓
ViewModel Layer (HastaKalaViewModel)
↓
Repository Layer (HastaKalaRepository)
↓
Data Layer (FirebaseDataSource + AuthManager + GeminiService)
↓
Cloud Services (Firebase Realtime DB + Firebase Auth + Gemini API)

---

## ⚙️ Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- Android device or emulator (API 24+)
- Firebase account
- Google AI Studio account

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/HastaKalaShop.git
cd HastaKalaShop
```

**2. Firebase Setup**
- Go to console.firebase.google.com
- Create a new project named HastaKalaShop
- Add an Android app with package name `com.hastakalashop.app`
- Download `google-services.json`
- Place it in the `app/` folder

**3. Gemini API Setup**
- Go to aistudio.google.com
- Create a new API key
- Open `local.properties` in the project root
- Add the following line:
- GEMINI_API_KEY=your_gemini_api_key_here

- **4. Enable Firebase Services**
- Firebase Realtime Database → Create database → Test mode
- Firebase Authentication → Enable Anonymous sign-in

**5. Sync and Run**
- Open project in Android Studio
- Click Sync Project with Gradle Files
- Connect Android device via USB
- Enable USB Debugging on device
- Click Run ▶️

---

## 📁 Project Structure
app/src/main/java/com/hastakalashop/app/
│
├── data/
│   ├── model/          → Product.kt, Sale.kt
│   ├── firebase/       → FirebaseDataSource.kt, AuthManager.kt
│   ├── ai/             → GeminiService.kt
│   └── repository/     → HastaKalaRepository.kt
│
├── di/                 → FirebaseModule.kt
│
├── viewmodel/          → HastaKalaViewModel.kt
│
├── navigation/         → Screen.kt, HastaKalaNavHost.kt
│
└── ui/
├── theme/          → Color.kt, Theme.kt, Type.kt, Shape.kt
├── components/     → ShadcnButton, ShadcnCard, ShadcnPieChart...
└── screens/        → HomeScreen, QuickBillScreen, AnalyticsScreen...

---

## ✨ Features

- ✅ 3-tap sale logging
- ✅ Real-time Firebase sync
- ✅ Best Seller Pie and Bar charts
- ✅ Time-range filtering — Today, Week, Month, All
- ✅ Stock alerts for low inventory
- ✅ Income log with totals
- ✅ Gemini AI Coach — weekly production suggestions
- ✅ Manage Products — Add, Edit, Stock update, Delete
- ✅ Dark Mode and Light Mode toggle
- ✅ Offline-first persistence
- ✅ Anonymous auth — no login screen

---

## 🎓 Internship Details

- **Company:** MindMatrix, Bengaluru
- **Project:** Project 14 — Self Employment
- **Intern:** Vrunda S
- **USN:** 1DA22AI052
- **Department:** AI and ML
- **College:** Dr. Ambedkar Institute of Technology
- **Guide:** Mrs. Rajeshwari

---

## 📄 License

This project was developed as part of the MindMatrix internship program 
for academic purposes.
