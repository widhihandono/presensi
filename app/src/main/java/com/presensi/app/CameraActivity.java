package com.presensi.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.PermissionDelegate;
import com.presensi.app.Util.SharedPref;

import io.fotoapparat.facedetector.Rectangle;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatSwitcher;
import io.fotoapparat.error.CameraErrorCallback;
import io.fotoapparat.facedetector.processor.FaceDetectorProcessor;
import io.fotoapparat.facedetector.view.RectanglesView;
import io.fotoapparat.parameter.LensPosition;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.view.CameraView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.lensPosition;


public class CameraActivity extends AppCompatActivity {
    private final PermissionDelegate permissionsDelegate = new PermissionDelegate(this);
    private boolean hasCameraPermission;
    private CameraView cameraView,view;
    private RectanglesView rectanglesView;

    private FotoapparatSwitcher fotoapparatSwitcher;
    private Fotoapparat frontFotoapparat;
    private Fotoapparat backFotoapparat;

//    private Button btnTakePicture;

    File imageFileName = null;
    File imageFileFolder = null;
    private MediaScannerConnection msConn;
    Context context;
    CameraErrorCallback cameraErrorCallback;
    Display display;
    boolean status_camera = false;

    Button camDepan,camBack;
    Button getpicture;
    Snackbar bar;
    int brightness = 0;
    Crud crudSqlite;
    private Api_Interface api_interface;
    private SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().hide();
        crudSqlite = new Crud(this);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        sharedPref = new SharedPref(this);

        cameraView = (CameraView) findViewById(R.id.camera_view);
        rectanglesView = (RectanglesView) findViewById(R.id.rectanglesView);
        camBack = findViewById(R.id.camBack);
        camDepan = findViewById(R.id.camDepan);
        getpicture = findViewById(R.id.getpicture);



//        if(crudSqlite.getData().size() == 0)
//        {
//            get_locationPresence();
//        }

        hasCameraPermission = permissionsDelegate.hasCameraPermission();

        display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (hasCameraPermission) {
            cameraView.setVisibility(View.VISIBLE);

        } else {
            permissionsDelegate.requestCameraPermission();
        }


        frontFotoapparat = createFotoapparat(LensPosition.FRONT);
        backFotoapparat = createFotoapparat(LensPosition.BACK);
        fotoapparatSwitcher = FotoapparatSwitcher.withDefault(frontFotoapparat);

//        btnTakePicture = findViewById(R.id.btnTakePicture);

        camDepan.setOnClickListener(l->{
            frontFotoapparat = createFotoapparat(LensPosition.FRONT);

            fotoapparatSwitcher.switchTo(frontFotoapparat);
            status_camera = false;
        });

        camBack.setOnClickListener(l->{
            backFotoapparat = createFotoapparat(LensPosition.BACK);
            fotoapparatSwitcher.switchTo(backFotoapparat);
            status_camera = true;
        });


        getpicture.setOnClickListener(l->{
            snackbarDialog("Mohon tunggu jangan di geser. akan dialihkan...");
            if(status_camera == true)
            {

                backFotoapparat.takePicture().toBitmap().whenDone(new PendingResult.Callback<BitmapPhoto>() {
                    @Override
                    public void onResult(BitmapPhoto bitmapPhoto) {

                        savePhoto(RotateBitmap(bitmapPhoto.bitmap,90));

                    }
                });
            }
            else
            {
                frontFotoapparat.takePicture().toBitmap().whenDone(new PendingResult.Callback<BitmapPhoto>() {
                    @Override
                    public void onResult(BitmapPhoto bitmapPhoto) {

                        savePhoto(RotateBitmap(bitmapPhoto.bitmap,-90));
                    }
                });
            }


        });


//        View switchCameraButton = findViewById(R.id.switchCamera);
//        switchCameraButton.setVisibility(
//                canSwitchCameras()
//                        ? View.VISIBLE
//                        : View.GONE
//        );
//        switchCameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchCamera();
//            }
//        });

    }

    private void get_locationPresence()
    {
        Call<List<Ent_lokasi_presence>> callLogin = api_interface.locationPresence_per_nip(sharedPref.sp.getString("nip",""));
        callLogin.enqueue(new Callback<List<Ent_lokasi_presence>>() {
            @Override
            public void onResponse(Call<List<Ent_lokasi_presence>> call, Response<List<Ent_lokasi_presence>> response) {

                List<Ent_lokasi_presence> listLokasi = response.body();

                if(listLokasi.size() != 0)
                {
                    for(int a=0;a<listLokasi.size();a++)
                    {
                        crudSqlite.InsertData(listLokasi.get(a).getId_lokasi_presence(),listLokasi.get(a).getLokasi_presence(),
                                listLokasi.get(a).getLatitude(),listLokasi.get(a).getLongitude(),listLokasi.get(a).getId_unit_kerja());
                        //                            Toast.makeText(getApplicationContext(),listLokasi.get(a).getLatitude(),Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    Log.i("Info","Data Kosong");
                    showSnackbar("Belum mempunyai lokasi presensi atau lokasi presensi kosong","Kembali");

                }


            }

            @Override
            public void onFailure(Call<List<Ent_lokasi_presence>> call, Throwable t) {

//                Log.d("error",t.getMessage());
                bar.dismiss();
                showSnackbar("Masalah dengan koneksi anda..!","Refresh");
            }
        });
    }

    private String tanggal()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyddmmHms");
        Date date = new Date();
        return sdf.format(date);
    }

    private void showSnackbar(String text, String action)
    {
        bar = Snackbar.make(findViewById(R.id.sb_camera_1),text, Snackbar.LENGTH_INDEFINITE);
        bar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
                startActivity(new Intent(CameraActivity.this,Menu_Utama_Activity.class));
                finish();
            }
        });
        bar.show();

    }


    private void snackbarDialog(String text)
    {

        bar = Snackbar.make(findViewById(R.id.sb_camera_1),text, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        contentLay.setBackgroundResource(R.color.colorPrimary);
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.setActionTextColor(Color.GRAY);
        bar.show();
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void savePhoto(Bitmap bmp) {
        imageFileFolder = new File(getFilesDir().toString(), "Presensi");
        imageFileFolder.mkdirs();
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.MONTH)) + fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.HOUR_OF_DAY)) + fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));
        imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
        try {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());

            if(isNetworkAvailable())
            {
                Intent intent = new Intent(CameraActivity.this, Presence_Activity.class);
                intent.putExtra("presensi", getIntent().getExtras().getString("presensi"));
                intent.putExtra("imageUri",Uri.fromFile(imageFileName).toString());
                intent.putExtra("currentPhotoPath",imageFileName.getAbsolutePath());
                bar.dismiss();
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(CameraActivity.this, Presence_Offline_Activity.class);
                intent.putExtra("presensi", getIntent().getExtras().getString("presensi"));
                intent.putExtra("imageUri",Uri.fromFile(imageFileName).toString());
                intent.putExtra("currentPhotoPath",imageFileName.getAbsolutePath());
                bar.dismiss();
                startActivity(intent);
            }


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


    private boolean canSwitchCameras() {
        return frontFotoapparat.isAvailable() == backFotoapparat.isAvailable();
    }

    private Fotoapparat createFotoapparat(LensPosition position) {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .lensPosition(lensPosition(position))
                .frameProcessor(
                        FaceDetectorProcessor.with(this)
                                .listener(new FaceDetectorProcessor.OnFacesDetectedListener() {
                                    @Override
                                    public void onFacesDetected(List<Rectangle> faces) {
                                        Log.d("&&&", "Detected faces: " + faces.size());
                                        if(faces.size() >= 1)
                                        {
                                            getpicture.setVisibility(View.VISIBLE);
                                            rectanglesView.setRectangles(faces);

                                        }
                                        else
                                        {
                                            getpicture.setVisibility(View.GONE);
                                            rectanglesView.setRectangles(faces);
                                        }
                                    }
                                })
                                .build()
                )
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .build();
    }

    private void switchCamera() {
        if (fotoapparatSwitcher.getCurrentFotoapparat() == frontFotoapparat) {
            fotoapparatSwitcher.switchTo(backFotoapparat);
            status_camera = true;
        } else {
            fotoapparatSwitcher.switchTo(frontFotoapparat);
            status_camera = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission) {
            fotoapparatSwitcher.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (hasCameraPermission) {
            fotoapparatSwitcher.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            fotoapparatSwitcher.start();
            cameraView.setVisibility(View.VISIBLE);
        }
    }


}