package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.facear.FaceActivity;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Preferences;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

public class KYC_PinConfirmActivity extends AppCompatActivity implements View.OnClickListener, OnOtpCompletionListener {
    private OtpView confirmPin;
    private ImageView btnConfirmshow;
    private boolean isConfirmShow;
    Boolean faceLiveness = false;
    boolean userProfile = false;
    int txtConfirmPin,txtCreatePin;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_pinconfirm);
        init();
        listener();
        bundle = getIntent().getExtras();
        txtCreatePin = bundle.getInt("txtCreatePin");
    }
    private  void init(){
        confirmPin = findViewById(R.id.confirmPin);
        btnConfirmshow = findViewById(R.id.btnConfirmShow);
    }
    private void listener(){
        btnConfirmshow.setOnClickListener(this);
        confirmPin.setOtpCompletionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConfirmShow:
                if (isConfirmShow == false) {
                    btnConfirmshow.setImageResource(R.drawable.ic_show);
                    confirmPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    isConfirmShow = true;
                } else {
                    btnConfirmshow.setImageResource(R.drawable.ic_hide);
                    confirmPin.setInputType(InputType.TYPE_CLASS_NUMBER);
                    isConfirmShow = false;
                }
                break;
        }
    }
    @Override
    public void onOtpCompleted(String otp) {
        txtConfirmPin = Integer.parseInt(confirmPin.getText().toString());
        if (txtConfirmPin == txtCreatePin){
            Preferences.setValue(KYC_PinConfirmActivity.this, Preferences.PROFILE_PASSWORD, txtConfirmPin);
            faceLiveness = true;
            userProfile = true;
            Intent intent_liveness = new Intent(KYC_PinConfirmActivity.this, FaceActivity.class);
            intent_liveness.putExtra("faceLiveness", faceLiveness);
            intent_liveness.putExtra("userProfile", userProfile);
            startActivity(intent_liveness);
        }else {
            Toast.makeText(this, "The Pin Code is not matched, Please try again", Toast.LENGTH_SHORT).show();
            confirmPin.setText("");
        }
    }
}