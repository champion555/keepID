package com.example.facear.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PassportVerificatioinActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgPassport,btnBack;
    private Button btnVerification;
    private TextView txtPassport;
    String currentPhotoPath,passportPath;
    Uri photoURI,demoPassportUri;
    static final int REQUEST_TAKE_PHOTO = 1;
    Bitmap passportBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_verificatioin);
        BackgroundService.mContext = PassportVerificatioinActivity.this;
        init();
        String passportPath = AppConstants.PassportVeriPath;
        if (passportPath.isEmpty()){
            imgPassport.setImageResource(R.drawable.ic_passport);
            txtPassport.setText("Capture Passport");
        }else if (passportPath != null){
            Uri passportUri = Uri.parse(passportPath);
            imgPassport.setImageURI(passportUri);
//            passportBitmap = BitmapFactory.decodeFile(passportPath);
//            imgPassport.setImageBitmap(passportBitmap);
            txtPassport.setText("Edit Passport");
        }
        imgPassport.setOnClickListener(this);
        btnVerification.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    private void init(){
        imgPassport = findViewById(R.id.passportImg);
        btnVerification = findViewById(R.id.btnVerification);
        txtPassport = findViewById(R.id.txtPassport);
        btnBack = findViewById(R.id.btnBack);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.passportImg:
               Intent intent_passport = new Intent(PassportVerificatioinActivity.this, IDDocCameraActivity.class);
               intent_passport.putExtra("CardType","Passport");
               startActivity(intent_passport);
               break;
           case R.id.btnVerification:
               // api call
               break;
           case R.id.btnBack:
               Intent intent_back = new Intent(PassportVerificatioinActivity.this, IDDocMainActivity.class);
               startActivity(intent_back);
               break;
       }
    }
}