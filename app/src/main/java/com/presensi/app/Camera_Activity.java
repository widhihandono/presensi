package com.presensi.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Util.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class Camera_Activity extends AppCompatActivity {
    private Camera mCamera=null;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
//    private Button capture, switchCamera;
    private Button camDepan,camBack,getPicture;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static Bitmap bitmap;
    Bitmap bmp, itembmp;
    static Bitmap mutableBitmap;
    private MediaScannerConnection msConn;

    File imageFileName = null;
    File imageFileFolder = null;

    private Uri mImageUri;
    String currentPhotoPath;
    private static final String IMAGE_DIRECTORY = "/CustomImage";
    Snackbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_);

        getSupportActionBar().setTitle("Take Photo Selfie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        cameraPreview = (LinearLayout) findViewById(R.id.cPreview);
        getPicture = (Button) findViewById(R.id.getpicture);
        camDepan = (Button) findViewById(R.id.camDepan);
        camBack = findViewById(R.id.camBack);
//        mCamera =  Camera.open();

            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPreview = new CameraPreview(myContext, mCamera);
            cameraPreview.addView(mPreview);

            mCamera.startPreview();





        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbarDialog("Mohon tunggu, akan dialihkan ke halaman presensi...");
                mCamera.takePicture(null, null, photoCallback);
            }
        });


        camDepan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFront = false;
                //get the number of cameras
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    //release the old camera instance
                    //switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    mCamera.takePicture(null, null, photoCallback);
                }
            }
        });

        camBack.setOnClickListener(l->{
            cameraFront = true;
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                mCamera.takePicture(null, null, photoCallback);
            }
        });




    }



    Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, final Camera camera) {
//            Toast.makeText(getApplicationContext(),"Saving",Toast.LENGTH_LONG).show();
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {}
                    onPictureTake(data, camera);
                }
            }.start();
        }
    };

    public void onPictureTake(byte[] data, Camera camera) {

        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        savePhoto(mutableBitmap);
    }

    class SavePhotoTask extends AsyncTask< byte[], String, String > {@Override
    protected String doInBackground(byte[]...jpeg) {
        File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(jpeg[0]);
            fos.close();
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return (null);
    }
    }

    public void savePhoto(Bitmap bmp) {
        imageFileFolder = new File(Environment.getExternalStorageDirectory(), "Presensi");
        imageFileFolder.mkdir();
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.MONTH)) + fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.HOUR_OF_DAY)) + fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));
        imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
        try {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());

            Intent intent = new Intent(getApplicationContext(), Presence_Activity.class);
            intent.putExtra("presensi", getIntent().getExtras().getString("presensi"));
            intent.putExtra("imageUri",Uri.fromFile(imageFileName).toString());
            intent.putExtra("currentPhotoPath",imageFileName.getAbsolutePath());
            bar.dismiss();
            mCamera.stopPreview();
            startActivity(intent);
            finish();

            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String fromInt(int val) {
        return String.valueOf(val);
    }
    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            @SuppressLint("LongLogTag")
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj  in Photo Utility", "connection established");
            }
            @SuppressLint("LongLogTag")
            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj in Photo Utility", "scan completed");
            }
        });
        msConn.connect();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        }else {
            Log.d("nu","no null");
        }

    }





    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();

    }

    //===========================================================
    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
//                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
//                cameraFront = false;
                break;

            }

        }
        return cameraId;
    }


    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }


    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                saveImage(bitmap);
                finish();
            }
        };
        return picture;
    }

    public void saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            mImageUri = Uri.fromFile(f);
            currentPhotoPath = f.getAbsolutePath();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void snackbarDialog(String text)
    {

        bar = Snackbar.make(findViewById(R.id.sb_camera),text, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
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
