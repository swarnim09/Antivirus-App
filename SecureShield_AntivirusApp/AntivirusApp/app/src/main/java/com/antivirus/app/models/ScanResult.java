package com.antivirus.app.models;

import java.util.List;

public class ScanResult {
    private int totalScanned;
    private int threatsFound;
    private int appsScanned;
    private int filesScanned;
    private long scanDuration;   // ms
    private List<ThreatItem> threats;
    private long scanTime;

    public ScanResult(int totalScanned, int threatsFound, int appsScanned,
                      int filesScanned, long scanDuration, List<ThreatItem> threats) {
        this.totalScanned  = totalScanned;
        this.threatsFound  = threatsFound;
        this.appsScanned   = appsScanned;
        this.filesScanned  = filesScanned;
        this.scanDuration  = scanDuration;
        this.threats       = threats;
        this.scanTime      = System.currentTimeMillis();
    }

    public int   getTotalScanned()  { return totalScanned; }
    public int   getThreatsFound()  { return threatsFound; }
    public int   getAppsScanned()   { return appsScanned; }
    public int   getFilesScanned()  { return filesScanned; }
    public long  getScanDuration()  { return scanDuration; }
    public List<ThreatItem> getThreats() { return threats; }
    public long  getScanTime()      { return scanTime; }

    public boolean isDeviceSafe()   { return threatsFound == 0; }
}
