package com.presensi.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.presensi.app.Adapter.UnitKerja_Adapter;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Unit_Kerja;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class List_Unit_Kerja_Activity extends AppCompatActivity {
private Api_Interface api_interface;
private RecyclerView rvUnitkerja;
private ProgressBar pgBar;
private UnitKerja_Adapter unitKerja_adapter;
private LinearLayoutManager layoutManager;
    List<Ent_Unit_Kerja> list_unitKerja;
    private int limit = 10, offset = 0; // offset / index

    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__unit__kerja_);
        getSupportActionBar().setTitle("Unit Kerja");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        api_interface = Api_Client.getClient().create(Api_Interface.class);
        rvUnitkerja = findViewById(R.id.rvUnitKerja);
        pgBar = findViewById(R.id.pgBar);
        layoutManager = new LinearLayoutManager(this);
        rvUnitkerja.setLayoutManager(layoutManager);

        show_unitKerja();

//        rvUnitkerja.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if(((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition() == list_unitKerja.size()-1)
//                {
////                    Toast.makeText(getApplicationContext(),"Terakhir",Toast.LENGTH_LONG).show();
//                    pgBar.setVisibility(View.VISIBLE);
//                    limit = limit+10;
//                    show_unitKerja(limit,0);
//
//
//                }
//                else
//                {
//                    pgBar.setVisibility(View.INVISIBLE);
//
//                }
//            }
//        });


    }



    private void show_unitKerja()
    {
        Call<List<Ent_Unit_Kerja>> callUnitKerja = api_interface.getLocationUnitKerja();
        callUnitKerja.enqueue(new Callback<List<Ent_Unit_Kerja>>() {
            @Override
            public void onResponse(Call<List<Ent_Unit_Kerja>> call, Response<List<Ent_Unit_Kerja>> response) {
                 list_unitKerja = response.body();
//                rvUnitkerja.scrollToPosition(list_unitKerja.size() - 10);
                unitKerja_adapter = new UnitKerja_Adapter(list_unitKerja,getApplicationContext());



                rvUnitkerja.setAdapter(unitKerja_adapter);


            }

            @Override
            public void onFailure(Call<List<Ent_Unit_Kerja>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Couldn't get data",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.search)
        {
            return true;
        }
        else if(item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

                return true;
            }
        });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
