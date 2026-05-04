package com.antivirus.app.models;

import android.graphics.drawable.Drawable;
import java.util.List;

public class AppInfo {
    private String appName;
    private String packageName;
    private Drawable icon;
    private List<String> permissions;
    private int riskScore;        // 0-100
    private String riskLevel;     // SAFE / LOW / MEDIUM / HIGH
    private boolean isSystemApp;
    private long installTime;

    public AppInfo(String appName, String packageName, Drawable icon,
                   List<String> permissions, boolean isSystemApp, long installTime) {
        this.appName     = appName;
        this.packageName = packageName;
        this.icon        = icon;
        this.permissions = permissions;
        this.isSystemApp = isSystemApp;
        this.installTime = installTime;
        this.riskScore   = 0;
        this.riskLevel   = "SAFE";
    }

    public String   getAppName()     { return appName; }
    public String   getPackageName() { return packageName; }
    public Drawable getIcon()        { return icon; }
    public List<String> getPermissions() { return permissions; }
    public int      getRiskScore()   { return riskScore; }
    public String   getRiskLevel()   { return riskLevel; }
    public boolean  isSystemApp()    { return isSystemApp; }
    public long     getInstallTime() { return installTime; }

    public void setRiskScore(int score) {
        this.riskScore = score;
        if      (score >= 70) riskLevel = "HIGH";
        else if (score >= 40) riskLevel = "MEDIUM";
        else if (score >= 10) riskLevel = "LOW";
        else                  riskLevel = "SAFE";
    }
}
