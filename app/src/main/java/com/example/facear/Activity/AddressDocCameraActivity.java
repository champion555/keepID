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
import com.example.facear.CustomView.AddressDocView;
import com.example.facear.CustomView.IDDocView;
import com.example.facear.IDDocCamera.CameraPreview;
import com.example.facear.IDDocCamera.CameraUtils;
import com.example.facear.R;
import com.example.facear.Utils.AppConstants;
import com.example.facear.Utils.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddressDocCameraActivity extends AppCompatActivity implements View.OnClickListener {
    private CameraPreview cameraPreview;
    private LinearLayout previewBtns;
    private ImageView btnCapture,btnFlash;
    private TextView txtMessage,txtTitle;
    private ImageView imgCrop;
    private RelativeLayout cropRelative,cameraRelative;
    private Button btnTake,btnRetake;
    private AddressDocView cropView;
    private static final String TAG = IDDocCameraActivity.class.getSimpleName();
    private Bitmap demoBitmap, mCropBitmap;
    private Bundle bundle;
    String imgFileName,imgFilePath,CardType;
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_doc_camera);
        BackgroundService.mContext = AddressDocCameraActivity.this;
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
        cameraPreview.setLayoutParams(layoutParams);
        if (CardType.equals("AddressDoc")){
            txtTitle.setText("Powered by BIOMIID");
            txtMessage.setText("Place the Address Document inside the Frame and take the photo");
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
        previewBtns = findViewById(R.id.previewBtns);
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
                                cameraRelative.setVisibility(View.GONE);
                                cropRelative.setVisibility(View.VISIBLE);
                                btnCapture.setVisibility(View.GONE);
                                btnFlash.setVisibility(View.GONE);
                                previewBtns.setVisibility(View.VISIBLE);
//                                btnRetake.setVisibility(View.VISIBLE);
//                                btnTake.setVisibility(View.VISIBLE);
                                txtTitle.setText("Preview Captured ID Document");
                                txtMessage.setText("Make sure the ID Document image is clear to read");
                                imgCrop.setImageBitmap(demoBitmap);
                            }else {
                                Toast.makeText(AddressDocCameraActivity.this, "Address document capture was failed", Toast.LENGTH_LONG).show();
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
                imgFilePath = AddressDocCameraActivity.this.getFilesDir().getPath().toString() + "/" + imgFileName;
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
                    if (CardType.equals("AddressDoc")){
                        Preferences.setValue(AddressDocCameraActivity.this, Preferences.PROFILE_ADDRESS_DOCPATH, imgFilePath);
                        Intent intent = new Intent(AddressDocCameraActivity.this,KYC_Setting_AddressDoc.class);
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
                previewBtns.setVisibility(View.GONE);
//                btnRetake.setVisibility(View.GONE);
//                btnTake.setVisibility(View.GONE);
                btnFlash.setVisibility(View.VISIBLE);
                if (CardType.equals("AddressDoc")){
                    txtTitle.setText("Powered by BIOMIID");
                    txtMessage.setText("Place the Address Document inside the Frame and take the photo");
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
}