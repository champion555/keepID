package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.Models.AuthenticationResponse;
import com.example.facear.Models.IDCardVeriResponse;
import com.example.facear.R;
import com.example.facear.Services.RetrofitClient;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.CoreApp;
import com.example.facear.Utils.LoadingDialog;
import com.example.facear.Utils.Preferences;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYC_PassportVeriActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgPassport,btnBack;
    private Button btnVerify;
    private TextView txtPassport;
    String passportPath;
    File passportFile;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_passport_veri);
        BackgroundService.mContext = KYC_PassportVeriActivity.this;
        init();
//        String passportPath = AppConstants.PassportVeriPath;
        String passportPath = Preferences.getValue_String(KYC_PassportVeriActivity.this, Preferences.PROFILE_PASSPORTPATH);
        if (passportPath.isEmpty()){
            imgPassport.setImageResource(R.drawable.ic_passport);
            txtPassport.setText("Capture Passport");
        }else if (passportPath != null){
            Uri passportUri = Uri.parse(passportPath);
            imgPassport.setImageURI(passportUri);
//            passportBitmap = BitmapFactory.decodeFile(passportPath);
//            imgPassport.setImageBitmap(passportBitmap);
            txtPassport.setText("Edit Passport");
            passportFile = new  File(passportPath);
        }
        imgPassport.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    private void init(){
        imgPassport = findViewById(R.id.passportImg);
        btnVerify = findViewById(R.id.btnVerify);
        txtPassport = findViewById(R.id.txtPassport);
        btnBack = findViewById(R.id.btnBack);
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(KYC_PassportVeriActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(KYC_PassportVeriActivity.this, Preferences.API_SecretKey);
        loadingDialog = new LoadingDialog(KYC_PassportVeriActivity.this, false);
        if (CoreApp.isNetworkConnection(KYC_PassportVeriActivity.this)) {
            Call<AuthenticationResponse> call = RetrofitClient
                    .getInstance().getApi().authenticateCheck(api_key, secret_key);
            call.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    AuthenticationResponse authenticationResponse = response.body();
                    int statuscode = Integer.parseInt(authenticationResponse.getStatusCode());
                    if (statuscode == 200) {
                        AppConstants.api_token = authenticationResponse.getApi_access_token();
                        VerificationIDCardToServer();
                    } else {
                        Toast.makeText(KYC_PassportVeriActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(KYC_PassportVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        } else {
            loadingDialog.hide();
            Toast.makeText(KYC_PassportVeriActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }
    private void VerificationIDCardToServer() {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"),
                passportFile);
        Call<IDCardVeriResponse> call = RetrofitClient
                .getInstance().getApi().IDCardVerification(reqFile);
        call.enqueue(new Callback<IDCardVeriResponse>() {
            @Override
            public void onResponse(Call<IDCardVeriResponse> call, Response<IDCardVeriResponse> response) {
                IDCardVeriResponse idCardVeriResponse = response.body();
                String status = idCardVeriResponse.getStatus();
                if (status.equals("SUCCESS")) {
                    loadingDialog.hide();
                    AppConstants.idCardVeriResponse = idCardVeriResponse;
                    Preferences.setValue(KYC_PassportVeriActivity.this, Preferences.PROFILE_PASSPORTVERIFY,"true" );
                    Intent intent = new Intent(KYC_PassportVeriActivity.this,KYC_IDCardVeriResultActivity.class);
                    intent.putExtra("IDCardType","Passport");
                    startActivity(intent);
                } else {
                    loadingDialog.hide();
                    Toast.makeText(KYC_PassportVeriActivity.this, "Bad input MRZ image!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IDCardVeriResponse> call, Throwable t) {
                Toast.makeText(KYC_PassportVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.passportImg:
                Intent intent_passport = new Intent(KYC_PassportVeriActivity.this, IDDocCameraActivity.class);
                intent_passport.putExtra("CardType","KYC_Passport");
                startActivity(intent_passport);
                break;
            case R.id.btnVerify:
//                Intent intent = new Intent(KYC_PassportVeriActivity.this,KYC_Setting_IDMain.class);
//                startActivity(intent);
                AuthenticationToServer();
                break;
            case R.id.btnBack:
                Intent intent_back = new Intent(KYC_PassportVeriActivity.this, KYC_Setting_IDMain.class);
                startActivity(intent_back);
                break;
        }
    }
}