📱 TimeTracker – Smart Task & Time Management App

TimeTracker is an Android productivity app that helps users efficiently manage tasks, set reminders, upload task images, and stay focused using a smart Pomodoro timer.
The app uses modern Android technologies like Room DB, WorkManager, Foreground Services, MVVM, LiveData, and Kotlin Coroutines.

🚀 Key Features
Feature	Description
✅ User Authentication	Secure login system
✅ App Lock Screen	App-level PIN lock for privacy
✅ Add / Edit / Delete Tasks	Manage tasks with title, description & priority
✅ Task Image Upload	Attach images to tasks (URI saved in DB)
✅ Task Reminders	WorkManager scheduled notifications
✅ Pomodoro Timer	Custom input timer + sound + vibration
✅ Light & Modern UI	Material UI + dark theme
✅ Local Secure Database	Room database for offline usage
🛠️ Tech Stack
Category	Tools
Language	Kotlin
Architecture	MVVM (Model-View-ViewModel)
Database	Room DB
Background Work	WorkManager
Async	Coroutines & Flow
UI	XML + Material Components
Security	SharedPreferences App-PIN
Notifications	NotificationChannel + Foreground Service
Navigation	Jetpack Navigation Component
📂 Project Structure
TimeTracker/
│── data/
│   ├── AppDb.kt
│   ├── Task.kt
│   └── TaskDao.kt
│
│── repo/
│   └── TaskRepository.kt
│
│── ui/
│   ├── AuthActivity.kt
│   ├── LoginFragment.kt
│   ├── SignupFragment.kt
│   ├── LockActivity.kt
│   ├── MainActivity.kt
│   ├── TimerFragment.kt
│   └── TaskEditorFragment.kt
│
│── timer/
│   └── TimerService.kt
│
│── reminders/
│   └── ReminderWorker.kt
│
└── util/
    └── SessionManager.kt

🔐 Permissions Used
Permission	Purpose
POST_NOTIFICATIONS	Show reminder notifications
VIBRATE	Vibrate on reminder
FOREGROUND_SERVICE	Run Pomodoro timer in background
READ_EXTERNAL_STORAGE (auto on image pick)	Pick task images from gallery
📊 App Flow
Splash → Lock Screen (PIN) → 
   ├── Logged In → Main Dashboard
   └── Not Logged In → Login/Register


Main Dashboard →

Task List

Create/Edit Task (+ image)

Timer Tools (Pomodoro)

Task Reminder → Notification → Opens App

🧠 Core Workflows
✅ Task Save Workflow
User input → TaskViewModel → TaskRepository → Room DB → RecyclerView list updates

✅ Reminder Workflow
Save Task with time → WorkManager schedules → NotificationChannel → Notification delivered

✅ Timer Workflow
Start Timer → ForegroundService runs countdown → Notification sound + vibration on finish

🗄️ Database Schema (Room)
Column	Type	Description
id	Long (PK)	Task ID
title	String	Task title
description	String	Task notes
priority	Int	Task priority (0-2)
dueAt	Long?	Timestamp for alarm
completed	Boolean	Task completed or not
userId	Long	Owner user id
imageUri	String?	Image attached to task
🔔 WorkManager Reminder Code
val req = OneTimeWorkRequestBuilder<ReminderWorker>()
    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
    .setInputData(ReminderWorker.data(task.title, task.description))
    .build()

WorkManager.getInstance(context).enqueue(req)

⏱️ Timer – Foreground Service

Uses:

Component	Purpose
startForegroundService()	Run timer even when app closed
NotificationChannel	Show ongoing timer notification
MediaPlayer + Vibrator	Alert on timer complete
🛡️ Security Layer

SharedPreferences stores user login

App-PIN protected entry screen

PIN stored securely in preferences

session.saveAppPin("1234")
session.getAppPin()



🚀 Future Enhancements

✅ Fingerprint unlock
✅ Cloud sync (Firebase)
✅ Voice task input
✅ Statistics dashboard
✅ Widget support
