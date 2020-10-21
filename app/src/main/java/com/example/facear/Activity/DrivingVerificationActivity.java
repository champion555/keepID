package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Preferences;

import java.io.File;
import java.io.FileOutputStream;

public class DrivingVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgFrontDrivingLicense,imgBackDrivingLicense,btnBack;
    private ScrollView captureRel;
    private Button btnVerification,btnEdit;
    private TextView txtFrontIDCard,txtBackIDCard;
    Bitmap frontbitmap,backbitmap;
    int width;
    String mergedPhotoFileName,mergedPhotoFilePath;
    File mergedPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_verification);
        BackgroundService.mContext = DrivingVerificationActivity.this;
        init();
        btnVerification.setVisibility(View.GONE);
        String frontPath = AppConstants.DrivingVeri_frontCardPath;
        String backPath = AppConstants.DrivingVeri_backCardPath;
        if (frontPath.isEmpty()){
            imgFrontDrivingLicense.setImageResource(R.drawable.ic_license_front);
            txtFrontIDCard.setText("Capture Front Driving License");
        }else if (frontPath != null){
            frontbitmap = BitmapFactory.decodeFile(frontPath);
            imgFrontDrivingLicense.setImageBitmap(frontbitmap);
            txtFrontIDCard.setText("Edit Front Driving License");
        }
        if (backPath.isEmpty()){
            imgBackDrivingLicense.setImageResource(R.drawable.ic_license_back);
            txtBackIDCard.setText("Capture Back Driving License");
        }else if (backPath != null){
            backbitmap = BitmapFactory.decodeFile(backPath);
            imgBackDrivingLicense.setImageBitmap(backbitmap);
            txtBackIDCard.setText("Edit Back Driving License");
            btnVerification.setVisibility(View.VISIBLE);
        }
        imgFrontDrivingLicense.setOnClickListener(this);
        imgBackDrivingLicense.setOnClickListener(this);
        btnVerification.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    private void init (){
        imgFrontDrivingLicense = findViewById(R.id.imgDrivingFront);
        imgBackDrivingLicense = findViewById(R.id.imgDrivingBack);
//        frontIDCameraIcon = findViewById(R.id.frontIDCameraIcon);
//        backIDCameraIcon = findViewById(R.id.backIDCameraIcon);
        captureRel = findViewById(R.id.captureRel);
        txtFrontIDCard = findViewById(R.id.txtFrontIDCard);
        txtBackIDCard = findViewById(R.id.txtBackIdCard);
        btnVerification = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);
//        btnEdit = findViewById(R.id.btnEdit);

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
        mergedPhotoFileName = "mergedFaceMatchIDCard.jpg";
        mergedPhotoFilePath = DrivingVerificationActivity.this.getFilesDir().getPath().toString() + "/" + mergedPhotoFileName;
        mergedPhotoFile = new File(mergedPhotoFilePath);
        if (mergedPhotoFile.exists())
            mergedPhotoFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(mergedPhotoFile);
            Log.e("out", String.valueOf(out));
            resultPhoto.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Preferences.setValue(DrivingVerificationActivity.this, Preferences.DIVING_VERIFY_PATH, mergedPhotoFilePath);
            // call verification call

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultPhoto;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgDrivingFront:
                Intent intent_front = new Intent(DrivingVerificationActivity.this, IDDocCameraActivity.class);
                intent_front.putExtra("CardType","Driving_FrontCard");
                startActivity(intent_front);
                break;
            case R.id.imgDrivingBack:
                Intent intent_back = new Intent(DrivingVerificationActivity.this, IDDocCameraActivity.class);
                intent_back.putExtra("CardType","Driving_BackCard");
                startActivity(intent_back);
                break;
            case  R.id.btnVerify:
                createSingleImageFromMultipleImages(frontbitmap,backbitmap);
                break;
            case R.id.btnBack:
                Intent intent = new Intent(DrivingVerificationActivity.this, IDDocMainActivity.class);
                startActivity(intent);
                break;

        }
    }
}