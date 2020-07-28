package com.presensi.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Util.CameraControllerV2WithPreview;
import com.presensi.app.Util.CameraControllerV2WithPreview_depan;
import com.presensi.app.Util.PhotoProvider;

//https://zatackcoder.com/android-camera-2-api-example-without-preview/
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera_New_Activity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    CameraControllerV2WithPreview ccv2WithPreview;
    CameraControllerV2WithPreview_depan ccv2WithPreview_depan;
//    CameraControllerV2WithoutPreview ccv2WithoutPreview;

    TextureView textureView;
    //    Switch startstoppreview;
    Button camDepan,camBack;
    private Size previewsize;
    private Size jpegSizes[] = null;
    //    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    Button getpicture;
    Snackbar bar;
    final int sdk = android.os.Build.VERSION.SDK_INT;
    Handler handler = new Handler();

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__new_);

        getSupportActionBar().setTitle("Take Photo Selfie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        snackbarDialog("Please Wait..load camera");
//         Intent intent = new Intent(Camera_New_Activity.this,Camera_New_Activity.class);

//        boolean showpreview = getIntent().getExtras().getBoolean("showpreview", true);

        textureView = findViewById(R.id.textureview);
//        startstoppreview = (Switch) findViewById(R.id.startstoppreview);
        camBack = findViewById(R.id.camBack);
        camDepan = findViewById(R.id.camDepan);
        getPermissions();

        if(getIntent().getExtras().getString("camera").equals("depan"))
        {

            bar.dismiss();
            ccv2WithPreview_depan = new CameraControllerV2WithPreview_depan(Camera_New_Activity.this, textureView, false);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
            {
                        camDepan.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_menu_press) );
            } else
            {
                        camDepan.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_menu_press));
            }
//            ccv2WithPreview = new CameraControllerV2WithPreview(Camera_New_Activity.this, textureView, true);

        }
        else
        {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
            {
                camBack.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_menu_press) );
            } else
            {
                camBack.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_menu_press));
            }
        }

        camDepan.setOnClickListener(l->{

            Intent intent = new Intent(this,Camera_New_Activity.class);
//            textureView.setSurfaceTextureListener(surfaceTextureListener);
            intent.putExtra("camera","depan");
            intent.putExtra("presensi",getIntent().getExtras().getString("presensi"));
            startActivity(intent);
            finish();
        });

        camBack.setOnClickListener(l->{
            Intent intent = new Intent(this,Camera_New_Activity.class);
            intent.putExtra("camera","back");
            intent.putExtra("presensi",getIntent().getExtras().getString("presensi"));
            startActivity(intent);
            finish();
//            ccv2WithPreview = new CameraControllerV2WithPreview(Camera_New_Activity.this, textureView, false);
        });



//        if (showpreview) {
//            ccv2WithPreview = new CameraControllerV2WithPreview(Camera_New_Activity.this, textureView, showpreview);

//            startstoppreview.setChecked(true);
//        } else {
//            ccv2WithPreview = new CameraControllerV2WithPreview(Camera_New_Activity.this, textureView, showpreview);
//            startstoppreview.setChecked(false);
//        }



//        startstoppreview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (startstoppreview.isChecked()) {
//                    intent.putExtra("showpreview", true);
//                    finish();
//                    startActivity(intent);
//
//                } else {
//                    intent.putExtra("showpreview", false);
//                    finish();
//                    startActivity(intent);
//                }
//            }
//        });

        findViewById(R.id.getpicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras().getString("camera").equals("back")) {
                    getPicture();
                }
                else
                {

                    ccv2WithPreview_depan.takePicture(getIntent().getExtras().getString("presensi"));

                }

//                else if(ccv2WithoutPreview != null){
////                    ccv2WithoutPreview.openCamera();
////                    try { Thread.sleep(20); } catch (InterruptedException e) {}
////                    ccv2WithoutPreview.takePicture();
//                    ccv2WithPreview.takePicture();
//                }

//                Toast.makeText(getApplicationContext(), "Picture Clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }


    //======================Camera Api 2=========================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ccv2WithPreview_depan != null) {
            ccv2WithPreview_depan.closeCamera();
        }
    }

    private void getPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Requesting permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override //Override from ActivityCompat.OnRequestPermissionsResultCallback Interface
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                }
                return;
            }
        }
    }

    void getPicture() {
        if (cameraDevice == null) {
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640, height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder capturebuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebuilder.addTarget(reader.getSurface());
            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (Exception ee) {
                    } finally {
                        if (image != null)
                            image.close();
                    }
                }

                void save(byte[] bytes) {
                    File file12 = getOutputMediaFile();
//                    File file = getOutputMediaFile();
                    Intent intent = new Intent(getApplicationContext(), Presence_Activity.class);
                    intent.putExtra("presensi", getIntent().getExtras().getString("presensi"));
                    intent.putExtra("imageUri", PhotoProvider.getPhotoUri(new File(file12, "cropped")).toString());
                    intent.putExtra("currentPhotoPath",file12.getAbsolutePath());
                    startActivity(intent);

//                    Toast.makeText(getApplication(),file12.getAbsolutePath(),Toast.LENGTH_LONG).show();
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file12);
                        outputStream.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null)
                                outputStream.close();
                        } catch (Exception e) {
                        }
                    }
                }
            };
            HandlerThread handlerThread = new HandlerThread("takepicture");
            handlerThread.start();
            final Handler handler = new Handler(handlerThread.getLooper());
            reader.setOnImageAvailableListener(imageAvailableListener, handler);
            final CameraCaptureSession.CaptureCallback previewSSession = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startCamera();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(capturebuilder.build(), previewSSession, handler);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, handler);

        } catch (Exception e) {
        }

    }

    public void openCamera() {
       handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                try {
                    String camerId = manager.getCameraIdList()[0];
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(camerId);
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    previewsize = map.getOutputSizes(SurfaceTexture.class)[1];
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    manager.openCamera(camerId, stateCallback, null);

                }catch (Exception e)
                {
                }

            }
        }, 5000);

    }

    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            openCamera();

        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

            if(cameraDevice != null){
                cameraDevice.close();

                cameraDevice = null;
            }

            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

    };
    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            bar.dismiss();
            handler.removeCallbacksAndMessages(null);
            cameraDevice=camera;
            startCamera();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice = camera;
            snackbarDialog("Camera Disconnect, Please use other camera");
        }
        @Override
        public void onError(CameraDevice camera, int error) {

//            openCamera();
//            snackbarDialog("Please Wait...Loading Camera. Or use other camera(Front Camera)");
//            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if(cameraDevice!=null)
        {
            cameraDevice.close();
            cameraDevice =null;
        }
    }



    void  startCamera()
    {
        if(cameraDevice==null||!textureView.isAvailable()|| previewsize==null)
        {
            return;
        }
        SurfaceTexture texture=textureView.getSurfaceTexture();
        if(texture==null)
        {
            return;
        }
        texture.setDefaultBufferSize(previewsize.getWidth(),previewsize.getHeight());
        Surface surface=new Surface(texture);
        try
        {
            previewBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        }catch (Exception e)
        {
        }
        previewBuilder.addTarget(surface);
        try
        {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession=session;
                    getChangedPreview();
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            },null);

        }catch (Exception e)
        {
        }
    }



    void getChangedPreview()
    {
        if(cameraDevice==null)
        {
            return;
        }
        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread=new HandlerThread("changed Preview");
        thread.start();
        Handler handler=new Handler(thread.getLooper());
        try
        {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
        }catch (Exception e){}
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStorageDirectory(),
                "Camera2Test");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera2Test", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    private void snackbarDialog(String text)
    {
        bar = Snackbar.make(findViewById(R.id.snackbar_camera),text, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentLay.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        contentLay.setLayoutParams(layoutParams);
        contentLay.setBackgroundResource(R.color.colorPrimary);
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.setActionTextColor(Color.GRAY);
        bar.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
