# 🛡️ SecureShield — Android Antivirus App

A fully functional Android Antivirus & Security app built in Java (Android SDK).

---

## 📱 Features

### 1. ⚡ Quick Scan
- Scans all installed apps against a malware signature database
- Calculates a **risk score (0–100)** for every app based on permissions
- Detects known malware package names
- Detects dangerous permission combinations (spyware, adware, data stealers)

### 2. 🔍 Full Scan
- Everything in Quick Scan PLUS
- Scans all files in device storage
- Detects suspicious file names (crack, hack, keygen, trojan, etc.)
- Detects sideloaded APKs from outside Play Store
- Detects files by MD5 hash (e.g. EICAR test virus)

### 3. 🔐 App Permission Checker
- Lists ALL installed apps sorted by risk level
- Shows permission count per app
- Color-coded risk bars: 🔴 HIGH | 🟠 MEDIUM | 🟡 LOW | 🟢 SAFE
- Search/filter apps by name or package

### 4. 🟢 Real-Time Protection (Background Service)
- Runs as a foreground service (persistent)
- Monitors for newly installed apps every 30 seconds
- Sends instant push notification if a threat is detected
- Automatically restarts on device reboot (via BootReceiver)

---

## 🏗️ Project Structure

```
AntivirusApp/
├── app/src/main/
│   ├── java/com/antivirus/app/
│   │   ├── models/
│   │   │   ├── AppInfo.java          # App data model
│   │   │   ├── ThreatItem.java       # Threat data model
│   │   │   └── ScanResult.java       # Scan result model
│   │   ├── scanner/
│   │   │   ├── AppScanner.java       # Scans installed apps
│   │   │   ├── FileScanner.java      # Scans device storage
│   │   │   └── RealTimeProtectionService.java  # Background service
│   │   ├── ui/
│   │   │   ├── SplashActivity.java
│   │   │   ├── MainActivity.java     # Dashboard
│   │   │   ├── ScanActivity.java     # Scan progress screen
│   │   │   ├── PermissionCheckerActivity.java
│   │   │   └── adapters/
│   │   │       ├── ThreatAdapter.java
│   │   │       └── AppPermissionAdapter.java
│   │   └── utils/
│   │       ├── ThreatDatabase.java   # Malware signatures DB
│   │       └── BootReceiver.java     # Auto-start on boot
│   ├── res/
│   │   ├── layout/                   # All XML layouts
│   │   ├── drawable/                 # Icons
│   │   └── values/                   # Strings, themes, colors
│   └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🚀 How to Build & Run

### Requirements
- **Android Studio** Hedgehog (2023.1.1) or newer — [Download free](https://developer.android.com/studio)
- Android SDK API 34
- Java 8+
- A physical Android device or emulator (API 24+)

### Steps

1. **Open Android Studio**
2. Click **"Open"** → select the `AntivirusApp` folder
3. Wait for Gradle sync to complete (first time takes 2–5 min)
4. Connect your Android phone via USB (enable USB Debugging in Developer Options)
5. Click the **▶ Run** button (green triangle)
6. App installs and launches on your device!

### Enable USB Debugging on Android
1. Go to **Settings → About Phone**
2. Tap **Build Number** 7 times (enables Developer Options)
3. Go to **Settings → Developer Options → USB Debugging → ON**

---

## 📋 Permissions Required

| Permission | Why needed |
|-----------|-----------|
| `READ_EXTERNAL_STORAGE` / `READ_MEDIA_*` | Full scan — read files |
| `QUERY_ALL_PACKAGES` | Scan installed apps |
| `INTERNET` | For future cloud threat DB updates |
| `RECEIVE_BOOT_COMPLETED` | Auto-start protection on reboot |
| `FOREGROUND_SERVICE` | Real-time protection service |
| `POST_NOTIFICATIONS` | Threat alert notifications |

---

## 🔬 Threat Detection Logic

### App Scanning
```
Risk Score = 0
For each permission:
  HIGH risk permission  → +15 points
  MEDIUM risk permission → +5 points

Bonus penalties:
  MICROPHONE + LOCATION combo → +20 (Spyware)
  SMS + CONTACTS combo        → +20 (Data Stealer)
  CAMERA + AUDIO + LOCATION   → +25 (Surveillance)

Score 0–9   → SAFE
Score 10–39 → LOW RISK
Score 40–69 → MEDIUM RISK
Score 70+   → HIGH RISK
```

### File Scanning
- Extension check: `.apk`, `.dex`, `.jar`, `.so`
- Name pattern match: `crack`, `hack`, `trojan`, `keylog`, etc.
- MD5 hash check against known virus signatures (EICAR test)
- Sideloaded APK detection

---

## 🧪 Testing the App

### Test Quick Scan
1. Open app → tap **Quick Scan**
2. Watch real-time progress as apps are analysed
3. Any suspicious apps will appear in the threat list

### Test Permission Checker
1. Tap the **Permission Checker** card on dashboard
2. All apps listed by risk score (highest first)
3. Type in search box to filter

### Test Real-Time Protection
1. App starts protection service automatically on launch
2. Install any new APK while the app is running
3. Service will scan it and notify you if risky

### Test with EICAR (Safe Test Virus)
1. Download the EICAR test file (it's harmless) from eicar.org
2. Save it to your device storage
3. Run Full Scan — it will be detected as a virus

---

## 🎓 Final Exam Presentation Topics

1. **Problem Statement** — Rising mobile malware threats
2. **System Architecture** — 4 modules: App Scanner, File Scanner, RT Protection, Permission Checker
3. **Detection Algorithm** — Signature-based + Heuristic (permission scoring)
4. **Technology Stack** — Java, Android SDK, Material Design, WorkManager
5. **Results** — Demo on device
6. **Future Scope** — Cloud threat DB, AI-based detection, VPN integration

---

## 📊 Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java |
| UI Framework | Material Design Components |
| Background Service | Android Foreground Service |
| Boot persistence | BroadcastReceiver |
| Detection | Signature DB + Heuristic scoring |
| Architecture | MVVM-ready (models/ui separated) |
| Min Android | API 24 (Android 7.0) |
| Target Android | API 34 (Android 14) |

---

*Built for Final Year Cybersecurity Project — 100% free & open source*
