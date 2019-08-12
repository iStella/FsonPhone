package com.example.Fson.ToolsClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.Fson.PublicClass.LoadActivity;

/**
 * Created by jiping on 7/31/17.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intent_ = new Intent(context, LoadActivity.class);
            intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_);
        }
    }
}
