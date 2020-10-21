package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Common;
import com.example.facear.Utils.Preferences;

public class KYC_Setting_Email extends AppCompatActivity {
    private EditText txtEmail;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_email);
        BackgroundService.mContext = KYC_Setting_Email.this;
        init();
        onSave();
    }
    private void init(){
        txtEmail = findViewById(R.id.txtEmail);
        btnSave = findViewById(R.id.btnSave);
//        txtEmail.setText(Preferences.getValue_String(KYC_Setting_Email.this, Preferences.PROFILE_USEREMAIL));
    }
    private void onSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                if (email.isEmpty()) {
                    Common.showAlert(KYC_Setting_Email.this, "Warning", "Please enter Email Address.");
                    return;
                }
                if(!Common.isValidEmail(email)) {
                    Common.showAlert(KYC_Setting_Email.this, "Warning", "Invalid Email");
                    return;
                }
                Preferences.setValue(KYC_Setting_Email.this, Preferences.PROFILE_USEREMAIL, email);
                Intent intent = new Intent(KYC_Setting_Email.this,KYC_ProfileSetting.class);
                startActivity(intent);
            }
        });
    }
}