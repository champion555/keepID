package com.example.facear.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facear.BackgroundService.BackgroundService;
import com.example.facear.CustomView.IDDocView;
import com.example.facear.IDDocCamera.CameraPreview;
import com.example.facear.IDDocCamera.CameraUtils;
import com.example.facear.Models.AuthenticationResponse;
import com.example.facear.Models.ImageBlurryArray;
import com.example.facear.Models.ImageCheckResponse;
import com.example.facear.R;
import com.example.facear.Services.RetrofitClient;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.CoreApp;
import com.example.facear.Utils.LoadingDialog;
import com.example.facear.Utils.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IDDocCameraActivity extends AppCompatActivity implements View.OnClickListener{
    private CameraPreview cameraPreview;
    private ImageView btnCapture,btnFlash;
    private TextView txtMessage,txtTitle,txtErrorMessage;
    private ImageView imgCrop;
    private RelativeLayout cropRelative,cameraRelative;
    private LinearLayout linErrorMessage;
    private Button btnTake,btnRetake;
    private IDDocView cropView;
//    private LoadingDialog loadingDialog;
    private static final String TAG = IDDocCameraActivity.class.getSimpleName();
    private Bitmap demoBitmap, mCropBitmap;
    private Bundle bundle;
    String imgFileName,imgFilePath,CardType;
    File imgFile;
    File demoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iddoc_camera);
        BackgroundService.mContext = IDDocCameraActivity.this;
        init();
        listener();
        bundle = getIntent().getExtras();
        CardType = bundle.getString("CardType");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams((int) width, (int) height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        linErrorMessage.setVisibility(View.GONE);
        cameraPreview.setLayoutParams(layoutParams);
        if (CardType.equals("FaceMatch_FrontCard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  ID Card inside \n the Frame and take the photo");
        }else if (CardType.equals("FaceMatch_BackCard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back page of ID Card inside \n the Frame and take the photo");
        }
        if (CardType.equals("IDcard_FrontCard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  ID Card inside \n the Frame and take the photo");
        }else if (CardType.equals("IDcard_BackCard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back page of ID Card inside \n the Frame and take the photo");
        }
        if (CardType.equals("Passport")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Passport inside the Frame and take the photo");
        }
        if (CardType.equals("Driving_FrontCard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  Driving License \n inside the Frame and take the photo");
        }else if (CardType.equals("Driving_BackCard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back Page of  Driving License \n inside the Frame and take the photo");
        }
        if (CardType.equals("Resident_Front")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  Resident Permit \n inside the Frame and take the photo");
        }else if (CardType.equals("Resident_Back")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back Page of  Resident Permit \n inside the Frame and take the photo");
        }
        if (CardType.equals("KYC_Passport")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Passport inside the Frame and take the photo");
        }
        if (CardType.equals("Profile_FrontIDcard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  ID Card inside \n the Frame and take the photo");
        }else if (CardType.equals("Profile_BackIDcard")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back page of ID Card inside \n the Frame and take the photo");
        }
        if (CardType.equals("Profile_FrontDriving")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  Driving License \n inside the Frame and take the photo");
        }else if (CardType.equals("Profile_BackDriving")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back Page of  Driving License \n inside the Frame and take the photo");
        }
        if (CardType.equals("Profile_FrontResident")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Front Page of  Resident Permit \n inside the Frame and take the photo");
        }else if (CardType.equals("Profile_BackResient")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Back Page of  Resident Permit \n inside the Frame and take the photo");
        }

    }
    private void init(){
        cameraPreview = (CameraPreview) findViewById(R.id.camera_surface);
        btnCapture = findViewById(R.id.btnCapture);
        txtMessage = findViewById(R.id.txtMessage);
        txtTitle = findViewById(R.id.txtTitle);
        imgCrop = findViewById(R.id.imgCrop);
        cropRelative = findViewById(R.id.cropRel);
        cameraRelative = findViewById(R.id.cameraRel);
        btnTake = findViewById(R.id.btnTake);
        btnRetake = findViewById(R.id.btnRetake);
        cropView = findViewById(R.id.custom_view);
        btnFlash = findViewById(R.id.imgFlash);
        linErrorMessage = findViewById(R.id.linMessage);
        txtErrorMessage = findViewById(R.id.errorMessage);
    }
    private void listener(){
        cameraPreview.setOnClickListener(this);
        btnCapture.setOnClickListener(this);
        btnTake.setOnClickListener(this);
        btnRetake.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void captureImage() {
        SurfaceView view = cameraPreview;
        Log.e("screen size:","width:"+ cameraPreview.getWidth()+"height"+ cameraPreview.getHeight());
        demoBitmap = Bitmap.createBitmap(cameraPreview.getWidth(), cameraPreview.getHeight(),
                Bitmap.Config.ARGB_8888);
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        PixelCopy.request(view, demoBitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                if (copyResult == PixelCopy.SUCCESS) {
                    Log.e(TAG, demoBitmap.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (demoBitmap != null){
                                String demoImageFileName = "demoImage.jpg";
                                String imgDemoFilePath = IDDocCameraActivity.this.getFilesDir().getPath().toString() + "/" + demoImageFileName;
                                demoFile = new File(imgDemoFilePath);
                                Log.i(TAG, "" + demoFile);
                                if (demoFile.exists())
                                    demoFile.delete();
                                try {
                                    FileOutputStream out = new FileOutputStream(demoFile);
                                    Log.e("out", String.valueOf(out));
                                    demoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();
                                    AuthenticationToServer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                cameraRelative.setVisibility(View.GONE);
//                                cropRelative.setVisibility(View.VISIBLE);
//                                btnCapture.setVisibility(View.GONE);
//                                btnFlash.setVisibility(View.GONE);
//                                btnRetake.setVisibility(View.VISIBLE);
//                                btnTake.setVisibility(View.VISIBLE);
//                                txtTitle.setText("Preview Captured ID Document");
//                                txtMessage.setText("Make sure the ID Document image is clear to read");
//                                imgCrop.setImageBitmap(demoBitmap);
                            }else {
                                Toast.makeText(IDDocCameraActivity.this, "Id card capture was failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Failed to take the photo from camera");
                }
                handlerThread.quitSafely();
            }
        }, new Handler(handlerThread.getLooper()));
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera_surface:
                cameraPreview.focus();
                break;
            case R.id.btnCapture:
                captureImage();
                break;
            case R.id.btnTake:
                float crop_left = cropView.rect.left;
                float crop_top = cropView.rect.top;
                float crop_width = cropView.rect.width();
                float crop_height = cropView.rect.height();
                mCropBitmap = Bitmap.createBitmap(demoBitmap,
                        (int) (crop_left),
                        (int) (crop_top),
                        (int) (crop_width),
                        (int) (crop_height));
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imgFileName = "IDDoc"+timeStamp+"_"+".jpg";
                imgFilePath = IDDocCameraActivity.this.getFilesDir().getPath().toString() + "/" + imgFileName;
                imgFile = new File(imgFilePath);
                Log.i(TAG, "" + imgFile);
                if (imgFile.exists())
                    imgFile.delete();
                try {
                    FileOutputStream out = new FileOutputStream(imgFile);
                    Log.e("out", String.valueOf(out));
                    mCropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
//                            Toast.makeText(CameraActivity.this, imgFilePath, Toast.LENGTH_LONG).show();
                    if (CardType.equals("FaceMatch_FrontCard")){
                        AppConstants.faceMatch_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,FaceMatchToIDActivity.class);
                        startActivity(intent);
                    }else if(CardType.equals("FaceMatch_BackCard")) {
                        AppConstants.faceMatch_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,FaceMatchToIDActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("IDcard_FrontCard")){
                        AppConstants.IDCardVeri_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,IDCardVerificationActivity.class);
                        startActivity(intent);
                    }else if (CardType.equals("IDcard_BackCard")){
                        AppConstants.IDCardVeri_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,IDCardVerificationActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("Passport")){
                        AppConstants.PassportVeriPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,PassportVerificatioinActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("Driving_FrontCard")){
                        AppConstants.DrivingVeri_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,DrivingVerificationActivity.class);
                        startActivity(intent);
                    }else if (CardType.equals("Driving_BackCard")){
                        AppConstants.DrivingVeri_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,DrivingVerificationActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("Resident_Front")){
                        AppConstants.ResidentVeri_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,ResidentVerificationActivity.class);
                        startActivity(intent);
                    }else if (CardType.equals("Resident_Back")){
                        AppConstants.ResidentVeri_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,ResidentVerificationActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("KYC_Passport")){
                        Preferences.setValue(IDDocCameraActivity.this, Preferences.PROFILE_PASSPORTPATH, imgFilePath);
//                        AppConstants.PassportVeriPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,KYC_PassportVeriActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("Profile_FrontIDcard")){
                        AppConstants.IDCardVeri_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,KYC_IDCardVeriActivity.class);
                        startActivity(intent);
                    }else if (CardType.equals("Profile_BackIDcard")){
                        AppConstants.IDCardVeri_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,KYC_IDCardVeriActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("Profile_FrontDriving")){
                        AppConstants.DrivingVeri_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,KYC_DriveVeriActivity.class);
                        startActivity(intent);
                    }else if (CardType.equals("Profile_BackDriving")){
                        AppConstants.DrivingVeri_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this,KYC_DriveVeriActivity.class);
                        startActivity(intent);
                    }
                    if (CardType.equals("Profile_FrontResident")){
                        AppConstants.ResidentVeri_frontCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this, KYC_ResidentVeriActivity.class);
                        startActivity(intent);
                    }else if (CardType.equals("Profile_BackResient")){
                        AppConstants.ResidentVeri_backCardPath = imgFilePath;
                        Intent intent = new Intent(IDDocCameraActivity.this, KYC_ResidentVeriActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnRetake:
                cameraRelative.setVisibility(View.VISIBLE);
                cropRelative.setVisibility(View.GONE);
                btnCapture.setVisibility(View.VISIBLE);
                btnRetake.setVisibility(View.GONE);
                btnTake.setVisibility(View.GONE);
                linErrorMessage.setVisibility(View.GONE);
                btnFlash.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.VISIBLE);
                if (CardType.equals("FaceMatch_FrontCard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  ID Card inside \n the Frame and take the photo");
                }else if (CardType.equals("FaceMatch_BackCard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back page of ID Card inside \n the Frame and take the photo");
                }
                if (CardType.equals("IDcard_FrontCard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  ID Card inside \n the Frame and take the photo");
                }else if (CardType.equals("IDcard_BackCard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back page of ID Card inside \n the Frame and take the photo");
                }
                if (CardType.equals("Passport")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Passport inside the Frame and take the photo");
                }
                if (CardType.equals("Driving_FrontCard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  Driving License \n inside the Frame and take the photo");
                }else if (CardType.equals("Driving_BackCard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back Page of  Driving License \n inside the Frame and take the photo");
                }
                if (CardType.equals("Resident_Front")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  Resident Permit \n inside the Frame and take the photo");
                }else if (CardType.equals("Resident_Back")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back Page of  Resident Permit \n inside the Frame and take the photo");
                }
                if (CardType.equals("Profile_FrontIDcard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  ID Card inside \n the Frame and take the photo");
                }else if (CardType.equals("Profile_BackIDcard")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back page of ID Card inside \n the Frame and take the photo");
                }
                if (CardType.equals("Profile_FrontDriving")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  Driving License \n inside the Frame and take the photo");
                }else if (CardType.equals("Profile_BackDriving")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back Page of  Driving License \n inside the Frame and take the photo");
                }
                if (CardType.equals("Profile_FrontResident")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Front Page of  Resident Permit \n inside the Frame and take the photo");
                }else if (CardType.equals("Profile_BackResient")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Back Page of  Resident Permit \n inside the Frame and take the photo");
                }
                break;
            case R.id.imgFlash:
                if (CameraUtils.hasFlash(this)) {
                    boolean isFlashOn = cameraPreview.switchFlashLight();
                    btnFlash.setImageResource(isFlashOn ? R.drawable.camera_flash_on : R.drawable.camera_flash_off);
                } else {
                    Toast.makeText(this, "The device does not support flash", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (cameraPreview != null) {
            cameraPreview.onStart();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (cameraPreview != null) {
            cameraPreview.onStop();
        }
    }
    private void AuthenticationToServer() {
        String api_key = Preferences.getValue_String(IDDocCameraActivity.this, Preferences.API_Key);
        String secret_key = Preferences.getValue_String(IDDocCameraActivity.this, Preferences.API_SecretKey);
//        loadingDialog = new LoadingDialog(IDDocCameraActivity.this, false);
        if (CoreApp.isNetworkConnection(IDDocCameraActivity.this)) {
            Call<AuthenticationResponse> call = RetrofitClient
                    .getInstance().getApi().authenticateCheck(api_key, secret_key);
            call.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    AuthenticationResponse authenticationResponse = response.body();
                    int statuscode = Integer.parseInt(authenticationResponse.getStatusCode());
                    if (statuscode == 200) {
                        AppConstants.api_token = authenticationResponse.getApi_access_token();
                        ImageCheckToServer();
                    } else {
                        Toast.makeText(IDDocCameraActivity.this, response.message(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(IDDocCameraActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
//                    loadingDialog.hide();
                }
            });
        } else {
//            loadingDialog.hide();
            Toast.makeText(IDDocCameraActivity.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
    }
    private void ImageCheckToServer() {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"),
                demoFile);
        Call<ImageCheckResponse> call = RetrofitClient
                .getInstance().getApi().imageCheck(reqFile);
        call.enqueue(new Callback<ImageCheckResponse>() {
            @Override
            public void onResponse(Call<ImageCheckResponse> call, Response<ImageCheckResponse> response) {
                ImageCheckResponse imageCheckResponse = response.body();
                int statuscode = Integer.parseInt(imageCheckResponse.getStatusCode());
//                ImageBlurryArray err = imageCheckResponse.getErrorList().add();
                if (statuscode == 200) {
//                    loadingDialog.hide();
                    cameraRelative.setVisibility(View.GONE);
                    cropRelative.setVisibility(View.VISIBLE);
                    btnCapture.setVisibility(View.GONE);
                    btnFlash.setVisibility(View.GONE);
                    linErrorMessage.setVisibility(View.GONE);
                    btnRetake.setVisibility(View.VISIBLE);
                    btnTake.setVisibility(View.VISIBLE);
                    txtTitle.setText("Preview Captured ID Document");
                    txtMessage.setText("Make sure the ID Document image is clear to read");
                    imgCrop.setImageBitmap(demoBitmap);
                } else {
//                    loadingDialog.hide();
                    List<ImageBlurryArray> errorList = imageCheckResponse.getErrorList();
                    int i = errorList.size();
                    if (i == 1){
                        String errorType1 = errorList.get(0).getErrorType();
                        if (errorType1.equals("ImageTooBlurry")){
                            txtErrorMessage.setText("- image too blur");
                        }else if (errorType1.equals("GlareFound")){
                            txtErrorMessage.setText("- Glares are detected on Image");
                        }
                    }else if (i == 2){
                        txtErrorMessage.setText("- image too blur\n- Glares are detected on Image");
                    }
//                    for (int i = 0 ; i<= errorList.size(); i++){
//                        String errorType = errorList.get(i).getErrorType();
//                    }
                    cameraRelative.setVisibility(View.GONE);
                    cropRelative.setVisibility(View.VISIBLE);
                    btnCapture.setVisibility(View.GONE);
                    btnFlash.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.GONE);
                    btnRetake.setVisibility(View.VISIBLE);
                    btnTake.setVisibility(View.GONE);
                    linErrorMessage.setVisibility(View.VISIBLE);
                    txtTitle.setText("Preview Captured ID Document");
                    imgCrop.setImageBitmap(demoBitmap);
                }
            }

            @Override
            public void onFailure(Call<ImageCheckResponse> call, Throwable t) {
                Toast.makeText(IDDocCameraActivity.this, "Server is not working now", Toast.LENGTH_LONG).show();
//                loadingDialog.hide();
            }
        });

    }
}