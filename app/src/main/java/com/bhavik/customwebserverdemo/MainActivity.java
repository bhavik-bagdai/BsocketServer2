package com.bhavik.customwebserverdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.grubbrr.bsocket2.HttpServiceThread;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    HttpServiceThread httpServiceThread;

    TextView infoIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpServiceThread = new HttpServiceThread();

        infoIp = (TextView) findViewById(R.id.infoip);
        infoIp.setText(httpServiceThread.getIpAddress() + ":"
                + httpServiceThread.getHttpServerPORT() + "\n");


        httpServiceThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpServiceThread.stopServer();
    }
}