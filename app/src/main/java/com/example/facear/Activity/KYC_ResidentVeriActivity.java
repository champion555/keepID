package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KYC_ResidentVeriActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgFrontResident,imgBackResident,imgMerge,btnBack;
    private TextView txtFrontResident,txtBackResident;
    private RelativeLayout mergeRelative;
    private ScrollView captureView;
    private Button btnVerify;
    int width;
    private LoadingDialog loadingDialog;
    Bitmap frontbitmap,backbitmap;
    String mergedPhotoFileName,mergedPhotoFilePath;
    File mergedPhotoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_residential);
        BackgroundService.mContext = KYC_ResidentVeriActivity.this;
        init();
        Listener();
        String IDCardPath = Preferences.getValue_String(KYC_ResidentVeriActivity.this, Preferences.PROFILE_RESIDENT_DOCPATH);
        if (IDCardPath.isEmpty()){
            captureView.setVisibility(View.VISIBLE);
            mergeRelative.setVisibility(View.GONE);
        }else if (IDCardPath != null){
            mergeRelative.setVisibility(View.VISIBLE);
            captureView.setVisibility(View.GONE);
            Uri idCardUri = Uri.parse(IDCardPath);
            imgMerge.setImageURI(idCardUri);
        }
        String frontPath = AppConstants.ResidentVeri_frontCardPath;
        String backPath = AppConstants.ResidentVeri_backCardPath;
        if (frontPath.isEmpty()){
            imgFrontResident.setImageResource(R.drawable.ic_idcard_front);
            txtFrontResident.setText("Capture Front Residential Permit");
        }else if (frontPath != null){
            frontbitmap = BitmapFactory.decodeFile(frontPath);
            imgFrontResident.setImageBitmap(frontbitmap);
            txtFrontResident.setText("Edit Front Resident Permit");
        }
        if (backPath.isEmpty()){
            imgBackResident.setImageResource(R.drawable.ic_idcard_back);
            txtBackResident.setText("Capture Back Residential Permit");
        }else if (backPath != null){
            backbitmap = BitmapFactory.decodeFile(backPath);
            imgBackResident.setImageBitmap(backbitmap);
            txtBackResident.setText("Edit Back Residential Permit");
        }
    }
    private void Listener(){
        imgFrontResident.setOnClickListener(this);
        imgBackResident.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        imgMerge.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    private void init (){
        imgFrontResident = findViewById(R.id.imgFrontResident);
        imgBackResident = findViewById(R.id.imgBackResident);
        imgMerge = findViewById(R.id.mergeImage);
        mergeRelative = findViewById(R.id.mergeRel);
        captureView = findViewById(R.id.captureView);
        txtFrontResident = findViewById(R.id.txtFrontResident);
        txtBackResident = findViewById(R.id.txtBackResident);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);

    }
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        if(firstImage.getWidth() < secondImage.getWidth()){
            width = firstImage.getWidth();
        }else {
            width = secondImage.getWidth();
        }
        Bitmap resultPhoto = Bitmap.createBitmap(width, firstImage.getHeight()+secondImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(resultPhoto);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 0f, firstImage.getHeight(), null);
        mergedPhotoFileName = "profile_mergedResident.jpg";
        mergedPhotoFilePath = KYC_ResidentVeriActivity.this.getFilesDir().getPath().toString() + "/" + mergedPhotoFileName;
        mergedPhotoFile = new File(mergedPhotoFilePath);
//        mergeImage.setImageBitmap(resultPhoto);
        if (mergedPhotoFile.exists())
            mergedPhotoFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(mergedPhotoFile);
            Log.e("out", String.valueOf(out));
            resultPhoto.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            AuthenticationToServer();
//            Intent intent = new Intent(KYC_ResidentVeriActivity.this,KYC_Setting_IDMain.class);
//            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultPhoto;
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(KYC_ResidentVeriActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(KYC_ResidentVeriActivity.this, Preferences.API_SecretKey);
        loadingDialog = new LoadingDialog(KYC_ResidentVeriActivity.this, false);
        if (CoreApp.isNetworkConnection(KYC_ResidentVeriActivity.this)) {
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
                        Toast.makeText(KYC_ResidentVeriActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(KYC_ResidentVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        } else {
            loadingDialog.hide();
            Toast.makeText(KYC_ResidentVeriActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }
    private void VerificationIDCardToServer() {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"),
                mergedPhotoFile);
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
                    Preferences.setValue(KYC_ResidentVeriActivity.this, Preferences.PROFILE_RESIDENT_DOCPATH, mergedPhotoFilePath);
                    Preferences.setValue(KYC_ResidentVeriActivity.this, Preferences.PROFILE_RESIDENTVERIFY,"true" );
                    Intent intent = new Intent(KYC_ResidentVeriActivity.this,KYC_IDCardVeriResultActivity.class);
                    intent.putExtra("IDCardType","Resident Permit");
                    startActivity(intent);
                } else {
                    loadingDialog.hide();
                    Toast.makeText(KYC_ResidentVeriActivity.this, "Bad input MRZ image!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IDCardVeriResponse> call, Throwable t) {
                Toast.makeText(KYC_ResidentVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgFrontResident:
                Intent intent_front = new Intent(KYC_ResidentVeriActivity.this, IDDocCameraActivity.class);
                intent_front.putExtra("CardType","Profile_FrontResident");
                startActivity(intent_front);
                break;
            case R.id.imgBackResident:
                Intent intent_back = new Intent(KYC_ResidentVeriActivity.this, IDDocCameraActivity.class);
                intent_back.putExtra("CardType","Profile_BackResient");
                startActivity(intent_back);
                break;
            case R.id.mergeImage:
                mergeRelative.setVisibility(View.GONE);
                captureView.setVisibility(View.VISIBLE);
                Preferences.setValue(KYC_ResidentVeriActivity.this, Preferences.PROFILE_RESIDENT_DOCPATH, null);
                break;
            case  R.id.btnVerify:
                createSingleImageFromMultipleImages(frontbitmap,backbitmap);
                break;
            case R.id.btnBack:
                Intent intent = new Intent(KYC_ResidentVeriActivity.this,KYC_Setting_IDMain.class);
                startActivity(intent);
        }
    }
}