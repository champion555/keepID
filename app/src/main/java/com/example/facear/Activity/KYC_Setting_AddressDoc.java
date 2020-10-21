package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.Preferences;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KYC_Setting_AddressDoc extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgAddressDoc,btnBack;
    private Button btnSave;
    private TextView txtAddressProof;
    String currentPhotoPath,addressDocImgPath;
    Uri photoURI,demoAddressDocUri;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_setting_addressdoc);
        BackgroundService.mContext = KYC_Setting_AddressDoc.this;
        init();
        String passportPath = Preferences.getValue_String(KYC_Setting_AddressDoc.this, Preferences.PROFILE_ADDRESS_DOCPATH);
        if (passportPath.isEmpty()){
            imgAddressDoc.setImageResource(R.drawable.ic_address_doc);
            txtAddressProof.setText("Capture Address Document");
        }else if (passportPath != null){
            Uri passportUri = Uri.parse(passportPath);
            imgAddressDoc.setImageURI(passportUri);
//            passportBitmap = BitmapFactory.decodeFile(passportPath);
//            imgPassport.setImageBitmap(passportBitmap);
            txtAddressProof.setText("Edit Address Docuement");
        }
        listener();
    }
    private void init(){
        imgAddressDoc = findViewById(R.id.addressDocImg);
        btnSave = findViewById(R.id.btnSave);
        txtAddressProof = findViewById(R.id.txtAddressProof);
        btnBack = findViewById(R.id.btnBack);
    }
    private void listener(){
        imgAddressDoc.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addressDocImg:
                Intent intent_passport = new Intent(KYC_Setting_AddressDoc.this, AddressDocCameraActivity.class);
                intent_passport.putExtra("CardType","AddressDoc");
                startActivity(intent_passport);
                break;
            case R.id.btnSave:
                Intent intent = new Intent(KYC_Setting_AddressDoc.this,KYC_ProfileSetting.class);
                startActivity(intent);
            case R.id.btnBack:
                Intent intent_back = new Intent(KYC_Setting_AddressDoc.this, KYC_ProfileSetting.class);
                startActivity(intent_back);
                break;
        }

    }
}