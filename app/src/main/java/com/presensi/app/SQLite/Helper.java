package com.presensi.app.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class Helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "lokasi_presensi_db";
    public static final String TABLE_NAME = "lokasi_presensi";
    public static final String TABLE_PRESENCE = "presence";
    public static final int DATABASE_Version = 2;
    public static final String UID = "id";
    public static final String ID_LOKASI_PRESENCE = "id_lokasi_presence";
    public static final String LOKASI_PRESENCE = "lokasi_presence";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ID_UNIT_KERJA = "id_unit_kerja";

    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            "("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ID_LOKASI_PRESENCE+" int, "+
            LOKASI_PRESENCE+" VARCHAR(255), "+LATITUDE+" VARCHAR(30), "+LONGITUDE+" VARCHAR(30),"+ID_UNIT_KERJA+" VARCHAR(15));";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public static final String CREATE_TABLE_Presence = "CREATE TABLE "+TABLE_PRESENCE+
            "("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, nip VARCHAR(20), "+
            "status VARCHAR(20), "+LATITUDE+" VARCHAR(20), "+LONGITUDE+" VARCHAR(20),imei VARCHAR(40),image TEXT,id_unit_kerja VARCHAR(50)," +
            "id_lokasi_presence VARCHAR(40), ket_presence TEXT, time VARCHAR(20),date VARCHAR(15));";
    public static final String DROP_TABLE_PRESENCE = "DROP TABLE IF EXISTS "+TABLE_PRESENCE;
    public Context context;

    public Helper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_Version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE_Presence);

        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            Toast.makeText(context,"OnUpgrade",Toast.LENGTH_LONG).show();
            db.execSQL(DROP_TABLE);
            db.execSQL(DROP_TABLE_PRESENCE);
            onCreate(db);
        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

}
