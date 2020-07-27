package com.presensi.app.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class imageUtils {

    public static Bitmap getScaled(Bitmap b, double maxSize)
    {
        double ratio = 1;

        double w = b.getWidth();
        double h = b.getHeight();

        if(w == maxSize || h == maxSize)
            return b;

        if (w < maxSize || h < maxSize)
            return b;

        if (w < h)
        {
            if (w > maxSize)
            {
                w = maxSize;

                ratio = maxSize / b.getWidth();
                h = h * ratio;
            }
        }
        else if (w > h)
        {
            if (h > maxSize)
            {
                h = maxSize;
                ratio = maxSize / b.getHeight();
                w = w * ratio;
            }
        }
        else
        {
            if (h > maxSize)
            {
                h = maxSize;
                w = maxSize;
            }
        }

        if (w != b.getWidth() || h != b.getHeight())
            return Bitmap.createScaledBitmap(b, (int)w, (int)h, false);
        else
            return null;
    }

    public static String bitmapToBase64String(Bitmap bmp,int quality)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,quality,baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }

    public static String imageUrlToString(String src, int sizeResize, int sizeQuality)
    {
        InputStream imgStream = null;
        String encode="";
        try {
            URL url = new URL(src);

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                imgStream = connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(imgStream);
                Bitmap resize = getScaled(bmp,sizeResize);//sizeResize = resize image/bmp to encode. lebih kecil lebih cepat tampil encodenya.
                encode = bitmapToBase64String(resize,sizeQuality);//sizeQuality = Quality Maksimum image/bmp (max 100).
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return encode;
    }

    public static String encodeBitmapAndResizze(Bitmap bmp,int sizeResize, int sizeQuality)
    {
        String encode ="";
        Bitmap resize = getScaled(bmp,sizeResize);//sizeResize = resize image/bmp to encode. lebih kecil lebih cepat tampil encodenya.
        encode = bitmapToBase64String(resize,sizeQuality);//sizeQuality = Quality Maksimum image/bmp (max 100).
        return encode;
    }
}
