package com.antivirus.app.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.antivirus.app.R;
import com.antivirus.app.models.AppInfo;
import com.antivirus.app.scanner.AppScanner;
import com.antivirus.app.ui.adapters.AppPermissionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionCheckerActivity extends AppCompatActivity {

    private RecyclerView rvApps;
    private EditText     etSearch;
    private TextView     tvCount;
    private AppPermissionAdapter adapter;
    private List<AppInfo> allApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_checker);

        rvApps   = findViewById(R.id.rvApps);
        etSearch = findViewById(R.id.etSearch);
        tvCount  = findViewById(R.id.tvCount);

        adapter = new AppPermissionAdapter(new ArrayList<>());
        rvApps.setLayoutManager(new LinearLayoutManager(this));
        rvApps.setAdapter(adapter);

        loadApps();
        setupSearch();
    }

    private void loadApps() {
        tvCount.setText("Loading apps...");
        new Thread(() -> {
            AppScanner scanner = new AppScanner(this);
            allApps = scanner.getAllAppsWithPermissions();
            runOnUiThread(() -> {
                adapter.updateData(allApps);
                tvCount.setText(allApps.size() + " apps analysed");
            });
        }).start();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterApps(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterApps(String query) {
        if (query.isEmpty()) {
            adapter.updateData(allApps);
            return;
        }
        List<AppInfo> filtered = allApps.stream()
            .filter(a -> a.getAppName().toLowerCase().contains(query.toLowerCase())
                      || a.getPackageName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
        adapter.updateData(filtered);
    }
}
