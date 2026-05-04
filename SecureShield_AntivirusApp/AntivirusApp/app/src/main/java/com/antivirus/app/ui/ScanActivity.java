package com.antivirus.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.antivirus.app.R;
import com.antivirus.app.models.ScanResult;
import com.antivirus.app.models.ThreatItem;
import com.antivirus.app.scanner.AppScanner;
import com.antivirus.app.scanner.FileScanner;
import com.antivirus.app.ui.adapters.ThreatAdapter;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private ProgressBar  progressBar;
    private TextView     tvScanStatus, tvScanning, tvProgress;
    private TextView     tvThreatsFound, tvScanned;
    private RecyclerView rvThreats;
    private Button       btnAction;
    private View         cardResult;

    private ThreatAdapter   threatAdapter;
    private List<ThreatItem> threats = new ArrayList<>();
    private AppScanner  appScanner;
    private FileScanner fileScanner;

    private String scanType;
    private int totalScanned = 0;
    private boolean scanningDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scanType = getIntent().getStringExtra("SCAN_TYPE");
        if (scanType == null) scanType = "QUICK";

        initViews();
        startScan();
    }

    private void initViews() {
        progressBar    = findViewById(R.id.progressBar);
        tvScanStatus   = findViewById(R.id.tvScanStatus);
        tvScanning     = findViewById(R.id.tvScanning);
        tvProgress     = findViewById(R.id.tvProgress);
        tvThreatsFound = findViewById(R.id.tvThreatsFound);
        tvScanned      = findViewById(R.id.tvScanned);
        rvThreats      = findViewById(R.id.rvThreats);
        btnAction      = findViewById(R.id.btnAction);
        cardResult     = findViewById(R.id.cardResult);

        threatAdapter = new ThreatAdapter(threats);
        rvThreats.setLayoutManager(new LinearLayoutManager(this));
        rvThreats.setAdapter(threatAdapter);

        btnAction.setOnClickListener(v -> {
            if (scanningDone) finish();
            else cancelScan();
        });
        btnAction.setText("Cancel");

        cardResult.setVisibility(View.GONE);

        String title = "FULL".equals(scanType) ? "Full Scan" : "Quick Scan";
        tvScanStatus.setText("Running " + title + "...");
    }

    private void startScan() {
        appScanner  = new AppScanner(this);
        fileScanner = new FileScanner(this);

        // Always scan apps
        appScanner.scanApps(new AppScanner.AppScanCallback() {
            @Override
            public void onAppScanned(String appName, int scanned, int total) {
                runOnUiThread(() -> {
                    totalScanned = scanned;
                    int progress = (int) ((scanned / (float) total) * (scanType.equals("FULL") ? 50 : 100));
                    progressBar.setProgress(progress);
                    tvScanning.setText("Scanning: " + appName);
                    tvProgress.setText(scanned + " / " + total + " apps");
                });
            }

            @Override
            public void onThreatFound(ThreatItem threat) {
                runOnUiThread(() -> {
                    threats.add(threat);
                    threatAdapter.notifyItemInserted(threats.size() - 1);
                    tvThreatsFound.setText(threats.size() + " threat(s) found");
                });
            }

            @Override
            public void onScanComplete(List<com.antivirus.app.models.AppInfo> allApps, List<ThreatItem> appThreats) {
                if ("FULL".equals(scanType)) {
                    // Continue with file scan
                    runOnUiThread(() -> tvScanStatus.setText("Scanning files..."));
                    startFileScan(totalScanned, allApps.size());
                } else {
                    runOnUiThread(() -> showScanComplete(allApps.size(), 0));
                }
            }
        });
    }

    private void startFileScan(int appsTotal, int appCount) {
        fileScanner.scanStorage(new FileScanner.FileScanCallback() {
            @Override
            public void onFileScanned(String fileName, int scanned, int total) {
                runOnUiThread(() -> {
                    int progress = 50 + (int) ((scanned / (float) Math.max(total, 1)) * 50);
                    progressBar.setProgress(progress);
                    tvScanning.setText("Scanning: " + fileName);
                    tvProgress.setText(scanned + " / " + total + " files");
                });
            }

            @Override
            public void onThreatFound(ThreatItem threat) {
                runOnUiThread(() -> {
                    threats.add(threat);
                    threatAdapter.notifyItemInserted(threats.size() - 1);
                    tvThreatsFound.setText(threats.size() + " threat(s) found");
                });
            }

            @Override
            public void onScanComplete(int totalFiles, List<ThreatItem> fileThreats) {
                runOnUiThread(() -> showScanComplete(appCount, totalFiles));
            }
        });
    }

    private void showScanComplete(int apps, int files) {
        scanningDone = true;
        progressBar.setProgress(100);
        tvScanStatus.setText(threats.isEmpty() ? "✅ Device is Clean!" : "⚠️ Threats Found!");
        tvScanning.setText(threats.isEmpty() ? "No threats detected." : threats.size() + " threats detected.");
        tvProgress.setText("Scanned: " + apps + " apps" + (files > 0 ? " + " + files + " files" : ""));
        tvScanned.setText(apps + (files > 0 ? " + " + files : "") + " items scanned");
        cardResult.setVisibility(View.VISIBLE);
        btnAction.setText("Done");
    }

    private void cancelScan() {
        if (appScanner  != null) appScanner.cancel();
        if (fileScanner != null) fileScanner.cancel();
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelScan();
        super.onBackPressed();
    }
}
