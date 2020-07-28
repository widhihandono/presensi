package com.presensi.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.presensi.app.Adapter.LocationPresence_Adapter;
import com.presensi.app.Adapter.New_UnitKerja_location_Adapter;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Unit_Kerja;
import com.presensi.app.Model.Ent_lokasi_presence;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class List_Location_Activity extends AppCompatActivity {
private RecyclerView rvLocation;
private LocationPresence_Adapter locationPresence_adapter;
private RecyclerView.LayoutManager layoutManager;
private Api_Interface api_interface;
private New_UnitKerja_location_Adapter unitKerja_adapter;
    Snackbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__location_);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lokasi Presensi");

        rvLocation = findViewById(R.id.rvLocation);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        layoutManager = new LinearLayoutManager(this);
        rvLocation.setLayoutManager(layoutManager);

//        showLocationPresence();
        snackbarDialog();
        show_unitKerja_location();
    }

    private void show_unitKerja_location()
    {
        Call<List<Ent_Unit_Kerja>> callUnitKerja = api_interface.getLocationUnitKerja();
        callUnitKerja.enqueue(new Callback<List<Ent_Unit_Kerja>>() {
            @Override
            public void onResponse(Call<List<Ent_Unit_Kerja>> call, Response<List<Ent_Unit_Kerja>> response) {
                List<Ent_Unit_Kerja> list_unitKerja = response.body();
//                rvUnitkerja.scrollToPosition(list_unitKerja.size() - 10);
                unitKerja_adapter = new New_UnitKerja_location_Adapter(list_unitKerja,getApplicationContext());

                rvLocation.setAdapter(unitKerja_adapter);
                bar.dismiss();


            }

            @Override
            public void onFailure(Call<List<Ent_Unit_Kerja>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Couldn't get data",Toast.LENGTH_LONG).show();
                showSnackbar("Couldn't get data, check your connection");
            }
        });

    }

    private void showLocationPresence()
    {
        Call<List<Ent_lokasi_presence>> lokasiCall = api_interface.get_location_presence();
        lokasiCall.enqueue(new Callback<List<Ent_lokasi_presence>>() {
            @Override
            public void onResponse(Call<List<Ent_lokasi_presence>> call, Response<List<Ent_lokasi_presence>> response) {
                List<Ent_lokasi_presence> listLokasi = response.body();
                locationPresence_adapter = new LocationPresence_Adapter(listLokasi,getApplicationContext());
                rvLocation.setAdapter(locationPresence_adapter);
            }

            @Override
            public void onFailure(Call<List<Ent_lokasi_presence>> call, Throwable t) {
                Log.d("Location_presence",t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.search:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView)
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                unitKerja_adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    private void snackbarDialog() {
        bar = Snackbar.make(findViewById(R.id.sb_list_location), "Please Wait...get Data", Snackbar.LENGTH_INDEFINITE);
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

    private void showSnackbar(String text) {
        bar = Snackbar.make(findViewById(R.id.sb_list_location), text, Snackbar.LENGTH_INDEFINITE);
        bar.setAction("Refresh", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
                startActivity(new Intent(List_Location_Activity.this,List_Location_Activity.class));
                finish();
            }
        });
        bar.setActionTextColor(Color.GRAY);
        bar.show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
