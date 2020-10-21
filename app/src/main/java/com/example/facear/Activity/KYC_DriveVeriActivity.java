package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

public class KYC_DriveVeriActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgFrontDriving,imgBackDriving,imgMerge,btnBack;
    private TextView txtFrontDriving,txtBackDriving;
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
        setContentView(R.layout.activity_kyc_drive_veri);
        BackgroundService.mContext = KYC_DriveVeriActivity.this;
        init();
        Listener();
        String DrivingPath = Preferences.getValue_String(KYC_DriveVeriActivity.this, Preferences.PROFILE_DRIVINGPATH);
        if (DrivingPath.isEmpty()){
            captureView.setVisibility(View.VISIBLE);
            mergeRelative.setVisibility(View.GONE);
        }else if (DrivingPath != null){
            mergeRelative.setVisibility(View.VISIBLE);
            captureView.setVisibility(View.GONE);
            Uri drivingUri = Uri.parse(DrivingPath);
            imgMerge.setImageURI(drivingUri);
        }
        String frontPath = AppConstants.DrivingVeri_frontCardPath;
        String backPath = AppConstants.DrivingVeri_backCardPath;
        if (frontPath.isEmpty()){
            imgFrontDriving.setImageResource(R.drawable.ic_license_front);
            txtFrontDriving.setText("Capture Front Driving License");
        }else if (frontPath != null){
            frontbitmap = BitmapFactory.decodeFile(frontPath);
            imgFrontDriving.setImageBitmap(frontbitmap);
            txtFrontDriving.setText("Edit Front Driving License");
        }
        if (backPath.isEmpty()){
            imgBackDriving.setImageResource(R.drawable.ic_license_back);
            txtBackDriving.setText("Capture Back Driving License");
        }else if (backPath != null){
            backbitmap = BitmapFactory.decodeFile(backPath);
            imgBackDriving.setImageBitmap(backbitmap);
            txtBackDriving.setText("Edit Back Driving License");
        }
    }
    private void Listener(){
        imgFrontDriving.setOnClickListener(this);
        imgBackDriving.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        imgMerge.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    private void init (){
        imgFrontDriving = findViewById(R.id.imgFrontDriving);
        imgBackDriving = findViewById(R.id.imgBackDriving);
        imgMerge = findViewById(R.id.mergeImage);
        mergeRelative = findViewById(R.id.mergeRel);
        captureView = findViewById(R.id.captureView);
        txtFrontDriving = findViewById(R.id.txtFrontDriving);
        txtBackDriving = findViewById(R.id.txtBackDriving);
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
        mergedPhotoFileName = "merge_ProfileDriving.jpg";
        mergedPhotoFilePath = KYC_DriveVeriActivity.this.getFilesDir().getPath().toString() + "/" + mergedPhotoFileName;
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
//            Preferences.setValue(KYC_DriveVeriActivity.this, Preferences.PROFILE_DRIVINGPATH, mergedPhotoFilePath);
//            Intent intent = new Intent(KYC_DriveVeriActivity.this,KYC_Setting_IDMain.class);
//            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultPhoto;
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(KYC_DriveVeriActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(KYC_DriveVeriActivity.this, Preferences.API_SecretKey);
        loadingDialog = new LoadingDialog(KYC_DriveVeriActivity.this, false);
        if (CoreApp.isNetworkConnection(KYC_DriveVeriActivity.this)) {
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
                        Toast.makeText(KYC_DriveVeriActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(KYC_DriveVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                    loadingDialog.hide();
                }
            });
        } else {
            loadingDialog.hide();
            Toast.makeText(KYC_DriveVeriActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
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
                    Preferences.setValue(KYC_DriveVeriActivity.this, Preferences.PROFILE_DRIVINGPATH, mergedPhotoFilePath);
                    Preferences.setValue(KYC_DriveVeriActivity.this, Preferences.PROFILE_DRIVINGVERIFY,"true" );
                    Intent intent = new Intent(KYC_DriveVeriActivity.this,KYC_IDCardVeriResultActivity.class);
                    intent.putExtra("IDCardType","Driving License");
                    startActivity(intent);
                } else {
                    loadingDialog.hide();
                    Toast.makeText(KYC_DriveVeriActivity.this, "Bad input MRZ image!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IDCardVeriResponse> call, Throwable t) {
                Toast.makeText(KYC_DriveVeriActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
                loadingDialog.hide();
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgFrontDriving:
                Intent intent_front = new Intent(KYC_DriveVeriActivity.this, IDDocCameraActivity.class);
                intent_front.putExtra("CardType","Profile_FrontDriving");
                startActivity(intent_front);
                break;
            case R.id.imgBackDriving:
                Intent intent_back = new Intent(KYC_DriveVeriActivity.this, IDDocCameraActivity.class);
                intent_back.putExtra("CardType","Profile_BackDriving");
                startActivity(intent_back);
                break;
            case R.id.mergeImage:
                mergeRelative.setVisibility(View.GONE);
                captureView.setVisibility(View.VISIBLE);
                Preferences.setValue(KYC_DriveVeriActivity.this, Preferences.PROFILE_DRIVINGPATH, null);
                break;
            case  R.id.btnVerify:
                createSingleImageFromMultipleImages(frontbitmap,backbitmap);
                break;
            case R.id.btnBack:
                Intent intent = new Intent(KYC_DriveVeriActivity.this,KYC_Setting_IDMain.class);
                startActivity(intent);
        }
    }
}