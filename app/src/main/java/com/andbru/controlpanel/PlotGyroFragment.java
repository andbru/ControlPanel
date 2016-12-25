package com.andbru.controlpanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Anders on 2016-11-22.
 */

public class PlotGyroFragment extends Fragment {

    GyroPlot mPlot;

    public PlotGyroFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View mPlotRegView = inflater.inflate(R.layout.fragment_plotreg, container, false);

        mPlot = new GyroPlot(container.getContext());
        mPlot.setBackgroundColor(0xff000000);

        RelativeLayout insertPlot = (RelativeLayout)  mPlotRegView.findViewById(R.id.psurface);

        insertPlot.addView(mPlot);

        return mPlotRegView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlot.count = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void update(PilotData mPD) {
        // Update the plot
        double w = new Double(mPD.w);
        //double dYawIs = new Double(mPD.yawIs);
        if(mPlot != null) {
            mPlot.plot(w);
            mPlot.invalidate();
        }
    }

    public class GyroPlot extends SurfaceView implements SurfaceHolder.Callback {

        Paint ptText = new Paint();
        Paint ptGrid = new Paint();
        Paint ptLine = new Paint();
        float width;
        float height;
        float xMin = 0;
        float xMax = 10;        // Grid physical size
        float yMin = -10;
        float yMax = 10;
        float[] plotData = new float[200];
        int count = 0;

        public GyroPlot(Context ctext) {
            super(ctext);

            getHolder().addCallback(this);

            ptText.setStrokeWidth(1);
            ptText.setColor(Color.WHITE);
            ptText.setStyle(Paint.Style.FILL_AND_STROKE);
            ptText.setAlpha(255);
            ptText.setAntiAlias(true);
            ptText.setTextScaleX((float)1.1);
            ptText.setTextAlign(Paint.Align.CENTER);

            ptGrid.setStrokeWidth(1);
            ptGrid.setColor(Color.WHITE);
            ptGrid.setStyle(Paint.Style.FILL_AND_STROKE);
            ptGrid.setAlpha(255);
            ptGrid.setAntiAlias(true);
            ptGrid.setTextSize(400);
            ptGrid.setTextScaleX((float)1.1);
            ptGrid.setTextAlign(Paint.Align.CENTER);

            ptLine.setStrokeWidth(6);
            ptLine.setColor(Color.GREEN);
            ptLine.setStyle(Paint.Style.FILL_AND_STROKE);
            ptLine.setAlpha(255);
            ptLine.setAntiAlias(true);
            ptLine.setTextAlign(Paint.Align.CENTER);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            width = canvas.getWidth();
            height = canvas.getHeight();

            canvas.drawColor(Color.BLACK);

            GyroPlot.Point p1 = new GyroPlot.Point();
            GyroPlot.Point p2 = new GyroPlot.Point();

            // Draw horizontal grid lines and legends
            ptText.setTextSize(30);
            for(float y = yMin; y <= yMax; y += (yMax-yMin) / 4) {
                p1 = getPoint(xMin, y);
                p2 = getPoint(xMax, y);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, ptGrid);
                canvas.drawText(Float.toString(y), p1.x/1.5F, p1.y+10, ptText);
            }

            // Draw vertical grid lines and legends
            ptText.setTextSize(30);
            for(float x = xMin; x <= xMax; x += (xMax-xMin) / 5) {
                p1 = getPoint(x, yMin);
                p2 = getPoint(x, yMax);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, ptGrid);
                canvas.drawText(Float.toString(x), p1.x, height*.95F, ptText);
            }

            canvas.drawLines(plotData, 0, count, ptLine);

        }

        public void plot(double wIn) {
            Float w = (float) wIn;

            if (count > 196) {                           // plot array full
                for (int i = 0; i <= 192; i += 4) {      // Shift all y-plot points one timestep
                    plotData[i+1] = plotData[i + 5];
                    plotData[i+3] = plotData[i + 7];
                }
                count = 196;
            }

            if (count == 0) {                           // First time, fake first reading
                GyroPlot.Point p = getPoint(0, w);
                plotData[count] = p.x;
                plotData[count+1] = p.y;
            } else {
                plotData[count] = plotData[count-2];    // and for the rest, copy last reading
                plotData[count + 1] = plotData[count-1];
            }

            float x = (float)count/20;                  // Add new reading
            GyroPlot.Point p = getPoint(x, w);
            plotData[count+2] =p.x;
            plotData[count+3] = p.y;

            count += 4;


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

        public class Point {
            float x = 0;
            float y = 0;
        }

        public GyroPlot.Point getPoint (float x, float y) {
            float margin = 2;       // percent
            float xOffset = 10;     //
            float yOffset = 10;     //
            GyroPlot.Point pt = new GyroPlot.Point();

            pt.x = width*((100-2*margin-xOffset)/(xMax-xMin)*(x-xMin)+margin+xOffset)/100;
            pt.y = height*((100-2*margin-yOffset)/(yMax-yMin)*(yMax-y)+margin)/100;

            return pt;
        }
    }
}
