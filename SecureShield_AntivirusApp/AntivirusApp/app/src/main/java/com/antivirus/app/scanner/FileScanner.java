package com.antivirus.app.scanner;

import android.content.Context;
import android.os.Environment;
import com.antivirus.app.models.ThreatItem;
import com.antivirus.app.utils.ThreatDatabase;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    public interface FileScanCallback {
        void onFileScanned(String fileName, int scanned, int total);
        void onThreatFound(ThreatItem threat);
        void onScanComplete(int totalFiles, List<ThreatItem> threats);
    }

    private final Context context;
    private boolean cancelled = false;

    public FileScanner(Context context) {
        this.context = context;
    }

    public void cancel() { cancelled = true; }

    public void scanStorage(FileScanCallback callback) {
        new Thread(() -> {
            List<ThreatItem> threats = new ArrayList<>();
            List<File> allFiles = new ArrayList<>();

            // Collect files from common locations
            File[] roots = {
                Environment.getExternalStorageDirectory(),
                context.getFilesDir(),
                context.getCacheDir()
            };

            for (File root : roots) {
                if (root != null && root.exists()) {
                    collectFiles(root, allFiles, 0);
                }
            }

            int total = allFiles.size();

            for (int i = 0; i < allFiles.size() && !cancelled; i++) {
                File file = allFiles.get(i);
                callback.onFileScanned(file.getName(), i + 1, total);

                // Scan file
                ThreatItem threat = scanFile(file);
                if (threat != null) {
                    threats.add(threat);
                    callback.onThreatFound(threat);
                }

                // Simulate scan delay for UX
                try { Thread.sleep(5); } catch (InterruptedException e) { break; }
            }

            callback.onScanComplete(total, threats);
        }).start();
    }

    private void collectFiles(File dir, List<File> result, int depth) {
        if (depth > 5) return; // Limit recursion depth
        if (dir == null || !dir.canRead()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isFile()) {
                result.add(f);
            } else if (f.isDirectory() && !f.isHidden()) {
                collectFiles(f, result, depth + 1);
            }
        }
    }

    private ThreatItem scanFile(File file) {
        String name = file.getName().toLowerCase();

        // Check for dangerous file types
        if (ThreatDatabase.isDangerousFile(name)) {
            // Check suspicious name patterns
            if (ThreatDatabase.isSuspiciousFileName(name)) {
                return new ThreatItem(
                    file.getName(),
                    "Suspicious executable file detected: " + file.getAbsolutePath(),
                    file.getAbsolutePath(),
                    ThreatItem.ThreatLevel.HIGH,
                    ThreatItem.ThreatType.MALWARE
                );
            }
            // APKs from outside Play Store
            if (name.endsWith(".apk") && !file.getPath().contains("com.android.vending")) {
                return new ThreatItem(
                    file.getName(),
                    "Sideloaded APK found — may be unsafe: " + file.getAbsolutePath(),
                    file.getAbsolutePath(),
                    ThreatItem.ThreatLevel.MEDIUM,
                    ThreatItem.ThreatType.SUSPICIOUS_FILE
                );
            }
        }

        // Suspicious name even without dangerous extension
        if (ThreatDatabase.isSuspiciousFileName(name)) {
            return new ThreatItem(
                file.getName(),
                "File with suspicious name: " + file.getAbsolutePath(),
                file.getAbsolutePath(),
                ThreatItem.ThreatLevel.LOW,
                ThreatItem.ThreatType.SUSPICIOUS_FILE
            );
        }

        // Check file hash (EICAR test signature)
        String hash = getMD5(file);
        if ("44d88612fea8a8f36de82e1278abb02f".equals(hash)) {
            return new ThreatItem(
                file.getName(),
                "EICAR test virus detected!",
                file.getAbsolutePath(),
                ThreatItem.ThreatLevel.HIGH,
                ThreatItem.ThreatType.MALWARE
            );
        }

        return null;
    }

    private String getMD5(File file) {
        try {
            if (file.length() > 10 * 1024 * 1024) return ""; // skip files > 10MB
            MessageDigest md = MessageDigest.getInstance("MD5");
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            byte[] buf = new byte[8192];
            int len;
            while ((len = fis.read(buf)) != -1) md.update(buf, 0, len);
            fis.close();
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
