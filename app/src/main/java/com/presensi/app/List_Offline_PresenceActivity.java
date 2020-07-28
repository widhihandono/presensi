package com.presensi.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.presensi.app.Adapter.Offline_Presence_Adapter;
import com.presensi.app.SQLite.Crud;

public class List_Offline_PresenceActivity extends AppCompatActivity {
private RecyclerView rvOfflinePresence;
private RecyclerView.LayoutManager layoutManager;
private Crud crudSqlite;
private Offline_Presence_Adapter offline_presence_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__offline__presence);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Offline Presence");

        crudSqlite = new Crud(this);
        rvOfflinePresence = findViewById(R.id.rvOfflinePresence);
        layoutManager = new LinearLayoutManager(this);
        rvOfflinePresence.setLayoutManager(layoutManager);

        offline_presence_adapter = new Offline_Presence_Adapter(this,crudSqlite.getData_Presence());
        rvOfflinePresence.setAdapter(offline_presence_adapter);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(List_Offline_PresenceActivity.this,Menu_Utama_Activity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(List_Offline_PresenceActivity.this,Menu_Utama_Activity.class));
        finish();
    }

    //=========Check Internet Connection==========================
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
