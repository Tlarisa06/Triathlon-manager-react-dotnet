# Triathlon Manager React Dotnet

## Project Overview

This project is a full-stack, distributed Client-Server web application developed to manage triathlon competition results in real-time. It features a robust multi-layered architecture designed to handle secure stateless authentication, concurrent multi-user interactions, and instant data synchronization across connected clients.

The system is built as a modular solution, emphasizing clean code separation, high-performance web communication via asynchronous protocols, and a highly responsive single-page user interface.

---

## Core Functionalities & Features

### Security & Authentication
* **Stateless Token Validation (JWT):** Secure login system for event referees. Authentication is handled statelessly via JSON Web Tokens (HMAC-SHA512), ensuring secure data protection without heavy server-side sessions.
* **Cryptographic Request Guarding:** All critical data-mutating endpoints (POST, PUT, DELETE) are guarded by a backend interceptor filter. Requests lacking a valid Authorization: Bearer <token> header are automatically short-circuited.

### Real-Time Synchronization (Observer Pattern)
* **Automated Broadcast Notifications:** Implements a reactive real-time update mechanism using STOMP over WebSockets. Whenever a referee modifies or adds a result, the server broadcasts an event notification instantly.
* **Asynchronous Push Communication:** Connected frontend clients act as active Observers. The moment they intercept a server broadcast, they implicitly trigger localized data re-fetching, keeping the global standings consistent across all active screens without full-page reloads.

### Competition & Result Management
* **Global Participant Dashboard:** A comprehensive, dynamic view displaying participants along with their total cumulative scores across all triathlon trials.
* **Granular Target Filtering:** Advanced query-filtering mechanisms allowing referees to instantly search, isolate, and view event records based on specific parameters (Referee ID, Participant ID, or point values).
* **Live Result Mutations:** Authorized referees can efficiently register new scores or edit existing ones for their designated trials, with changes reflected globally across all screens in sub-second intervals.

---

## Technical Architecture

The application is organized into distinct, decoupled modules to ensure high maintainability, scalability, and a strict separation of concerns:

### 1. Persistence Layer (Relational Database)
* **Native ADO.NET / JDBC Style Mapping:** Data persistence is managed through an optimized, low-overhead relational database access layer utilizing raw SQL queries to prevent ORM bloat.
* **Relational Storage Engine:** Powered by SQLite, providing a reliable, zero-configuration embedded storage engine for athletes, user credentials, and trial records.

### 2. Networking & Distributed Logic
* **Asynchronous Event-Driven Messaging:** Uses persistent full-duplex WebSocket channels to handle live infrastructure pushes, bypassing traditional high-overhead HTTP polling.
* **Data Transfer Object (DTO) Pattern:** Uses lightweight serializable objects to bundle data over the network, minimizing transport payload sizes and completely decoupling internal database structures from client-facing models.

### 3. Presentation Layer (Modern Web SPA)
* **Reactive Component-Driven UI:** A responsive, declarative single-page application built with React, utilizing state encapsulation hooks (useState, useEffect) to ensure smooth visual performance.
* **Safe Asynchronous UI Re-rendering:** Safely intercepts incoming background network packets to update the user interface state seamlessly, preventing main-thread blocking, layout freezes, or view crashes.

---

## Project Modules

The workspace repository is cleanly isolated into separate root directories to guarantee clean full-stack deployment boundaries:

* 📁 Backend: The core business engine (Java Spring Boot server environment or .NET framework wrapper), housing the security interceptors, cryptographic token builders, repository layers, and the primary WebSocket message broker.
* 📁 Frontend: The modern web single-page interface built with React, encapsulating the state managers, Axios API service connectors, and custom visual rendering components (EventTable, EventForm, EventFilter).
* 📄 README.md: Comprehensive ecosystem documentation.

---

## 💻 Execution Instructions

### 1. Boot up the Backend Core
Navigate to the server directory and initialize the database connection along with the REST/WebSocket infrastructure:

    cd Backend
    ./gradlew bootRun

The server assembly will begin listening for incoming requests at http://localhost:8080.

### 2. Launch the Frontend Workspace
Open a secondary terminal window, pull the required package recipes, and boot up the local web deployment environment:

    cd Frontend
    npm install
    npm start

The interactive single-page application will automatically spin up in your browser at http://localhost:3000.

---

Developed for the "Distributed Cross-Platform Applications" Assignment - 2026
