package com.presensi.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.presensi.app.Util.MapsTileProvider;

import java.io.Serializable;

public class Location_Activity extends FragmentActivity {
    //maps
    private GoogleMap Mmap;
    private Serializable escolas;
    private ProgressDialog dialog;
    private Circle mCircle;
    private Marker mMarker;
    double latitude, longitude;
    Marker marker;
    SupportMapFragment mapFrag;

    //OSM
    private String osmURL = "http://a.tile.openstreetmap.org/{z}/{x}/{y}.png";
    private MapsTileProvider mTileProvider = new MapsTileProvider(256, 256, osmURL);

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    int permsRequestCode = 200;
    private LatLng latLngBiru = new LatLng(35.6829733, 139.7321275);
    private LatLng latLngKuning = new LatLng(35.6847009, 139.7314891);
    private LatLng latLngMerah = new LatLng(35.6839537, 139.7308615);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_);

//        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//
//        mapFrag.getMapAsync(g ->
//        {
//            Mmap=g;
//            tampil();
//        });
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mapFrag.getMapAsync(g -> {
            Mmap = g;
            Mmap.setMyLocationEnabled(true);
            Mmap.addMarker(new MarkerOptions()
                    .position(latLngBiru)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("tempat rahasia").snippet("rahasia lho"));
            Mmap.addMarker(new MarkerOptions()
                    .position(latLngKuning)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .title("bangunan kampus").snippet("bangunan utama"));
            Mmap.addMarker(new MarkerOptions()
                    .position(latLngMerah)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("kantin kampus").snippet("makan makan"));

            Mmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBiru, 17));

            Mmap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    Mmap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider));
                }
            });
        });



    }

    private void tampil()
    {
        FusedLocationProviderClient flpc = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
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
                            .zoom(19).bearing(0).tilt(45).build();
                    Mmap.animateCamera(CameraUpdateFactory.newCameraPosition(cPosition));


                }
            }
        });

        Mmap.setMyLocationEnabled(true);
        Mmap.getUiSettings().setMyLocationButtonEnabled(true);

        // Changing map type
        Mmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Showing / hiding your current location
        Mmap.setMyLocationEnabled(true);

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
                Mmap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider));
                LatLng midLatlng = Mmap.getCameraPosition().target;
//                    Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(midLatlng,14));
                //menampilkan markernya
//                    marker.setPosition(midLatlng);
                latitude = midLatlng.latitude;
                longitude = midLatlng.longitude;
//                    Toast.makeText(getApplicationContext(),midLatlng.toString(),Toast.LENGTH_SHORT).show();
//                    Mmap.addMarker(new MarkerOptions().position(midLatlng)
//                            .title("Draggable Marker")
//                            .snippet("Long press and move the marker if needed.")
//                            .draggable(true)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_camera)));

//                    Mmap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//                        @Override
//                        public void onMarkerDragStart(Marker marker) {
//                            Log.d("Marker","Dragging");
//                        }
//
//                        @Override
//                        public void onMarkerDrag(Marker marker) {
//                            LatLng markerLocation = marker.getPosition();
//                            Toast.makeText(getApplicationContext(),markerLocation.toString(),Toast.LENGTH_SHORT).show();;
//                            Log.d("Marker","Finished");
//                        }
//
//                        @Override
//                        public void onMarkerDragEnd(Marker marker) {
//                            Log.d("Marker","started");
//                        }
//                    });
            }
        });

    }


}
