package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;

public class KYC_ProfileMain extends AppCompatActivity implements View.OnClickListener{
    private Button btnVerificatioin,btnSetProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_profile_main);
        BackgroundService.mContext = KYC_ProfileMain.this;
        init();
        btnSetProfile.setOnClickListener(this);
        btnVerificatioin.setOnClickListener(this);
    }
    private void init(){
        btnVerificatioin = findViewById(R.id.btnVerification);
        btnSetProfile = findViewById(R.id.btnSetProfile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVerification:
                // verificatioin api call
                break;
            case R.id.btnSetProfile:
                Intent intent = new Intent(KYC_ProfileMain.this,KYC_ProfileSetting.class);
                startActivity(intent);
                break;
        }
    }
}