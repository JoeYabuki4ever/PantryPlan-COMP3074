package com.pantryplan.app;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import androidx.work.*;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public final class ReminderWorker extends Worker {
    static final String CHANNEL = "package-dates", JOB = "daily-pantry-review";
    static final int NOTICE = 56;
    public ReminderWorker(Context context, WorkerParameters parameters) { super(context, parameters); }
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("MainActivity", Context.MODE_PRIVATE); }
    static NotificationManager manager(Context c) { return c.getSystemService(NotificationManager.class); }
    static void channel(Context c) {
        manager(c).createNotificationChannel(new NotificationChannel(CHANNEL, "Package date reminders", NotificationManager.IMPORTANCE_DEFAULT));
    }
    static boolean permitted(Context c) {
        channel(c);
        return (Build.VERSION.SDK_INT < 33 || c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            && manager(c).areNotificationsEnabled() && manager(c).getNotificationChannel(CHANNEL).getImportance() != NotificationManager.IMPORTANCE_NONE;
    }
    static void enable(Context c, boolean enabled) {
        prefs(c).edit().putBoolean("remind", enabled).apply();
        WorkManager work = WorkManager.getInstance(c);
        if (enabled) work.enqueueUniquePeriodicWork(JOB, ExistingPeriodicWorkPolicy.KEEP,
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 1, TimeUnit.DAYS).setInitialDelay(1, TimeUnit.DAYS).build());
        else { work.cancelUniqueWork(JOB); manager(c).cancel(NOTICE); }
    }
    static int count(SQLiteDatabase db, int window) {
        int total = 0; LocalDate today = LocalDate.now();
        try (Cursor rows = db.rawQuery("SELECT date FROM food WHERE date IS NOT NULL AND date != ''", null)) {
            while (rows.moveToNext()) if (PantryReview.group(rows.getString(0), today, window) == PantryReview.Group.UPCOMING) total++;
        }
        return total;
    }
    static boolean post(Context c, int count) {
        if (!prefs(c).getBoolean("remind", false) || !permitted(c)) return false;
        if (count == 0) { manager(c).cancel(NOTICE); return false; }
        Intent open = new Intent(c, MainActivity.class).setAction("com.pantryplan.REVIEW")
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent tap = PendingIntent.getActivity(c, 56, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n = new Notification.Builder(c, CHANNEL).setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("PantryPlan • Expiring soon")
                .setContentText(count + (count == 1 ? " item has" : " items have") + " a package date in your review window.")
                .setContentIntent(tap).setAutoCancel(true).build();
        try { manager(c).notify(NOTICE, n); return true; } catch (SecurityException ignored) { return false; }
    }
    @Override public Result doWork() {
        Context c = getApplicationContext();
        if (!prefs(c).getBoolean("remind", false) || !permitted(c)) return Result.success();
        try (SQLiteDatabase db = SQLiteDatabase.openDatabase(c.getDatabasePath("pantry.db").getPath(), null, SQLiteDatabase.OPEN_READONLY)) {
            post(c, count(db, prefs(c).getInt("window", 3))); return Result.success();
        } catch (android.database.sqlite.SQLiteException e) { return getRunAttemptCount() < 3 ? Result.retry() : Result.failure(); }
    }
}
