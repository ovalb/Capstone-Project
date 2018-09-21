package com.onval.capstone.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.onval.capstone.R;
import com.onval.capstone.service.RecordBinder;
import com.onval.capstone.service.RecordService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordActivity extends AppCompatActivity {
    @BindView(R.id.timer) TextView timerTextView;

    private boolean isBound = false;
    private RecordService service;
    Intent intentService;

    Handler handler;
    private long currentTimeMillis, startTime, timeAtLastPause;

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION)
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (permissionToRecordAccepted) {
            intentService = new Intent(this, RecordService.class);
            startService(intentService);
            bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
            handler = new Handler();
        }
        else
            finish();

    }

    public void recordButton(View view) {
        if (isBound && service.isPlaying()) {
            service.pauseRecording();
            handler.removeCallbacks(timerRunnable);

        }
        else {
            service.startRecording();
            startTime = SystemClock.uptimeMillis();
            handler.post(timerRunnable);
        }

        upgradeRecordDrawable(view);
    }

    private void upgradeRecordDrawable(View view) {
        if (view instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) view;

            int drawableId = (service.isPlaying()) ? R.drawable.ic_pause_white_24dp : R.drawable.ic_fab_dot;
            fab.setImageDrawable(ContextCompat.getDrawable(this, drawableId));
        }
    }

    private void resetTimer() {
        timeAtLastPause = 0;
        currentTimeMillis = 0;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            RecordBinder recordBinder = (RecordBinder) binder;
            service = (RecordService) recordBinder.getService();
            isBound = true;

            while (!service.isReady) {
                //Polling is bad but c'mon, it's just a few millis
            }

            service.startRecording();
            startTime = SystemClock.uptimeMillis();
            handler.post(timerRunnable);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentTimeMillis = SystemClock.uptimeMillis() - startTime + timeAtLastPause;
            timerTextView.setText(timeFormatFromMills(currentTimeMillis));
            handler.postDelayed(this, 1000);
        }
    };

    @SuppressLint("DefaultLocale")
    private String timeFormatFromMills(long millis) {
        int seconds = (int) (millis / 1000);

        int hh = seconds / 3600;
        int mm = seconds / 60 % 60;
        int ss = seconds % 60;

        if (hh == 0)
            return String.format("%02d:%02d", mm, ss);

        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }
}
