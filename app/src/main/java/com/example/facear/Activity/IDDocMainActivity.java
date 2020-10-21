package com.example.facear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.jdev.countryutil.Constants;
import com.jdev.countryutil.CountryUtil;

public class IDDocMainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout btnPassport,btnNationalID,btnDrivingLicense,btnResidentialPermit;
    private ImageView btnBack;
    String countryName;
    int countryFlag;
    String IDDocType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_doc_main);
        BackgroundService.mContext = IDDocMainActivity.this;
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
                Intent intent_passport = new Intent(IDDocMainActivity.this, PassportVerificatioinActivity.class);
                startActivity(intent_passport);
                break;
            case R.id.btnIdcard:
                Intent intent_id = new Intent(IDDocMainActivity.this, IDCardVerificationActivity.class);
                startActivity(intent_id);
                break;
            case R.id.btnDrivingLicence:
                Intent intent_driving = new Intent(IDDocMainActivity.this, DrivingVerificationActivity.class);
                startActivity(intent_driving);
                break;
            case R.id.btnResidentialPermit:
                Intent intent_resident = new Intent(IDDocMainActivity.this, ResidentVerificationActivity.class);
                startActivity(intent_resident);
                break;
            case R.id.btnBack:
                Intent intent_back = new Intent(IDDocMainActivity.this,MainActivity.class);
                startActivity(intent_back);
                break;

        }

    }
}