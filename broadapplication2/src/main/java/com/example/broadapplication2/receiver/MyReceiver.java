package com.example.broadapplication2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.broadapplication".equals(intent.getAction())) {
            String customValue = intent.getStringExtra("extraKey");
            Toast.makeText(context, "在MyReceiver中接收到广播", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "接收com.example.broadapplication中发出的自定义广播: " + customValue);
            Log.d(TAG, "onReceive: 修改代码");
        }
    }
}

