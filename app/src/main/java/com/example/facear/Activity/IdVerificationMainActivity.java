package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.jdev.countryutil.Constants;
import com.jdev.countryutil.CountryUtil;

public class IdVerificationMainActivity extends AppCompatActivity {
    LinearLayout btnPassport,btnIdCard;
    String countryName;
    int countryFlag;
    boolean isIDCard = false;
    boolean isPassport = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_verification_main);
        BackgroundService.mContext = IdVerificationMainActivity.this;
        initialize();
        onPassport();
        onIdCard();
    }
    private void initialize(){
        btnPassport = findViewById(R.id.btnPassport);
        btnIdCard = findViewById(R.id.btnIdcard);
    }
    private void onPassport(){
        btnPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View var1) {
                isPassport = true;
                new CountryUtil(IdVerificationMainActivity.this).setTitle("Select Issuer Country").build();
            }
        });
    }
    private void onIdCard(){
        btnIdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View var1) {
                isIDCard = true;
                new CountryUtil(IdVerificationMainActivity.this).setTitle("Select Issuer Country").build();
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
                if (isIDCard){
                    isIDCard = false;
                    Intent i = new Intent(IdVerificationMainActivity.this, KYC_IDCardVeriActivity.class);
                    i.putExtra("countryName", countryName);
                    i.putExtra("countryFlag", countryFlag);
                    startActivity(i);
                }else if (isPassport){
                    isPassport = false;
                    Intent intent = new Intent(IdVerificationMainActivity.this, KYC_PassportVeriActivity.class);
                    intent.putExtra("countryName", countryName);
                    intent.putExtra("countryFlag", countryFlag);
                    startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}