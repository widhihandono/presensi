package com.presensi.app;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.presensi.app.Adapter.Presensi_Adapter;
import com.presensi.app.Adapter.TabPager_Adapter;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Fragement.Fg_PresensiHarian;
import com.presensi.app.Fragement.Fg_PresensiPulang;
import com.presensi.app.Fragement.Fg_PresensiDatang;
import com.presensi.app.Util.SharedPref;

public class History_Presensi_Activity extends AppCompatActivity implements Fg_PresensiPulang.OnFragmentInteractionListener,
        Fg_PresensiDatang.OnFragmentInteractionListener, Fg_PresensiHarian.OnFragmentInteractionListener {
private Api_Interface api_interface;
private RecyclerView rvHistory;
private RecyclerView.LayoutManager layoutManager;
private Presensi_Adapter presensi_adapter;
private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history__presensi_);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Histori Presensi");

        sharedPref = new SharedPref(this);
        api_interface = Api_Client.getClient().create(Api_Interface.class);
//        rvHistory = findViewById(R.id.rvHistory);
        layoutManager = new LinearLayoutManager(this);
//        rvHistory.setLayoutManager(layoutManager);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabPager_Adapter myPagerAdapter = new TabPager_Adapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
