package com.antivirus.app.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.antivirus.app.R;
import com.antivirus.app.models.AppInfo;
import java.util.List;

public class AppPermissionAdapter extends RecyclerView.Adapter<AppPermissionAdapter.VH> {

    private List<AppInfo> apps;

    public AppPermissionAdapter(List<AppInfo> apps) { this.apps = apps; }

    public void updateData(List<AppInfo> newData) {
        this.apps = newData;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_app_permission, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        AppInfo app = apps.get(pos);
        h.tvAppName.setText(app.getAppName());
        h.tvPackage.setText(app.getPackageName());
        h.tvPermCount.setText(app.getPermissions().size() + " permissions");
        h.ivIcon.setImageDrawable(app.getIcon());

        String risk = app.getRiskLevel();
        int color;
        switch (risk) {
            case "HIGH":   color = Color.parseColor("#D32F2F"); break;
            case "MEDIUM": color = Color.parseColor("#F57C00"); break;
            case "LOW":    color = Color.parseColor("#FBC02D"); break;
            default:       color = Color.parseColor("#388E3C"); break;
        }
        h.tvRisk.setText(risk);
        h.tvRisk.setTextColor(color);

        // Show risk bar
        h.riskBar.setScaleX(app.getRiskScore() / 100f);
        h.riskBar.setBackgroundColor(color);
    }

    @Override public int getItemCount() { return apps.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView  tvAppName, tvPackage, tvPermCount, tvRisk;
        View      riskBar;
        VH(View v) {
            super(v);
            ivIcon      = v.findViewById(R.id.ivAppIcon);
            tvAppName   = v.findViewById(R.id.tvAppName);
            tvPackage   = v.findViewById(R.id.tvPackageName);
            tvPermCount = v.findViewById(R.id.tvPermCount);
            tvRisk      = v.findViewById(R.id.tvRiskLevel);
            riskBar     = v.findViewById(R.id.riskBar);
        }
    }
}
