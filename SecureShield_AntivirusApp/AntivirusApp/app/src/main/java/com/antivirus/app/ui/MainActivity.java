package com.antivirus.app.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.antivirus.app.R;
import com.antivirus.app.scanner.RealTimeProtectionService;
import com.antivirus.app.utils.ThreatDatabase;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        requestPermissions();
        startProtectionService();
    }

    private void setupUI() {
        // Status card
        TextView tvStatus      = findViewById(R.id.tvStatus);
        TextView tvLastScan    = findViewById(R.id.tvLastScan);
        TextView tvSignatures  = findViewById(R.id.tvSignatures);

        tvStatus.setText("Device Protected ✓");
        tvLastScan.setText("Last scan: " + getCurrentTime());
        tvSignatures.setText("Signatures: " + ThreatDatabase.getTotalSignatures());

        // Quick scan button
        Button btnQuickScan = findViewById(R.id.btnQuickScan);
        btnQuickScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("SCAN_TYPE", "QUICK");
            startActivity(intent);
        });

        // Full scan button
        Button btnFullScan = findViewById(R.id.btnFullScan);
        btnFullScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("SCAN_TYPE", "FULL");
            startActivity(intent);
        });

        // Permission checker card
        MaterialCardView cardPermissions = findViewById(R.id.cardPermissions);
        cardPermissions.setOnClickListener(v ->
            startActivity(new Intent(this, PermissionCheckerActivity.class)));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            }, PERMISSION_REQUEST);
        } else {
            if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                    PERMISSION_REQUEST);
            }
        }
    }

    private void startProtectionService() {
        Intent service = new Intent(this, RealTimeProtectionService.class);
        startForegroundService(service);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(new Date());
    }
}
