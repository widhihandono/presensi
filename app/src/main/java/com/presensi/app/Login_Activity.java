package com.presensi.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Settingan_Emulator;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.Model.Ent_pegawai;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login_Activity extends AppCompatActivity {
    private TextView tvLogin, tvResetPass;
    EditText etNip, etPass;
    private Api_Interface api_interface;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private boolean dialogSet = false;
    private Snackbar bar;


    //SQLITE
    Crud crudSQlite;

    //SET EMULATOR
    String bootloader,host,id;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        getSupportActionBar().hide();

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
//        tvLogin.setText(bootloader+",,,"+host+",,,"+id+",,,"+board+",,,"+brand+",,,"+fingerprint+",,,"+hardware);
        cekEmulator(bootloader,host,id);

//        if (bootloader.equals("uboot") || bootloader.equals("moto") || isEmulator() == true ||
//                host.equals("se.infra") || host.equals("SWHE7705") || id.equals("LMY48Z"))
//        {
//            showDialogEmulator();
//        }


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
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
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





}
