package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;

public class KYC_Setting_IDMain extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout btnPassport,btnNationalID,btnDrivingLicense,btnResidentialPermit;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_id_main);
        BackgroundService.mContext = KYC_Setting_IDMain.this;
        init();
        listener();
    }
    private void init(){
        btnPassport = findViewById(R.id.btnPassport);
        btnNationalID = findViewById(R.id.btnIdcard);
        btnDrivingLicense = findViewById(R.id.btnDrivingLicence);
        btnResidentialPermit = findViewById(R.id.btnResidentialPermit);
        btnBack = findViewById(R.id.btnBack);
    }
    private void listener(){
        btnPassport.setOnClickListener(this);
        btnNationalID.setOnClickListener(this);
        btnDrivingLicense.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnResidentialPermit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPassport:
                Intent intent_passport = new Intent(KYC_Setting_IDMain.this, KYC_PassportVeriActivity.class);
                startActivity(intent_passport);
                break;
            case R.id.btnIdcard:
                Intent intent_id = new Intent(KYC_Setting_IDMain.this, KYC_IDCardVeriActivity.class);
                startActivity(intent_id);
                break;
            case R.id.btnDrivingLicence:
                Intent intent_driving = new Intent(KYC_Setting_IDMain.this,KYC_DriveVeriActivity.class);
                startActivity(intent_driving);
                break;
            case R.id.btnResidentialPermit:
                Intent intent_resident = new Intent(KYC_Setting_IDMain.this, KYC_ResidentVeriActivity.class);
                startActivity(intent_resident);
                break;
            case R.id.btnBack:
                Intent intent_back = new Intent(KYC_Setting_IDMain.this,KYC_ProfileSetting.class);
                startActivity(intent_back);
                break;

        }

    }
}