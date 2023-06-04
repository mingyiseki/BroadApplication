package com.example.broadapplication4;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.broadapplication4.service.ClockService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockActivity extends AppCompatActivity {
    private EditText mEtTime;
    private TextView mTvResult;
    private Button mBtnStart;

    private ClockService mClockService;
    private boolean mIsBound = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ClockService.ACTION_COUNTDOWN_TICK)) {
                long millisUntilFinished = intent.getLongExtra(ClockService.KEY_MILLIS_UNTIL_FINISHED, 0);
                mTvResult.setText(getTimeDisplayString(millisUntilFinished));
            } else if (intent.getAction().equals(ClockService.ACTION_COUNTDOWN_FINISH)) {
                mTvResult.setText("时间到!");
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ClockService.LocalBinder binder = (ClockService.LocalBinder) service;
            mClockService = binder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mClockService = null;
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtTime = findViewById(R.id.et_time);
        mTvResult = findViewById(R.id.tv_result);
        mBtnStart = findViewById(R.id.btn_start);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStr = mEtTime.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                try {
                    Date date = sdf.parse(timeStr);
                    long millis = 0;
                    if (date != null) {
                        millis = date.getTime();
                    }

                    if (mIsBound && mClockService != null) {
                        mClockService.startCountdown(millis);
                    }
                } catch (ParseException e) {
                    Toast.makeText(ClockActivity.this, "请正确输入时间格式", Toast.LENGTH_SHORT).show();
                }
//                int hour = mEtTime.getHour();
//                int minute = mEtTime.getMinute();
//                long millis = (hour * 60 + minute) * 60 * 1000;
//
//                if (mIsBound && mClockService != null) {
//                    mClockService.startCountdown(millis);
//                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(ClockService.ACTION_COUNTDOWN_TICK);
        filter.addAction(ClockService.ACTION_COUNTDOWN_FINISH);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ClockService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private String getTimeDisplayString(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
