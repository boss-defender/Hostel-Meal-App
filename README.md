# 🍱 Hostel Meal Tracker

A modern, visually elegant, and hyper-practical Android application designed to eliminate the daily headache of hostel mess management. No more messy diaries, lost calculators, or end-of-the-month disputes. Track meals, manage members, and calculate bills effortlessly in a few taps.

Built entirely in the cloud using **Firebase Studio** with a sleek, modern GUI.

---

## 🚀 Key Features

* **✨ Smart One-Time Onboarding:** A smart setup wizard that configures your entire hostel setup on the very first launch and never bothers you again.
* **💵 Flexible Rate Management:** Supports flat meal rates or separate custom pricing for Breakfast, Lunch, and Dinner.
* **🍿 Optional Snacks Tracker:** Dedicated toggles for Morning (10 AM - 11 AM), Afternoon, and Evening snacks with independent pricing.
* **👥 Member Management:** Add or remove hostel roommates on the fly, with real-time search functionality by name.
* **📈 Real-Time Billing Dashboard:** Displays "Today's Bill" and cumulative "Total Bill From Beginning" for every member side-by-side.
* **👑 Individual Pricing Overrides:** Need to set a custom rate or a discount for a specific member? Modify individual rates in the settings without breaking the global rules.
* **📅 Chronological History Log:** A detailed, date-wise timeline calendar for each member showing exactly what they ate or missed on any given day.

---

## 📱 How It Works (Screen Flow)

### 1. The First-Time Setup Wizard 🧙‍♂️
* **Splash Screen (1s):** A clean, elegant introduction banner.
* **Rate Config:** Choose between uniform meal rates or split rates (Breakfast/Lunch/Dinner).
* **Snacks Config:** Toggle snack tracking on or off and set the custom snack rates.
* **Add Members:** Input your hostel mates' names one by one.
* **Success Banner (2.5s):** Confirms your settings are saved, then unlocks the main dashboard forever.

### 2. The Daily Dashboard 📊
* **Search:** Quickly filter members by name when the mess list gets long.
* **Expandable Rows (`V` Icon):** Click any member to expand their daily profile. Toggle what they ate today (`Yes` / `No`). The bills recalculate instantly!
* **Global Settings:** Access the gear icon anytime to change global rates, add/remove members, or apply custom pricing to specific people.

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Modern, declarative UI with smooth transitions)
* **Local Database:** Room DB (SQLite wrapper for ultra-fast, offline-first data persistence)
* **Build System:** Gradle (Compiled using remote Firebase Studio cloud environments)

---

## 📲 Installation

Since this app is built for private hostel usage and is not hosted on the Google Play Store, you can install it directly via the APK file:

1. Download the **`app-debug.apk`** file from this repository.
2. Transfer the APK file to your Android device.
3. Enable **"Install from Unknown Sources"** in your phone's security settings if prompted.
4. Tap the APK file and select **Install**.
5. Open the app and start tracking!

---

## 💡 Why This Project Exists
Living in a hostel means sharing responsibilities, but calculating monthly mess bills manually is always a hassle. This app was engineered to provide a **hassle-free, completely transparent, and easy way** to determine exactly who owes what at the end of the month based on real data.

*Developed for students, by students.* 🚀
