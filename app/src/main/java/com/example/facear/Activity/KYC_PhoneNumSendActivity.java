package com.example.facear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.facear.Models.AuthenticationResponse;
import com.example.facear.Models.SendMailResponse;
import com.example.facear.Models.SendPhoneNumResponse;
import com.example.facear.R;
import com.example.facear.Services.RetrofitClient;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Common;
import com.example.facear.Utils.CoreApp;
import com.example.facear.Utils.LoadingDialog;
import com.example.facear.Utils.Preferences;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYC_PhoneNumSendActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText txtPhoneNumber;
    private Button btnSend;
    private String countrycode,phoneNumber,jobId;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_phonenum_send);
        init();
        onCountryPickerClick();
        onSend();
    }
    private void init(){
        ccp = findViewById(R.id.ccp);
        txtPhoneNumber = findViewById(R.id.txtPhoneNum);
        btnSend = findViewById(R.id.btnSave);
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
    private void onSend(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = countrycode+txtPhoneNumber.getText().toString();
                if (txtPhoneNumber.getText().toString().isEmpty()) {
                    Common.showAlert(KYC_PhoneNumSendActivity.this, "Warning", "Please enter Phone Number.");
                    return;
                }
                AuthenticationToServer();
            }
        });
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(KYC_PhoneNumSendActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(KYC_PhoneNumSendActivity.this, Preferences.API_SecretKey);
        loadingDialog = new LoadingDialog(KYC_PhoneNumSendActivity.this, false);
        if (CoreApp.isNetworkConnection(KYC_PhoneNumSendActivity.this)) {
            Call<AuthenticationResponse> call = RetrofitClient
                    .getInstance().getApi().authenticateCheck(api_key, secret_key);
            call.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    AuthenticationResponse authenticationResponse = response.body();
                    int statuscode = Integer.parseInt(authenticationResponse.getStatusCode());
                    if (statuscode == 200) {
                        AppConstants.api_token = authenticationResponse.getApi_access_token();
                        sendPhoneNumToServer();
                    } else {
                        Toast.makeText(KYC_PhoneNumSendActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(KYC_PhoneNumSendActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        } else {
            loadingDialog.hide();
            Toast.makeText(KYC_PhoneNumSendActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPhoneNumToServer() {
        String language = "en";
        Call<SendPhoneNumResponse> call = RetrofitClient
                .getInstance().getApi().sendPhone(phoneNumber, language);
        call.enqueue(new Callback<SendPhoneNumResponse>() {
            @Override
            public void onResponse(Call<SendPhoneNumResponse> call, Response<SendPhoneNumResponse> response) {
                loadingDialog.hide();
                SendPhoneNumResponse sendPhoneNumResponse = response.body();
                jobId = sendPhoneNumResponse.getJob_id();
                int statuscode = Integer.parseInt(sendPhoneNumResponse.getStatusCode());
                if (statuscode == 200) {
                    Intent intent = new Intent(KYC_PhoneNumSendActivity.this, KYC_PhoneVerificationActivity.class);
                    intent.putExtra("jobId",jobId);
                    intent.putExtra("txtPhoneNumber", phoneNumber);
                    startActivity(intent);
                } else {
                    Toast.makeText(KYC_PhoneNumSendActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SendPhoneNumResponse> call, Throwable t) {
                Toast.makeText(KYC_PhoneNumSendActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });
    }
}