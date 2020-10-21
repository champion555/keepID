package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.facear.FaceActivity;
import com.example.facear.R;

public class KYC_MainActivity extends AppCompatActivity implements View.OnClickListener{
    private  Button btnCreate,btnRestore;
    private ImageView btnKYCSetting;
    private static final int PERMISSONS_REQUEST_CODE= 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_main);
        init();
        isPermissionGranted();
        btnCreate.setOnClickListener(this);
        btnRestore.setOnClickListener(this);
        btnKYCSetting.setOnClickListener(this);
    }
    private void init(){
        btnCreate = findViewById(R.id.btnCreateID);
        btnRestore = findViewById(R.id.btnRestore);
        btnKYCSetting = findViewById(R.id.btnKYCSetting);
    }
    private void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()){
                initData();
            } else {
                requestPermission(KYC_MainActivity.this);
            }
        } else {
            initData();
        }
    }
    public boolean checkPermission() {
        int cameraResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);
        int recordAudioResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        int StorageResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storageReadResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return cameraResult == PackageManager.PERMISSION_GRANTED && recordAudioResult ==
                PackageManager.PERMISSION_GRANTED && StorageResult == PackageManager.PERMISSION_GRANTED && storageReadResult == PackageManager.PERMISSION_GRANTED;
    }
    public void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSONS_REQUEST_CODE);
    }
    private void initData(){
        setContentView(R.layout.activity_kyc_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateID:
//                Intent intent_setting = new Intent(KYC_MainActivity.this,KYC_PhoneNumSendActivity.class);
                Intent intent_setting = new Intent(KYC_MainActivity.this, KYC_EmailVeriActivity.class);
//                Intent intent_setting = new Intent(KYC_MainActivity.this, KYC_ProfileSetting.class);
                startActivity(intent_setting);
                break;
            case R.id.btnRestore:

                break;
            case R.id.btnKYCSetting:
                Intent intent = new Intent(KYC_MainActivity.this,KYC_SettingActivity.class);
                startActivity(intent);
                break;
        }

    }
}