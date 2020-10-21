package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facear.Models.AuthenticationResponse;
import com.example.facear.Models.CheckMailResponse;
import com.example.facear.Models.CheckPhoneNumResponse;
import com.example.facear.Models.SendPhoneNumResponse;
import com.example.facear.R;
import com.example.facear.Services.RetrofitClient;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.CoreApp;
import com.example.facear.Utils.LoadingDialog;
import com.example.facear.Utils.Preferences;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYC_PhoneVerificationActivity extends AppCompatActivity implements View.OnClickListener,OnOtpCompletionListener{
    private Button btnValidation;
    private TextView btnResend;
    private OtpView otpView;
    private TextView txtPhoneNum;
    private ImageView btnshow;
    private LoadingDialog loadingDialog;
    Bundle bundle;
    String txtPhoneNumber,jobId;
    boolean isShow = false,isFullNum = false,isSend = false, isValidate = false;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        initializeUi();
        setListener();

        bundle = getIntent().getExtras();
        txtPhoneNumber = bundle.getString("txtPhoneNumber");
        jobId = bundle.getString("jobId");
        txtPhoneNum.setText(txtPhoneNumber);

    }
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnValidation:
                if (isFullNum){
                    isValidate = true;
                    AuthenticationToServer();
                }else {
                    Toast.makeText(this, "Please Enter PinCode fully", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnReSend:
                isSend = true;
                AuthenticationToServer();
                break;
            case R.id.btnShow:
                if (isShow == false) {
                    btnshow.setImageResource(R.drawable.ic_show);
                    otpView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    isShow = true;
                } else {
                    btnshow.setImageResource(R.drawable.ic_hide);
                    otpView.setInputType(InputType.TYPE_CLASS_NUMBER);
                    isShow = false;
                }
                break;
            }

    }
    private void initializeUi() {
        otpView = findViewById(R.id.otp_view);
        btnValidation = findViewById(R.id.btnValidation);
        btnResend = findViewById(R.id.btnReSend);
        btnshow = findViewById(R.id.btnShow);
        txtPhoneNum = findViewById(R.id.txtPhoneNum);
    }
    private void setListener(){
        btnValidation.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        otpView.setOtpCompletionListener(this);
        btnshow.setOnClickListener(this);
    }

    @Override public void onOtpCompleted(String otp) {
        isFullNum = true;
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(KYC_PhoneVerificationActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(KYC_PhoneVerificationActivity.this, Preferences.API_SecretKey);
        loadingDialog = new LoadingDialog(KYC_PhoneVerificationActivity.this, false);
        if (CoreApp.isNetworkConnection(KYC_PhoneVerificationActivity.this)) {
            Call<AuthenticationResponse> call = RetrofitClient
                    .getInstance().getApi().authenticateCheck(api_key, secret_key);
            call.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    AuthenticationResponse authenticationResponse = response.body();
                    int statuscode = Integer.parseInt(authenticationResponse.getStatusCode());
                    if (statuscode == 200) {
                        AppConstants.api_token = authenticationResponse.getApi_access_token();
                        if (isSend){
                            isSend = false;
                            sendPhoneNumToServer();
                        }else if (isValidate){
                            isValidate = false;
                            validatePhoneToServer();
                        }
                    } else {
                        Toast.makeText(KYC_PhoneVerificationActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(KYC_PhoneVerificationActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        } else {
            loadingDialog.hide();
            Toast.makeText(KYC_PhoneVerificationActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }
    private void validatePhoneToServer() {
        final String otpCode = otpView.getText().toString();
        Call<CheckPhoneNumResponse> call = RetrofitClient
                .getInstance().getApi().checkPhone(jobId, otpCode);
        call.enqueue(new Callback<CheckPhoneNumResponse>() {
            @Override
            public void onResponse(Call<CheckPhoneNumResponse> call, Response<CheckPhoneNumResponse> response) {
                loadingDialog.hide();
                CheckPhoneNumResponse checkPhoneNumResponse = response.body();
                int otp = Integer.parseInt(checkPhoneNumResponse.getOtp());
                if (otp == Integer.parseInt(otpCode)) {
                    Preferences.setValue(KYC_PhoneVerificationActivity.this, Preferences.PROFILE_PHONENUMBER,txtPhoneNumber );
                    Preferences.setValue(KYC_PhoneVerificationActivity.this, Preferences.PROFILE_PHONEVERIFY,"true" );
                    Intent intent = new Intent(KYC_PhoneVerificationActivity.this, KYC_PinCreateActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(KYC_PhoneVerificationActivity.this, checkPhoneNumResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CheckPhoneNumResponse> call, Throwable t) {
                Toast.makeText(KYC_PhoneVerificationActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });
    }
    private void sendPhoneNumToServer() {
        String language = "en";
        Call<SendPhoneNumResponse> call = RetrofitClient
                .getInstance().getApi().sendPhone(txtPhoneNumber, language);
        call.enqueue(new Callback<SendPhoneNumResponse>() {
            @Override
            public void onResponse(Call<SendPhoneNumResponse> call, Response<SendPhoneNumResponse> response) {
                loadingDialog.hide();
                SendPhoneNumResponse sendPhoneNumResponse = response.body();
                int statuscode = Integer.parseInt(sendPhoneNumResponse.getStatusCode());
                if (statuscode == 200) {
                    jobId = sendPhoneNumResponse.getJob_id();
                } else {
                    Toast.makeText(KYC_PhoneVerificationActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SendPhoneNumResponse> call, Throwable t) {
                Toast.makeText(KYC_PhoneVerificationActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });
    }

}