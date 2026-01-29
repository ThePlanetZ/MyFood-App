

# MyFood – Homemade Food Ordering App

## Overview

**MyFood** is an Android mobile application that connects home chefs, customers, and delivery persons to facilitate the ordering and delivery of homemade meals. The app was developed as a **Final Year Project** for the *Diplôme de Technicien Supérieur en Informatique* and demonstrates practical application of Android development, Firebase integration, and Google Maps features.

The application allows:

* Chefs to publish, update, and manage their dishes.
* Customers to browse meals, place orders, and track deliveries.
* Delivery persons to accept orders and manage deliveries in real time.

---

## Features

* User authentication with email verification using Firebase.
* Role-based interfaces: Customer, Chef, and Delivery Person.
* Real-time order management and notifications.
* Google Maps integration for delivery tracking.
* CRUD operations for dishes (create, update, delete).
* Password recovery and profile management.

---

## Technologies Used

* **Language:** Java
* **Platform:** Android Studio
* **Backend & Database:** Firebase Realtime Database & Firebase Authentication
* **Location Services:** Google Maps API

---

## Project Structure

* **app/src/main/java** – Application source code organized by packages for different user roles.
* **app/src/main/res** – Layouts, drawables, and resources for the UI.
* **Firebase integration** – Authentication and Realtime Database for managing users and orders.
* **Google Maps integration** – Used for delivery tracking and location services.

---

## Setup Instructions

1. Clone the repository:

```bash
git clone https://github.com/ThePlanetZ/MyFood-App.git
```

2. Open the project in **Android Studio**.
3. Add your `google-services.json` file to the `app/` directory (Firebase configuration).
4. Add your Google Maps API key in `AndroidManifest.xml`
5. Build and run the project on an Android device or emulator.



---

## Authors

* **Zakaria Kaoukab**
* **Mohamed Makrani**


