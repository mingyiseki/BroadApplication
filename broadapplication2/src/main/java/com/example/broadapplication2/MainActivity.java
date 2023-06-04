package com.example.broadapplication2;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.broadapplication2.receiver.MyReceiver;

public class MainActivity extends AppCompatActivity {
    private MyReceiver myReceiver;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadapplication");
            intent.putExtra("extraKey", "CustomValue");
            sendBroadcast(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 注册广播接收器
        myReceiver = new MyReceiver();
        IntentFilter filter2 = new IntentFilter("com.example.broadapplication");
        registerReceiver(myReceiver, filter2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消广播接收器的注册
        unregisterReceiver(myReceiver);
    }
}