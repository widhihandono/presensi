package com.presensi.app;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Settingan_Emulator;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.Model.Ent_pegawai;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.SharedPref;
import com.scottyab.rootbeer.RootBeer;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login_Activity extends AppCompatActivity implements LocationListener {
    private TextView tvLogin, tvResetPass;
    EditText etNip, etPass;
    private Api_Interface api_interface;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private boolean dialogSet = false;
    private Snackbar bar;

    LocationManager locationManager;
    Geocoder geocoder;
    Location location;
    String status = "", latitude = "0", longitude = "0", encode = "", ket_presence = "Imei sudah digunakan oleh : ";


    //SQLITE
    Crud crudSQlite;

    //SET EMULATOR
    String bootloader, host, id;

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};
    int permsRequestCode = 200;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        getSupportActionBar().hide();

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        crudSQlite = new Crud(this);

//        progressDialog = new ProgressDialog(this);
        sharedPref = new SharedPref(this);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        tvLogin = findViewById(R.id.tvLogin);
        etNip = findViewById(R.id.etNip);
        etPass = findViewById(R.id.etPass);
        tvResetPass = findViewById(R.id.tvResetPass);

        bootloader = Build.BOOTLOADER;
        host = Build.HOST;
        id = Build.ID;
        String board = Build.BOARD;
        String brand = Build.BRAND;
        String fingerprint = Build.FINGERPRINT;
        String hardware = Build.HARDWARE;
//
        tvLogin.setText("Login");



        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRooted()) {
            showDialogEmulator("Aplikasi tidak bisa digunakan di HP yang sudah di Root dan dilarang menggunakan Emulator !");
        } else {
            executeShellCommand("su");
            cekEmulator(bootloader,host,id);
        }




//        if (bootloader.equals("uboot") || bootloader.equals("moto") || isEmulator() == true ||
//                host.equals("se.infra") || host.equals("SWHE7705") || id.equals("LMY48Z"))
//        {
//            showDialogEmulator();
//        }

//======================================= check Location=======================================================================================
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        FusedLocationProviderClient flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        } else {
            locationManager.getBestProvider(criteria,true);
            locationManager.requestLocationUpdates(1000, 1, criteria, this,null);

            if(locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
//                    Toast.makeText(Login_Activity.this,latitude,Toast.LENGTH_LONG).show();
                    if (isMockLocationOn(location, this)) {
                        showDialogEmulator("Anda terdeteksi menggunakan Fake GPS !");
                    }


                } else {

                    flpc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null)
                            {
                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());
                                if (isMockLocationOn(location, Login_Activity.this)) {
                                    showDialogEmulator("Anda terdeteksi menggunakan Fake GPS !");
                                }

                                //                        Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        }


        if(isMockLocationEnabled())
        {
            showDialogEmulator("Fake GPS Detected");
        }

        //==============================================================================================================================


        if(!isTimeAutomatic(this) || !isTimeZoneAutomatic(this))
        {
            showDialogEmulator("Terdeteksi merubah jam");
//                    Toast.makeText(getApplicationContext(),"Time harus Auto",Toast.LENGTH_LONG).show();
        }


        tvLogin.setOnClickListener(l -> {

//            showProgressDialog();
            snackbarDialog();
            Call<Ent_pegawai> callLogin = api_interface.login(etNip.getText().toString(), etPass.getText().toString());
            callLogin.enqueue(new Callback<Ent_pegawai>() {
                @Override
                public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                    if (!response.isSuccessful()) {
                        showSnackbar("Terjadi gangguan pada server, tunggu beberapa saat lagi...mohon maaaf atas ketidaknyamanan ini.");
                    } else {
                        if (response.body().getResponse() == 1) {
                            bar.dismiss();
                            tvLogin.setEnabled(true);
                            Intent intent = new Intent(getApplicationContext(), Menu_Utama_Activity.class);

                            sharedPref.saveSPBoolean(SharedPref.SP_SUDAH_LOGIN, true);
                            sharedPref.saveSPString("nama", response.body().getNama());
                            sharedPref.saveSPString("nip", response.body().getNip());
                            sharedPref.saveSPString("no_hp", response.body().getNo_hp());
                            sharedPref.saveSPString("email", response.body().getEmail());
                            sharedPref.saveSPString("id_unit_kerja", response.body().getId_unit_kerja());
                            sharedPref.saveSPString("unit_kerja", response.body().getUnit_kerja());
                            sharedPref.saveSPString("password", etPass.getText().toString());
                            sharedPref.saveSPString("level", response.body().getLevel());
                            sharedPref.saveSPInt("radius", response.body().getRadius());

                            List<Ent_lokasi_presence> listLokasi = response.body().getLokasi();

                            if (listLokasi.size() != 0) {
                                for (int a = 0; a < listLokasi.size(); a++) {
                                    crudSQlite.InsertData(listLokasi.get(a).getId_lokasi_presence(), listLokasi.get(a).getLokasi_presence(),
                                            listLokasi.get(a).getLatitude(), listLokasi.get(a).getLongitude(), listLokasi.get(a).getId_unit_kerja());
                                    //                            Toast.makeText(getApplicationContext(),listLokasi.get(a).getLatitude(),Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Lokasi Presensi tidak ada", Toast.LENGTH_LONG).show();
                                Log.i("Info", "Data Kosong");
                            }


                            startActivity(intent);
                            finish();
                        } else if (response.body().getResponse() == 2) {
                            bar.dismiss();
                            tvLogin.setEnabled(true);
                            showSnackbar("Password Salah !");
                        } else if (response.body().getResponse() == 3) {
                            bar.dismiss();
                            tvLogin.setEnabled(true);
                            showSnackbar("Anda belum memiliki lokasi presensi !");
                        } else {
                            bar.dismiss();
                            tvLogin.setEnabled(true);
                            showSnackbar("Anda Belum Terdaftar");
                        }
                    }

                }

                @Override
                public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                    bar.dismiss();
                    tvLogin.setEnabled(true);
                    showSnackbar("Terjadi gangguan dengan koneksi anda atau server");
                    Log.d("error", "Terjadi gangguan dengan koneksi anda atau server");
                }
            });
        });

        tvResetPass.setOnClickListener(l -> {
            startActivity(new Intent(this, Reset_password_Activity.class));
            finish();
        });


    }

    private void executeShellCommand(String su) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(su);
            showDialogEmulator("Aplikasi tidak bisa digunakan di HP yang sudah di Root dan dilarang menggunakan Emulator !");
//            Toast.makeText(Login_Activity.this, "It is rooted device", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
//            showDialogEmulator("No Rooted");
        } finally {
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) { }
            }
        }
    }

    public boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }


    public boolean isMockLocationEnabled() {
        boolean isMockLocation = false;
        try {
            //if marshmallow
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
            } else {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return isMockLocation;
        }
        return isMockLocation;
    }



    public static boolean isMockLocationOn(Location location, Context context) {
        boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMock;
    }


    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static boolean isTimeZoneAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME_ZONE, 0) == 1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "Tersentuh", Toast.LENGTH_LONG).show();
        return super.onTouchEvent(event);
    }

    //    private void showProgressDialog()
//    {
//        dialogSet = true;
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Please Wait...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//    }
//        if (bootloader.equals("uboot") || isEmulator() == true || host.equals("se.infra") || id.equals("LMY48Z"))
//    {
//        showDialogEmulator();
//    }




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
                        showDialogEmulator(response.body().getPesan());
                    }
                }
                else
                {
                    Toast.makeText(Login_Activity.this,"Terjadi gangguan pada server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Ent_Settingan_Emulator> call, Throwable t) {

                showSnackbar("Network Failed");
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
                || Build.PRODUCT.contains("simulator")
                || Build.HOST.startsWith("Build") //MSI
                || Build.BOARD == "QC_Reference_Phone"; //bluestacks
    }

    private void showDialogEmulator(String pesan){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(pesan)
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


    private void snackbarDialog()
    {
        tvLogin.setEnabled(false);
        bar = Snackbar.make(findViewById(R.id.sb_login),"Authentification...", Snackbar.LENGTH_INDEFINITE);
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

    private void showSnackbar(String text)
    {
        bar = Snackbar.make(findViewById(R.id.sb_login),text, Snackbar.LENGTH_INDEFINITE);
        View sbView = bar.getView();
        sbView.setBackgroundResource(R.color.colorPrimary);
        bar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.setActionTextColor(Color.GRAY);
        bar.show();
    }

    private void showDialog_resetPassword(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_Activity.this);

        final EditText input = new EditText(Login_Activity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Input No.Hp");
        input.setHintTextColor(Color.GRAY);
        input.setBackgroundResource(R.drawable.border_table);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        input.setLayoutParams(lp);
        // set title dialog
        alertDialogBuilder.setTitle("Masukkan no.Hp anda untuk reset password");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Tekan Ya untuk reset Password")
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        Call<Ent_pegawai> callEditNomorHp = api_interface.resetPassword(input.getText().toString());

                        callEditNomorHp.enqueue(new Callback<Ent_pegawai>() {
                            @Override
                            public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                                if(response.body().getResponse() == 1)
                                {
//                                    Toast.makeText(getApplicationContext(),"Password berhasil dikirim ke No.Hp anda, Cek SMS",Toast.LENGTH_LONG).show();
                                    showSnackbar("Password berhasil dikirim ke No.Hp anda, Cek SMS");
                                    dialog.cancel();
                                }
                                else if(response.body().getResponse() == 0)
                                {
//                                    Toast.makeText(getApplicationContext(),"Password gagal diubah",Toast.LENGTH_LONG).show();
                                    showSnackbar("Password gagal diubah");
                                    dialog.cancel();
                                }
                                else if(response.body().getResponse() == 2)
                                {
//                                    Toast.makeText(getApplicationContext(),"Nomor belum terdaftar, data tidak ada",Toast.LENGTH_LONG).show();
                                    showSnackbar("Nomor belum terdaftar, data tidak ada");
                                    dialog.cancel();
                                }
                                else if(response.body().getResponse() == 3)
                                {
                                    showSnackbar("Password belum berhasil diubah, coba lagi");
                                    dialog.cancel();
                                }
                            }

                            @Override
                            public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                                showSnackbar("Terjadi gangguan koneksi");
                            }
                        });

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
        alertDialog.setView(input);
        // menampilkan alert dialog
        alertDialog.show();

        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
