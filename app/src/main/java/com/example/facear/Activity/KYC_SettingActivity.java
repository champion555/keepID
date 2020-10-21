package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.facear.R;
import com.example.facear.Utils.Preferences;

public class KYC_SettingActivity extends AppCompatActivity {
    private EditText txtUserSession;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting);
        init();
        onSave();
        txtUserSession.setText(Preferences.getValue_String(KYC_SettingActivity.this, Preferences.user_session));
    }
    private void init(){
        txtUserSession = findViewById(R.id.txtUserSession);
        btnSave = findViewById(R.id.btSettingSave);
    }
    private void onSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.setValue(KYC_SettingActivity.this, Preferences.user_session, txtUserSession.getText().toString());
                Intent intent = new Intent(KYC_SettingActivity.this, KYC_MainActivity.class);
                startActivity(intent);
            }
        });
    }
}