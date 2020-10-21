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

public class KYC_Setting_Name extends AppCompatActivity {
    private EditText txtFistName,txtLastName;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_name);
        BackgroundService.mContext = KYC_Setting_Name.this;
        init();
        onSave();
        String firstName = Preferences.getValue_String(KYC_Setting_Name.this, Preferences.PROFILE_FIRSTNAME);
        String lastName = Preferences.getValue_String(KYC_Setting_Name.this, Preferences.PROFILE_LASTNAME);
        if (firstName !=null && lastName != null){
            txtFistName.setText(firstName);
            txtLastName.setText(lastName);
        }
    }
    private void init(){
        txtFistName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        btnSave = findViewById(R.id.btnSave);

    }
    private void onSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = txtFistName.getText().toString();
                String lastName = txtLastName.getText().toString();
                if (firstName.isEmpty()) {
                    Common.showAlert(KYC_Setting_Name.this, "Warning", "Please enter First Name.");
                    return;
                }
                if (lastName.isEmpty()) {
                    Common.showAlert(KYC_Setting_Name.this, "Warning", "Please enter Last Name.");
                    return;
                }
                Preferences.setValue(KYC_Setting_Name.this, Preferences.PROFILE_FIRSTNAME, firstName);
                Preferences.setValue(KYC_Setting_Name.this, Preferences.PROFILE_LASTNAME, lastName);
                Intent intent = new Intent(KYC_Setting_Name.this,KYC_ProfileSetting.class);
                startActivity(intent);
            }
        });

    }
}