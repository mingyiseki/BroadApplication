package com.example.broadapplication4.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

public class ClockService extends Service {
    public static final String ACTION_COUNTDOWN_TICK = "com.example.clock.COUNTDOWN_TICK";
    public static final String ACTION_COUNTDOWN_FINISH = "com.example.clock.COUNTDOWN_FINISH";
    public static final String KEY_MILLIS_UNTIL_FINISHED = "KEY_MILLIS_UNTIL_FINISHED";

    private CountDownTimer mCountDownTimer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            long millis = intent.getLongExtra(KEY_MILLIS_UNTIL_FINISHED, 0);
            startCountdown(millis);
        }
        return START_STICKY;
    }

    public void startCountdown(long millis) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mCountDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Intent intent = new Intent(ACTION_COUNTDOWN_TICK);
                intent.putExtra(KEY_MILLIS_UNTIL_FINISHED, millisUntilFinished);
                sendBroadcast(intent);
                updateNotification(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(ACTION_COUNTDOWN_FINISH);
                sendBroadcast(intent);
                updateNotification(0);
                stopSelf();
            }
        };
        mCountDownTimer.start();
    }

    private void updateNotification(long millisUntilFinished) {
        String contentText = getTimeDisplayString(millisUntilFinished);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("倒计时")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOngoing(true);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    private String getTimeDisplayString(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public ClockService getService() {
            return ClockService.this;
        }
    }
}
