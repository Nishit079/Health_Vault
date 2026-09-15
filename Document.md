# Health Vault
 
**A Personal, Offline-First Digital Health Locker for Android**
 
Health Vault is an Android application that lets a person store, organize, search, and share their entire medical history — prescriptions, lab reports, vaccination records, doctor visit notes, scans — directly on their own phone, with **no internet connection and no cloud account required**. It also keeps a special "Emergency Medical ID" screen that a first responder can pull up in seconds during a medical emergency.
 
This document explains, in plain language, what the project does, why it exists, and how it works internally.
 
---
 
## 1. Why This Project Exists
 
Most people's health records today are scattered across multiple hospital apps, WhatsApp chats with doctors, and paper folders. This creates three real problems:
 
1. **Fragmentation** — Nobody has one single place to see "everything about my health."
2. **Privacy risk** — Most health-tracking apps upload your data to a company's cloud server, which can be breached, sold, or misused.
3. **Emergency risk** — In an accident or sudden medical event, paramedics often can't find critical information (blood type, allergies, medications) quickly, especially if it requires unlocking apps or logging into cloud accounts.
Health Vault solves all three by keeping everything **100% local to the device** — nothing ever leaves the phone unless the user actively chooses to share it.
 
---
 
## 2. Who Uses It
 
| Type of User | How they use the app |
|---|---|
| **Patient / caregiver** | Logs their own or a family member's prescriptions, test results, and doctor visits over time. |
| **Elderly / occasional user** | Uses a simple, high-contrast interface to quickly look up past instructions or vaccination history. |
| **Paramedic / first responder** | Opens the Emergency Profile screen to instantly see blood group, allergies, and emergency contact — no login needed. |
| **Doctor receiving a shared record** | Receives a clean, readable text summary via WhatsApp/email/SMS — they don't need to install the app at all. |
 
---
 
## 3. What the App Can Do (Core Features)
 
### 🔒 PIN-Protected Access
Every time the app is opened, it asks for a 4-digit PIN before showing any data. This is the only "lock" protecting the vault, since the phone itself is assumed to be personal/family-shared.
 
### 📁 Add & Organize Health Records
Users can add a record with a title, category (Lab Report, Prescription, Doctor Visit, Vaccination, Hospital Record, Scan/X-Ray, Medical Document, Other), doctor's name, date, and free-text notes. Each record is saved instantly to the phone's local database.
 
### 🔍 Instant Search & Filter
Typing in the search bar filters records live, matching against the title, category, doctor's name, or date. A dropdown lets the user narrow results down to just one category (e.g., only "Prescriptions").
 
### 🕒 Health Timeline
A single tap generates a scrollable, chronological view of the user's whole medical history, with an icon for each category, so patterns over time are easy to see.
 
### 📤 Share Pack (Smart Sharing)
Instead of sending a screenshot or a messy copy-paste, the user can select records and generate a clean, formatted plain-text summary — the "Share Pack." They choose exactly what to include (doctor name, date, category, notes) before sending it through any messaging or email app on the phone. This means a doctor who has never even heard of Health Vault can still read the shared record perfectly.
 
### 🆘 Emergency Medical ID
A dedicated screen stores: full name, blood type, known allergies, current medications, and an emergency contact number. This is designed to be the very first thing a first responder checks — no searching required.
 
### 📊 Health Insights
The app automatically calculates simple statistics: total number of records, number of different doctors seen, number of categories used, and which category appears most often — giving the user a quick "state of my health records" snapshot.
 
---
 
## 4. How It Works Under the Hood
 
### 4.1 Overall Design (MVVM + Repository Pattern)
 
The app is built using Android's recommended architecture, which keeps the code organized into clear layers:
 
```
UI (Fragments/Activities)
        ↓ observes
ViewModel (holds live data, survives screen rotation)
        ↓ calls
Repository (single gateway to the database)
        ↓ queries
Room Database (local SQLite storage on the phone)
```
 
- The **screens** (Home, Vault, Insights, Settings, Emergency Profile) never talk to the database directly — they just display whatever data the ViewModel gives them.
- The **ViewModel** holds the current state (e.g., "the list of records right now") and survives things like screen rotation.
- The **Repository** is the only class that actually knows how to read/write the database — this keeps the rest of the app clean and easy to test.
- The **Database** (built with Android's Room library, on top of SQLite) is what actually stores everything permanently, even after the app is closed.
All reading/writing happens on a background thread (via Kotlin Coroutines), so the app never freezes while saving or loading data.
 
### 4.2 What's Actually Stored on the Phone
 
Two main tables exist in the local database:
 
**`health_records`** — one row per medical record:
| Field | What it holds |
|---|---|
| id | unique number for each record |
| title | e.g., "Complete Blood Count" |
| category | e.g., "Lab Report" |
| doctorName | who the record is from |
| date | when it happened |
| notes | any extra remarks |
 
**`emergency_profile`** — exactly one row, always overwritten (not duplicated), holding: name, blood type, allergies, medications, and emergency contact.
 
There's also a tiny local settings file that stores the 4-digit PIN.
 
Everything lives inside the app's private, sandboxed storage folder on the phone — it is not accessible to other apps, and it is never transmitted anywhere.
 
### 4.3 Example: What Happens When You Add a Record
 
1. User taps "+" in the Vault screen and fills in Title, Category, Doctor, Date, Notes.
2. The app checks that at least the Title was entered (fills in "N/A" for missing doctor/date).
3. The ViewModel sends this new record to the Repository.
4. The Repository asks Room to insert it into the `health_records` table, on a background thread.
5. Because the UI is "observing" the database live, the new record appears in the list immediately — no manual refresh needed.
6. A small confirmation message ("Record added to your Vault.") appears.
### 4.4 Example: What Happens When You Share Records
 
1. User selects one or more records in the Vault screen and opens the Share Center.
2. User toggles which details to include (date, doctor, category, personal notes — notes are hidden by default for privacy).
3. The app builds a neatly formatted plain-text message listing each selected record.
4. Android's built-in "Share Sheet" pops up, letting the user pick WhatsApp, Email, SMS, etc.
5. The recipient — even without Health Vault installed — receives readable, well-formatted text.
### 4.5 Why No Internet Permission?
 
The app is deliberately built **without** the Android internet permission at all. This isn't just a setting — it's a structural guarantee: the app literally cannot make a network request, even if it had faulty code trying to do so. This is the technical proof behind the "your data never leaves your phone" promise.
 
---
 
## 5. Screens at a Glance
 
| Screen | Purpose |
|---|---|
| **Home** | Dashboard with quick stats and shortcuts. |
| **Vault** | Full record list, search, filter, add/share records. |
| **Insights** | Statistics and trends about your logged health data. |
| **Settings** | Change PIN, access Emergency Profile, view app version. |
| **Emergency Profile** | Medical ID for first responders. |
| **Share Center** | Build and dispatch the plain-text Share Pack. |
 
---
 
## 6. Current Limitations (By Design, for v2.0)
 
- No cloud backup or sync across multiple devices — everything is tied to one phone.
- No OCR (it can't scan and read a photo of a paper prescription).
- No direct doctor tele-consultation features.
- Losing the phone means losing the data, since there's no cloud recovery — the trade-off for guaranteed privacy.
---
 
## 7. Planned Future Improvements
 
- Fingerprint / Face Unlock instead of just a PIN.
- Encrypting the entire database (not just relying on the phone's sandboxing).
- Attaching real photos of prescriptions/reports to records.
- Exporting records as a signed PDF with a QR code for clinical verification.
---
 
## 8. Tech Stack Summary
 
- **Platform:** Android (API 24 – API 34+)
- **Language:** Kotlin
- **Architecture:** MVVM + Repository Pattern
- **Local Database:** Android Jetpack Room (SQLite)
- **Async handling:** Kotlin Coroutines
- **UI:** Fragments + BottomNavigationView (Material Design 3)
---
 
*For the full formal requirements breakdown (functional/non-functional requirements, database schema, and IEEE 830 mapping), see [`SRS.md`](./SRS.md) in this repository.*
 
