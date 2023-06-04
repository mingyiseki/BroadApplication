package com.example.broadapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText input;
    private Button btnStart;
    private Button btnStop;
    // 重置标识变量
    private volatile boolean isCountdownRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.et_input);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnStart.setOnClickListener(v -> {
            // 获取输入数字并启动倒计时
            int number = Integer.parseInt(input.getText().toString());
            startCountdown(number);
        });

        btnStop.setOnClickListener(v -> stopCountdown());
    }

    private void startCountdown(final int number) {
        // 创建 Handler 用于将计时结果发送回主线程
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                input.setText(String.valueOf(msg.what));
            }
        };

        // 创建子线程实现计时功能
        new Thread(() -> {

            isCountdownRunning = true;
            for (int i = number; i >= 0; i--) {
                if (!isCountdownRunning){
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 将计时结果通过 Handler 发送回主线程
                Message message = Message.obtain();
                message.what = i;
                handler.sendMessage(message);
            }
            // 重置标识变量
            isCountdownRunning = false;
        }).start();
    }

    private void stopCountdown() {
        // 将标识变量设置为 false，以停止线程的执行
        isCountdownRunning = false;
        // 停止正在执行的子线程
        Thread.currentThread().interrupt();
        // 将倒计时结果重置为初始数字
        input.setText(input.getText().toString());
    }


}
