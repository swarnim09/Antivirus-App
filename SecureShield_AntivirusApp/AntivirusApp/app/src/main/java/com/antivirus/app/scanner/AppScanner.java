package com.antivirus.app.scanner;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.antivirus.app.models.AppInfo;
import com.antivirus.app.models.ThreatItem;
import com.antivirus.app.utils.ThreatDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppScanner {

    public interface AppScanCallback {
        void onAppScanned(String appName, int scanned, int total);
        void onThreatFound(ThreatItem threat);
        void onScanComplete(List<AppInfo> allApps, List<ThreatItem> threats);
    }

    private final Context context;
    private boolean cancelled = false;

    public AppScanner(Context context) {
        this.context = context;
    }

    public void cancel() { cancelled = true; }

    public void scanApps(AppScanCallback callback) {
        new Thread(() -> {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
            List<AppInfo>    allApps  = new ArrayList<>();
            List<ThreatItem> threats  = new ArrayList<>();

            int total = packages.size();

            for (int i = 0; i < packages.size() && !cancelled; i++) {
                PackageInfo pkg = packages.get(i);
                String appName;
                try {
                    appName = pm.getApplicationLabel(pkg.applicationInfo).toString();
                } catch (Exception e) {
                    appName = pkg.packageName;
                }

                callback.onAppScanned(appName, i + 1, total);

                // Build permission list
                List<String> permissions = new ArrayList<>();
                if (pkg.requestedPermissions != null) {
                    permissions.addAll(Arrays.asList(pkg.requestedPermissions));
                }

                Drawable icon;
                try { icon = pm.getApplicationIcon(pkg.packageName); }
                catch (Exception e) { icon = pm.getDefaultActivityIcon(); }

                boolean isSystem = (pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                AppInfo appInfo = new AppInfo(
                    appName, pkg.packageName, icon,
                    permissions, isSystem, pkg.firstInstallTime
                );

                // --- Threat analysis ---

                // 1. Known malware check
                if (ThreatDatabase.isKnownMalware(pkg.packageName)) {
                    ThreatItem t = new ThreatItem(
                        appName,
                        "Known malware detected: " + pkg.packageName,
                        pkg.packageName,
                        ThreatItem.ThreatLevel.HIGH,
                        ThreatItem.ThreatType.MALWARE
                    );
                    threats.add(t);
                    callback.onThreatFound(t);
                    appInfo.setRiskScore(100);
                } else {
                    // 2. Permission risk scoring
                    int score = calculateRiskScore(permissions);
                    appInfo.setRiskScore(score);

                    if (score >= 70) {
                        ThreatItem t = new ThreatItem(
                            appName,
                            "High-risk permission combination detected",
                            pkg.packageName,
                            ThreatItem.ThreatLevel.HIGH,
                            ThreatItem.ThreatType.SPYWARE
                        );
                        threats.add(t);
                        callback.onThreatFound(t);
                    } else if (score >= 40) {
                        ThreatItem t = new ThreatItem(
                            appName,
                            "Suspicious permission usage detected",
                            pkg.packageName,
                            ThreatItem.ThreatLevel.MEDIUM,
                            ThreatItem.ThreatType.DANGEROUS_PERMISSION
                        );
                        threats.add(t);
                        callback.onThreatFound(t);
                    }
                }

                allApps.add(appInfo);

                try { Thread.sleep(10); } catch (InterruptedException e) { break; }
            }

            callback.onScanComplete(allApps, threats);
        }).start();
    }

    private int calculateRiskScore(List<String> permissions) {
        int score = 0;
        int highCount = 0;

        for (String perm : permissions) {
            String risk = ThreatDatabase.getPermissionRisk(perm);
            if ("HIGH".equals(risk))   { score += 15; highCount++; }
            if ("MEDIUM".equals(risk)) { score += 5; }
        }

        // Bonus penalty for combinations
        if (hasPermission(permissions, "android.permission.RECORD_AUDIO") &&
            hasPermission(permissions, "android.permission.ACCESS_FINE_LOCATION")) {
            score += 20; // Spyware combo
        }
        if (hasPermission(permissions, "android.permission.READ_SMS") &&
            hasPermission(permissions, "android.permission.READ_CONTACTS")) {
            score += 20; // Data stealer combo
        }
        if (hasPermission(permissions, "android.permission.CAMERA") &&
            hasPermission(permissions, "android.permission.RECORD_AUDIO") &&
            hasPermission(permissions, "android.permission.ACCESS_FINE_LOCATION")) {
            score += 25; // Surveillance combo
        }

        return Math.min(score, 100);
    }

    private boolean hasPermission(List<String> permissions, String permission) {
        return permissions.contains(permission);
    }

    // Get all apps for the permission checker screen
    public List<AppInfo> getAllAppsWithPermissions() {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<AppInfo> result = new ArrayList<>();

        for (PackageInfo pkg : packages) {
            String appName;
            try { appName = pm.getApplicationLabel(pkg.applicationInfo).toString(); }
            catch (Exception e) { appName = pkg.packageName; }

            List<String> perms = new ArrayList<>();
            if (pkg.requestedPermissions != null) {
                perms.addAll(Arrays.asList(pkg.requestedPermissions));
            }

            Drawable icon;
            try { icon = pm.getApplicationIcon(pkg.packageName); }
            catch (Exception e) { icon = pm.getDefaultActivityIcon(); }

            boolean isSystem = (pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            AppInfo info = new AppInfo(appName, pkg.packageName, icon, perms, isSystem, pkg.firstInstallTime);
            info.setRiskScore(calculateRiskScore(perms));
            result.add(info);
        }

        // Sort by risk score descending
        result.sort((a, b) -> b.getRiskScore() - a.getRiskScore());
        return result;
    }
}
