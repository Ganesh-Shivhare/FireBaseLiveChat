# Chat App 📱💬

A **real-time chat application** built using **MVVM Clean Architecture, Hilt for Dependency Injection, Firebase Firestore for messaging, and Firebase Cloud Messaging (FCM) for push notifications**. The app supports **real-time conversations, online/offline status tracking, push notifications, and message read receipts**.

---

## 📌 Features
✅ **User Authentication** (Google, Phone, Email)  
✅ **Real-time Messaging** using Firebase Firestore  
✅ **Push Notifications** using Firebase Cloud Messaging (FCM)  
✅ **User Online/Offline Status Tracking**  
✅ **Typing Indicator**  
✅ **Unread Message Counter**  
✅ **Mark Messages as Read**  
✅ **Load Older Messages on Scroll**  
✅ **Dark Mode Support**  
✅ **Local Storage for Notifications (Using Room Database)**  
✅ **Proguard Rules for Secure Release Builds**  

---

## 🚀 Tech Stack
| Component      | Technology Used |
|---------------|----------------|
| **Language** | Kotlin |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Dependency Injection** | Hilt |
| **Networking** | Firebase Firestore |
| **Authentication** | Firebase Auth (Google, Phone, Email) |
| **Push Notifications** | Firebase Cloud Messaging (FCM) |
| **Local Database** | Room Database |
| **Asynchronous Programming** | Kotlin Coroutines & Flow |
| **UI Components** | RecyclerView, ViewBinding, ConstraintLayout |
| **Image Loading** | Glide |
| **Testing** | JUnit, Espresso |

---

## 📂 Project Structure

```
📂 app/src/main/
│── 📂 data/              # Data sources (Firestore, Room, API)
│── 📂 di/                # Dependency Injection (Hilt modules)
│── 📂 ui/                # UI components (Activities, Fragments, Adapters)
│── 📂 viewmodel/         # ViewModel classes for UI logic
│── 📂 model/             # Data models
│── 📂 utils/             # Utility classes (extensions, helpers)
│── 📂 repository/        # Business logic and data handling
│── 📂 service/           # Firebase Messaging service
│── 📂 adapter/           # RecyclerView Adapters
│── 📂 notification/      # Notification handling logic
```

---

## 📥 Installation & Setup

### 1️⃣ Clone the Repository
```sh
git clone https://github.com/your-repo/chat-app.git
cd chat-app
```

### 2️⃣ Add Firebase Configuration
- Go to **[Firebase Console](https://console.firebase.google.com/)** and create a **Firebase project**.  
- Enable **Firestore Database**, **Firebase Authentication**, and **Firebase Cloud Messaging (FCM)**.  
- Download the `google-services.json` file from Firebase and place it inside:  
  ```
  app/src/main/
  ```

### 3️⃣ Install Dependencies
```sh
./gradlew build
```

### 4️⃣ Run the App
- Open the project in **Android Studio**.  
- Connect a physical device or use an emulator.  
- Click **Run ▶** to start the app.  

---

## 📜 Proguard Rules (Release Build Fixes)
For **Firestore & Gson serialization issues** in release mode, add this to `proguard-rules.pro`:  

```proguard
# Firebase Firestore
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }

# Gson Serialization
-keep class com.google.gson.** { *; }
-keep class my.package.models.** { *; }

# Hilt (Dependency Injection)
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class my.package.di.** { *; }
```

---

## 📌 Upcoming Features
🚀 **Message Reactions (Like, Love, Laugh, etc.)**  
🚀 **Voice & Video Calls (Using WebRTC)**  
🚀 **Chat Groups & Multi-user Conversations**  
🚀 **End-to-End Encryption (E2EE) for Secure Chats**  
🚀 **Media Sharing (Images, Videos, Documents, etc.)**  

---

## 📜 License
This project is licensed under the **Apache License 2.0**.

```
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

---

## 📞 Contact
For any issues or feature requests, feel free to **open an issue**
