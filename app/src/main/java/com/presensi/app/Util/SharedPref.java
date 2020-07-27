package com.presensi.app.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    public static final String SP_PANIC_BUTTON_APP = "spPresensi";

    public static final String SP_IDUSER = "spIdUser";
    public static final String SP_NAMA = "spNama";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_NOHP = "spNoHp";
    public static final String SP_password = "spPassword";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";
    public SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    public static boolean status = false;

    public static boolean isStatus() {
        return status;
    }

    public static void setStatus(boolean status) {
        SharedPref.status = status;
    }

    public SharedPref(Context context){
        sp = context.getSharedPreferences(SP_PANIC_BUTTON_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPNama(){
        return sp.getString(SP_NAMA, "");
    }

    public String getSPEmail(){
        return sp.getString(SP_EMAIL, "");
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
