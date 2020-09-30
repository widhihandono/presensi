package com.presensi.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_pegawai;
import com.presensi.app.Util.MyHttpEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
                UploadAsyncTask upload = new UploadAsyncTask(Reset_password_Activity.this,String.valueOf(10000+random.nextInt(99999)),etNoHp.getText().toString(),
                        etEmail.getText().toString(),etNip.getText().toString());
                upload.execute();
//                sendToEmailAndSms(String.valueOf(10000+random.nextInt(99999)),etNoHp.getText().toString(),
//                        etEmail.getText().toString(),etNip.getText().toString());
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

    private class  UploadAsyncTask extends AsyncTask<Void, Integer, Integer> {

        HttpClient httpClient = new DefaultHttpClient();
        private Context context;
        private Exception exception;
        private ProgressDialog progressDialog = null;
        String keterangan = "";
        String password,no_hp,email,nip;

        private UploadAsyncTask(Context context,String password,String no_hp,String email,String nip) {
            this.context = context;
            this.nip = nip;
            this.password = password;
            this.no_hp = no_hp;
            this.email = email;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            Integer responseString = 0;


            try {
                HttpPost httpPost = new HttpPost(Api_Client.BASE_URL+"Api_presence/sendEmailAndSms");
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                // Add the file to be uploaded
                multipartEntityBuilder.addTextBody("nip", nip);
                multipartEntityBuilder.addTextBody("password", password);
                multipartEntityBuilder.addTextBody("no_hp", no_hp);
                multipartEntityBuilder.addTextBody("email", email);

                // Progress listener - updates task's progress
                MyHttpEntity.ProgressListener progressListener =
                        new MyHttpEntity.ProgressListener() {
                            @Override
                            public void transferred(float progress) {
                                publishProgress((int) progress);
                            }
                        };

                // POST
                httpPost.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                        progressListener));


                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    // Server response
                    //cek respone

                    JSONObject myObject = new JSONObject(EntityUtils.toString(httpEntity));
//                        Log.i("BERHASIL CUY", String.valueOf(myObject.getInt("status")));
                    responseString = myObject.getInt("response");
                    keterangan = myObject.getString("pesan");
                } else {
                    responseString = 0;
                }
            } catch (UnsupportedEncodingException | ClientProtocolException e) {
//                    e.printStackTrace();
//                    Log.e("SAVE", e.getMessage());
//                    this.exception = e;
            } catch (IOException e) {
//                    e.printStackTrace();
            } catch (JSONException e) {
//                    e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {

            // Init and show dialog
            this.progressDialog = new ProgressDialog(this.context);
            if(this.progressDialog == null)
            {
                this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                this.progressDialog.setCancelable(false);
                this.progressDialog.show();
            }

        }

        @Override
        protected void onPostExecute(Integer result) {

            // Close dialog
            if (result == 1) {
                this.progressDialog.dismiss();
                dialog.dismiss();
//                    Toast.makeText(context, keterangan, Toast.LENGTH_LONG).show();
                Toast.makeText(context,
                        "Success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(intent);
                finish();
//                showSnackbar_noRefresh(keterangan);
            }
            else if (result == 2)
            {
                this.progressDialog.dismiss();
                dialog.dismiss();
                showSnackbar_noRefresh(keterangan);
            }
            else if(result == 3)
            {
                this.progressDialog.dismiss();
                dialog.dismiss();
                showSnackbar_noRefresh(keterangan);
            }
            else if(result == 4)
            {
                this.progressDialog.dismiss();
                dialog.dismiss();
                showSnackbar_noRefresh(keterangan);
            }
            else
            {
                this.progressDialog.dismiss();
                dialog.dismiss();
                showSnackbar_noRefresh("Masalah dengan Koneksi");
            }

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]-2);

        }

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
