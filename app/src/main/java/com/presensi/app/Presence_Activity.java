package com.presensi.app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.SQLite.Crud;
import com.presensi.app.Util.MyHttpEntity;
import com.presensi.app.Util.SharedPref;
import com.presensi.app.Util.imageUtils;

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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Presence_Activity extends AppCompatActivity implements LocationListener {
    private ImageButton btnBack, btnRefresh;
    private TextView tvPresensi, tvWaktu, tvCoordinat, tvAkurasi;
    private static final String SERVER_PATH = Api_Client.BASE_URL+"Api_presence/presensi";

    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    private Handler handler = new Handler();
    private Api_Interface api_interface;
    private SharedPref sharedPref;
    LocationManager locationManager;
    Geocoder geocoder;
    Location location;
    String status = "", latitude = "0", longitude = "0", encode = "", ket_presence = "Imei sudah digunakan oleh : ";
    int id_lokasi_presence = 0;
    double lat_presensi = 0, lon_presensi = 0;
    private Bitmap bmpSelfie;
    private boolean dialogSet;
    Snackbar bar;
    FusedLocationProviderClient client;
    LocationCallback locCallback;

    //Camera
    private static final int CAMERA_REQUEST = 1;
    private static final int CAMERA_PERMISSION = 2;
    private Uri mImageUri, mImageUri2;
    String currentPhotoPath;
    Bitmap bitmap;
    LinearLayout lnMaps;


    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};

    int permsRequestCode = 200;

    Crud crudSqlite;

    //maps
    private GoogleMap googleMap;
    private Serializable escolas;
    private Circle mCircle, mCircle_2;
    private Marker mMarker, mMarker_2;
    int jml_marker = 0;
    Map<String, Marker> markers = new HashMap<>();
    Map<String, Circle> circles = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence_);

        getSupportActionBar().hide();
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        crudSqlite = new Crud(this);

        sharedPref = new SharedPref(this);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvPresensi = findViewById(R.id.tvPresensi);

        tvWaktu = findViewById(R.id.tvWaktu);
        tvCoordinat = findViewById(R.id.tvCoordinat);
        tvAkurasi = findViewById(R.id.tvAkurasi);
        lnMaps = findViewById(R.id.lnMaps);

        tvPresensi.setText("Simpan Presensi " + getIntent().getExtras().getString("presensi", ""));


        if (getIntent().getExtras().getString("presensi").equals("Datang")) {
            status = "D";
        } else if (getIntent().getExtras().getString("presensi").equals("Pulang")) {
            status = "P";
        }

        //cek lokasi yang belum di setting.
        if(crudSqlite.getData().size() == 0)
        {
            show_have_not_location("Maaf, anda belum bisa presensi. Gagal mendapatkan lokasi atau Lokasi anda belum di setting. " +
                    "Coba tekan tombol 'AMBIL SETTINGAN' di Menu Utama. Jika masih belum bisa silhakan hubungi Administrator");
        }

        createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Cek Imei Presensi
        cekImei_presence();
        //=====================
        if(!isTimeAutomatic(Presence_Activity.this)) //check time auto or not.
        {
            showDialogCheckTime();
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

//API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        currentPhotoPath = getIntent().getExtras().getString("currentPhotoPath");
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
                    if (isMockLocationOn(location, Presence_Activity.this)) {
                        tvPresensi.setEnabled(false);
                        showDialogFakeGPS("Anda terdeteksi menggunakan Fake GPS !");
                    }

                    tvCoordinat.setText("{" + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()) + "}");
//                    tvAkurasi.setText("( Akurasi : " + Math.round(location.getAccuracy()) + " meter ): ");

//                getLocation();

                } else {

                    flpc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                            if (isMockLocationOn(location, Presence_Activity.this)) {
                                tvPresensi.setEnabled(false);
                                showDialogFakeGPS("Anda terdeteksi menggunakan Fake GPS !");
                            }

                            //                        Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }

        snackbarDialogLocation();
        map.getMapAsync(g ->
        {
            googleMap = g;
            // Changing map type
            LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//            googleMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 19));

            drawMarkerWithCircle(latLng, sharedPref.sp.getInt("radius", 0), latitude);

            for (int a = 0; a < crudSqlite.getData().size(); a++) {
//            Toast.makeText(getApplicationContext(),crudSqlite.getData().get(a).getLatitude(),Toast.LENGTH_LONG).show();
                drawMarkerWithCircle_LokasiPresensi(Double.parseDouble(crudSqlite.getData().get(a).getLatitude()), Double.parseDouble(crudSqlite.getData().get(a).getLongitude()), 10);

            }


            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.equals(mMarker)) {
                        showImageDialog();
                    }
                    return false;
                }
            });
        });

            start(this);
        btnRefresh.setOnClickListener(l -> {

            createLocationRequest();
            snackbarDialogLocation();

//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);
            locationManager.getBestProvider(criteria,true);
            locationManager.requestLocationUpdates(1000, 1, criteria, this,null);

            if(locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            getCurrentLocation();
            //disable button presensi

//            if(markers.containsKey(latitude) && circles.containsKey(latitude))
//            {
//                Circle crc = circles.get(latitude);
//                Marker mark = markers.get(latitude);
//                crc.remove();
//                mark.remove();
//            }
        });

        btnBack.setOnClickListener(l -> {
            createLocationRequest();
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            startActivity(new Intent(this, Menu_Utama_Activity.class));
            finish();
        });

        tvPresensi.setOnClickListener(l -> {
//            startActivity(new Intent(this,Location_Activity.class));
//            finish();
            if(!isTimeAutomatic(Presence_Activity.this)) //check time auto or not.
            {
                showDialogCheckTime();
            }
            else
            {
                if (id_lokasi_presence == 0) {
                    showSnackbar("Anda tidak mempunyai lokasi presensi, Hubungi Admin");
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bmpSelfie = BitmapFactory.decodeFile(currentPhotoPath, options);
                    if (bmpSelfie != null) {
                        encode = imageUtils.bitmapToBase64String(bmpSelfie, 17);

//                    savePresence(sharedPref.sp.getString("nip", ""),
//                            status, latitude, longitude, encode, sharedPref.sp.getString("id_unit_kerja", ""), String.valueOf(id_lokasi_presence), ket_presence);
//                        UploadAsyncTas uploadAsyncTask = new UploadAsyncTas(Presence_Activity.this,
//                                sharedPref.sp.getString("nip", ""),
//                                status, latitude, longitude, encode, sharedPref.sp.getString("id_unit_kerja", ""), String.valueOf(id_lokasi_presence), ket_presence, getTime(), getDate());
//                        uploadAsyncTask.execute();
                    if(crudSqlite.InsertData_Presence(sharedPref.sp.getString("nip", ""),status, latitude, longitude, encode, sharedPref.sp.getString("id_unit_kerja", ""),
                                id_lokasi_presence, ket_presence,getUniqueIMEIId(),getTime(),getDate()) != 0)
                    {
                        Toast.makeText(this, "Sukses presensi. Data tersimpan di Hp.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Presence_Activity.this,Menu_Utama_Activity.class));
                        finish();
                    }
                    } else {
                        Toast.makeText(this, "Foto masih kosong,Cek Foto", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });



//        grabImage(imgPhoto);


        handler.postDelayed(runnable, 1000);
//        handler.postDelayed(runnableLocation,5000);

    }

    //================check time hp auto or not.====================================
    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
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
//=====================================================================================================


    private String getTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    private String getDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return sdf.format(date);
    }

    private void getCurrentLocation()
    {
        FusedLocationProviderClient flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        flpc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (markers.containsKey("1") && circles.containsKey("1")) {
                    Circle crc = circles.get("1");
                    Marker mark = markers.get("1");
                    crc.remove();
                    mark.remove();
                }

                drawMarkerWithCircle(latLng, sharedPref.sp.getInt("radius", 0), latitude);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 19));

                for (int a = 0; a < crudSqlite.getData().size(); a++) {
                    float jarak = getDistanceLocation(location.getLatitude(), location.getLongitude(),
                            Double.parseDouble(crudSqlite.getData().get(a).getLatitude()),
                            Double.parseDouble(crudSqlite.getData().get(a).getLongitude()));
//                Toast.makeText(getApplicationContext(),String.valueOf(jarak),Toast.LENGTH_LONG).show();


                    if (jarak <= sharedPref.sp.getInt("radius", 0)) {

                        //Stop Snackbar
                        bar.dismiss();
                        tvPresensi.setEnabled(true);

                        lat_presensi = Double.parseDouble(crudSqlite.getData().get(a).getLatitude());
                        lon_presensi = Double.parseDouble(crudSqlite.getData().get(a).getLongitude());
                        id_lokasi_presence = crudSqlite.getData().get(a).getId_lokasi_presence();

                    }

                }

                //                        Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void drawMarkerWithCircle(LatLng position, float akurasi, String lat) {
        int strokeColor = 0xED4D8015; //green outline
        int shadeColor = 0x5184E911; //opaque green fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(akurasi).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(4);
        mCircle = googleMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        mMarker = googleMap.addMarker(markerOptions);
        mMarker.setTitle("Tap Marker to Show Photo");
        mMarker.showInfoWindow();
        mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        circles.put("1", mCircle);
        markers.put("1", mMarker);

    }

    private void drawMarkerWithCircle_LokasiPresensi(double lat, double longit, float akurasi) {

        LatLng latLng = new LatLng(lat, longit);
//        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(akurasi).fillColor(R.color.greenTransparant).strokeColor(R.color.green).strokeWidth(4);
//        mCircle_2 = googleMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);

        mMarker_2 = googleMap.addMarker(markerOptions);

    }

    //======================Show Image Marker Dialog==========================================
    private void showImageDialog() {
        currentPhotoPath = getIntent().getExtras().getString("currentPhotoPath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        bmpSelfie = BitmapFactory.decodeFile(currentPhotoPath, options);

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        LayoutInflater inflaterr = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewtemplelayout = inflaterr.inflate(R.layout.image_dialog, null);
        ImageView i = viewtemplelayout.findViewById(R.id.imgDialog);//and set image to image view

        i.setImageBitmap(bmpSelfie);
        alertdialog.setView(viewtemplelayout);//add your view to alert dilaog

        alertdialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertdialog.create();
        dialog.show();

        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.RED);
    }


    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private float getDistanceLocation(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        float distance = locationA.distanceTo(locationB);//To convert Meter in Kilometer
//        return Float.parseFloat(NumberFormat.getNumberInstance(Locale.getDefault()).format(distance));
        return Float.parseFloat(String.format("%.0f", distance));
    }

    private void presensi(String nip, String status, String lat, String longit, String image, String id_unit_kerja, int id_lokasi_presence, String ket_presence) {
        if (currentPhotoPath == null || currentPhotoPath == "") {
            Toast toast = Toast.makeText(getApplicationContext(), "Foto tidak boleh kosong!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            snackbarDialog();
            Call<Ent_Presensi> callPresensi = api_interface.presensi(nip, status, lat, longit, getUniqueIMEIId(), image, id_unit_kerja, id_lokasi_presence, ket_presence,getTime(),getDate());

            callPresensi.enqueue(new Callback<Ent_Presensi>() {
                @Override
                public void onResponse(Call<Ent_Presensi> call, Response<Ent_Presensi> response) {
                    if (response.body().getResponse() == 1) {
                        if (!response.body().getImei().equals("") || !response.body().getImei().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Sukses Presensi. Imei : " + response.body().getImei()
                                    + " sudah digunakan oleh NIP : " + response.body().getNip() +
                                    ", Nama : " + response.body().getNama() + ", Ini akan dicatat di sistem", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sukses Presensi", Toast.LENGTH_LONG).show();
                        }
                        bar.dismiss();
                        tvPresensi.setEnabled(true);
                        startActivity(new Intent(getApplicationContext(), Menu_Utama_Activity.class));
                        finish();
                    } else if (response.body().getResponse() == 2) {
                        bar.dismiss();
                        tvPresensi.setEnabled(true);
                        showSnackbar("Anda diluar Jangkauan, tidak bisa presensi");
                    } else {
                        bar.dismiss();
                        tvPresensi.setEnabled(true);
                        showSnackbar("Gagal Input");
                    }
                }

                @Override
                public void onFailure(Call<Ent_Presensi> call, Throwable t) {
                    bar.dismiss();
                    tvPresensi.setEnabled(true);
                    showSnackbar("Coba Lagi, koneksi gagal.");
                }
            });
        }

    }


    private String timer() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:ss");
        Date date = new Date();
        return sdf.format(date);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tvWaktu.setText(timer());

            handler.postDelayed(this, 1000);
        }

    };

    private Runnable runnableLocation = new Runnable() {
        @Override
        public void run() {
//            getLocation();
            handler.postDelayed(this, 5000);
        }

    };


    public void grabImage(ImageView imgeView) {
        currentPhotoPath = getIntent().getExtras().getString("currentPhotoPath");
//        Toast.makeText(getApplicationContext(), currentPhotoPath, Toast.LENGTH_LONG).show();
//        this.getContentResolver().notifyChange(Uri.parse(getIntent().getExtras().getString("imageUri")), null);
        ContentResolver cr = this.getContentResolver();
        cr.notifyChange(Uri.parse(getIntent().getExtras().getString("imageUri")), null);

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bmpSelfie = BitmapFactory.decodeFile(currentPhotoPath, options);
//            encode = imageUtils.bitmapToBase64String(bmpSelfie, 17);

//                bmpLapor = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(currentPhotoPath, options), 500, 500, false));


        } catch (Exception e) {
//            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("Error", "Failed to load", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (this.CAMERA_REQUEST == requestCode && resultCode == RESULT_OK) {
//                Bitmap bitmap =  (Bitmap) data.getExtras().get("data");
//                bmpLapor = bitmap;
//                img_lapor.setImageBitmap(bitmap);
//                grabImage(imgPhoto);

            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
//        if(isMockLocationOn(location, getApplicationContext()))
//        {
//            Toast.makeText(getApplicationContext(), "Fake location detected !", Toast.LENGTH_LONG).show();
//            tvPresensi.setEnabled(false);
//        }else {
//
////            Toast.makeText(getApplicationContext(), String.valueOf(location.getLatitude())+" + "+String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
////            tvAkurasi.setText("( Akurasi : " + Math.round(location.getAccuracy()) + " meter )");
//
//            if (markers.containsKey("1") && circles.containsKey("1")) {
//                Circle crc = circles.get("1");
//                Marker mark = markers.get("1");
//                crc.remove();
//                mark.remove();
//            }
//
//            drawMarkerWithCircle(latLng, sharedPref.sp.getInt("radius", 0), latitude);
//
////            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 19));
//
//            LatLngBounds.Builder builder = builder = new LatLngBounds.Builder();
//            for (int a = 0; a < crudSqlite.getData().size(); a++) {
//
//                builder.include(new LatLng(Double.parseDouble(crudSqlite.getData().get(a).getLatitude()),Double.parseDouble(crudSqlite.getData().get(a).getLongitude())));
//
//            }
//            builder.include(new LatLng(location.getLatitude(),location.getLongitude()));
//            LatLngBounds bounds = builder.build();
//
//            int width = getResources().getDisplayMetrics().widthPixels;
//            int height = lnMaps.getHeight();
//            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width,height,padding);
//
//            googleMap.animateCamera(cu);
//
//            for (int a = 0; a < crudSqlite.getData().size(); a++) {
//                float jarak = getDistanceLocation(latLng.latitude, latLng.longitude,
//                        Double.parseDouble(crudSqlite.getData().get(a).getLatitude()),
//                        Double.parseDouble(crudSqlite.getData().get(a).getLongitude()));
//
//
//                if (jarak <= sharedPref.sp.getInt("radius", 0)) {
//                    locationManager.removeUpdates(this);
//                    //Stop Snackbar
//                    bar.dismiss();
//                    tvPresensi.setEnabled(true);
//
//                    lat_presensi = Double.parseDouble(crudSqlite.getData().get(a).getLatitude());
//                    lon_presensi = Double.parseDouble(crudSqlite.getData().get(a).getLongitude());
//                    id_lokasi_presence = crudSqlite.getData().get(a).getId_lokasi_presence();
////                    Toast.makeText(getApplicationContext(),   "Id lokasi Presensi" + String.valueOf(id_lokasi_presence), Toast.LENGTH_SHORT).show();
//
//                    tvCoordinat.setText("{" + String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude) + "}");
//                    latitude = String.valueOf(latLng.latitude);
//                    longitude = String.valueOf(latLng.longitude);
//
//                    //Auto Out after 5 minute
//                    Thread thread = new Thread()
//                    {
//                        @Override
//                        public void run() {
//                            try {
//                                sleep(300000);
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            finally {
//                                    startActivity(new Intent(Presence_Activity.this,Menu_Utama_Activity.class));
//                                    finish();
//
//                            }
//                        }
//                    };
//                    thread.start();
//
//
//                }
//                //show radius
////                tvAkurasi.setText("( Radius : "+jarak+" meter )");
//
//
////                    Toast.makeText(getApplicationContext(), jarak + "," + String.valueOf(mCircle.getRadius()), Toast.LENGTH_SHORT).show();
//
//
//                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        if (marker.equals(mMarker)) {
//                            showImageDialog();
//                        }
//                        return false;
//                    }
//                });
//
//            }
//
//
////            getLocation();
//        }


    }

    private void show_have_not_location(String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(text)
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Presence_Activity.this,Menu_Utama_Activity.class));
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

    private void showDialogFakeGPS(String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(text)
                .setIcon(R.drawable.ic_pen)
                .setCancelable(false)
                .setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
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

    public static boolean isMockLocationOn(Location location, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        } else {
            String mockLocation = "0";
            try {
                mockLocation = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !mockLocation.equals("0");
        }
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

    private void snackbarDialog() {
        tvPresensi.setEnabled(false);
        bar = Snackbar.make(findViewById(R.id.snackbar), "Please Wait...Send Data", Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentLay.getLayoutParams();
        layoutParams.gravity = Gravity.TOP;
        contentLay.setLayoutParams(layoutParams);
        contentLay.setBackgroundResource(R.color.colorPrimary);
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.setActionTextColor(Color.GRAY);
        bar.show();

    }

    private void snackbarDialogLocation() {
        tvPresensi.setEnabled(false);
        bar = Snackbar.make(findViewById(R.id.snackbar), "Sedang Mencari Lokasi Presensi, Mohon Tunggu...", Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        contentLay.setBackgroundResource(R.color.colorPrimary);
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.setActionTextColor(Color.GRAY);
        bar.show();
    }

    private void showSnackbar(String text) {
        bar = Snackbar.make(findViewById(R.id.snackbar), text, Snackbar.LENGTH_INDEFINITE);
        bar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.setActionTextColor(Color.GRAY);
        bar.show();

    }

    @Override
    public void onBackPressed() {
        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        startActivity(new Intent(this, Menu_Utama_Activity.class));
        finish();
    }

    public String getUniqueIMEIId() {

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
        }
        String imei = telephonyManager.getDeviceId();
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.e("imei", "=" + imei);
        Log.e("android_id", "=" + android_id);
        if (imei != null && !imei.isEmpty()) {
            return imei;
        } else {

            return android_id;
        }
    }

    private void cekImei_presence() {
        Call<List<Ent_Presensi>> callCekImei = api_interface.cek_imei_presence(getUniqueIMEIId(), status, sharedPref.sp.getString("nip", ""));
        callCekImei.enqueue(new Callback<List<Ent_Presensi>>() {
            @Override
            public void onResponse(Call<List<Ent_Presensi>> call, Response<List<Ent_Presensi>> response) {


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Presence_Activity.this, android.R.layout.simple_list_item_1);

                List<Ent_Presensi> arrayList = response.body();
                if (arrayList.size() > 0) {
//                    showDialogImei(arrayList);
                    for (int a = 0; a < arrayList.size(); a++) {
                        ket_presence = ket_presence + "," + arrayList.get(a).getNama();

                        arrayAdapter.add(a + 1 + ". " + arrayList.get(a).getNama());

                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(Presence_Activity.this);
                    builder.setTitle("Warning !..Imei ini sudah digunakan oleh : ");
                    builder.setCancelable(false);

                    builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

//                        builder.show();
                    AlertDialog dialog = builder.create();

                    dialog.show();
                    Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setTextColor(Color.RED);
                } else {
                    Log.d("cek_imei_presensi", "lanjutkan !");
                    ket_presence = "";
                }
//                if(!response.body().getImei().equals("") || !response.body().getImei().isEmpty())
//                {
//                    showDialog("Imei sudah digunakan oleh NIP : " + response.body().getNip() +
//                        ", Nama : " + response.body().getNama() + ", Ini akan dicatat di sistem");
//                    ket_presence = "Imei sudah digunakan oleh NIP : " + response.body().getNip() +
//                            ", Nama : " + response.body().getNama() + ", Ini akan dicatat di sistem";
//                }
//                else
//                {
//                    Log.d("cek_imei_presensi","lanjutkan !");
//                    ket_presence = "";
//                }
            }

            @Override
            public void onFailure(Call<List<Ent_Presensi>> call, Throwable t) {
                Log.d("cek_imei_presensi", "unstable network");
            }
        });
    }

    //Upload use percentage


         private class UploadAsyncTas extends AsyncTask<Void, Integer, Integer> {

            HttpClient httpClient = new DefaultHttpClient();
            private Context context;
            private Exception exception;
            private ProgressDialog progressDialog = null;
            String keterangan = "";
             String nip,status,lat,longit,image,id_unit_kerja,id_lokasi_presence,ket_presence,time,date;

            private UploadAsyncTas(Context context,String nip, String status, String lat, String longit, String image,
                                   String id_unit_kerja, String id_lokasi_presence, String ket_presence,String time,String date) {
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
                    multipartEntityBuilder.addTextBody("imei",getUniqueIMEIId());
                    multipartEntityBuilder.addTextBody("ket_presence",ket_presence);
                    multipartEntityBuilder.addTextBody("time",getTime());
                    multipartEntityBuilder.addTextBody("date",getDate());

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
                if(this.progressDialog != null)
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
//                    Toast.makeText(getApplicationContext(), keterangan, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),
                            "Success", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Presence_Activity.this,Menu_Utama_Activity.class));
                    finish();
                }
                else if (result == 2) {
                    this.progressDialog.dismiss();
                    tvPresensi.setEnabled(true);
                    showSnackbar("Anda diluar Jangkauan, tidak bisa presensi");
                } else {
                    this.progressDialog.dismiss();
                    tvPresensi.setEnabled(true);
                    showSnackbar("Gagal Presensi, Coba lagi. Atau cek koneksi");
                }

            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                // Update process
                this.progressDialog.setProgress((int) progress[0]-2);

            }

         }

    public void start(Context ctx) {
        client = LocationServices
                .getFusedLocationProviderClient(ctx);
        //Define quality of service:
        LocationRequest request = LocationRequest.create();
        request.setInterval(3000); //Every second
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (locCallback != null) {
            client.removeLocationUpdates(locCallback);
        }
        locCallback = createNewLocationCallback();
        client.requestLocationUpdates(request, locCallback, null);
    }

    private LocationCallback createNewLocationCallback() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                Location location = result.getLastLocation();
                if(location != null)
                {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());

                    if(isMockLocationOn(location, getApplicationContext()))
                    {
                        Toast.makeText(getApplicationContext(), "Fake location detected !", Toast.LENGTH_LONG).show();
                        tvPresensi.setEnabled(false);
                    }else {

//            Toast.makeText(getApplicationContext(), String.valueOf(location.getLatitude())+" + "+String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//            tvAkurasi.setText("( Akurasi : " + Math.round(location.getAccuracy()) + " meter )");

                        if (markers.containsKey("1") && circles.containsKey("1")) {
                            Circle crc = circles.get("1");
                            Marker mark = markers.get("1");
                            crc.remove();
                            mark.remove();
                        }

                        drawMarkerWithCircle(latLng, sharedPref.sp.getInt("radius", 0), latitude);

//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 19));

                        LatLngBounds.Builder builder = builder = new LatLngBounds.Builder();
                        for (int a = 0; a < crudSqlite.getData().size(); a++) {

                            builder.include(new LatLng(Double.parseDouble(crudSqlite.getData().get(a).getLatitude()),Double.parseDouble(crudSqlite.getData().get(a).getLongitude())));

                        }
                        builder.include(new LatLng(location.getLatitude(),location.getLongitude()));
                        LatLngBounds bounds = builder.build();

                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = lnMaps.getHeight();
                        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width,height,padding);

                        googleMap.animateCamera(cu);

                        for (int a = 0; a < crudSqlite.getData().size(); a++) {
                            float jarak = getDistanceLocation(latLng.latitude, latLng.longitude,
                                    Double.parseDouble(crudSqlite.getData().get(a).getLatitude()),
                                    Double.parseDouble(crudSqlite.getData().get(a).getLongitude()));


                            if (jarak <= sharedPref.sp.getInt("radius", 0)) {
                                if(locCallback != null)
                                {
                                    client.removeLocationUpdates(locCallback);
                                }
                                //Stop Snackbar
                                bar.dismiss();
                                tvPresensi.setEnabled(true);

                                lat_presensi = Double.parseDouble(crudSqlite.getData().get(a).getLatitude());
                                lon_presensi = Double.parseDouble(crudSqlite.getData().get(a).getLongitude());
                                id_lokasi_presence = crudSqlite.getData().get(a).getId_lokasi_presence();
//                    Toast.makeText(getApplicationContext(),   "Id lokasi Presensi" + String.valueOf(id_lokasi_presence), Toast.LENGTH_SHORT).show();

                                tvCoordinat.setText("{" + String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude) + "}");
                                latitude = String.valueOf(latLng.latitude);
                                longitude = String.valueOf(latLng.longitude);

                                //Auto Out after 5 minute
                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(300000);

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        finally {
                                            startActivity(new Intent(Presence_Activity.this,Menu_Utama_Activity.class));
                                            finish();

                                        }
                                    }
                                };
                                thread.start();


                            }
                            //show radius
//                tvAkurasi.setText("( Radius : "+jarak+" meter )");


//                    Toast.makeText(getApplicationContext(), jarak + "," + String.valueOf(mCircle.getRadius()), Toast.LENGTH_SHORT).show();


                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    if (marker.equals(mMarker)) {
                                        showImageDialog();
                                    }
                                    return false;
                                }
                            });

                        }


//            getLocation();
                    }
                }

            }
        };
        return locationCallback;
    }


}