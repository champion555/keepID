package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Common;
import com.example.facear.Utils.Preferences;
import com.jdev.countryutil.Constants;
import com.jdev.countryutil.CountryUtil;

public class KYC_Setting_Address extends AppCompatActivity {
    private RelativeLayout countryRel;
    private ImageView imgCountryFlag;
    private TextView txtCountryName;
    private EditText txtCity,txtStreet1,txtStreet2,txtRegion,txtPostCode;
    private Button btnSave;
    String countryName;
    int countryFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_address);
        BackgroundService.mContext = KYC_Setting_Address.this;
        init();
        onSave();
        onSelectCountry();

        String flag_string = Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_COUNTRYFLAG);
        if (flag_string != null){
            try{
                int flag = Integer.parseInt(flag_string);
                imgCountryFlag.setImageResource(flag);
                txtCountryName.setText(Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_COUNTRYNAME));
                txtCity.setText(Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_CITY));
                txtStreet1.setText(Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_STREET1));
                txtStreet2.setText(Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_STREET2));
                txtRegion.setText(Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_REGION));
                txtPostCode.setText(Preferences.getValue_String(KYC_Setting_Address.this, Preferences.PROFILE_POSTCODE));
            } catch(NumberFormatException ex){}

        }
    }
    private void init(){
        countryRel = findViewById(R.id.countryView);
        imgCountryFlag = findViewById(R.id.imgFlag);
        txtCountryName = findViewById(R.id.txtCountryName);
        txtCity = findViewById(R.id.txtCity);
        txtStreet1 = findViewById(R.id.txtStreet1);
        txtStreet2 = findViewById(R.id.txtStreet2);
        txtRegion = findViewById(R.id.txtRegion);
        txtPostCode = findViewById(R.id.txtPostCode);
        btnSave = findViewById(R.id.btnSave);
    }
    private void onSelectCountry(){
        countryRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountryUtil(KYC_Setting_Address.this).setTitle("For Address write: Select Country").build();
            }
        });
    }
    private void onSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryName = txtCountryName.getText().toString();
                String city = txtCity.getText().toString();
                String street1 = txtStreet1.getText().toString();
                String street2 = txtStreet2.getText().toString();
                String region = txtRegion.getText().toString();
                String poscode = txtPostCode.getText().toString();
                if (countryName.isEmpty()) {
                    Common.showAlert(KYC_Setting_Address.this, "Warning", "Please select your Country.");
                    return;
                }
                if (city.isEmpty()) {
                    Common.showAlert(KYC_Setting_Address.this, "Warning", "Please enter your City.");
                    return;
                }
                if (street1.isEmpty()) {
                    Common.showAlert(KYC_Setting_Address.this, "Warning", "Please enter your Street1.");
                    return;
                }
                if (street2.isEmpty()) {
                    Common.showAlert(KYC_Setting_Address.this, "Warning", "Please enter your Street2.");
                    return;
                }
                if (region.isEmpty()) {
                    Common.showAlert(KYC_Setting_Address.this, "Warning", "Please enter your region/state.");
                    return;
                }
                if (poscode.isEmpty()) {
                    Common.showAlert(KYC_Setting_Address.this, "Warning", "Please enter your PostalCode.");
                    return;
                }
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_COUNTRYNAME, countryName);
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_CITY, city);
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_STREET1, street1);
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_STREET2, street2);
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_REGION, region);
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_POSTCODE, poscode);
                Preferences.setValue(KYC_Setting_Address.this, Preferences.PROFILE_COUNTRYFLAG,Integer.toString(countryFlag));

                Intent intent = new Intent(KYC_Setting_Address.this,KYC_ProfileSetting.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.KEY_RESULT_CODE) {
            try {
                countryName = data.getStringExtra(Constants.KEY_COUNTRY_NAME);
                countryFlag = data.getIntExtra(Constants.KEY_COUNTRY_FLAG, 0);
                imgCountryFlag.setImageResource(countryFlag);
                txtCountryName.setText(countryName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}