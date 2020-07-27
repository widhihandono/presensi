package com.presensi.app;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_pegawai;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reset_password_Activity extends AppCompatActivity {
private EditText etEmail,etNoHp,etNip;
private TextView tvSendPassword;
private Api_Interface api_interface;
    private Snackbar bar;
    Random random;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        random = new Random();

        api_interface = Api_Client.getClient().create(Api_Interface.class);
//        etNewPassword = findViewById(R.id.etNewPassword);
//        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvSendPassword = findViewById(R.id.tvSendPassword);
        etEmail = findViewById(R.id.etEmail);
        etNoHp = findViewById(R.id.etNoHp);
        etNip = findViewById(R.id.etNip);

        tvSendPassword.setOnClickListener(l->{
            if(etNoHp.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty())
            {
                showSnackbar_noRefresh("Lengkapi data diatas !");
            }
            else
            {
                showDialog_loading();
                sendToEmailAndSms(String.valueOf(10000+random.nextInt(99999)),etNoHp.getText().toString(),
                        etEmail.getText().toString(),etNip.getText().toString());
            }

        });


    }

    private void showSnackbar_Refresh(String text, String action)
    {
        bar = Snackbar.make(findViewById(R.id.sb_resetPassword),text, Snackbar.LENGTH_INDEFINITE);
        bar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
                startActivity(new Intent(Reset_password_Activity.this,Reset_password_Activity.class));
                finish();
            }
        });
        bar.setActionTextColor(Color.GRAY);
        bar.show();

    }

    private void showSnackbar_noRefresh(String text)
    {
        bar = Snackbar.make(findViewById(R.id.sb_resetPassword),text, Snackbar.LENGTH_INDEFINITE);
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

    private void sendToEmailAndSms(String password,String no_hp,String email,String nip)
    {
        Call<Ent_pegawai> sendToEmail = api_interface.sendEmailAndSms(email,no_hp,nip,password);

        sendToEmail.enqueue(new Callback<Ent_pegawai>() {
            @Override
            public void onResponse(Call<Ent_pegawai> call, Response<Ent_pegawai> response) {
                if (response.body().getResponse() == 1)
                {

                    Toast.makeText(getApplicationContext(),"Password berhasil dikirim ke email dan nomor anda, Cek email dan sms",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                    startActivity(intent);
                    finish();
                }
                else if(response.body().getResponse() == 5)
                {
                    dialog.dismiss();
                    showSnackbar_noRefresh("Maaf, gagal mengirim password ke email dan nomor anda");
                }
                else
                {
                    dialog.dismiss();
                    showSnackbar_noRefresh("Maaf, password belum berhasil diubah. coba lagi");
                }
            }

            @Override
            public void onFailure(Call<Ent_pegawai> call, Throwable t) {
                dialog.dismiss();
                showSnackbar_Refresh("Masalah dengan koneksi", "Refresh");
            }
        });
    }


    private void showDialog_loading()
    {
        // setup the alert builder
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        RelativeLayout layout = new RelativeLayout(this);
        final ProgressBar progressBar = new ProgressBar(Reset_password_Activity.this,null,android.R.attr.progressBarStyleLarge);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
        startActivity(intent);
        finish();
    }
}
