package com.wsy.sdkgradleplugingdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wsy.testsdk.SDKTestTools;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SDKTestTools.AddFunction();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
