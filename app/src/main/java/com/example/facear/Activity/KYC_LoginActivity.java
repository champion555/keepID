package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.FaceActivity;
import com.example.facear.R;
import com.example.facear.Utils.Preferences;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

public class KYC_LoginActivity extends AppCompatActivity implements View.OnClickListener, OnOtpCompletionListener {
    private OtpView loginPin;
    private ImageView btnShow;
    private boolean isShow;
    int txtLoginPin;
    int txtSavedPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_login);
        init();
        listener();
        startService(new Intent(this, BackgroundService.class));
    }
    private  void init(){
        loginPin = findViewById(R.id.loginPin);
        btnShow = findViewById(R.id.btnShow);
    }
    private void listener(){
        btnShow.setOnClickListener(this);
        loginPin.setOtpCompletionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShow:
                if (isShow == false) {
                    btnShow.setImageResource(R.drawable.ic_show);
                    loginPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    isShow = true;
                } else {
                    btnShow.setImageResource(R.drawable.ic_hide);
                    loginPin.setInputType(InputType.TYPE_CLASS_NUMBER);
                    isShow = false;
                }
                break;
        }

    }

    @Override
    public void onOtpCompleted(String otp) {
        txtLoginPin = Integer.parseInt(loginPin.getText().toString());
        txtSavedPin = Preferences.getValue_Integer(KYC_LoginActivity.this, Preferences.PROFILE_PASSWORD);
        try{
            if (txtLoginPin == txtSavedPin){
                Intent intent = new Intent(KYC_LoginActivity.this,KYC_ProfileMain.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(KYC_LoginActivity.this, "The PinCode is wrong, Please try again.", Toast.LENGTH_LONG).show();
            }

        } catch(NumberFormatException ex){}


    }
}