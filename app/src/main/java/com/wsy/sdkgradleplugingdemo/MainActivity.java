package com.wsy.sdkgradleplugingdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wsy.testsdk.SDKTestTools;
import com.wsy.testsdk.inject.InjectBind;
import com.wsy.testsdk.inject.InjectBindClass;

//加入自定义的注解
@InjectBindClass
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SDKTestTools.AddFunction();
    }

    @Override
    @InjectBind
    protected void onStart() {

//        SDKTestTools.AddFunction();
        super.onStart();
//        SDKTestTools.AddFunction2();


    }
}
