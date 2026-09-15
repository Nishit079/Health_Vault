# Software Requirements Specification
## Health Vault
**Personal, Offline-First Digital Health Locker — Android Application**
 
| | |
|---|---|
| **Version** | 2.0 |
| **Prepared for** | Android (API 24 – API 34+) |
| **Developer** | Nishit Patel (Enrollment No: 24012011115) |
| **Architecture** | MVVM + Repository Pattern \| Database: Android Jetpack Room (SQLite) |
| **Conforms to** | IEEE 830 / ISO/IEC/IEEE 29148 SRS Standards |
| **Date** | September 2026 |
 
---
 
## Table of Contents
 
1. [Introduction](#1-introduction)
   - 1.1 [Purpose](#11-purpose)
   - 1.2 [Document Conventions](#12-document-conventions)
   - 1.3 [Problem Statement](#13-problem-statement)
   - 1.4 [Scope](#14-scope)
   - 1.5 [References / Standards Mapping](#15-references--standards-mapping)
2. [Overall Description](#2-overall-description)
   - 2.1 [Product Perspective](#21-product-perspective)
   - 2.2 [User Classes and Characteristics](#22-user-classes-and-characteristics)
   - 2.3 [Operating Environment](#23-operating-environment)
   - 2.4 [Design and Implementation Constraints](#24-design-and-implementation-constraints)
   - 2.5 [Assumptions and Dependencies](#25-assumptions-and-dependencies)
3. [System Architecture (External Interface Overview)](#3-system-architecture-external-interface-overview)
   - 3.1 [Architectural Pattern](#31-architectural-pattern)
   - 3.2 [Component Catalog](#32-component-catalog)
4. [Data Requirements (Database Design)](#4-data-requirements-database-design)
   - 4.1 [Room Database Overview](#41-room-database-overview)
   - 4.2 [Entity: health_records](#42-entity-health_records)
   - 4.3 [Entity: emergency_profile](#43-entity-emergency_profile)
   - 4.4 [Local Preferences Schema](#44-local-preferences-schema)
5. [Specific Requirements — Functional Requirements](#5-specific-requirements--functional-requirements)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [External Interface Requirements (UI Specifications)](#7-external-interface-requirements-ui-specifications)
8. [Use Case Scenarios](#8-use-case-scenarios)
9. [Appendix A — IEEE 830 Mapping Guide](#9-appendix-a--ieee-830-mapping-guide)
10. [Appendix B — Future Roadmap & Extensibility](#10-appendix-b--future-roadmap--extensibility)
---
 
## 1. Introduction
 
### 1.1 Purpose
 
Health Vault is an offline-first, privacy-guaranteed personal health records management system for Android smartphones. This Software Requirements Specification (SRS) defines the functional and non-functional requirements, data model, interfaces, and architecture of Health Vault v2.0. It is intended for use by developers, reviewers, and academic evaluators as the authoritative baseline for design, implementation, and testing of the system.
 
In an era where healthcare data is routinely fragmented across multiple clinics or locked into proprietary cloud systems vulnerable to breaches, Health Vault provides an autonomous, device-local digital locker that allows users to log, organize, retrieve, analyze, and share their medical history without transmitting any sensitive information to external cloud servers.
 
### 1.2 Document Conventions
 
This document follows IEEE 830 / ISO/IEC/IEEE 29148 conventions. Functional requirements are labelled `FR-XX` and non-functional requirements are labelled `NFR-<CATEGORY>-XX` (e.g., `NFR-SEC-01`). The keywords **must** and **shall** denote mandatory requirements; **should** denotes a recommended but non-mandatory behaviour.
 
### 1.3 Problem Statement
 
- **Data Fragmentation** — Medical records (prescriptions, diagnostic lab reports, vaccination certificates, surgical summaries) are scattered across disparate physical folders and hospital portals.
- **Privacy & Security Vulnerabilities** — Commercial health tracking services routinely upload sensitive user biometrics, diagnostic history, and demographic data to remote third-party cloud infrastructure.
- **Emergency Inaccessibility** — In acute medical emergencies (accidents, anaphylaxis, sudden cardiac events), paramedics and first responders require instantaneous access to vital information (blood group, allergies, chronic medications, emergency contacts) without being blocked by network loss or complex cloud login flows.
- **Formatting Inconsistencies during Sharing** — Sharing records via SMS or messaging apps often breaks layout, rendering records unreadable to consulting physicians.
### 1.4 Scope
 
#### 1.4.1 In-Scope (Release v2.0)
- On-device SQLite/Room persistence of structured medical records.
- Multi-criteria real-time search and categorization.
- Medical emergency profile (Medical ID) for first-responder inspection.
- PIN-gated application launch security.
- Formatted ASCII "Share Pack" generation and Android Share Sheet dispatch.
- Analytics and health event frequency visualization.
#### 1.4.2 Out-of-Scope (Release v2.0)
- Cloud backup, server synchronization, or multi-device real-time sync.
- Optical Character Recognition (OCR) of scanned papers.
- Direct tele-consultation or electronic prescription transmission APIs.
### 1.5 References / Standards Mapping
 
This SRS conforms structurally to IEEE Std 830-1998 and ISO/IEC/IEEE 29148:2018 Software Requirements Specification guidelines. A full section-by-section mapping is provided in [Appendix A](#9-appendix-a--ieee-830-mapping-guide).
 
---
 
## 2. Overall Description
 
### 2.1 Product Perspective
 
Health Vault operates as a self-contained, standalone mobile application on the Android operating system. It interfaces directly with native Android Jetpack libraries and local SQLite storage. It requires zero network permissions (`android.permission.INTERNET` is omitted from the manifest), ensuring a verifiable zero-trust, offline runtime environment. The system is not a component of any larger system and does not depend on any external backend service.
 
### 2.2 User Classes and Characteristics
 
| User Class | Characteristics |
|---|---|
| Primary Patient / Chronic Care Manager | Tech-literate individual managing recurring prescriptions, doctor visits, and blood work for themselves or dependent family members. |
| Geriatric / Occasional User | Needs a simple, high-contrast UI to quickly check past physician instructions or vaccination records. |
| First Responder / Paramedic / ER Staff | Time-critical actor needing to ascertain blood compatibility, allergy warnings, and emergency contact numbers within seconds. |
| Consulting Physician (Recipient) | Receives clean, standardized plain-text ASCII summaries via messaging or email without needing to install the Health Vault application. |
 
### 2.3 Operating Environment
 
- **Operating System:** Android 7.0 (Nougat, API Level 24) up to Android 14+ (API Level 34).
- **Display:** Scalable across mdpi, hdpi, xhdpi, xxhdpi, and xxxhdpi mobile screen form factors.
- **Hardware:** Standard Android smartphone with a minimum of 1GB RAM, 50MB free internal flash storage, and a touchscreen display.
### 2.4 Design and Implementation Constraints
 
- **Zero Cloud Architecture** — No REST APIs, Firebase, or cloud backend may be used.
- **Sandbox Confinement** — All records remain within the app's internal sandbox directory (`/data/data/com.example.healthvault/databases/`).
- **Memory Footprint** — Efficient background database querying using Kotlin Coroutines on `Dispatchers.IO` to ensure 60fps UI rendering on low-tier hardware.
### 2.5 Assumptions and Dependencies
 
- The device on which Health Vault is installed is assumed to be single-user or family-shared, with the PIN acting as the sole access gate.
- The application depends on Android Jetpack libraries (Room, ViewModel, LiveData, Fragment, Coroutines) being available at the target API level.
- Loss or forgetting of the device is assumed to be mitigated by physical device security, since no cloud recovery mechanism exists in this release.
---
 
## 3. System Architecture (External Interface Overview)
 
### 3.1 Architectural Pattern
 
The software follows Android's recommended architectural guidelines: **Model-View-ViewModel (MVVM)** combined with the **Repository Pattern**. The View layer (Activities/Fragments) observes reactive `LiveData` exposed by ViewModels. ViewModels delegate all persistence operations to a Repository, which in turn mediates access to Data Access Objects (DAOs) backed by a singleton Room database instance. This separation ensures that UI components remain free of business logic and that all database operations execute asynchronously via Kotlin Coroutines, preventing main-thread blocking.
 
**Layer summary:**
 
- **View Layer:** `MainActivity` (PIN security barrier, bottom navigation host) hosting `HomeFragment`, `VaultFragment`, `InsightsFragment`, `SettingsFragment`, `EmergencyProfileFragment`, and `RecordDetailFragment`.
- **ViewModel Layer:** `RecordListViewModel` and `EmergencyProfileViewModel`, each exposing `LiveData` streams and coroutine-scoped write operations.
- **Repository Layer:** `HealthRecordRepository` abstracts DAO access from the ViewModels.
- **Data Layer:** `HealthRecordDao` and `EmergencyProfileDao` operate against the `HealthVaultDatabase` (Room/SQLite) singleton.
### 3.2 Component Catalog
 
| Component | Type | Responsibility |
|---|---|---|
| `MainActivity` | AppCompatActivity | App entry point, PIN authentication prompt, bottom navigation management, PIN change orchestration. |
| `HomeFragment` | Fragment | Primary dashboard providing executive metrics (record count, doctor count, categories) and quick navigation cards. |
| `VaultFragment` | Fragment | Records view, full-text search, category filter, record entry dialog, timeline modal, and Share Pack builder. |
| `RecordDetailFragment` | Fragment | Deep-dive viewer for individual records; handles record deletion and single-record sharing. |
| `InsightsFragment` | Fragment | Statistical breakdown of recorded health events, most frequent category identification, and health trend summaries. |
| `SettingsFragment` | Fragment | Security settings (PIN update trigger), navigation link to Emergency Profile, and version metadata display. |
| `EmergencyProfileFragment` | Fragment | Medical ID configuration interface for vital statistics, allergies, medications, and emergency contact numbers. |
| `RecordListViewModel` | ViewModel | Holds reactive state of health records via LiveData, executes async operations via `viewModelScope`. |
| `EmergencyProfileViewModel` | ViewModel | Exposes emergency profile Flow as LiveData, dispatches profile-save coroutines. |
| `HealthRecordRepository` | Repository | Abstracts database access for health records; exposes streams to ViewModels. |
| `HealthVaultDatabase` | RoomDatabase | Thread-safe Singleton managing the SQLite database instance and DAOs. |
 
---
 
## 4. Data Requirements (Database Design)
 
### 4.1 Room Database Overview
 
- **Database Name:** `health_vault_database`
- **Schema Version:** 3
- **Migration Mode:** `fallbackToDestructiveMigration()`
### 4.2 Entity: health_records
 
Represents individual clinical records, lab test results, prescriptions, or consultations.
 
| Attribute | Type | Null | PK | Default | Description |
|---|---|---|---|---|---|
| `id` | INTEGER (Int) | No | Yes (Auto) | 0 | Unique internal record identifier. |
| `title` | TEXT (String) | No | No | — | Descriptive summary (e.g., "Complete Blood Count"). |
| `category` | TEXT (String) | No | No | — | Medical classification (e.g., "Lab Report", "Prescription"). |
| `doctorName` | TEXT (String) | No | No | — | Consulting physician or facility name. Defaults to "N/A". |
| `date` | TEXT (String) | No | No | — | Date of test/consultation (e.g., "13 Aug 2026"). Defaults to "N/A". |
| `notes` | TEXT (String) | No | No | `""` | Clinical remarks, dosage schedules, or patient notes. |
 
**Data Access Object — `HealthRecordDao`**
 
- `insert(record: HealthRecordEntity): suspend Unit` — Persists a new record.
- `update(record: HealthRecordEntity): suspend Unit` — Modifies existing record attributes.
- `delete(record: HealthRecordEntity): suspend Unit` — Removes a specific record.
- `getAllRecords(): LiveData<List<HealthRecordEntity>>` — Reactive stream ordered by `id DESC`.
- `getRecordById(id: Int): suspend HealthRecordEntity?` — Direct lookup by primary key.
### 4.3 Entity: emergency_profile
 
Represents the user's critical medical identification for triage and emergency scenarios.
 
| Attribute | Type | Null | PK | Default | Description |
|---|---|---|---|---|---|
| `id` | INTEGER (Int) | No | Yes (Fixed) | 1 | Singleton row identifier (ensures exactly 1 profile). |
| `name` | TEXT (String) | No | No | `""` | Full legal name of the user. |
| `bloodType` | TEXT (String) | No | No | `""` | ABO/Rh blood group (e.g., "O+", "A-", "AB+"). |
| `allergies` | TEXT (String) | No | No | `""` | Known adverse reactions (e.g., "Penicillin, Peanuts"). |
| `medications` | TEXT (String) | No | No | `""` | Chronic or daily life-support medications (e.g., "Insulin, Metoprolol"). |
| `emergencyContact` | TEXT (String) | No | No | `""` | Primary contact name and phone number. |
 
**Data Access Object — `EmergencyProfileDao`**
 
- `getProfile(): Flow<EmergencyProfileEntity?>` — Continuous Flow querying the row WHERE `id = 1`.
- `updateProfile(profile: EmergencyProfileEntity): suspend Unit` — Upserts profile using `OnConflictStrategy.REPLACE`.
### 4.4 Local Preferences Schema
 
- **File Name:** `health_vault_security` (`Context.MODE_PRIVATE`)
- **Key:** `vault_pin` (String) — 4-digit security PIN string (Default: `"1234"`).
---
 
## 5. Specific Requirements — Functional Requirements
 
### FR-01: App Security & Access Control
 
**Description:** Protects medical data upon launch from unauthorized physical device access.
 
**Input:** 4-digit numeric string.
 
**Processing:**
- `MainActivity` checks whether `vault_pin` exists in SharedPreferences; if missing, it initializes the default `"1234"`.
- A non-cancelable modal `AlertDialog` presents a PIN input field (`TYPE_NUMBER_VARIATION_PASSWORD`).
- User input is evaluated against the stored PIN upon clicking "Unlock".
**Outputs / Actions:**
- Success: Dialog dismisses, unlocking access to `HomeFragment`.
- Failure: Input cleared, error Toast displayed ("Incorrect PIN. Try again.").
**PIN Update:** Invoked from Settings; requires entering a matching "Current PIN" and a valid 4-digit "New PIN".
 
### FR-02: Health Record Creation & Storage
 
**Description:** Enables the user to capture medical records with categorization.
 
**Input:** Title (required), Category (selected from predefined spinner), Doctor name (optional), Date (optional), Notes (optional).
 
**Supported Categories:**
- Lab Report
- Prescription
- Doctor Visit
- Vaccination
- Hospital Record
- Scan / X-Ray
- Medical Document
- Other
**Processing:** Validates non-empty title; supplies fallback defaults ("N/A") for missing doctor or date; persists asynchronously to Room via `RecordListViewModel.addRecord()`.
 
**Feedback:** Confirmation Toast ("Record added to your Vault.").
 
### FR-03: Dynamic Search & Filtering
 
**Description:** Real-time search across the entire health record library.
 
**Trigger:** Text change in `etSearch` or selection change in `spinnerCategory`.
 
**Filter Logic:**
- Substring, case-insensitive match on: `title` OR `category` OR `doctorName` OR `date`.
- AND category equality match (or wildcard "All Categories").
**State Handling:** Displays an empty placeholder message (`tvEmpty`) if the filtered list size is zero.
 
### FR-04: Health Timeline Visualization
 
**Description:** Assembles an intuitive chronological digest of recorded health events.
 
**Processing:** Traverses loaded records, prefixes each record with its category emoji icon, and formats date, title, category, and physician into an ASCII timeline hierarchy.
 
**Presentation:** Rendered via a clean, scrollable dialog modal.
 
### FR-05: Advanced Share Center (Share Pack Builder)
 
**Description:** Generates a structured, plain-text document designed for messaging apps without loss of formatting.
 
**Configurable Attributes:**
- Doctor Name (default: ON)
- Date (default: ON)
- Category (default: ON)
- Personal Notes (default: OFF for privacy)
**Dispatch Mechanism:** Dispatched through the standard `Intent.ACTION_SEND` (type = `"text/plain"`) via `Intent.createChooser()`.
 
**Sample Output Format:**
```
====================================
      HEALTH VAULT
   HEALTH SHARE PACK
====================================
Records shared: 2
Generated by Health Vault
 
1. Complete Blood Count
   Category: Lab Report
   Doctor: Dr. Smith
   Date: 12 Aug 2026
 
2. Amoxicillin Prescription
   Category: Prescription
   Doctor: Dr. Adams
   Date: 10 Aug 2026
====================================
Shared from Health Vault
```
 
### FR-06: Emergency Medical Profile (Medical ID)
 
**Description:** Manages critical triage information accessible immediately.
 
**Attributes:** Name, Blood Group, Documented Allergies, Current Medications, Emergency Contact Phone.
 
**Data Binding:** Reactive pre-population of fields via Room `Flow.asLiveData()`.
 
**Saving:** Instant upsert on "Save Profile" click with a confirmation Toast.
 
### FR-07: Health Insights & Metrics
 
**Description:** Computes real-time analytical metrics from local data.
 
**Metrics Calculated:**
- Total number of records in storage.
- Count of unique attending physicians.
- Count of unique medical categories logged.
- Most frequently occurring medical category.
- Dynamic narrative summary explaining vault status.
---
 
## 6. Non-Functional Requirements
 
| ID | Category | Requirement Specification |
|---|---|---|
| `NFR-SEC-01` | Security | Zero cloud communication: the application must contain no network sockets or internet permissions (`android.permission.INTERNET`). |
| `NFR-SEC-02` | Privacy | All application databases must reside exclusively in internal sandbox storage (`Context.MODE_PRIVATE`). |
| `NFR-SEC-03` | Authentication | PIN verification is required each time the application task is initiated from the OS launcher. |
| `NFR-PERF-01` | Performance | Cold launch to PIN prompt must execute within < 800ms on baseline Android devices. |
| `NFR-PERF-02` | Latency | Database reads and writes must occur on background threads (`Dispatchers.IO`), ensuring zero main-thread UI jank. |
| `NFR-REL-01` | Reliability | The app must function identically with or without cellular/Wi-Fi connectivity (100% offline availability). |
| `NFR-COMP-01` | Compatibility | Compatible with all Android versions from API 24 (Nougat) to API 34 (Android 14). |
| `NFR-USAB-01` | Usability | Responsive navigation with `BottomNavigationView` adhering to Material Design 3 guidelines. |
 
---
 
## 7. External Interface Requirements (UI Specifications)
 
The application exposes four primary screens accessed via a persistent bottom navigation bar (Home, Vault, Insights, Settings) plus two modal/detail screens (Emergency Profile and Advanced Share Center).
 
### 7.1 Screen 1 — Home Dashboard
- Header: application title and quick summary link.
- Metric cards: Records count, Doctors count, Categories count, Most Recent record.
- Navigation shortcuts: "Open Full Vault" and "View Insights".
- Persistent bottom navigation bar (Home / Vault / Insights / Settings).
### 7.2 Screen 2 — Health Vault
- Header showing total record count.
- Search bar (`etSearch`) and category filter spinner (`spinnerCategory`).
- Scrollable list of record cards grouped by category icon, each showing title, doctor, date, a Share action, and a detail chevron.
- Bottom action bar: Share Pack, Timeline, and Add Record (+).
### 7.3 Screen 3 — Emergency Profile (Medical ID)
- Editable fields: Name, Blood Type, Allergies, Medications, Emergency Contact.
- "Save Profile" primary action button with instant upsert and confirmation Toast.
### 7.4 Screen 4 — Advanced Share Center
- Selection summary (e.g., "2 record(s) selected").
- Toggle checkboxes: Include doctor, Include date, Include category, Include personal notes.
- "Cancel" and "Generate Pack" action buttons.
---
 
## 8. Use Case Scenarios
 
### UC-1: First Responder Checks Emergency Medical ID
 
- **Actor:** Paramedic / First Responder.
- **Precondition:** Device unlocked or handed to paramedic by a family member.
**Flow:**
1. Open Health Vault.
2. Enter PIN or bypass to settings.
3. Tap Emergency Profile.
4. System immediately displays patient blood group, life-threatening allergies, active medications, and immediate contact.
**Outcome:** Rapid clinical intervention without medication contraindication.
 
### UC-2: Patient Exports a Health Record to a New Specialist
 
- **Actor:** Patient.
**Flow:**
1. Navigate to Vault.
2. Filter category to "Lab Report".
3. Tap Share Center.
4. Select desired metadata toggles (Date, Doctor, Category; uncheck private personal notes).
5. Tap Generate Share Pack.
6. Android Share Sheet opens; user selects WhatsApp or Doctor's Email.
**Outcome:** Specialist receives a clean ASCII summary without needing Health Vault installed.
 
---
 
## 9. Appendix A — IEEE 830 Mapping Guide
 
The table below documents how this SRS maps to the standard IEEE 830 outline.
 
| IEEE 830 Section | Corresponding Section in this SRS |
|---|---|
| 1. Introduction | Section 1.1 (Purpose), 1.3 (Problem Statement), 1.4 (Scope) |
| 2. Overall Description | Section 2.1 (Perspective), 2.2 (User Classes), 2.3 (Operating Environment), 2.4 (Constraints) |
| 3. Specific Requirements (Functional) | Section 5 (FR-01 to FR-07) with Inputs, Processing, Outputs |
| 4. External Interface Requirements | Section 3 (Architecture), Section 7 (UI Specifications) |
| 5. Data & Database Requirements | Section 4 (Schema, Tables, DAOs, Preferences) |
| 6. Non-Functional Requirements | Section 6 (Security, Privacy, Performance, Reliability, Usability) |
| 7. Appendices & Use Cases | Section 8 (Use Cases), Section 3.2 (Component Catalog) |
 
---
 
## 10. Appendix B — Future Roadmap & Extensibility
 
- **Biometric Authentication:** Integration of `androidx.biometric:biometric` for fingerprint and Face Unlock.
- **Encrypted Persistence:** Upgrading Room to use SQLCipher for database-level AES-256 encryption.
- **Structured Timestamps:** Transition from freeform string dates to ISO 8601 (`java.time.Instant`) with a custom Room `TypeConverter`.
- **Document & Image Attachments:** Secure internal camera/gallery storage for photo captures of physical prescription slips.
- **PDF Export Engine:** Direct generation of signed PDF summaries with QR codes for clinical verification.
---
 
*— End of Document —*
 
