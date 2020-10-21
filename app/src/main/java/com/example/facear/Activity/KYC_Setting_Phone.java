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
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class KYC_Setting_Phone extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText txtPhoneNumber;
    private Button btnSave;
    private String countrycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_phone);
        BackgroundService.mContext = KYC_Setting_Phone.this;
        init();
        onCountryPickerClick();
        onSave();
        String phone = Preferences.getValue_String(KYC_Setting_Phone.this, Preferences.PROFILE_PHONENUMBER);
        if (phone != null){
            txtPhoneNumber.setText(phone);
        }
    }
    private void init(){
        ccp = findViewById(R.id.ccp);
        txtPhoneNumber = findViewById(R.id.txtPhoneNum);
        btnSave = findViewById(R.id.btnSave);
        ccp.registerPhoneNumberTextView(txtPhoneNumber);
        countrycode = ccp.getSelectedCountryCodeWithPlus();
    }
    private void onCountryPickerClick(){
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                countrycode = ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }
    private void onSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = countrycode+" "+txtPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty()) {
                    Common.showAlert(KYC_Setting_Phone.this, "Warning", "Please enter Phone Number.");
                    return;
                }
                Preferences.setValue(KYC_Setting_Phone.this, Preferences.PROFILE_PHONENUMBER, phoneNumber);
                Intent intent = new Intent(KYC_Setting_Phone.this,KYC_ProfileSetting.class);
                startActivity(intent);

            }
        });
    }
}