package com.broadcom.android.tv.miccheck;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "MicCheck";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private CompoundButton mButtonRecord;
    private CompoundButton mButtonPlay;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            finish();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mButtonRecord) {
            if (isChecked) {
                startRecording();
            } else {
                stopRecording();
            }
        } else if (buttonView == mButtonPlay) {
            if (isChecked) {
                startPlaying();
            } else {
                stopPlaying();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonRecord = findViewById(R.id.buttonRecord);
        mButtonPlay = findViewById(R.id.buttonPlay);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        TextView textView = findViewById(R.id.textView);
        String s = "devices=" + Arrays.toString(devices);
        try {
            List<MicrophoneInfo> microphones = audioManager.getMicrophones();
            s += ", microphones=" + microphones.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setText(s);
        Log.d(TAG, s);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
