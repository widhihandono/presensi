package com.presensi.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_pegawai;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.SharedPref;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile_Activity extends AppCompatActivity {
private Toolbar toolbar;
private TextView tvResetPass,tvLogout,tvNip,tvNama,tvUnitKerja;
private EditText etEmail,etNoHP;
private ImageView imgEditEmail,imgEditNoHp,imgBack;
private SharedPref sharedPref;
private Api_Interface api_interface;
Transition transition;
    Crud crudSqlite;
    private ImageView imgProfile;
    private LinearLayout ln_toolbar;
    private Snackbar bar;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = new SharedPref(this);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        crudSqlite = new Crud(this);

        tvResetPass = findViewById(R.id.tvResetPass);
        etNoHP = findViewById(R.id.etNoHp);
        etEmail = findViewById(R.id.etEmail);
        tvLogout = findViewById(R.id.tvLogout);
        imgEditEmail = findViewById(R.id.imgEditEmail);
        imgEditNoHp = findViewById(R.id.imgEditNoHp);
        imgProfile = findViewById(R.id.imgProfile);
        tvNip = findViewById(R.id.tvNip);
        tvNama = findViewById(R.id.tvNama);
        tvUnitKerja = findViewById(R.id.tvUnitKerja);
        imgBack = findViewById(R.id.imgBack);
        ln_toolbar = findViewById(R.id.ln_toolbar);


        tvNip.setText(sharedPref.sp.getString("nip",""));
        tvNama.setText(sharedPref.sp.getString("nama",""));
        tvUnitKerja.setText(sharedPref.sp.getString("unit_kerja",""));
        etEmail.setText(sharedPref.sp.getString("email",""));
        etNoHP.setText(sharedPref.sp.getString("no_hp",""));


        Glide.with(this)
                .load(Uri.parse("http://sipgan.magelangkab.go.id/sipgan/images/photo/"+sharedPref.sp.getString("nip","")+".jpg"))
                .into(imgProfile);


        toolbar.findViewById(R.id.imgCancel).setOnClickListener(l->{
            ln_toolbar.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            etNoHP.setEnabled(false);
            etEmail.setEnabled(false);
        });


        toolbar.findViewById(R.id.imgSave).setOnClickListener(l->{
            if(etNoHP.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() ||
                    etEmail.getText().toString().trim().contains(" ") || etNoHP.getText().toString().trim().contains(" ")
            || etEmail.getText().toString().contains(" ") || etNoHP.getText().toString().contains(" "))
            {
                showSnackbar_noRefresh("Lengkapi data anda !..tidak boleh ada spasi kosong");
            }
            else if(etNoHP.getText().length() < 10)
            {
                showSnackbar_noRefresh("Masukkan nomor hp dengan benar !");
            }
            else if(!etEmail.getText().toString().trim().matches(emailPattern))
            {
                showSnackbar_noRefresh("Masukkan email dengan benar !");
            }
            else
            {
                showDialog_edit_no_hp_or_email();
            }

        });

        tvResetPass.setOnClickListener(l->{
            showDialog_sendPass();
        });

        tvLogout.setOnClickListener(l->{
            showDialogLogOut();
        });

        imgEditNoHp.setOnClickListener(l->{
            etNoHP.setEnabled(true);
            etNoHP.requestFocus();
            ln_toolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        });

        imgEditEmail.setOnClickListener(l->{
            etEmail.setEnabled(true);
            etEmail.requestFocus();
            ln_toolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        });

        imgBack.setOnClickListener(l->{
            startActivity(new Intent(Profile_Activity.this,Menu_Utama_Activity.class));
            finish();
        });
    }

    private void showDialogLogOut(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Activity.this);

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


    private void showDialog_edit_no_hp_or_email(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Activity.this);

        // set title dialog
        alertDialogBuilder.setTitle("Yakin ingin mengubah data ?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Tekan Ya untuk mengubah data")
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        Call<Ent_pegawai> callEditNomorHp = api_interface.edit_no_hp_or_email(sharedPref.sp.getString("nip",""),etNoHP.getText().toString(),etEmail.getText().toString());

                        callEditNomorHp.enqueue(new Callback<Ent_pegawai>() {
                            @Override
                            public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                                if(response.body().getResponse() == 1)
                                {
                                    Toast.makeText(getApplicationContext(),"Data berhasil diubah",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
                                    sharedPref.saveSPString("no_hp", etNoHP.getText().toString());
                                    sharedPref.saveSPString("email",etEmail.getText().toString());
                                    startActivity(intent);
                                    finish();
//                                    ln_toolbar.setVisibility(View.VISIBLE);
//                                    toolbar.setVisibility(View.GONE);
                                    etNoHP.setEnabled(false);
                                    etEmail.setEnabled(false);
                                }
                                else
                                {
//                                    Toast.makeText(getApplicationContext(),"Data gagal diubah",Toast.LENGTH_LONG).show();
//                                    Intent intent = new Intent(getApplicationContext(), Profile_Activity.class);
//                                    startActivity(intent);
//                                    finish();
                                    showSnackbar_noRefresh("Data gagal diubah");
                                }
                            }

                            @Override
                            public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                                showSnackbar("Terjadi gangguan koneksi","Refresh");
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
        // menampilkan alert dialog
        alertDialog.show();

        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.RED);
    }


    private void showSnackbar(String text, String action)
    {
        bar = Snackbar.make(findViewById(R.id.sb_profile),text, Snackbar.LENGTH_INDEFINITE);
        bar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
                startActivity(new Intent(Profile_Activity.this,Profile_Activity.class));
                finish();
            }
        });
        bar.setActionTextColor(Color.GRAY);
        bar.show();

    }

    private void showSnackbar_noRefresh(String text)
    {
        bar = Snackbar.make(findViewById(R.id.sb_profile),text, Snackbar.LENGTH_INDEFINITE);
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

    private void showDialog_sendPass()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(45,0,20,0);

        final EditText input = new EditText(Profile_Activity.this);
        input.setHint("New Password");
        input.setHintTextColor(Color.GRAY);
        input.setMaxWidth(80);
        input.setBackgroundResource(R.drawable.border_table);

        layout.addView(input,lp);

        builder.setView(layout);

        builder.setTitle("Password dikirim ke email dan sms");


        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().isEmpty())
                {
                    Toast.makeText(Profile_Activity.this,"Masukkan password baru anda",Toast.LENGTH_LONG).show();
                }
                else if(sharedPref.sp.getString("email","").isEmpty() || sharedPref.sp.getString("no_hp","").isEmpty())
                {
                    showSnackbar_noRefresh("Mohon Lengkapi email dan nomor hp anda");
                }
                else
                {
                    showDialog_loading();
                    sendToSms(input.getText().toString());
                    sendToEmail(input.getText().toString());
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });


// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLUE);
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
    }

    private void showDialog_loading()
    {
        // setup the alert builder
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        RelativeLayout layout = new RelativeLayout(this);
        final ProgressBar progressBar = new ProgressBar(Profile_Activity.this,null,android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar,params);

        builder.setView(layout);

// create and show the alert dialog
        dialog = builder.create();
        dialog.show();

        dialog.getWindow().setLayout(200,180);

    }

    private void sendToEmail(String password)
    {
        Call<Ent_pegawai> sendToEmail = api_interface.sendToEmail(sharedPref.sp.getString("email",""),
                                        sharedPref.sp.getString("nip",""),password);

        sendToEmail.enqueue(new Callback<Ent_pegawai>() {
            @Override
            public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                if (response.body().getResponse() == 1)
                {
                    Toast.makeText(getApplicationContext(),"Password berhasil dikirim ke email anda, Cek email",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                    crudSqlite.hapus();
                    sharedPref.saveSPBoolean(SharedPref.SP_SUDAH_LOGIN, false);
                    sharedPref.saveSPString("nama", "");
                    sharedPref.saveSPString("nip", "");
                    sharedPref.saveSPString("no_hp", "");
                    sharedPref.saveSPString("id_unit_kerja", "");
                    sharedPref.saveSPString("email","");
                    sharedPref.saveSPString("password","");
                    sharedPref.saveSPString("unit_kerja", "");
                    sharedPref.saveSPString("level", "");
                    sharedPref.saveSPInt("radius", 0);
                    startActivity(intent);
                    finish();
                }
                else if(response.body().getResponse() == 2)
                {
                    dialog.dismiss();
                    showSnackbar_noRefresh("Password belum berhasil diubah. coba lagi");

                }
                else
                {
                    dialog.dismiss();
                    showSnackbar_noRefresh("Maaf, gagal mengirim password ke email");
                }
            }

            @Override
            public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                dialog.dismiss();
                showSnackbar("Masalah dengan koneksi", "Refresh");
            }
        });
    }

    private void sendToSms(String password)
    {
        Call<Ent_pegawai> callEditNomorHp = api_interface.gantiPassword(sharedPref.sp.getString("no_hp",""),
                                            sharedPref.sp.getString("nip",""),password);

        callEditNomorHp.enqueue(new Callback<Ent_pegawai>() {
            @Override
            public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                if(response.body().getResponse() == 1)
                {
                    Toast.makeText(getApplicationContext(),"Password berhasil dikirim ke No.Hp anda, Cek SMS",Toast.LENGTH_LONG).show();


                    crudSqlite.hapus();
//                    sharedPref.saveSPBoolean(SharedPref.SP_SUDAH_LOGIN, false);
//                    sharedPref.saveSPString("nama", "");
//                    sharedPref.saveSPString("nip", "");
//                    sharedPref.saveSPString("no_hp", "");
//                    sharedPref.saveSPString("id_unit_kerja", "");
//                    sharedPref.saveSPString("email","");
//                    sharedPref.saveSPString("unit_kerja", "");
//                    sharedPref.saveSPString("level","");
                }
                else if(response.body().getResponse() == 0)
                {
//                      Toast.makeText(getApplicationContext(),"Password gagal diubah",Toast.LENGTH_LONG).show();
                    showSnackbar_noRefresh("Password gagal dikirim melalui sms");

                }
                else if(response.body().getResponse() == 2)
                {
                    showSnackbar_noRefresh("Password belum berhasil diubah, coba lagi");

                }
            }

            @Override
            public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                showSnackbar("Terjadi gangguan koneksi","Refresh");
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Profile_Activity.this,Menu_Utama_Activity.class));
        finish();
    }
}
