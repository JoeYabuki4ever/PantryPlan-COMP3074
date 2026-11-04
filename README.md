# PantryPlan Android UI Prototype
Native Java Android project. Android 8.0+; Java 17; Android SDK 35.

## Open and run (Windows)
1. Extract this ZIP completely.
2. Android Studio: File > Open. Select PantryPlan (the folder containing settings.gradle).
3. Allow Gradle sync and install SDK Platform 35 if prompted. Internet is needed for this first setup.
4. Settings > Build, Execution, Deployment > Build Tools > Gradle: use JDK 17 for Gradle.
5. Device Manager: create/start a Pixel emulator running Android API 26 or later.
6. Select app in the top toolbar, select the emulator, then press Run.

## Demonstration
Pantry starts with Milk, Spinach and Brown rice on first launch only.
Search or filter storage; add a food; enter name, positive quantity, unit, storage and optional date.
Edit or delete food (deletion asks for confirmation). Dates shows upcoming items within 1/3/7 days and explicitly labels past dates. Copy any food to Shopping. Add/edit shopping items, check purchases and clear purchased records with confirmation. Restart the app to demonstrate SQLite persistence.

## Scope
This is a functional UI prototype, not the completed final assignment. Native controls are generated in Java; the proposal's XML/Fragments/ViewModel structure is not yet implemented. SQLite stores food and shopping records. The review window is saved in SharedPreferences. Background reminders, notification permissions, CSV export and final architecture remain future work. Settings explicitly states that reminders are not scheduled. No dangerous permissions or accounts are required.

## Verification
Manifest XML parsed and project structure checked. Android SDK is unavailable in the creation environment; APK compilation and emulator interaction have NOT been verified. Build/run in Android Studio using the instructions above. No APK is included.

Prepared for PantryPlan, Group P 56. Taharim Haque (101418321), Fatma Akkoca, Punar Sharifli.
