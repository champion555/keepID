package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facear.Models.AuthenticationResponse;
import com.example.facear.Models.CheckMailResponse;
import com.example.facear.Models.SendMailResponse;
import com.example.facear.R;
import com.example.facear.Services.RetrofitClient;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Common;
import com.example.facear.Utils.CoreApp;
import com.example.facear.Utils.LoadingDialog;
import com.example.facear.Utils.Preferences;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYC_EmailVeriActivity extends AppCompatActivity implements View.OnClickListener, OnOtpCompletionListener {
    private Button btnVerify,btnCancel,btnValidate;
    private EditText txtEmail;
    private LinearLayout emailSendLinear;
    private TextView txtTitle,btnReSend,txtSecond;
    private ImageView btnShow;
    private OtpView otpView;
    private RelativeLayout verifyRelative;
    boolean isshow = false;
    LoadingDialog loadingDialog;
    String email;
    String jobId;
    boolean isSendMail = false,isValidate = false,isFullCode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_email_veri);
        init();
        listener();
        emailSendLinear.setVisibility(View.VISIBLE);
        verifyRelative.setVisibility(View.GONE);
    }
    private void init(){
        btnVerify = findViewById(R.id.btnEmailVerify);
        btnCancel = findViewById(R.id.btnCancel);
        txtEmail = findViewById(R.id.textVeriEmail);
        emailSendLinear = findViewById(R.id.emailSendLinear);
        txtTitle = findViewById(R.id.textEmail);
        otpView = findViewById(R.id.otp_view);
        btnShow = findViewById(R.id.btnShow);
        btnValidate = findViewById(R.id.btnValidation);
        btnReSend = findViewById(R.id.btnReSend);
        verifyRelative = findViewById(R.id.verifyRelative);
        txtSecond = findViewById(R.id.txtSecond);
    }
    private void listener(){
        btnVerify.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnValidate.setOnClickListener(this);
        btnReSend.setOnClickListener(this);
        otpView.setOtpCompletionListener(this);
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(KYC_EmailVeriActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(KYC_EmailVeriActivity.this, Preferences.API_SecretKey);
        loadingDialog = new LoadingDialog(KYC_EmailVeriActivity.this, false);
        if (CoreApp.isNetworkConnection(KYC_EmailVeriActivity.this)) {
            Call<AuthenticationResponse> call = RetrofitClient
                    .getInstance().getApi().authenticateCheck(api_key, secret_key);
            call.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    AuthenticationResponse authenticationResponse = response.body();
                    int statuscode = Integer.parseInt(authenticationResponse.getStatusCode());
                    if (statuscode == 200) {
                        AppConstants.api_token = authenticationResponse.getApi_access_token();
                        if (isSendMail){
                            isSendMail = false;
                            sendMailToServer();
                        }
                        if (isValidate){
                            isValidate = false;
                            if (isFullCode){
                                validateMailToServer();
                            }else {
                                Toast.makeText(KYC_EmailVeriActivity.this, "you must enter the code fully", Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        Toast.makeText(KYC_EmailVeriActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(KYC_EmailVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        } else {
            loadingDialog.hide();
            Toast.makeText(KYC_EmailVeriActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }
    private void sendMailToServer() {
        String language = "en";
        Call<SendMailResponse> call = RetrofitClient
                .getInstance().getApi().sendMail(email, language);
        call.enqueue(new Callback<SendMailResponse>() {
            @Override
            public void onResponse(Call<SendMailResponse> call, Response<SendMailResponse> response) {
                loadingDialog.hide();
                SendMailResponse sendMailResponse = response.body();
                jobId = sendMailResponse.getJob_id();
                int statuscode = Integer.parseInt(sendMailResponse.getStatusCode());
                if (statuscode == 200) {
                    emailSendLinear.setVisibility(View.GONE);
                    verifyRelative.setVisibility(View.VISIBLE);
                    txtTitle.setText(email);
//                    new CountDownTimer(30000, 1000) {
//                        public void onTick(long millisUntilFinished) {
//                            txtSecond.setText(" " + millisUntilFinished / 1000);
//                            //here you can have your logic to set text to edittext
//                        }
//                        public void onFinish() {
//                            txtSecond.setText("done!");
//                        }
//                    }.start();
                } else {
                    Toast.makeText(KYC_EmailVeriActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<SendMailResponse> call, Throwable t) {
                Toast.makeText(KYC_EmailVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });
    }
    private void validateMailToServer() {
        final String otpCode = otpView.getText().toString();
        Call<CheckMailResponse> call = RetrofitClient
                .getInstance().getApi().checkMail(jobId, otpCode);
        call.enqueue(new Callback<CheckMailResponse>() {
            @Override
            public void onResponse(Call<CheckMailResponse> call, Response<CheckMailResponse> response) {
                loadingDialog.hide();
                CheckMailResponse checkMailResponse = response.body();
                String status = checkMailResponse.getStatus();
                int statuscode = Integer.parseInt(checkMailResponse.getStatusCode());
                int otp = Integer.parseInt(checkMailResponse.getOtp());
                if (otp == Integer.parseInt(otpCode)) {
                    Preferences.setValue(KYC_EmailVeriActivity.this, Preferences.PROFILE_USEREMAIL,email );
                    Preferences.setValue(KYC_EmailVeriActivity.this, Preferences.PROFILE_EMAILVERIFY,"true" );
                    Intent intent = new Intent(KYC_EmailVeriActivity.this,KYC_PhoneNumSendActivity.class);
//                    Intent intent = new Intent(KYC_EmailVeriActivity.this,KYC_PinCreateActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(KYC_EmailVeriActivity.this, checkMailResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<CheckMailResponse> call, Throwable t) {
                Toast.makeText(KYC_EmailVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });
    }
    @Override
    public void onClick(View v) {
        email = txtEmail.getText().toString();
        switch (v.getId()){
            case R.id.btnEmailVerify:
                if (email.isEmpty()) {
                    Common.showAlert(KYC_EmailVeriActivity.this, "Warning", "Please enter Email.");
                    return;
                }
                if(!Common.isValidEmail(email)) {
                    Common.showAlert(KYC_EmailVeriActivity.this, "Warning", "Invalid Email");
                    return;
                }
                isSendMail = true;
                AuthenticationToServer();
                break;
            case R.id.btnCancel:
                Intent i = new Intent(KYC_EmailVeriActivity.this,KYC_MainActivity.class);
                startActivity(i);
                break;
            case R.id.btnShow:
                if (isshow == false) {
                    btnShow.setImageResource(R.drawable.ic_show);
                    otpView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    isshow = true;
                } else {
                    btnShow.setImageResource(R.drawable.ic_hide);
                    otpView.setInputType(InputType.TYPE_CLASS_NUMBER);
                    isshow = false;
                }

                break;
            case R.id.btnValidation:
                isValidate = true;
                AuthenticationToServer();
                break;
            case R.id.btnReSend:
                isSendMail = true;
                AuthenticationToServer();
                break;
        }
    }

    @Override
    public void onOtpCompleted(String otp) {
       isFullCode = true;
    }
}