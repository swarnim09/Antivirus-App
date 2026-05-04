package com.antivirus.app.models;

public class ThreatItem {
    public enum ThreatLevel { HIGH, MEDIUM, LOW, SAFE }
    public enum ThreatType  { MALWARE, SPYWARE, ADWARE, SUSPICIOUS_FILE, DANGEROUS_PERMISSION, SAFE }

    private String name;
    private String detail;
    private String packageNameOrPath;
    private ThreatLevel level;
    private ThreatType  type;
    private long detectedAt;
    private boolean quarantined;

    public ThreatItem(String name, String detail, String packageNameOrPath,
                      ThreatLevel level, ThreatType type) {
        this.name               = name;
        this.detail             = detail;
        this.packageNameOrPath  = packageNameOrPath;
        this.level              = level;
        this.type               = type;
        this.detectedAt         = System.currentTimeMillis();
        this.quarantined        = false;
    }

    public String    getName()               { return name; }
    public String    getDetail()             { return detail; }
    public String    getPackageNameOrPath()  { return packageNameOrPath; }
    public ThreatLevel getLevel()            { return level; }
    public ThreatType  getType()             { return type; }
    public long      getDetectedAt()         { return detectedAt; }
    public boolean   isQuarantined()         { return quarantined; }
    public void      setQuarantined(boolean q){ this.quarantined = q; }

    public String getLevelString() {
        switch (level) {
            case HIGH:   return "HIGH RISK";
            case MEDIUM: return "MEDIUM RISK";
            case LOW:    return "LOW RISK";
            default:     return "SAFE";
        }
    }

    public String getTypeString() {
        switch (type) {
            case MALWARE:              return "Malware";
            case SPYWARE:              return "Spyware";
            case ADWARE:               return "Adware";
            case SUSPICIOUS_FILE:      return "Suspicious File";
            case DANGEROUS_PERMISSION: return "Dangerous Permission";
            default:                   return "Safe";
        }
    }
}
