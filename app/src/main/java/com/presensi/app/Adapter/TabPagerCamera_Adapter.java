package com.presensi.app.Adapter;

import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.presensi.app.Fragement.Fg_CamBelakang;
import com.presensi.app.Fragement.Fg_CamDepan;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TabPagerCamera_Adapter extends FragmentPagerAdapter {

    public TabPagerCamera_Adapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0: return new Fg_CamBelakang();
            case 1: return new Fg_CamDepan();
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
            case 0: return "Camera Belakang";
            case 1: return "Camera Depan";
            default: return null;
        }
    }
}
