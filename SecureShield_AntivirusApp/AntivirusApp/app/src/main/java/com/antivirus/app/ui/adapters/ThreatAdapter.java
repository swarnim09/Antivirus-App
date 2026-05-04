package com.antivirus.app.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.antivirus.app.R;
import com.antivirus.app.models.ThreatItem;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ThreatAdapter extends RecyclerView.Adapter<ThreatAdapter.VH> {

    private final List<ThreatItem> threats;

    public ThreatAdapter(List<ThreatItem> threats) { this.threats = threats; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_threat, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ThreatItem t = threats.get(pos);
        h.tvName.setText(t.getName());
        h.tvDetail.setText(t.getDetail());
        h.tvType.setText(t.getTypeString());
        h.tvLevel.setText(t.getLevelString());

        // Color-code by level
        int color;
        switch (t.getLevel()) {
            case HIGH:   color = Color.parseColor("#FFE0E0"); break;
            case MEDIUM: color = Color.parseColor("#FFF3E0"); break;
            default:     color = Color.parseColor("#FFFDE0"); break;
        }
        h.card.setCardBackgroundColor(color);

        int badgeColor;
        switch (t.getLevel()) {
            case HIGH:   badgeColor = Color.parseColor("#D32F2F"); break;
            case MEDIUM: badgeColor = Color.parseColor("#F57C00"); break;
            default:     badgeColor = Color.parseColor("#FBC02D"); break;
        }
        h.tvLevel.setBackgroundColor(badgeColor);
        h.tvLevel.setTextColor(Color.WHITE);
    }

    @Override public int getItemCount() { return threats.size(); }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvName, tvDetail, tvType, tvLevel;
        VH(View v) {
            super(v);
            card     = v.findViewById(R.id.card);
            tvName   = v.findViewById(R.id.tvThreatName);
            tvDetail = v.findViewById(R.id.tvThreatDetail);
            tvType   = v.findViewById(R.id.tvThreatType);
            tvLevel  = v.findViewById(R.id.tvThreatLevel);
        }
    }
}
