# ABET Assessment App

## Project Overview

This application streamlines the ABET assessment process by automating some of the process, as well as providing easy data analysis.

---

### Features

1. User Authentication & Role Management

    1. A secure login system with individualized professor accounts.

    2. Role-based access control ensuring appropriate permissions.

    3. Administrator password management and authentication.

2. Course-to-Professor Mapping

    1. Courses can be mapped directly to professors, cutting down on overhead.

3. Course Assessment & Reporting

   1. Professors can efficiently manage and input assessment data.

   2. Data is stored securely and exported easily for analysis and record-keeping.

4. Database & Export Functionality

   1. All assessment data is stored in a structured database.

   2. Supports exporting data in standard formats (CSV, Excel).

### Stretch Goals

1. Admin Dashboard

    1. Enables admins to manage outcomes, course-professor mappings, assessment schedules, and thresholds.

    2. User-friendly UI for configuring schedules, thresholds, and assessment parameters.

2. Professor Reference Pane

    1. Professors have quick access to recent FCAR data, making referencing and updating seamless.

3. Advanced Features

    1. Notifications (assessment reminders, system alerts)

    2. Analytics dashboard for tracking course performance trends

**Features are subject to change at any time as the application is in active development.**

---

## Technology Stack

### Frontend
- JSPs and Servlets

### Backend
- Java
- MySQL Database

---

## Getting Started
### Requirements:
- Java JDK 17
- Apache Maven

### Instructions for setup, installation, and running the project locally:
`git clone https://github.com/tdavis36/ABET-Assessment-App.git`

`cd ABET-Assessment-App`

`mvn install`

`mvn jetty:run`

---

## Contribution & Issues

Please open an issue or PR for feedback, improvements, or bug reporting.

---

## License
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)