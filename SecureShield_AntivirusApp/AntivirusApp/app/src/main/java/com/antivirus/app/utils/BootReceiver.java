package com.antivirus.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.antivirus.app.scanner.RealTimeProtectionService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent service = new Intent(context, RealTimeProtectionService.class);
            context.startForegroundService(service);
        }
    }
}
