package com.presensi.app.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.presensi.app.Fragement.Fg_PresensiHarian;
import com.presensi.app.Fragement.Fg_PresensiDatang;


public class TabPager_Adapter extends FragmentPagerAdapter {

    public TabPager_Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0: return new Fg_PresensiHarian();
            case 1: return new Fg_PresensiDatang();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0: return "Presensi Harian";
            case 1: return "Presensi Bulanan";
            default: return null;
        }
    }

}
