# ResQNet — Smart Emergency Response & Assistance System

> **A Complete Java Full-Stack MVC Web Application for Life-Saving Emergency Response**

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture & MVC Structure](#architecture--mvc-structure)
4. [Database Schema (`resqnet_db`)](#database-schema-resqnet_db)
5. [Key Modules & Features](#key-modules--features)
6. [Prerequisites & System Setup](#prerequisites--system-setup)
7. [Step-by-Step Execution Guide (VS Code)](#step-by-step-execution-guide-vs-code)
8. [Testing & Verification Guide](#testing--verification-guide)
9. [Troubleshooting Common Errors](#troubleshooting-common-errors)

---

## 1. Project Overview
**ResQNet** (*Smart Emergency Response & Assistance System*) is an enterprise-grade, emergency-response platform designed with a modern **Navy Blue, Crisp White, and Emergency Red** aesthetic. It empowers citizens to store critical medical history (blood group, allergies, conditions, and doctor details) and trusted emergency contacts once, and immediately activate synchronized emergency notifications and first-aid protocols during crises.

### Core Capabilities:
- **Instant First Aid Protocols:** Step-by-step guidance for Snake bites, Heart attack symptoms, Unconscious persons, and Breathing difficulties.
- **Accident & Fire Reporting:** Incident categorization, severity rating, GPS coordinate capture with explicit user consent, and photo upload verification.
- **Automated In-Website Notification Workflow:** Automatically logs and stages urgent alerts for primary emergency contacts and regional trauma center intake queues.
- **Hospital Intake Portal:** Dedicated, authenticated dashboard for hospital ER coordinators to review incoming ambulances and update patient status in real time.
- **Crime & Personal Safety:** Anonymous reporting mode to protect victim identities, alongside direct police helpline calling (`100` / `112`).

---

## 2. Technology Stack

| Layer | Technologies Used |
|---|---|
| **Frontend** | HTML5, CSS3 (Custom Responsive Design), Vanilla JavaScript (ES6+), Geolocation API |
| **Backend** | Java Servlets (Jakarta Servlet 6.0), JavaServer Pages (JSP 3.1), JSTL 3.0 |
| **Database** | MySQL / MariaDB (`resqnet_db`) with relational foreign keys and indexes |
| **Connectivity** | JDBC with `PreparedStatement` (SQL Injection Protection), MySQL Connector/J 8.3.0 |
| **Security** | BCrypt Password Hashing (`org.mindrot:jbcrypt`), Session Management (`HttpSession`) |
| **Server** | Apache Tomcat 10.1+ (Jakarta EE 10 compatible) |
| **Build Tool** | Apache Maven 3.9+ |
| **IDE** | Visual Studio Code (VS Code) |

---

## 3. Architecture & MVC Structure

ResQNet follows a strict, clean **Model-View-Controller (MVC)** layered design pattern:

```text
ResQNet/
├── pom.xml                                   # Maven dependency and build configuration
├── database.sql                              # Complete relational schema and sample seed data
├── README.md                                 # Beginner-friendly documentation and guide
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/resqnet/
│   │   │       ├── model/                    # Data models & entities
│   │   │       │   ├── User.java             # Core citizen profile entity
│   │   │       │   ├── UserMedicalDetails.java # Blood group, diseases, allergies, doctor
│   │   │       │   ├── EmergencyContact.java # Primary & backup contact records
│   │   │       │   ├── Hospital.java         # Hospital directory records
│   │   │       │   ├── HospitalStaff.java    # Hospital coordinator login entity
│   │   │       │   ├── EmergencyReport.java  # Incident reports across all 6 modules
│   │   │       │   ├── ReportAttachment.java # Photo attachment metadata
│   │   │       │   └── NotificationRecord.java # Audit log of in-website alerts
│   │   │       │
│   │   │       ├── util/                     # Utilities & security
│   │   │       │   ├── DatabaseConnection.java # Robust JDBC connection manager
│   │   │       │   ├── PasswordUtil.java     # Secure BCrypt password hasher
│   │   │       │   └── ValidationUtil.java   # Input sanitization and validators
│   │   │       │
│   │   │       ├── dao/                      # Data Access Objects (SQL PreparedStatements)
│   │   │       │   ├── UserDao.java          # User account transactional registration
│   │   │       │   ├── MedicalDetailsDao.java # Medical details CRUD
│   │   │       │   ├── EmergencyContactDao.java # Contact management
│   │   │       │   ├── HospitalDao.java      # Hospital facility queries & staff auth
│   │   │       │   ├── EmergencyReportDao.java # Incident creation & tracking
│   │   │       │   └── NotificationDao.java  # Dispatch logging
│   │   │       │
│   │   │       ├── service/                  # Business Logic Layer
│   │   │       │   ├── UserService.java      # Registration validation & login logic
│   │   │       │   ├── EmergencyService.java # Report submission & photo validation
│   │   │       │   ├── HospitalService.java  # Intake queue & status updates
│   │   │       │   └── NotificationService.java # Notification preparation & audit logs
│   │   │       │
│   │   │       ├── filter/
│   │   │       │   └── AuthFilter.java       # Session protection for private user pages
│   │   │       │
│   │   │       └── controller/               # Java Servlets (HTTP Handlers)
│   │   │           ├── HomeServlet.java      # Landing page ("/" and "/home")
│   │   │           ├── RegisterServlet.java  # Registration GET/POST ("/register")
│   │   │           ├── LoginServlet.java     # Authentication GET/POST ("/login")
│   │   │           ├── LogoutServlet.java    # Session invalidation ("/logout")
│   │   │           ├── DashboardServlet.java # 6 emergency tiles dashboard ("/dashboard")
│   │   │           ├── ProfileServlet.java   # My Details view & update ("/profile")
│   │   │           ├── MedicalEmergencyServlet.java # First-aid & reporting ("/emergency/medical")
│   │   │           ├── FireEmergencyServlet.java # Fire precautions & reporting ("/emergency/fire")
│   │   │           ├── AccidentEmergencyServlet.java # Accident intake ("/emergency/accident")
│   │   │           ├── CrimeEmergencyServlet.java # Police safety & reporting ("/emergency/crime")
│   │   │           ├── DisasterEmergencyServlet.java # Natural disaster relief ("/emergency/disaster")
│   │   │           ├── OtherEmergencyServlet.java # Custom crisis reporting ("/emergency/other")
│   │   │           ├── MyReportsServlet.java # Incident tracking ("/my-reports")
│   │   │           ├── HospitalPortalServlet.java # Hospital ER desk ("/hospital-portal")
│   │   │           ├── EmergencyContactsServlet.java # Contact management ("/emergency-contacts")
│   │   │           └── ImageServlet.java     # Safe streaming of incident photos ("/uploads/*")
│   │   │
│   │   └── webapp/                           # Presentation Layer (JSP, CSS, JS)
│   │       ├── WEB-INF/
│   │       │   └── web.xml                   # Deployment descriptor & session configuration
│   │       ├── css/
│   │       │   └── resqnet.css               # Navy blue, white, and red theme
│   │       ├── js/
│   │       │   └── resqnet.js                # Geolocation, first-aid tabs, form validation
│   │       ├── common/
│   │       │   ├── header.jsp                # Common meta tags & CSS includes
│   │       │   ├── navbar.jsp                # Dynamic navigation bar with auth status
│   │       │   └── footer.jsp                # Emergency disclaimer & helplines
│   │       ├── index.jsp                     # Public welcome & landing page
│   │       ├── register.jsp                  # 14-field registration form
│   │       ├── login.jsp                     # User login form
│   │       ├── dashboard.jsp                 # 6 large emergency tiles dashboard
│   │       ├── profile.jsp                   # "My Details" view & update form
│   │       ├── emergency-medical.jsp         # First-aid guidance & report form
│   │       ├── emergency-fire.jsp            # Fire safety (PASS method) & report form
│   │       ├── emergency-accident.jsp        # Accident reporting & hospital alert
│   │       ├── emergency-crime.jsp           # Personal safety & anonymous reporting
│   │       ├── emergency-disaster.jsp        # Disaster precautions & rescue requests
│   │       ├── emergency-other.jsp           # General crisis reporting form
│   │       ├── my-reports.jsp                # Live report tracking & notification audit
│   │       ├── hospital-portal.jsp           # Hospital intake queue & status updater
│   │       └── emergency-contacts.jsp        # Contact & hospital management
│   │
│   └── test/
│       └── java/com/resqnet/util/
│           ├── PasswordUtilTest.java         # BCrypt unit test
│           └── ValidationUtilTest.java       # Input validator unit test
```

---

## 4. Database Schema (`resqnet_db`)

The database consists of 8 normalized relational tables designed for security and scalability:

1. **`users`**: Stores user ID, full name, date of birth, gender, phone, unique email, address, and BCrypt hashed password.
2. **`user_medical_details`**: Normalized health record storing blood group, existing diseases, medical conditions, allergies, current medications, consulting doctor name and phone.
3. **`emergency_contacts`**: Relational table storing primary (`is_primary = 1`) and backup contacts per user.
4. **`hospitals`**: Directory of regional emergency care facilities and trauma centers (clearly marked as sample data).
5. **`hospital_staff`**: Credentials for authorized hospital coordinators and intake physicians.
6. **`emergency_reports`**: Incident records storing tracking ID (`RQ-YYYYMMDD-XXXX`), category, sub-category, severity, description, location, GPS latitude/longitude, patient condition, and live status.
7. **`report_attachments`**: Metadata for uploaded incident photographs (file name, path, MIME type, size).
8. **`notification_records`**: Audit log of automated in-website emergency alerts generated for contacts and hospital desks.

---

## 5. Key Modules & Features

### 5.1 Six Clickable Emergency Modules
1. **Medical Emergency (`/emergency/medical`)**:
   - Interactive first-aid tabs for **Snake bites**, **Heart attack symptoms**, **Unconscious person**, **Breathing difficulties**, and **Other acute crises**.
   - One-click ambulance calling (`tel:108`).
   - Browser GPS integration with user permission.
   - Medical disclaimer reminding users that the portal assists but does not replace certified EMTs.

2. **Fire Emergency (`/emergency/fire`)**:
   - Report residential, electrical, LPG cylinder, or chemical blazes.
   - Interactive **P.A.S.S.** fire extinguisher protocol (Pull, Aim, Squeeze, Sweep).
   - "Stop, Drop, and Roll" instructions and smoke avoidance tips.
   - Optional fire photo upload (validated to 5MB, JPG/PNG/WEBP).
   - Click-to-call Fire Control (`tel:101`).

3. **Accident Emergency (`/emergency/accident`)**:
   - Report road collisions, two-wheeler crashes, machinery injuries, or falls.
   - Condition assessment (Critical, Bleeding, Unconscious, Fractures).
   - Optional GPS coordinate tagging and accident photograph attachment.
   - Automatic dispatch alert prepared for primary emergency contact and trauma hospital intake.

4. **Crime & Personal Safety (`/emergency/crime`)**:
   - Specific reporting for harassment, theft, stalking, or suspicious activity.
   - **Anonymous Reporting Toggle**: Omits victim name and health records from responder view to protect privacy.
   - Immediate police helpline dialing (`tel:100` / `tel:112`) and Women Helpline (`tel:1091`).

5. **Natural Disasters (`/emergency/disaster`)**:
   - Survival guidelines and checklists for **Floods**, **Earthquakes**, **Landslides**, and **Cyclones**.
   - Relief requests (Boat rescue, food/water, medical evacuation).
   - NDRF Disaster Helpline dialing (`tel:1078`).

6. **Other Emergencies (`/emergency/other`)**:
   - Customizable form for unexpected crises (hazardous chemicals, wild animal encounters, building cracks).
   - Callback phone number, description, GPS, and photo attachment.

### 5.2 Hospital Intake Portal (`/hospital-portal`)
- Authenticated desk for hospital trauma teams.
- **Demo Staff Credentials**:
  - **Staff Code**: `STAFF-101`
  - **Password**: `hospital123`
- Inspect active incident reports, view patient blood group and allergies, and update dispatch status:
  - `Reported` &rarr; `Hospital Notified` &rarr; `Ambulance Dispatched` &rarr; `En Route` &rarr; `Resolved`.

---

## 6. Prerequisites & System Setup

Your system is already configured with all essential tools:

| Component | Verified Location | Version |
|---|---|---|
| **JDK (Java)** | `C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot` | Java 25 LTS (compiler release 25) |
| **Apache Maven** | `C:\apache-maven-3.9.9` (Set in system PATH) | Apache Maven 3.9.16 |
| **Apache Tomcat** | `C:\apache-tomcat-10.1` | Apache Tomcat 10.1.61 (Jakarta EE 10) |
| **MySQL / MariaDB** | `C:\xampp\mysql` | MariaDB 10.4.32 (Port 3306) |

---

## 7. Step-by-Step Execution Guide (VS Code)

### Step 1: Open the Project in VS Code
1. Launch **Visual Studio Code**.
2. Click **File &rarr; Open Folder...** and select:
   ```text
   C:\Users\aitha\Desktop\project
   ```
   *(VS Code's Java and Maven extensions will automatically detect `ResQNet/pom.xml`).*

### Step 2: Ensure MySQL is Running
1. Open a PowerShell terminal in VS Code (**Terminal &rarr; New Terminal**).
2. Check if MySQL is active:
   ```powershell
   & "C:\xampp\mysql\bin\mysql.exe" -u root -e "SHOW DATABASES;"
   ```
3. If MySQL is not running, start it using XAMPP Control Panel or run:
   ```powershell
   Start-Process -FilePath "C:\xampp\mysql\bin\mysqld.exe" -WorkingDirectory "C:\xampp\mysql" -ArgumentList "--defaults-file=bin\my.ini" -WindowStyle Hidden
   ```

### Step 3: Initialize the Database (`database.sql`)
1. Create the database and seed records:
   ```powershell
   Get-Content "C:\Users\aitha\Desktop\project\ResQNet\database.sql" | & "C:\xampp\mysql\bin\mysql.exe" -u root
   ```
2. Verify all 8 tables are present:
   ```powershell
   & "C:\xampp\mysql\bin\mysql.exe" -u root -e "USE resqnet_db; SHOW TABLES;"
   ```

### Step 4: Build the Project with Maven
In your VS Code terminal, build the WAR package:
```powershell
cd C:\Users\aitha\Desktop\project\ResQNet
mvn clean package
```
*Expected Result:*
```text
[INFO] Building war: C:\Users\aitha\Desktop\project\ResQNet\target\ResQNet.war
[INFO] BUILD SUCCESS
```

### Step 5: Deploy to Apache Tomcat 10.1
1. Copy the generated WAR file into Tomcat's `webapps` folder:
   ```powershell
   Copy-Item -Path "C:\Users\aitha\Desktop\project\ResQNet\target\ResQNet.war" -Destination "C:\apache-tomcat-10.1\webapps\ResQNet.war" -Force
   ```
2. Start Apache Tomcat:
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot"
   $env:CATALINA_HOME="C:\apache-tomcat-10.1"
   & "C:\apache-tomcat-10.1\bin\catalina.bat" run
   ```
3. Wait 3–5 seconds until you see:
   ```text
   INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [...] ms
   ```

### Step 6: Open the Website in Your Browser
Open Chrome, Edge, or Firefox and navigate to:
```text
http://localhost:8080/ResQNet/
```

---

## 8. Testing & Verification Guide

### Test Case 1: Citizen Registration
1. Navigate to: `http://localhost:8080/ResQNet/register`.
2. Fill in the 14 required fields:
   - **Full Name**: `Jane Smith`
   - **DOB**: `1998-08-20`
   - **Gender**: `Female`
   - **Phone**: `+91 9876543210`
   - **Email**: `jane.smith@example.com`
   - **Address**: `14 Palm Grove, Bangalore`
   - **Password**: `SecurePass123!`
   - **Confirm Password**: `SecurePass123!`
   - **Blood Group**: `B+`
   - **Existing Diseases**: `None`
   - **Medical Conditions**: `Mild Asthma`
   - **Allergies**: `Penicillin, Peanuts`
   - **Current Medications**: `Inhaler as needed`
   - **Doctor**: `Dr. Rao` (`+91 9845099999`)
   - **Primary Contact**: `David Smith`, `Spouse`, `+91 9876500099`
3. Click **Complete Registration & Secure My Profile**.
4. You will be redirected to the login page with a green success message.

### Test Case 2: Secure Citizen Login & Dashboard
1. Log in at `http://localhost:8080/ResQNet/login` with `jane.smith@example.com` and `SecurePass123!`.
2. Verify that:
   - Your name **"Jane Smith"** appears on the dashboard welcome banner.
   - Your blood group **"B+"** and primary contact **"David Smith"** appear prominently.
   - Six clickable emergency cards are displayed.

### Test Case 3: Accident Emergency Report & Photo Upload
1. Click the **3. Accident Emergency** card (or go to `/ResQNet/emergency/accident`).
2. Select **Road / Vehicle Collision** and **Critical**.
3. Click **📍 Share My GPS Location** (allow browser location permission).
4. Enter description: `Collision near signal junction. Driver has wrist injury.`
5. (Optional) Choose a sample image (`.jpg` or `.png`).
6. Click **Submit Accident Report & Alert Trauma Responders**.
7. Confirm that:
   - A unique tracking ID (`RQ-YYYYMMDD-XXXX`) is generated.
   - Notification records for David Smith and the assigned hospital are logged in the audit trail.

### Test Case 4: Hospital Intake Status Update
1. Open a new incognito window and visit: `http://localhost:8080/ResQNet/hospital-portal`.
2. Log in using demo staff credentials:
   - **Staff Code**: `STAFF-101`
   - **Password**: `hospital123`
3. Locate Jane Smith's accident report in the active intake queue.
4. Select `Ambulance Dispatched` in the dropdown and click **Set**.
5. Switch back to Jane Smith's browser window at `/ResQNet/my-reports` and refresh:
   - Confirm the status badge updates to **Ambulance Dispatched** in real time!

### Test Case 5: "My Details" Profile Update
1. Click **My Details** in the navigation bar (`/ResQNet/profile`).
2. Update your address or add daily vitamins to medications.
3. Click **Save Updated Profile & Medical Details**.
4. Log out and log back in: confirm your updated details persist without requiring re-registration.

---

## 9. Troubleshooting Common Errors

### 1. `Can't connect to MySQL server on 'localhost' (10061)`
- **Cause**: MySQL daemon is stopped.
- **Fix**: Open XAMPP Control Panel and start MySQL, or execute:
  ```powershell
  Start-Process -FilePath "C:\xampp\mysql\bin\mysqld.exe" -WorkingDirectory "C:\xampp\mysql" -ArgumentList "--defaults-file=bin\my.ini" -WindowStyle Hidden
  ```

### 2. `Address already in use: bind (Port 8080)`
- **Cause**: An earlier Tomcat instance is already running on port 8080.
- **Fix**: Terminate the previous process or run:
  ```powershell
  Get-Process -Name "*java*" | Stop-Process -Force
  ```
  Then restart Tomcat.

### 3. `ClassNotFoundException: jakarta.servlet.http.HttpServlet`
- **Cause**: The application is deployed to an outdated Tomcat server (like Tomcat 8.5 or Tomcat 9) which uses `javax.servlet` instead of `jakarta.servlet`.
- **Fix**: Deploy to **Apache Tomcat 10.1+** located at `C:\apache-tomcat-10.1`, which natively supports Jakarta EE 10 / Servlet 6.0.

### 4. `File size exceeds 5MB limit`
- **Cause**: Uploaded image is larger than 5 megabytes.
- **Fix**: Compress or resize the photo to under 5MB (JPG, PNG, or WEBP).

---

## 📄 License & Academic Note
This project is built for the academic engineering curriculum on **Smart Emergency Response & Assistance Systems**. All demo hospital facilities and personnel are sample records designed for simulation and evaluation.
