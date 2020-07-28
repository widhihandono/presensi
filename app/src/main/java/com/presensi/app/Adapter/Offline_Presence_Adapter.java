package com.presensi.app.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.List_Offline_PresenceActivity;
import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.Model.Ent_Settingan_Emulator;
import com.presensi.app.Model.Ent_Time;
import com.presensi.app.R;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.MyHttpEntity;
import com.presensi.app.Util.SharedPref;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Offline_Presence_Adapter extends RecyclerView.Adapter<Offline_Presence_Adapter.Holder> {
    private Context context;
    private List<Ent_Presensi> list_presensi;
    SharedPref sharedPref;
    private static final String SERVER_PATH = Api_Client.BASE_URL+"Api_presence/presence";
    Crud crudSqlite;
    Api_Interface api_interface;
    String bootloader,host,id;
     boolean status = false;


    public Offline_Presence_Adapter(Context context, List<Ent_Presensi> list_presensi) {
        this.context = context;
        this.list_presensi = list_presensi;
        this.sharedPref = new SharedPref(context);
        crudSqlite = new Crud(context);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        bootloader = Build.BOOTLOADER;
        host = Build.HOST;
        id = Build.ID;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_offline_presence,viewGroup,false);
        Holder holder = new Holder(root);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
         holder.tvJam.setText(list_presensi.get(i).getTime());
         holder.tvTanggal.setText("  "+list_presensi.get(i).getDate());

         holder.imgUpload.setOnClickListener(l->{
             cekEmulator(bootloader,host,id,i);

         });


    }


    private void cekEmulator(String boat,String host,String id, int position)
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
                    else
                    {
                        Call<Ent_Time> callJamUpdate = api_interface.cekjamupdate();
                        callJamUpdate.enqueue(new Callback<Ent_Time>() {
                            @Override
                            public void onResponse(Call<Ent_Time> call, Response<Ent_Time> response) {
                                if(response.isSuccessful())
                                {
                                    if(response.body().getResponse() == 1)
                                    {
                                        UploadAsyncTas uploadAsyncTask = new UploadAsyncTas(context,list_presensi.get(position).getId(),
                                                sharedPref.sp.getString("nip", ""),
                                                list_presensi.get(position).getStatus(), list_presensi.get(position).getLatitude(),
                                                list_presensi.get(position).getLongitude(), list_presensi.get(position).getImage(), list_presensi.get(position).getId_unit_kerja(),
                                                String.valueOf(list_presensi.get(position).getId_lokasi_presence()), list_presensi.get(position).getKet_presence(),list_presensi.get(position).getImei(),
                                                list_presensi.get(position).getTime(),list_presensi.get(position).getDate());
                                        uploadAsyncTask.execute();
                                    }
                                    else
                                    {
//                             Toast.makeText(context,response.body().getPesan(),Toast.LENGTH_LONG).show();
                                        showDialogPesanJamUpdate(response.body().getPesan());
                                    }
                                }
                                else
                                {
                                    showDialogPesanJamUpdate("Mohon Maaf atas gangguan ini. tunggu beberapa saat lagi untuk kirim data presensi. " +
                                            "Untuk kirim data presensi, mohon dilakukan diluar jam presensi");
                                }

                            }

                            @Override
                            public void onFailure(Call<Ent_Time> call, Throwable t) {
//                     bar.dismiss();
//                     enableDisableButton(false);
//                     showSnackbar("Terjadi gangguan dengan koneksi anda atau server","Refresh");
                                Toast.makeText(context,"Terjadi gangguan dengan koneksi anda atau server",Toast.LENGTH_LONG).show();
                                Log.d("error","Terjadi gangguan dengan koneksi anda atau server");
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(context,"Terjadi gangguan pada server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Ent_Settingan_Emulator> call, Throwable t) {
                Toast.makeText(context,"Terjadi gangguan koneksi", Toast.LENGTH_LONG).show();
                Log.d("error", "Terjadi gangguan dengan koneksi anda atau server");
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

    //=========Check Internet Connection==========================
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void showDialogEmulator(String pesan){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(pesan)
                .setIcon(R.drawable.ic_warning)
                .setCancelable(false)
                .setPositiveButton("Keluar",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity)context).finishAffinity();
                    }
                });


        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setTextColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return list_presensi.size();
    }



    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvJam,tvTanggal;
        private ImageView imgUpload;
        public Holder(@NonNull View itemView) {
            super(itemView);

            tvJam = itemView.findViewById(R.id.tvJam);
            imgUpload = itemView.findViewById(R.id.imgUpload);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
        }
    }

    private class  UploadAsyncTas extends AsyncTask<Void, Integer, Integer> {

        HttpClient httpClient = new DefaultHttpClient();
        private Context context;
        private Exception exception;
        private ProgressDialog progressDialog = null;
        String keterangan = "";
        String nip,status,lat,longit,image,id_unit_kerja,id_lokasi_presence,ket_presence,time,date,imei,id;

        private UploadAsyncTas(Context context,String id,String nip, String status, String lat, String longit, String image,
                               String id_unit_kerja, String id_lokasi_presence, String ket_presence,String imei,String time,String date) {
            this.context = context;
            this.nip = nip;
            this.status = status;
            this.lat = lat;
            this.longit = longit;
            this.image = image;
            this.id_unit_kerja = id_unit_kerja;
            this.id_lokasi_presence = id_lokasi_presence;
            this.ket_presence = ket_presence;
            this.time = time;
            this.date = date;
            this.imei = imei;
            this.id = id;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            HttpResponse httpResponse = null;
            HttpEntity httpEntity = null;
            Integer responseString = 0;


            try {
                HttpPost httpPost = new HttpPost(SERVER_PATH);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

                // Add the file to be uploaded
                multipartEntityBuilder.addTextBody("nip", nip);
                multipartEntityBuilder.addTextBody("status", status);
                multipartEntityBuilder.addTextBody("latitude", lat);
                multipartEntityBuilder.addTextBody("longitude", longit);
                multipartEntityBuilder.addTextBody("image", image);
                multipartEntityBuilder.addTextBody("id_unit_kerja", id_unit_kerja);
                multipartEntityBuilder.addTextBody("id_lokasi_presence", id_lokasi_presence);
                multipartEntityBuilder.addTextBody("imei",imei);
                multipartEntityBuilder.addTextBody("ket_presence",ket_presence);
                multipartEntityBuilder.addTextBody("time",time);
                multipartEntityBuilder.addTextBody("date",date);

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
                    keterangan = "Sukses Presensi. Imei : " + myObject.getString("imei")
                            + " sudah digunakan oleh NIP : " + myObject.getString("nip") +
                            ", Nama : " + myObject.getString("nama") + ", Ini akan dicatat di sistem";
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
//                    Toast.makeText(context, keterangan, Toast.LENGTH_LONG).show();
                Toast.makeText(context,
                        "Success", Toast.LENGTH_LONG).show();
                if(crudSqlite.hapus_presence_by_id(id))
                {
                    context.startActivity(new Intent(context, List_Offline_PresenceActivity.class));
                }

            }
            else if (result == 2) {
                Toast.makeText(context,
                        ""+result, Toast.LENGTH_LONG).show();
                this.progressDialog.dismiss();
            } else {
                Toast.makeText(context,
                        ""+result, Toast.LENGTH_LONG).show();
                this.progressDialog.dismiss();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]-2);

        }

    }

    private void showDialogPesanJamUpdate(String pesan){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

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

}
