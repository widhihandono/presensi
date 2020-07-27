package com.presensi.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_lokasi_presence;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Location_Activity extends AppCompatActivity {
    GoogleMap Mmap;
    private ImageView imgMarker;
    double latitude, longitude;
    private EditText etLokasi;
    private LinearLayout ln_lokasi;
    private boolean tampil_editText = false;
    private String lokasi_presensi="";
    Marker marker;
    private Api_Interface api_interface;

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    int permsRequestCode = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__location_);

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        imgMarker = findViewById(R.id.imgMarker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Lokasi");

        api_interface = Api_Client.getClient().create(Api_Interface.class);
        ln_lokasi = findViewById(R.id.ln_lokasi);
        etLokasi = findViewById(R.id.etLokasi);

        if(tampil_editText)
        {
            ln_lokasi.setVisibility(View.VISIBLE);
            tampil_editText = true;
        }
        else
        {
            ln_lokasi.setVisibility(View.GONE);
            tampil_editText = false;
        }

        map.getMapAsync(g ->
        {
            Mmap=g;
            tampil();
        });
    }


    private void tampil()
    {
        FusedLocationProviderClient flpc = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        flpc.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    Log.d("My Current Location", "Lat : " + location.getLatitude() +
                            "Long : " + location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                        Toast.makeText(getApplicationContext(),String.valueOf(longitude).toString() +
//                                String.valueOf(latitude).toString(),Toast.LENGTH_SHORT).show();
                    CameraPosition cPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude,longitude))
                            .zoom(19).bearing(0).tilt(0).build();
                    Mmap.animateCamera(CameraUpdateFactory.newCameraPosition(cPosition));


                }
            }
        });

        Mmap.setMyLocationEnabled(true);
        Mmap.getUiSettings().setMyLocationButtonEnabled(true);

        // Changing map type
        Mmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Enable / Disable zooming controls
        Mmap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable Compass icon
        Mmap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        Mmap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        Mmap.getUiSettings().setZoomGesturesEnabled(true);
        //menampilkan markernya
//            marker = Mmap.addMarker(new MarkerOptions().position(new LatLng(-7.592992,110.2170397)));
//        LatLng currentPos = new LatLng(-7.592992,110.2170397);


        Mmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng midLatlng = Mmap.getCameraPosition().target;
                latitude = midLatlng.latitude;
                longitude = midLatlng.longitude;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.btn_save:
                input_presence();
                return true;
            case R.id.input_lokasi:
//                    ln_lokasi.setVisibility(View.VISIBLE);
                if(tampil_editText == false)
                {
                    ln_lokasi.setVisibility(View.VISIBLE);
                    tampil_editText = true;
                }
                else
                {
                    ln_lokasi.setVisibility(View.GONE);
                    tampil_editText=false;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void input_presence()
    {
        if(etLokasi.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Lokasi Presensi Tidak Boleh Kosong",Toast.LENGTH_LONG).show();
        }
        else
        {
            Call<Ent_lokasi_presence> callinputPresence = api_interface.input_location(etLokasi.getText().toString(),
                    String.valueOf(latitude),String.valueOf(longitude),getIntent().getExtras().getString("id_unit_kerja"));
            callinputPresence.enqueue(new Callback<Ent_lokasi_presence>() {
                @Override
                public void onResponse(Call<Ent_lokasi_presence> call, Response<Ent_lokasi_presence> response) {
                    if(response.body().getResponse() == 1)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),"Sukses Input Lokasi",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        startActivity(new Intent(getApplicationContext(),Menu_Utama_Activity.class));
                        finish();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),"Gagal Input Lokasi",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<Ent_lokasi_presence> call, Throwable t) {
                    Log.d("Add_location",t.getMessage());
                }
            });
        }

    }

}
