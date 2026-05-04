package com.antivirus.app.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ThreatDatabase: Simulated malware signature database.
 * In a real app this would sync from a cloud threat intelligence feed.
 */
public class ThreatDatabase {

    // Known malware package names (simulated threat signatures)
    private static final Set<String> KNOWN_MALWARE_PACKAGES = new HashSet<>(Arrays.asList(
        "com.fake.antivirus",
        "com.malware.stealer",
        "com.spy.tracker",
        "com.adware.popup",
        "com.trojan.banker",
        "com.ransomware.locker",
        "com.keylogger.spy",
        "com.rootkit.system",
        "com.phishing.fake.bank",
        "com.adware.aggressive",
        "com.spyware.location",
        "com.trojan.sms",
        "com.virus.test.eicar"
    ));

    // Dangerous permission combinations that indicate spyware/malware
    private static final Map<String, String> DANGEROUS_PERMISSIONS = new HashMap<>();

    // Single permissions that are high-risk
    private static final Set<String> HIGH_RISK_PERMISSIONS = new HashSet<>(Arrays.asList(
        "android.permission.READ_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.SEND_SMS",
        "android.permission.READ_CALL_LOG",
        "android.permission.PROCESS_OUTGOING_CALLS",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.READ_CONTACTS",
        "android.permission.GET_ACCOUNTS",
        "android.permission.USE_BIOMETRIC",
        "android.permission.BIND_DEVICE_ADMIN",
        "android.permission.SYSTEM_ALERT_WINDOW"
    ));

    // Medium risk permissions
    private static final Set<String> MEDIUM_RISK_PERMISSIONS = new HashSet<>(Arrays.asList(
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.CHANGE_NETWORK_STATE",
        "android.permission.CHANGE_WIFI_STATE",
        "android.permission.BLUETOOTH",
        "android.permission.NFC",
        "android.permission.VIBRATE",
        "android.permission.WAKE_LOCK"
    ));

    // File extension blacklist
    private static final Set<String> DANGEROUS_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".apk", ".dex", ".so", ".jar", ".zip", ".rar"
    ));

    // Suspicious file name patterns
    private static final Set<String> SUSPICIOUS_PATTERNS = new HashSet<>(Arrays.asList(
        "crack", "hack", "keygen", "patch", "loader",
        "cheat", "exploit", "bypass", "inject", "trojan",
        "virus", "worm", "rootkit", "spyware", "keylog"
    ));

    static {
        DANGEROUS_PERMISSIONS.put("LOCATION+MICROPHONE", "Potential spyware: records audio and tracks location");
        DANGEROUS_PERMISSIONS.put("SMS+CONTACTS", "Potential data stealer: reads SMS and contacts");
        DANGEROUS_PERMISSIONS.put("CAMERA+STORAGE", "Potential surveillance: captures and stores media");
    }

    public static boolean isKnownMalware(String packageName) {
        return KNOWN_MALWARE_PACKAGES.contains(packageName.toLowerCase());
    }

    public static boolean isHighRiskPermission(String permission) {
        return HIGH_RISK_PERMISSIONS.contains(permission);
    }

    public static boolean isMediumRiskPermission(String permission) {
        return MEDIUM_RISK_PERMISSIONS.contains(permission);
    }

    public static boolean isDangerousFile(String fileName) {
        String lower = fileName.toLowerCase();
        for (String ext : DANGEROUS_EXTENSIONS) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    public static boolean isSuspiciousFileName(String fileName) {
        String lower = fileName.toLowerCase();
        for (String pattern : SUSPICIOUS_PATTERNS) {
            if (lower.contains(pattern)) return true;
        }
        return false;
    }

    public static String getPermissionRisk(String permission) {
        if (HIGH_RISK_PERMISSIONS.contains(permission)) return "HIGH";
        if (MEDIUM_RISK_PERMISSIONS.contains(permission)) return "MEDIUM";
        return "LOW";
    }

    public static int getTotalSignatures() {
        return KNOWN_MALWARE_PACKAGES.size() + HIGH_RISK_PERMISSIONS.size() + SUSPICIOUS_PATTERNS.size();
    }
}
