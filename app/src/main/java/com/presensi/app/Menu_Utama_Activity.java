package com.presensi.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Settingan_Emulator;
import com.presensi.app.Model.Ent_Time;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.Model.Ent_pegawai;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.SharedPref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Menu_Utama_Activity extends AppCompatActivity implements AppCompatCallback, View.OnClickListener {
    private LinearLayout lnPresensiDatang,lnPresensiPulang;
    private TextView tvJam,tvTgl,tvHistory,tvNip,tvNama,
            tvVersion,tvEditProfile,tvUpdateData,tvPresensiOffline,tvHelp;
    private Handler handler = new Handler();
    private Api_Interface api_interface;
    private SharedPref sharedPref;
    private boolean dialogSet;
    String jam = "",waktu="",presensi="";
    final int sdk = android.os.Build.VERSION.SDK_INT;
    RemoteViews notificationLayout;
    Crud crudSqlite;
    Snackbar bar;
    private CircleImageView imgProfile;

    //Camera
    private static final int CAMERA_REQUEST = 1;
    private static final int CAMERA_PERMISSION = 2;
    private Uri mImageUri, mImageUri2;
    String currentPhotoPath;
    Bitmap bitmap;

    String bootloader,host,id;

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE};

    int permsRequestCode = 200;
    private static final String TAG = "Main_Navigation";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    //Animation
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab_add_unit_kerja,fab_list;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private SwipeRefreshLayout swLayout;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__utama_);
        getSupportActionBar().hide();


        crudSqlite = new Crud(this);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        sharedPref = new SharedPref(this);

        lnPresensiDatang = findViewById(R.id.lnPresensiDatang);
        lnPresensiPulang = findViewById(R.id.lnPresensiPulang);
        tvJam = findViewById(R.id.tvJam);
        tvTgl = findViewById(R.id.tvTgl);
        tvHistory = findViewById(R.id.tvHistory);
        tvNip = findViewById(R.id.tvNip);
        tvNama = findViewById(R.id.tvNama);
        tvVersion = findViewById(R.id.tvVersion);
        imgProfile = findViewById(R.id.imgProfile);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvUpdateData = findViewById(R.id.tvUpdateData);
        tvPresensiOffline = findViewById(R.id.tvPresensiOffline);
        tvHelp = findViewById(R.id.tvHelp);

        tvVersion.setText("Version 3.2");
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab_add_unit_kerja = (FloatingActionButton)findViewById(R.id.fab_add_unit_kerja);
        fab_list = (FloatingActionButton)findViewById(R.id.fab_list);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab_add_unit_kerja.setOnClickListener(this);
        fab_list.setOnClickListener(this);
        swLayout = findViewById(R.id.swLayout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        }

        bootloader = Build.BOOTLOADER;
        host = Build.HOST;
        id = Build.ID;

        cekEmulator(bootloader,host,id);

        if (isEmulator() == true)
        {
            showDialogEmulator();
        }
        else {


            if (sharedPref.getSPSudahLogin() == false) {
                startActivity(new Intent(this, Login_Activity.class));
                finish();
            }
            else
            {

//                createFolder();

                getVersion();

//                check_and_get_data(); //cek from database
//                waktuServer();


                if (sharedPref.sp.getString("level", "").equals("master")) {
                    fab.setVisibility(View.VISIBLE);
                    fab.startAnimation(fab_open);
                    enableDisableButton(false);
                } else {
                    fab.setVisibility(View.GONE);
                    fab.startAnimation(fab_close);
                    enableDisableButton(true);
                }

                //cek Time automatic
                if(!isTimeAutomatic(Menu_Utama_Activity.this))
                {
                    showDialogCheckTime();
//                    Toast.makeText(getApplicationContext(),"Time harus Auto",Toast.LENGTH_LONG).show();
                }

//                crudSqlite.hapus();
                displayLocationSettingsRequest(this);

                tvNip.setText("NIP : " + sharedPref.sp.getString("nip", ""));
                tvNama.setText("Nama : " + sharedPref.sp.getString("nama", ""));

                Glide.with(this)
                        .load(Uri.parse("http://sipgan.magelangkab.go.id/sipgan/images/photo/" + sharedPref.sp.getString("nip", "") + ".jpg"))
                        .into(imgProfile);
                //            Toast.makeText(getApplicationContext(), "Android", Toast.LENGTH_LONG).show();



                tvTgl.setText(tanggal());
                handler.postDelayed(runnable, 1000);


                //Get Radius
//                radius();

                lnPresensiDatang.setOnClickListener(l -> {
//                    get_locationPresence();
                    presensi = "Datang";
//                showDialogPhoto();
                    if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(this, CameraActivity.class);
                        intent.putExtra("presensi", presensi);
                        intent.putExtra("camera", "depan");
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(this, CameraActivity.class);
                        intent.putExtra("presensi", presensi);
                        startActivity(intent);
                    }

                });

                lnPresensiPulang.setOnClickListener(l -> {
//                    get_locationPresence();
                    presensi = "Pulang";
//                showDialogPhoto();

                    if (sdk >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(this, CameraActivity.class);
                        intent.putExtra("presensi", presensi);
                        intent.putExtra("camera", "depan");
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(this, CameraActivity.class);
                        intent.putExtra("presensi", presensi);
                        startActivity(intent);
                    }

                });

                tvHistory.setOnClickListener(l -> {
                    Call<Ent_Time> callJamUpdate = api_interface.cekjamupdate();
                    callJamUpdate.enqueue(new Callback<Ent_Time>() {
                        @Override
                        public void onResponse(Call<Ent_Time> call, Response<Ent_Time> response) {
                            if(response.isSuccessful())
                            {
                                if(response.body().getResponse() == 1)
                                {
                                    Intent intent = new Intent(Menu_Utama_Activity.this, History_Presensi_Activity.class);
                                    startActivity(intent);
                                }
                                else
                                {
//                                bar.dismiss();
                                    showDialogPesanJamUpdate(response.body().getPesan());
//                                    Toast.makeText(Menu_Utama_Activity.this,response.body().getPesan(),Toast.LENGTH_LONG).show();

                                }
                            }
                            else
                            {
                                showSnackbar("Coba beberapa saat lagi","Refresh");
                            }

                        }

                        @Override
                        public void onFailure(Call<Ent_Time> call, Throwable t) {
//                            bar.dismiss();
//                            enableDisableButton(false);
//                            showSnackbar("Terjadi gangguan dengan koneksi anda atau server","Refresh");
                            Log.d("error","Terjadi gangguan dengan koneksi anda atau server");
                        }
                    });



                });


            }


            imgProfile.setOnClickListener(l -> {
                startActivity(new Intent(this, Profile_Activity.class));
                finish();
            });

            tvEditProfile.setOnClickListener(l -> {
                startActivity(new Intent(this, Profile_Activity.class));
                finish();
            });

            swLayout.setColorSchemeResources(R.color.colorPrimary);

            swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            crudSqlite.hapus();
                            snackbarDialog();
                            swLayout.setRefreshing(false);
                            check_and_get_data();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    }, 5000);
                }
            });

        }

        tvUpdateData.setOnClickListener(l->{
            if(isNetworkAvailable())
            {
                snackbarDialog();
//            crudSqlite.hapus();
                    check_and_get_data();
            }
            else
            {
                showDialogOnNetwork();
            }


        });

        tvPresensiOffline.setText("Presensi Belum Dikirim ("+crudSqlite.getData_Presence().size()+")");
        tvPresensiOffline.setOnClickListener(l->{
            startActivity(new Intent(Menu_Utama_Activity.this,List_Offline_PresenceActivity.class));
            finish();
        });

        tvHelp.setOnClickListener(l->{
            startActivity(new Intent(Menu_Utama_Activity.this,Bantuan_Activity.class));
            finish();
        });

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab_add_unit_kerja:

                startActivity(new Intent(this, List_Unit_Kerja_Activity.class));
                break;
            case R.id.fab_list:

                startActivity(new Intent(this, List_Location_Activity.class));
                break;
        }
    }


    private void cekEmulator(String boat,String host,String id)
    {
        Call<Ent_Settingan_Emulator> setEmulator = api_interface.set_emulator(boat,host,id);
        setEmulator.enqueue(new Callback<Ent_Settingan_Emulator>() {
            @Override
            public void onResponse(Call<Ent_Settingan_Emulator> call, Response<Ent_Settingan_Emulator> response) {

                if(response.isSuccessful())
                {
                    if(response.body().getResponse() == 1 || isEmulator() == true)
                    {
                        showDialogEmulator();
                    }
                }
                else
                {
                    Toast.makeText(Menu_Utama_Activity.this,"Terjadi gangguan pada server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Ent_Settingan_Emulator> call, Throwable t) {
                Log.d("error", "Terjadi gangguan dengan koneksi anda ");
            }
        });
    }

    private boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }


    //=========Check Internet Connection==========================
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showDialogOnNetwork(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Mohon hidupkan koneksi internet anda. Jika ingin Ambil Settingan")
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }
    //===============================================

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    private void check_and_get_data()
    {
        Call<Ent_pegawai> callLogin = api_interface.login(sharedPref.sp.getString("nip",""),sharedPref.sp.getString("password",""));
        callLogin.enqueue(new Callback<Ent_pegawai>() {
            @Override
            public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                if(!response.isSuccessful())
                {
                    showSnackbar_noRefresh("Terjadi gangguan pada server, tunggu beberapa saat lagi...mohon maaaf atas ketidaknyamanan ini.");
                }
                else {
                    if (response.body().getResponse() == 1) {
                        crudSqlite.hapus();
                        enableDisableButton(true);
                        sharedPref.saveSPString("nama", response.body().getNama());
                        sharedPref.saveSPString("nip", response.body().getNip());
                        sharedPref.saveSPString("no_hp", response.body().getNo_hp());
                        sharedPref.saveSPString("email", response.body().getEmail());
                        sharedPref.saveSPString("id_unit_kerja", response.body().getId_unit_kerja());
                        sharedPref.saveSPString("unit_kerja", response.body().getUnit_kerja());
                        sharedPref.saveSPString("password", sharedPref.sp.getString("password", ""));
                        sharedPref.saveSPString("level", response.body().getLevel());
                        sharedPref.saveSPInt("radius", response.body().getRadius());

                        List<Ent_lokasi_presence> listLokasi = response.body().getLokasi();

                        if (listLokasi.size() != 0) {
                            for (int a = 0; a < listLokasi.size(); a++) {
                                crudSqlite.InsertData(listLokasi.get(a).getId_lokasi_presence(), listLokasi.get(a).getLokasi_presence(),
                                        listLokasi.get(a).getLatitude(), listLokasi.get(a).getLongitude(), listLokasi.get(a).getId_unit_kerja());
                                //                            Toast.makeText(getApplicationContext(),listLokasi.get(a).getLatitude(),Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Lokasi Presensi tidak ada", Toast.LENGTH_LONG).show();
                            Log.i("Info", "Data Kosong");
                        }

                        bar.dismiss();
                        startActivity(new Intent(Menu_Utama_Activity.this, Menu_Utama_Activity.class));
                        finish();

                    } else if (response.body().getResponse() == 2) {
                        showDialog_cekData("Password anda sudah berubah, mohon keluar ! " + response.body().getPassword());
                    } else {

                        showDialog_cekData("Nip anda sudah berubah, mohon keluar !");
                    }
                }
            }

            @Override
            public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                bar.dismiss();
                enableDisableButton(false);
                showSnackbar("Terjadi gangguan dengan koneksi anda atau server","Refresh");
                Log.d("error","Terjadi gangguan dengan koneksi anda atau server");
            }
        });
    }


    private void showDialogEmulator(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Terdeteksi Menggunakan Emulator !")
                .setIcon(R.drawable.ic_warning)
                .setCancelable(false)
                .setPositiveButton("Keluar",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }


    private void getVersion()
    {
        Call<Ent_lokasi_presence> callVersion = api_interface.version();
        callVersion.enqueue(new Callback<Ent_lokasi_presence>() {
            @Override
            public void onResponse(Call<Ent_lokasi_presence> call, Response<Ent_lokasi_presence> response) {
                if(!response.isSuccessful())
                {
                    Log.e("versi : ","Terjadi gangguan pada server, tunggu beberapa saat lagi");
//                    showSnackbar("Terjadi gangguan pada server, tunggu beberapa saat lagi","Refresh");
//                    Toast.makeText(getApplicationContext(), "Terjadi gangguan pada server, tunggu beberapa saat lagi",Toast.LENGTH_LONG).show();
//                    lnPresensiDatang.setEnabled(false);
//                    lnPresensiPulang.setEnabled(false);

                }
                else
                {
                    sharedPref.saveSPInt("radius",response.body().getRadius());
                    if(Float.parseFloat(response.body().getVersion().split(" ")[1])
                            > Float.parseFloat(tvVersion.getText().toString().split(" ")[1]) )
                    {
                        showDialogUpdateSystem(response.body().getVersion().split(" ")[1]);
                    }
                    else
                    {
                        Log.i("versi : ","Versi Sudah Memenuhi");
                    }
                }



            }

            @Override
            public void onFailure(Call<Ent_lokasi_presence> call, Throwable t) {
                Log.d("Version_Error","Masalah dengan koneksi anda atau server..!");
//                bar.dismiss();
//                showSnackbar("Masalah dengan koneksi anda atau server..!","Refresh");
            }

        });
    }


    //================Get Radius===================
//    private void radius()
//    {
//        Call<Ent_Presensi> callRadius = api_interface.radius();
//        callRadius.enqueue(new Callback<Ent_Presensi>() {
//            @Override
//            public void onResponse(Call<Ent_Presensi> call, Response<Ent_Presensi> response) {
//
//                sharedPref.saveSPInt("radius",response.body().getRadius());
//            }
//
//            @Override
//            public void onFailure(Call<Ent_Presensi> call, Throwable t) {
//
////                Log.i("message",t.getMessage());
//                bar.dismiss();
//                showSnackbar("Masalah dengan koneksi anda..!","Refresh");
//            }
//        });
//    }

    private String timer()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    private String tanggal()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        Date date = new Date();
        return sdf.format(date);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tvJam.setText(timer());

            handler.postDelayed(this,1000);
        }

    };




    private void snackbarDialog()
    {
        enableDisableButton(false);
        bar = Snackbar.make(findViewById(R.id.snackbar_utama),"Please Wait...Get Data From Server", Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentLay.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        contentLay.setLayoutParams(layoutParams);
        contentLay.setBackgroundResource(R.color.colorPrimary);
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();

    }

    private void showSnackbar(String text, String action)
    {
        enableDisableButton(false);
        bar = Snackbar.make(findViewById(R.id.snackbar_utama),text, Snackbar.LENGTH_INDEFINITE);
        bar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
                startActivity(new Intent(Menu_Utama_Activity.this,Menu_Utama_Activity.class));
                finish();
            }
        });
        bar.show();

    }

    private void showSnackbar_noRefresh(String text)
    {
        bar = Snackbar.make(findViewById(R.id.snackbar_utama),text, Snackbar.LENGTH_INDEFINITE);
        View sbView = bar.getView();
        sbView.setBackgroundResource(R.color.colorPrimary);
        bar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.show();
    }

    private void enableDisableButton(boolean tf)
    {
        tvHistory.setEnabled(tf);
        lnPresensiDatang.setEnabled(tf);
        lnPresensiPulang.setEnabled(tf);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (this.CAMERA_REQUEST == requestCode && resultCode == RESULT_OK) {
//                Bitmap bitmap =  (Bitmap) data.getExtras().get("data");
//                bmpLapor = bitmap;
//                img_lapor.setImageBitmap(bitmap);
                if(presensi.equals("Datang"))
                {
                    Intent intent = new Intent(this, Presence_Activity.class);
                    intent.putExtra("presensi", "Datang");
                    intent.putExtra("imageUri",mImageUri.toString());
                    intent.putExtra("currentPhotoPath",currentPhotoPath);
                    startActivity(intent);
                    finish();

                }
                else if(presensi.equals("Pulang"))
                {
                    Intent intent = new Intent(this, Presence_Activity.class);
                    intent.putExtra("presensi", "Pulang");
                    intent.putExtra("imageUri",mImageUri.toString());
                    intent.putExtra("currentPhotoPath",currentPhotoPath);
                    startActivity(intent);
                    finish();

                }

            }
        }
    }





    private void showDialogKeluar(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title dialog
        alertDialogBuilder.setTitle("Yakin pingin keluar ?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Tekan Ya untuk keluar")
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        handler.removeCallbacks(runnable);
                        finishAffinity();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
//                    setupLocationListener();

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        Menu_Utama_Activity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException | ClassCastException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    //cek data presensi offline
    private void showDialog_check_data_presence(String nip,String nama,int jml){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog

                if(nip.equals(sharedPref.sp.getString("nip","")))
                {
                    alertDialogBuilder
                            .setMessage("Masih ada "+ jml +" data presensi yang belum di kirim ke database server !. " +
                                    "Mohon kirim data presensi anda dahulu. Nip : "+ nip)
                            .setIcon(R.drawable.ic_pen)
                            .setCancelable(false)
                            .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Menu_Utama_Activity.this,List_Offline_PresenceActivity.class));
                                finish();
                            }
                            });
                }
                else if(!nip.equals(sharedPref.sp.getString("nip","")))
                {
                    alertDialogBuilder
                            .setMessage("Masih ada "+ jml +" data presensi orang lain yang belum di kirim ke database server !. " +
                                    "Mohon kirim data presensi dengan Nip : "+ nip+" tersebut")
                            .setIcon(R.drawable.ic_pen)
                            .setCancelable(false)
                            .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDialogLogOut();
                                dialog.dismiss();
                            }
                            });

                }


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btnPostiv = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPostiv.setTextColor(Color.RED);
        Button btnNegativ = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnNegativ.setTextColor(Color.RED);
    }


    private void showDialogCheckTime(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Dilarang merubah jam pada handphone")
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("Keluar",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }

    private void showDialogPesanJamUpdate(String pesan){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(pesan)
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }

    private void showDialogUpdateSystem(String versi){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Aplikasi Sudah Update ke versi : "+versi+", Mohon Untuk Update aplikasi. Jika tidak ada tulisan Update di Playstore, Uninstall aplikasi, kemudian Install kembali.")
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("Lanjutkan Update",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        if(crudSqlite.getData_Presence_all_nip().size() > 0)
                        {
                            showDialog_check_data_presence(crudSqlite.getData_Presence_all_nip().get(0).getNip(),crudSqlite.getData_Presence_all_nip().get(0).getNama(),crudSqlite.getData_Presence_all_nip().size());
//                            dialog.dismiss();
                        }
                        else
                        {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                finish();
//                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=siaba")));
//                                finish();
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                finish();
                            }

                        }


                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }


    private void showDialog_cekData(String pesan){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(pesan)
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("Keluar",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        handler.removeCallbacks(runnable);

                        Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                        crudSqlite.hapus();
                        sharedPref.saveSPBoolean(SharedPref.SP_SUDAH_LOGIN, false);
//                sharedPref.saveSPInt("id_user", 0);
                        sharedPref.saveSPString("nama", "");
                        sharedPref.saveSPString("nip", "");
                        sharedPref.saveSPString("no_hp", "");
                        sharedPref.saveSPString("id_unit_kerja", "");
                        sharedPref.saveSPString("email", "");
                        sharedPref.saveSPString("password","");
                        sharedPref.saveSPString("level","");
//                sharedPref.saveSPInt("id_role", 0);
//                sharedPref.saveSPString("name_role", "");
                        sharedPref.saveSPString("unit_kerja", "");
                        sharedPref.saveSPInt("radius",0);
                        startActivity(intent);
                        finish();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }

    //dialog to logout
    private void showDialogLogOut(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu_Utama_Activity.this);

        // set title dialog
        alertDialogBuilder.setTitle("Yakin pingin Logout ?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Tekan Ya untuk Logout")
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {


                        Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                        crudSqlite.hapus();
                        sharedPref.saveSPBoolean(SharedPref.SP_SUDAH_LOGIN, false);
//                sharedPref.saveSPInt("id_user", 0);
                        sharedPref.saveSPString("nama", "");
                        sharedPref.saveSPString("nip", "");
                        sharedPref.saveSPString("no_hp", "");
                        sharedPref.saveSPString("id_unit_kerja", "");
//                sharedPref.saveSPInt("id_role", 0);
//                sharedPref.saveSPString("name_role", "");
                        sharedPref.saveSPString("password","");
                        sharedPref.saveSPString("unit_kerja", "");
                        sharedPref.saveSPString("email","");
                        sharedPref.saveSPString("level","");
                        sharedPref.saveSPInt("radius",0);
                        startActivity(intent);
                        finish();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();

        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
    }


    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab_add_unit_kerja.startAnimation(fab_close);
            fab_list.startAnimation(fab_close);
            fab_add_unit_kerja.setClickable(false);
            fab_list.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab_add_unit_kerja.startAnimation(fab_open);
            fab_list.startAnimation(fab_open);
            fab_add_unit_kerja.setClickable(true);
            fab_list.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }


    @Override
    public void onBackPressed() {
        showDialogKeluar();
    }
}
