// Copyright 2011 Google Inc. All Rights Reserved.

package com.presensi.app.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anirudhd@google.com (Anirudh Dewani)
 */
public class ViewFinderView extends View {

    private static final String TAG = "facedetection";
    Paint paint = new Paint();
    List<Camera.Face> faces = new ArrayList<Camera.Face>();
    Matrix matrix = new Matrix();
    RectF rect = new RectF();
    private int mDisplayOrientation;
    private int mOrientation;

    /**
     * @param context
     */
    public ViewFinderView(Context context) {
        super(context);

    }

    private void dumpRect(RectF rect, String msg) {
        Log.v(TAG, msg + "=(" + rect.left + "," + rect.top
                + "," + rect.right + "," + rect.bottom + ")");
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRect(50f, 50f, 200f, 200f, paint);
        canvas.drawARGB(0, 0, 0, 0);
        Face f = new Face();

        prepareMatrix(matrix, 0, getWidth(), getHeight());
        //canvas.save();
        //matrix.postRotate(mOrientation); // postRotate is clockwise
        //canvas.rotate(-mOrientation); // rotate is counter-clockwise (for
        // canvas)
        for (Face face : faces) {
            rect.set(face.rect);
            dumpRect(rect, "before");
            matrix.mapRect(rect);
            dumpRect(rect, "after");
            canvas.drawRect(rect, paint);
        }
        //canvas.restore();
        Log.d(TAG, "Drawing Faces - " + faces.size());
        super.onDraw(canvas);
    }

    public void setDisplayOrientation(int orientation) {
        mDisplayOrientation = orientation;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
        invalidate();
    }

    /**
     */
    public void setFaces(List<Camera.Face> faces) {
        this.faces = faces;
        invalidate();
    }

    public ViewFinderView(Context context, AttributeSet attr) {
        super(context, attr);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    public static void prepareMatrix(Matrix matrix, int displayOrientation,
                                     int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

}
