package com.broadcom.android.tv.miccheck;

import android.app.Activity;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        TextView textView = findViewById(R.id.textView);
        textView.setText(String.format("devices=%s", Arrays.toString(devices)));
    }
}
