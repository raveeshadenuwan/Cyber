# CyberGuard Mobile 🛡️

**CyberGuard Mobile** is a production-grade Android cybersecurity assessment and security utility application built with Kotlin and Jetpack Compose. It evaluates device security posture, calculates an objective posture score, audits installed application permission attack surfaces, inspects network connection telemetry, and offers offline cryptographic defensive tools.

**Developed by Raveesha Denuwan**

---

## 🚀 Key Features

1. **Device Security Posture Audit**
   - Screen lock verification (Biometric, PIN, Password, Pattern).
   - Developer Options and ADB (USB Debugging) exposure detection.
   - Sideloading and non-market app installation checks.
   - Android OS security patch age and firmware version evaluation.
   - Hardware/File-Based Encryption (FBE) validation.
   - Transparent Google Play Protect guidance.

2. **Objective Security Scoring Engine**
   - Calculates a 0–100 security score with weighted deductions for actual vulnerabilities.
   - Clamps score into 5 defined bands: *Excellent*, *Good*, *Needs Attention*, *Poor*, *Critical*.
   - Never penalizes the user for API restrictions imposed by the Android sandbox.

3. **Application Permission Auditing**
   - Audits installed launcher applications and filters by permission footprint.
   - Identifies sensitive combinations: Camera, Microphone, Fine Location, SMS, Contacts, and Storage.
   - Categorizes attack surface into *Low*, *Medium*, and *High* without falsely labeling apps as malware.

4. **Network Telemetry & Exposure Inspector**
   - Probes active connection interface (Wi-Fi, Cellular, Ethernet, VPN, Offline).
   - Identifies active VPN encapsulation and metered networks.
   - Resolves local IPv4 addresses, gateways, link throughput, and DNS servers.
   - Discloses Android OS privacy protections surrounding BSSID/SSID.

5. **Offline Password Defense Suite**
   - **Password Strength Analyzer**: Analyzes entropy bits, common dictionary blacklist, character variety, and estimated crack times.
   - **Cryptographic Generator**: Employs `java.security.SecureRandom` to generate high-entropy keys (8–64 characters) across customizable character pools.
   - **Zero-Storage Guarantee**: Passwords exist solely in RAM; never written to storage, logs, or network sockets.

6. **Heuristic URL Phishing Inspector**
   - Analyzes links locally for raw IP hosts, IDN homograph punycode (`xn--`), non-standard ports, `@` authority manipulation, and phishing keywords.
   - Strictly disclaims heuristic results and enforces explicit user confirmation before opening external browsers.

7. **Security Hardening Guide**
   - Curated remediation actions directing the user directly to appropriate Android system settings.

8. **Local Audit History**
   - Backed by AndroidX Room Database with cascading relations.
   - Stores historical scores, timestamps, and findings locally on-device.

---

## 🛠️ Technology Stack

- **Language:** Kotlin 2.2
- **UI Toolkit:** Jetpack Compose (Material 3 Dark-First design system)
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistence:** AndroidX Room Database (with KSP)
- **Asynchrony:** Kotlin Coroutines & reactive `StateFlow` / `collectAsStateWithLifecycle`
- **Navigation:** AndroidX Navigation Compose
- **Platform APIs:** Android `KeyguardManager`, `ConnectivityManager`, `NetworkCapabilities`, `PackageManager`, `DevicePolicyManager`, `Settings.Global`

---

## 🏗️ Architecture & Project Structure

```
com.example/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── ScanDao.kt
│   │   ├── ScanEntity.kt
│   │   ├── FindingEntity.kt
│   │   └── ScanWithFindings.kt
│   └── repository/
│       ├── AppRepository.kt
│       ├── NetworkRepository.kt
│       ├── SecurityScannerRepository.kt
│       └── UserPreferencesRepository.kt
├── domain/
│   ├── model/
│   │   ├── InstalledAppInfo.kt
│   │   ├── NetworkSecurityInfo.kt
│   │   ├── PasswordModels.kt
│   │   ├── SecurityCheck.kt
│   │   ├── SecurityFinding.kt
│   │   ├── SecurityScanResult.kt
│   │   ├── SecurityScore.kt
│   │   ├── SecuritySeverity.kt
│   │   └── UrlModels.kt
│   └── security/
│       ├── PasswordAnalyzer.kt
│       ├── PasswordGenerator.kt
│       ├── SecurityScanner.kt
│       ├── SecurityScoreCalculator.kt
│       └── UrlAnalyzer.kt
├── presentation/
│   ├── components/
│   │   ├── CommonViews.kt
│   │   ├── CyberGuardButton.kt
│   │   ├── CyberGuardTopBar.kt
│   │   ├── CyberGuardBottomBar.kt
│   │   ├── FindingCard.kt
│   │   ├── SecurityCheckRow.kt
│   │   ├── SecurityScoreCard.kt
│   │   └── SeverityBadge.kt
│   ├── navigation/
│   │   ├── AppNavigation.kt
│   │   └── Screen.kt
│   └── screens/
│       ├── about/
│       ├── appdetails/
│       ├── apps/
│       ├── dashboard/
│       ├── history/
│       ├── network/
│       ├── password/
│       ├── privacy/
│       ├── recommendations/
│       ├── scan/
│       ├── settings/
│       ├── splash/
│       ├── tools/
│       └── urlchecker/
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

---

## 🔒 Permissions & Security Model

CyberGuard Mobile follows the principle of **least privilege**:

| Permission | Purpose |
| :--- | :--- |
| `android.permission.INTERNET` | Optional future API extensions (Disabled by default, core features 100% offline) |
| `android.permission.ACCESS_NETWORK_STATE` | Inspect network transport types (Wi-Fi, Cellular, VPN) and metering |
| `android.permission.ACCESS_WIFI_STATE` | Query physical link parameters and Wi-Fi state |

### Privacy Principles
1. **Offline-First:** All scans, password checks, and URL analyses run 100% on the device without telemetry or cloud servers.
2. **Zero Password Logging:** Passwords evaluated in memory are never saved, logged, or cached.
3. **Android Sandbox Compliance:** CyberGuard inspects declared app metadata using `<queries>` without requiring root or invasive background monitoring.

---

## 🧪 Testing

Comprehensive unit and Robolectric tests are provided:
- **`SecurityScoreCalculatorTest`**: Verifies deduction calculations, boundary conditions, and non-penalization of unavailable checks.
- **`PasswordAnalyzerTest`**: Tests password strength scoring, dictionary match penalties, and entropy calculations.
- **`PasswordGeneratorTest`**: Verifies character pool distribution, length guarantees, and cryptographic randomness.
- **`UrlAnalyzerTest`**: Tests detection of raw IP hosts, `@` authority manipulation, punycode spoofing, and insecure protocols.

Run unit tests:
```bash
gradle test
```

---

## 📦 How to Build and Run

1. Open the project in **Android Studio** (Giraffe or newer recommended).
2. Ensure **JDK 17** or **JDK 21** is selected under Gradle settings.
3. Sync Gradle and build the project.
4. Run on an Android device or emulator running Android 8.0 (API 26) through Android 15 (API 35+).

---

## 🔮 Future Improvements

- Safe Browsing / VirusTotal API integration via optional opt-in toggle.
- Export audit logs to encrypted PDF / JSON reports.
- Deep background scheduled audits with notification alerts via WorkManager.

---

**Developed with precision for mobile security defense by Raveesha Denuwan.**
