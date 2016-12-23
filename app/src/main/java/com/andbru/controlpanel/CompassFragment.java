package com.andbru.controlpanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Anders on 2016-11-11.
 */

public class CompassFragment extends Fragment {

    Compass mCompass = null;

    public CompassFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mCompassView = inflater.inflate(R.layout.fragment_compass, container, false);

        mCompass = new Compass(container.getContext());
        mCompass.setBackgroundColor(0xffffffff);
        RelativeLayout insertCompass = (RelativeLayout)  mCompassView.findViewById(R.id.csurface);
        insertCompass.addView(mCompass);

        return mCompassView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void update(PilotData mPD) {
        // Update the compass
        double dYawCmd = new Double(mPD.yawCmd);
        double dYawIs = new Double(mPD.yawIs);
        mCompass.setCourses(dYawCmd, dYawIs);
        mCompass.invalidate();
    }


    public class Compass extends SurfaceView implements SurfaceHolder.Callback {

        Bitmap bM = null;
        Paint pt = new Paint();
        Paint ptBar = new Paint();
        Paint ptText = new Paint();
        Matrix m = new Matrix();
        double yawCmd = 0;
        double yawIs = 0;
        Path pathCirk;


        public Compass(Context ctext) {
            super(ctext);

            getHolder().addCallback(this);

            pt.setStrokeWidth(3);
            pt.setColor(Color.BLACK);
            pt.setStyle(Paint.Style.FILL_AND_STROKE);
            pt.setAlpha(255);
            pt.setAntiAlias(true);
            pt.setTextSize(40);

            ptBar.setStrokeWidth(30);
            ptBar.setColor(0xFF2499E8);
            ptBar.setStyle(Paint.Style.STROKE);

            ptText.setStrokeWidth(1);
            ptText.setColor(Color.BLACK);
            ptText.setStyle(Paint.Style.FILL_AND_STROKE);
            ptText.setAlpha(255);
            ptText.setAntiAlias(true);
            ptText.setTextSize(400);
            ptText.setTextScaleX((float)1.1);
            ptText.setTextAlign(Paint.Align.CENTER);

            pathCirk = new Path();

            bM = BitmapFactory.decodeResource(ctext.getResources(), R.drawable.compa2);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            /**
             * Draw part of compass on canvas.
             *
             * Translate bitmap and rotate around center. Scale and translate back to
             * show part of compass cirkle.
             *
             */
            //float widthW = getWidth();
            //float heightW = getHeight();
            float width = canvas.getWidth();
            float height = canvas.getHeight();
            float bMWidth = bM.getWidth();
            float bMHeight = bM.getHeight();

            m.reset();
            m.setTranslate(-bMWidth/2, -bMHeight/2);
            m.postRotate(-(float)yawIs);
            m.postScale(height/470F*1333F / bMHeight, height / 470F * 1333F / bMHeight);
            m.postTranslate(width/2, height*(float)1.32);

            canvas.drawBitmap(bM, m, pt);


            /**
             * Draw remaining information on canvas
             */

            // Vertical line
            canvas.drawLine(width/2, 0, width/2, height * 0.04F, pt);
            canvas.drawLine(width/2,  height * 0.092F, width/2, height * 0.45F, pt);
            //canvas.drawLine(width/2,  height * 0.47F, width/2, height * 0.6F, pt);

            // Calculate rotation centre and radius
            double r = height*0.9 * 1.3;
            double xC=width/2;
            double yC= height * 1.3F;

            // Draw bar for course difference
            double rv = -11;
            RectF rekt = new RectF((float)(xC-r),(float)(yC-r),(float)(xC+r),(float)(yC+r));
            pathCirk.reset();
            double maxRotationSpeed = 40;
            float diff = (float)(yawCmd - yawIs);
            if (diff < -20) diff = -20;
            if (diff > +20) diff = +20;
            pathCirk.addArc(rekt, 270, (float) diff);
            canvas.drawPath(pathCirk, ptBar);

            // Draw yawCmd
            int iCourse = (int) Math.round(yawCmd);
            if (iCourse<0) iCourse = iCourse + 360;
            String sCourse = String.valueOf(iCourse);
            if (sCourse.length()==1) sCourse = "00" + sCourse;
            if (sCourse.length()==2) sCourse = "0" + sCourse;
            float xT = (float) xC;
            float yT = (float) height * .95F;
            ptText.setTextSize(240);
            canvas.drawText(sCourse,xT, yT, ptText);

            // Draw headline
            ptText.setTextSize(25);
            canvas.drawText("Course", xT, height * .08F, ptText);

            // Draw deg
            ptText.setTextSize(20);
            canvas.drawText("degrees", xT, height * .5F, ptText);

            ptText.setTextSize(20);
            canvas.drawText("difference", width *.12F, height * .04F, ptText);

            int iTrueDelta = (int) Math.round(yawIs - yawCmd);
            String sTrueDelta = String.valueOf(iTrueDelta);
            ptText.setTextSize(45);
            canvas.drawText(sTrueDelta, width *.12F, height * .15F, ptText);

            ptText.setTextSize(20);
            canvas.drawText("COG", width *.91F, height * .04F, ptText);

            int iTrueCourse = (int) Math.round(yawIs);
            if (iTrueCourse<0) iTrueCourse = iTrueCourse + 360;
            String sTrueCourse = String.valueOf(iTrueCourse);
            ptText.setTextSize(50);
            canvas.drawText(String.valueOf(sTrueCourse), width *.91F, height * .15F, ptText);
        }

        public void setCourses(double cmd, double is) {
            yawCmd = cmd;
            yawIs = is;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            setWillNotDraw(false);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }

    }
}
