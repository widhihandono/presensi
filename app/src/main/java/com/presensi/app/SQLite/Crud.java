package com.presensi.app.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.Util.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class Crud {
    Helper helper;
    SharedPref sharedPref;

    public Crud(Context context) {
        helper = new Helper(context);
        sharedPref = new SharedPref(context);
    }

    public long InsertData(int id_lokasi_presence,String lokasi_presence,String latitude,String longitude,String id_unit_kerja)
    {
        SQLiteDatabase dbb = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Helper.ID_LOKASI_PRESENCE,id_lokasi_presence);
        contentValues.put(Helper.LOKASI_PRESENCE,lokasi_presence);
        contentValues.put(Helper.LATITUDE,latitude);
        contentValues.put(Helper.LONGITUDE,longitude);
        contentValues.put(Helper.ID_UNIT_KERJA,id_unit_kerja);

        long id = dbb.insert(Helper.TABLE_NAME,null,contentValues);
        return id;
    }

    public long InsertData_Presence(String nip, String status, String lat, String longit, String image,
                                    String id_unit_kerja, int id_lokasi_presence, String ket_presence,String imei,String time,String date)
    {
        SQLiteDatabase dbb = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nip",nip);
        contentValues.put("status",status);
        contentValues.put("latitude",lat);
        contentValues.put("longitude",longit);
        contentValues.put("image",image);
        contentValues.put("id_unit_kerja",id_unit_kerja);
        contentValues.put("id_lokasi_presence",id_lokasi_presence);
        contentValues.put("ket_presence",ket_presence);
        contentValues.put("imei",imei);
        contentValues.put("time",time);
        contentValues.put("date",date);


        long id = dbb.insert(Helper.TABLE_PRESENCE,null,contentValues);
        return id;
    }

    public List<Ent_Presensi> getData_Presence_all_nip()
    {

        SQLiteDatabase db = helper.getWritableDatabase();
        String[] coloumn = {Helper.UID,"nip","status","latitude","longitude","imei","image","id_unit_kerja","id_lokasi_presence","ket_presence","time","date"};
        Cursor cursor = db.query(Helper.TABLE_PRESENCE,coloumn,null,null,null,null,null);
        List<Ent_Presensi> listPresence = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Ent_Presensi ep = new Ent_Presensi();
            ep.setId(cursor.getString(cursor.getColumnIndex("id")));
            ep.setNip(cursor.getString(cursor.getColumnIndex("nip")));
            ep.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            ep.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
            ep.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
            ep.setImage(cursor.getString(cursor.getColumnIndex("image")));
            ep.setId_unit_kerja(cursor.getString(cursor.getColumnIndex("id_unit_kerja")));
            ep.setId_lokasi_presence(cursor.getInt(cursor.getColumnIndex("id_lokasi_presence")));
            ep.setKet_presence(cursor.getString(cursor.getColumnIndex("ket_presence")));
            ep.setImei(cursor.getString(cursor.getColumnIndex("imei")));
            ep.setTime(cursor.getString(cursor.getColumnIndex("time")));
            ep.setDate(cursor.getString(cursor.getColumnIndex("date")));


//            int cid = cursor.getInt(cursor.getColumnIndex(Helper.UID));
//            String nomor = cursor.getString(cursor.getColumnIndex(Helper.NOMOR));
//            String alamat = cursor.getString(cursor.getColumnIndex(Helper.ALAMAT));
//            String kategori = cursor.getString(cursor.getColumnIndex(Helper.KATEGORI));

                listPresence.add(ep);

        }
        return listPresence;
    }

    public List<Ent_Presensi> getData_Presence()
    {

        SQLiteDatabase db = helper.getWritableDatabase();
        String[] coloumn = {Helper.UID,"nip","status","latitude","longitude","imei","image","id_unit_kerja","id_lokasi_presence","ket_presence","time","date"};
        Cursor cursor = db.query(Helper.TABLE_PRESENCE,coloumn,null,null,null,null,null);
        List<Ent_Presensi> listPresence = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Ent_Presensi ep = new Ent_Presensi();
            ep.setId(cursor.getString(cursor.getColumnIndex("id")));
            ep.setNip(cursor.getString(cursor.getColumnIndex("nip")));
            ep.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            ep.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
            ep.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
            ep.setImage(cursor.getString(cursor.getColumnIndex("image")));
            ep.setId_unit_kerja(cursor.getString(cursor.getColumnIndex("id_unit_kerja")));
            ep.setId_lokasi_presence(cursor.getInt(cursor.getColumnIndex("id_lokasi_presence")));
            ep.setKet_presence(cursor.getString(cursor.getColumnIndex("ket_presence")));
            ep.setImei(cursor.getString(cursor.getColumnIndex("imei")));
            ep.setTime(cursor.getString(cursor.getColumnIndex("time")));
            ep.setDate(cursor.getString(cursor.getColumnIndex("date")));


//            int cid = cursor.getInt(cursor.getColumnIndex(Helper.UID));
//            String nomor = cursor.getString(cursor.getColumnIndex(Helper.NOMOR));
//            String alamat = cursor.getString(cursor.getColumnIndex(Helper.ALAMAT));
//            String kategori = cursor.getString(cursor.getColumnIndex(Helper.KATEGORI));

            if(cursor.getString(cursor.getColumnIndex("nip")).equals(sharedPref.sp.getString("nip","")))
            {
                listPresence.add(ep);
            }
        }
        return listPresence;
    }

    public List<Ent_lokasi_presence> getData()
    {

        SQLiteDatabase db = helper.getWritableDatabase();
        String[] coloumn = {Helper.UID,Helper.ID_LOKASI_PRESENCE,Helper.LOKASI_PRESENCE,Helper.LATITUDE,Helper.LONGITUDE,Helper.ID_UNIT_KERJA};
        Cursor cursor = db.query(Helper.TABLE_NAME,coloumn,null,null,null,null,null);
        List<Ent_lokasi_presence> listNomorDarurat = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Ent_lokasi_presence nd = new Ent_lokasi_presence();
            nd.setId_lokasi_presence(cursor.getInt(cursor.getColumnIndex(Helper.ID_LOKASI_PRESENCE)));
            nd.setLokasi_presence(cursor.getString(cursor.getColumnIndex(Helper.LOKASI_PRESENCE)));
            nd.setLatitude(cursor.getString(cursor.getColumnIndex(Helper.LATITUDE)));
            nd.setLongitude(cursor.getString(cursor.getColumnIndex(Helper.LONGITUDE)));
            nd.setId_unit_kerja(cursor.getString(cursor.getColumnIndex(Helper.ID_UNIT_KERJA)));

//            int cid = cursor.getInt(cursor.getColumnIndex(Helper.UID));
//            String nomor = cursor.getString(cursor.getColumnIndex(Helper.NOMOR));
//            String alamat = cursor.getString(cursor.getColumnIndex(Helper.ALAMAT));
//            String kategori = cursor.getString(cursor.getColumnIndex(Helper.KATEGORI));

            listNomorDarurat.add(nd);
        }
        return listNomorDarurat;
    }

    public int hapus()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(Helper.TABLE_NAME,null,null);
        return count;
    }

    public boolean hapus_presence_by_id(String id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(Helper.TABLE_PRESENCE,helper.UID+"="+id,null) > 0;
    }

    public boolean delete_all_presence()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(Helper.TABLE_PRESENCE,null,null) > 0;
    }

}
