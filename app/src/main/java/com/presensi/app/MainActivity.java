package com.presensi.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Notification notification;
    int elapsedTime;
    private Handler handler = new Handler();
    RemoteViews mRemoteViews;
    private TextView tvUtama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUtama = findViewById(R.id.tvUtama);

        tvUtama.setText(getUniqueIMEIId(this));

        float jarak = getDistanceLocation(-7.5929331,110.2191977,-7.592974737178306,110.21918751901433);
        Toast.makeText(getApplicationContext(),String.valueOf(jarak),Toast.LENGTH_LONG).show();
        notif();
//        if (Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
//        {
//            Toast.makeText(getApplicationContext(),"Fake GPS",Toast.LENGTH_LONG).show();
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(),"GPS Benar",Toast.LENGTH_LONG).show();
//        }

//        if(isEmulator())
//        {
//            Toast.makeText(getApplicationContext(),"Emulator",Toast.LENGTH_LONG).show();
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(),"Android",Toast.LENGTH_LONG).show();
//        }
    }

    private float getDistanceLocation(double lat1, double lon1, double lat2, double lon2) {
        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        float distance = locationA.distanceTo(locationB);//To convert Meter in Kilometer
        return Float.parseFloat(NumberFormat.getNumberInstance(Locale.getDefault()).format(distance));

    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private void notif()
    {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.list_presensi);
        TextView textView = new TextView(this);
        textView.setId(R.id.tvTime);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this, "1");
        notification = mBuilder.setSmallIcon(R.drawable.logo_menu).setTicker("ABC").setWhen(0)
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentTitle("ABC")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.logo_menu)
                .setUsesChronometer(true)
                .build();

       Runnable runnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime += 1000;

                String timer = DateUtils.formatElapsedTime(elapsedTime);

                remoteViews.setTextViewText(textView.getId(),timer);

                handler.postDelayed(this,1000);
            }
        };
        handler.postDelayed(runnable, 1000);

        mBuilder.setCustomBigContentView(remoteViews);
        mBuilder.setUsesChronometer(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);

    }

    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }
}
