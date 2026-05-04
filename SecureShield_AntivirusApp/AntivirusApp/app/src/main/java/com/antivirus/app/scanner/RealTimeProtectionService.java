package com.antivirus.app.scanner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.antivirus.app.R;
import com.antivirus.app.ui.MainActivity;
import com.antivirus.app.utils.ThreatDatabase;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RealTimeProtectionService extends Service {

    public static final String CHANNEL_ID     = "antivirus_protection";
    public static final String CHANNEL_ALERT  = "antivirus_alerts";
    public static final int    NOTIF_ID       = 1001;

    private Handler  handler;
    private Runnable monitorRunnable;
    private Set<String> knownPackages = new HashSet<>();
    private static final long CHECK_INTERVAL_MS = 30_000; // 30 seconds

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        handler = new Handler(Looper.getMainLooper());

        // Seed known packages
        List<PackageInfo> pkgs = getPackageManager()
            .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo p : pkgs) knownPackages.add(p.packageName);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_ID, buildProtectionNotification("Real-time protection active"));
        startMonitoring();
        return START_STICKY;
    }

    private void startMonitoring() {
        monitorRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewApps();
                handler.postDelayed(this, CHECK_INTERVAL_MS);
            }
        };
        handler.postDelayed(monitorRunnable, CHECK_INTERVAL_MS);
    }

    private void checkForNewApps() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> current = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo pkg : current) {
            if (!knownPackages.contains(pkg.packageName)) {
                // New app installed — scan it
                knownPackages.add(pkg.packageName);
                analyzeNewApp(pkg, pm);
            }
        }
    }

    private void analyzeNewApp(PackageInfo pkg, PackageManager pm) {
        String appName;
        try { appName = pm.getApplicationLabel(pkg.applicationInfo).toString(); }
        catch (Exception e) { appName = pkg.packageName; }

        // Check against malware DB
        if (ThreatDatabase.isKnownMalware(pkg.packageName)) {
            sendThreatAlert(appName, "⚠️ MALWARE DETECTED! " + appName + " is a known threat.");
            return;
        }

        // Check permissions
        if (pkg.requestedPermissions != null) {
            int highRiskCount = 0;
            for (String perm : pkg.requestedPermissions) {
                if (ThreatDatabase.isHighRiskPermission(perm)) highRiskCount++;
            }
            if (highRiskCount >= 3) {
                sendThreatAlert(appName, "⚠️ " + appName + " requests " + highRiskCount + " high-risk permissions. Review immediately.");
            }
        }
    }

    private void sendThreatAlert(String appName, String message) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ALERT)
            .setSmallIcon(R.drawable.ic_shield_alert)
            .setContentTitle("🛡️ Threat Detected!")
            .setContentText(message)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build();

        nm.notify((int) System.currentTimeMillis(), notification);

        // Also update foreground notification
        nm.notify(NOTIF_ID, buildProtectionNotification("⚠️ Threat detected: " + appName));
    }

    private Notification buildProtectionNotification(String text) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle("🛡️ Antivirus Active")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pi)
            .build();
    }

    private void createNotificationChannels() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel protection = new NotificationChannel(
            CHANNEL_ID, "Protection Status", NotificationManager.IMPORTANCE_LOW);
        protection.setDescription("Shows real-time protection status");

        NotificationChannel alerts = new NotificationChannel(
            CHANNEL_ALERT, "Threat Alerts", NotificationManager.IMPORTANCE_HIGH);
        alerts.setDescription("Alerts for detected threats");

        nm.createNotificationChannel(protection);
        nm.createNotificationChannel(alerts);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && monitorRunnable != null)
            handler.removeCallbacks(monitorRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
