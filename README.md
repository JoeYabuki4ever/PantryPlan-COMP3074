# PantryPlan | Functional prototype
COMP3074 · Group P 56 · Taharim Haque, Fatma Akkoca, Punar Sharifli

## Run in Android Studio (Windows)
1. Right-click the ZIP and select **Extract All**.
2. In Android Studio choose **File → Open**. Select the extracted folder that directly contains **settings.gradle**, **app**, and **gradlew.bat**. The archive places these at its top level.
3. Wait for Gradle sync. Use a Gradle JDK of 17 or later and install Android SDK Platform 35 if prompted. First setup needs an internet connection.
4. Start an Android 8.0 (API 26) or newer emulator in Device Manager, or connect an Android phone with USB debugging enabled. Select **app**, select your device, and click **Run ▶**.
5. If Run is grey and the toolbar says **Add Configuration**, first verify you opened the folder containing settings.gradle and that Gradle sync succeeded. Check the Build window for any sync errors.

## Demonstrate the functional prototype
| Action | What to check |
|---|---|
| Pantry → Add food. Enter Yogurt, 2, cups, Fridge. Pick tomorrow's date. Rotate before saving. | The draft stays filled. Save it, then search for Yogurt and filter by Fridge. |
| Edit Yogurt. Try quantity 0, then change it to 1 and save. Close and reopen PantryPlan. | Zero is rejected. The saved record still shows 1 cup (unit text as entered). |
| Add a food dated yesterday and another with no date. Open Dates and choose 1 day. | Yesterday appears under Passed dates. Yogurt appears under Today and upcoming. The undated food stays in Pantry only. Today and the last day of the selected window are both included. |
| Tap + Shopping list on Yogurt, then delete Yogurt from Pantry and confirm. | Its shopping copy remains. Add a second shopping item, edit the copy, check it, then clear purchased. Only the checked copy disappears. |
| Add a food dated today. Settings → Turn on reminders. Allow notifications if Android asks. Tap Check dates now. | One summary notification appears. Tap it to open Expiring soon. If you deny permission, inventory, dates and shopping remain usable. Use Android notification settings to allow notifications later. |
| Turn off reminders. Try the pantry and shopping actions in airplane mode. | The scheduled check is cancelled, the current summary is dismissed, and local data remains accessible without signing in. |

Daily reminders use AndroidX WorkManager. The first background check is scheduled after about a day; Android controls the actual time. A check posts at most one summary and only for upcoming dated food in the selected window. Check dates now runs the same counting and posting logic immediately. The settings switch controls reminders; the Dates screen always works independently.

## Storage and scope
SQLite stores food and shopping records. SharedPreferences stores the date window, first-run marker and reminder preference. The app uses the same package name and database as the UI prototype; an in-place update signed with the same key keeps its data. Clearing app storage or uninstalling deletes local records. The five native Java screens use programmatically created views; meals, cloud sharing and scanning are outside this milestone.

## Verification
Host checks passed for the date groups (18 boundary cases including leap day), SQLite reopen and edits, shopping-copy independence and clearing purchased rows. The Java source syntax was checked. These checks do not exercise the Android UI or notification delivery. The Android SDK was unavailable and the Gradle distribution download was blocked in the packaging environment; no APK or successful device run is claimed. Use the table above for device acceptance testing.

Re-run the host checks from the project root with Python 3 and a JDK:
- python checks/check_storage.py
- javac -d checks/out app/src/main/java/com/pantryplan/app/PantryReview.java checks/ReviewChecks.java
- java -cp checks/out com.pantryplan.app.ReviewChecks
