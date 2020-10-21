package com.example.facear.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Preferences;

public class KYC_ProfileSetting extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linName,linEmail,linAddress,linPhone,linIDDoc,linBirthday,linAddressDoc;
    private TextView txtName,txtEmail,txtAddress,txtPhone,txtIDDoc,txtBirth,txtAddressDoc;
    private ImageView nameMark,emailMark,addressMark,phoneMark,idMark,birthMark,addressDocMark,btnBack,imgName,imgEmail,imgAddress,imgPhone,imgID,imgBirth,imgProofAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_profile_setting);
        BackgroundService.mContext = KYC_ProfileSetting.this;
        init();
        setProperties();
        listener();
    }
    private void init(){
        linName = findViewById(R.id.linName);
        linEmail = findViewById(R.id.linEmail);
        linAddress = findViewById(R.id.linAddress);
        linPhone = findViewById(R.id.linPhoneNumber);
        linIDDoc = findViewById(R.id.linIDDoc);
        linBirthday = findViewById(R.id.linBirth);
        linAddressDoc = findViewById(R.id.linAddressDoc);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtIDDoc = findViewById(R.id.txtIDDoc);
        txtBirth = findViewById(R.id.txtBirth);
        txtAddressDoc = findViewById(R.id.txtAddressDoc);

        nameMark = findViewById(R.id.nameMark);
        emailMark = findViewById(R.id.emailMark);
        addressMark = findViewById(R.id.addressMark);
        phoneMark = findViewById(R.id.phoneMark);
        idMark = findViewById(R.id.idMark);
        birthMark = findViewById(R.id.birthMark);
        addressDocMark = findViewById(R.id.addressdocMark);

        imgName = findViewById(R.id.imgName);
        imgEmail = findViewById(R.id.imgEmail);
        imgAddress = findViewById(R.id.imgAddress);
        imgPhone = findViewById(R.id.imgPhone);
        imgID = findViewById(R.id.imgID);
        imgBirth = findViewById(R.id.imgBirth);
        imgProofAddress = findViewById(R.id.imgproofAddress);

        btnBack = findViewById(R.id.btnBack);
    }
    private void setProperties(){
        String firstName = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_FIRSTNAME);
        String lastName = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_LASTNAME);
        String country = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_COUNTRYNAME);
        String city = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_CITY);
        String email = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_USEREMAIL);
        String phone_num = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_PHONENUMBER);
        String id_path = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_IDPHOTOPATH);
        String passport_path = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_PASSPORTPATH);
        String driving_path = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_DRIVINGPATH);
        String residence_path = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_RESIDENT_DOCPATH);
        String birthday = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_BIRTHDAY);
        String addressDocPath = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_ADDRESS_DOCPATH);
        String emailVerify = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_EMAILVERIFY);
        String phoneVerify = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_PHONEVERIFY);
        String idCardVerify = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_IDCARDVERIFY);
        String passportVerify = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_PASSPORTVERIFY);
        String drivingVerify = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_PASSPORTVERIFY);
        String residentVerify = Preferences.getValue_String(KYC_ProfileSetting.this, Preferences.PROFILE_RESIDENTVERIFY);
        if (firstName.isEmpty()){
            nameMark.setImageResource(R.drawable.ic_failed);
            imgName.setImageResource(R.drawable.ic_name_gray);
        }else if (firstName !=null){
            nameMark.setImageResource(R.drawable.ic_verify);
            imgName.setImageResource(R.drawable.ic_name_purple);
            txtName.setText(firstName+" "+lastName);
        }
        if (email.isEmpty()){
            emailMark.setImageResource(R.drawable.ic_failed);
            imgEmail.setImageResource(R.drawable.ic_email_gray);
        }else if (email !=null){
            if (emailVerify.equals("true")){
                emailMark.setImageResource(R.drawable.ic_success);
                imgEmail.setImageResource(R.drawable.ic_email_purple);
                txtEmail.setText(email);
            }else {
                emailMark.setImageResource(R.drawable.ic_verify);
                imgEmail.setImageResource(R.drawable.ic_email_purple);
                txtEmail.setText(email);
            }
        }
        if (country.isEmpty()){
            addressMark.setImageResource(R.drawable.ic_failed);
            imgAddress.setImageResource(R.drawable.ic_address_gray);
        }else if (country != null){
            addressMark.setImageResource(R.drawable.ic_verify);
            imgAddress.setImageResource(R.drawable.ic_address_purple);
            txtAddress.setText(city+"/"+country);
        }
        if (phone_num.isEmpty()){
            phoneMark.setImageResource(R.drawable.ic_failed);
            imgPhone.setImageResource(R.drawable.ic_phone_gray);
        }else if (phone_num != null){
            if (phoneVerify.equals("true")){
                phoneMark.setImageResource(R.drawable.ic_success);
                imgPhone.setImageResource(R.drawable.ic_phone_purple);
                txtPhone.setText(phone_num);
            }else{
                phoneMark.setImageResource(R.drawable.ic_verify);
                imgPhone.setImageResource(R.drawable.ic_phone_purple);
                txtPhone.setText(phone_num);
            }
        }
        if (id_path.isEmpty() || passport_path.isEmpty() || driving_path.isEmpty() || residence_path.isEmpty()){
            idMark.setImageResource(R.drawable.ic_failed);
            imgID.setImageResource(R.drawable.ic_identity_gray);
            txtIDDoc.setText("The Documents are't added.");
        }else {
            if (idCardVerify.equals("true") && passportVerify.equals("true") && residentVerify.equals("true") && drivingVerify.equals("true")){
                idMark.setImageResource(R.drawable.ic_success);
                imgID.setImageResource(R.drawable.ic_identity_purple);
                txtIDDoc.setText("The Documents are verified successfully");

            }else {
                idMark.setImageResource(R.drawable.ic_verify);
                imgID.setImageResource(R.drawable.ic_identity_purple);
                txtIDDoc.setText("The Documents are added successfully");
            }
        }
        if (birthday.isEmpty()){
            birthMark.setImageResource(R.drawable.ic_failed);
            imgBirth.setImageResource(R.drawable.ic_birthday_gray);
        }else if (birthday != null){
            birthMark.setImageResource(R.drawable.ic_verify);
            imgBirth.setImageResource(R.drawable.ic_birth_purple);
            txtBirth.setText(birthday);
        }
        if (addressDocPath.isEmpty()){
            addressDocMark.setImageResource(R.drawable.ic_failed);
            imgProofAddress.setImageResource(R.drawable.ic_addressproof_gray);
            txtAddressDoc.setText("The Address Doc isn't added.");
        }else if (addressDocPath !=null){
            addressDocMark.setImageResource(R.drawable.ic_verify);
            imgProofAddress.setImageResource(R.drawable.ic_addressproof_purple);
            txtAddressDoc.setText("The Address Doc is added successfully");
        }
    }
    private void listener(){
        linName.setOnClickListener(this);
        linEmail.setOnClickListener(this);
        linAddress.setOnClickListener(this);
        linPhone.setOnClickListener(this);
        linIDDoc.setOnClickListener(this);
        linBirthday.setOnClickListener(this);
        linAddressDoc.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.linName:
                Intent intent_name = new Intent(KYC_ProfileSetting.this,KYC_Setting_Name.class);
                startActivity(intent_name);
                break;
            case R.id.linEmail:
//                Intent intent_email = new Intent(KYC_ProfileSetting.this,KYC_Setting_Email.class);
////                startActivity(intent_email);
                break;
            case R.id.linAddress:
                Intent intent_address = new Intent(KYC_ProfileSetting.this,KYC_Setting_Address.class);
                startActivity(intent_address);
                break;
            case R.id.linPhoneNumber:
//                Intent intent_phone = new Intent(KYC_ProfileSetting.this,KYC_Setting_Phone.class);
//                startActivity(intent_phone);
                break;
            case R.id.linIDDoc:
                Intent intent_id = new Intent(KYC_ProfileSetting.this, KYC_Setting_IDMain.class);
                startActivity(intent_id);
                break;
            case R.id.linBirth:
                Intent intent_birth = new Intent(KYC_ProfileSetting.this,KYC_Setting_Birth.class);
                startActivity(intent_birth);
                break;
            case R.id.linAddressDoc:
                Intent intent_addressDoc = new Intent(KYC_ProfileSetting.this,KYC_Setting_AddressDoc.class);
                startActivity(intent_addressDoc);
                break;
            case R.id.btnBack:
                Intent intent_back = new Intent(KYC_ProfileSetting.this,KYC_ProfileMain.class);
                startActivity(intent_back);
                break;
        }
    }
}