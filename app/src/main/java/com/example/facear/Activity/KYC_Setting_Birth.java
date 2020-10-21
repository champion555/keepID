package com.example.facear.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Common;
import com.example.facear.Utils.Preferences;

import java.util.Calendar;

public class KYC_Setting_Birth extends AppCompatActivity {
    private TextView txtBirth;
    private Button btnSave;
    private DatePickerDialog pickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_birth);
        BackgroundService.mContext = KYC_Setting_Birth.this;
        init();
        onSave();
        onSelectDate();
        String birth = Preferences.getValue_String(KYC_Setting_Birth.this, Preferences.PROFILE_BIRTHDAY);
        if (birth != null){
            txtBirth.setText(birth);
        }
    }
    private void init(){
        txtBirth = findViewById(R.id.txtBirth);
        btnSave = findViewById(R.id.btnSave);
    }
    private void onSelectDate(){
        txtBirth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                pickerDialog = new DatePickerDialog(KYC_Setting_Birth.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                      txtBirth.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                pickerDialog.show();
            }
        });
    }
    private void onSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birthDay = txtBirth.getText().toString();
                if (birthDay.isEmpty()) {
                    Common.showAlert(KYC_Setting_Birth.this, "Warning", "Please select your BirthDay.");
                    return;
                }
                Preferences.setValue(KYC_Setting_Birth.this, Preferences.PROFILE_BIRTHDAY, birthDay);
                Intent intent = new Intent(KYC_Setting_Birth.this,KYC_ProfileSetting.class);
                startActivity(intent);
            }
        });

    }
}