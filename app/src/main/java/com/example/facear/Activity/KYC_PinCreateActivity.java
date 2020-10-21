package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.facear.FaceActivity;
import com.example.facear.R;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

public class KYC_PinCreateActivity extends AppCompatActivity implements View.OnClickListener, OnOtpCompletionListener {
    private OtpView createPin;
    private ImageView btnCreateShow;
    private boolean isCreateShow;
    int txtCreatePin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_pincreate);
        init();
        listener();
    }
    private  void init(){
        createPin = findViewById(R.id.createPin);
        btnCreateShow = findViewById(R.id.btnCreateShow);
    }
    private void listener(){
        btnCreateShow.setOnClickListener(this);
        createPin.setOtpCompletionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCreateShow:
                if (isCreateShow == false) {
                    btnCreateShow.setImageResource(R.drawable.ic_show);
                    createPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    isCreateShow = true;
                } else {
                    btnCreateShow.setImageResource(R.drawable.ic_hide);
                    createPin.setInputType(InputType.TYPE_CLASS_NUMBER);
                    isCreateShow = false;
                }
                break;
        }

    }

    @Override
    public void onOtpCompleted(String otp) {
        txtCreatePin = Integer.parseInt(createPin.getText().toString());
        Intent intent = new Intent(KYC_PinCreateActivity.this,KYC_PinConfirmActivity.class);
        intent.putExtra("txtCreatePin", txtCreatePin);
        startActivity(intent);
    }
}