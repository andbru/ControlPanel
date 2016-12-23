package com.andbru.controlpanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Anders on 2016-11-26.
 */

public class PlotCourseFragment extends Fragment {

    PlotCourseFragment.CoursePlot mPlot;

    public PlotCourseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View mPlotCourseView = inflater.inflate(R.layout.fragment_plotcourse, container, false);

        mPlot = new PlotCourseFragment.CoursePlot(container.getContext());
        mPlot.setBackgroundColor(0xff000000);

        RelativeLayout insertPlot = (RelativeLayout)  mPlotCourseView.findViewById(R.id.psurface);

        insertPlot.addView(mPlot);

        return mPlotCourseView;
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
        double dYawCmd = new Double(mPD.yawCmd);
        double dYawIs = new Double(mPD.yawIs);

        float v = (float)dYawCmd;
        if((v>mPlot.center+5) || (v<mPlot.center-5))
        {
            float rem5 = v % 5;

            if (rem5 < 2.5) {
                mPlot.center = v - rem5;
                mPlot.count = 0;
            } else {
                mPlot.center = v - rem5 + 5;
                mPlot.count = 0;
            }

            mPlot.yMin = mPlot.center - 10;
            mPlot.yMax = mPlot.center + 10;
        }
        if(mPlot != null) mPlot.plot(dYawCmd, dYawIs);
        if(mPlot != null) mPlot.invalidate();
    }

    public class CoursePlot extends SurfaceView implements SurfaceHolder.Callback {

        Paint ptText = new Paint();
        Paint ptGrid = new Paint();
        Paint ptLine = new Paint();
        float width;
        float height;
        float xMin = 0;
        float xMax = 10;        // Grid physical size
        float yMin = -10;
        float yMax = 10;
        float center = 0;
        float[] plotData1 = new float[200];
        float[] plotData2 = new float[200];
        int count = 0;

        public CoursePlot(Context ctext) {
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

            PlotCourseFragment.CoursePlot.Point p1 = new PlotCourseFragment.CoursePlot.Point();
            PlotCourseFragment.CoursePlot.Point p2 = new PlotCourseFragment.CoursePlot.Point();

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
            ptLine.setColor(Color.GREEN);
            canvas.drawLines(plotData1, 0, count, ptLine);
            ptLine.setColor(Color.RED);
            canvas.drawLines(plotData2, 0, count, ptLine);
        }

        public void plot(double dyCmd, double dyIs) {

            float yCmd = (float) dyCmd;
            float yIs = (float) dyIs;

            // Green line
            if (count > 196) {                           // plot array full
                for (int i = 0; i <= 192; i += 4) {      // Shift all y-plot points one timestep
                    plotData1[i+1] = plotData1[i + 5];
                    plotData1[i+3] = plotData1[i + 7];
                }
            }

            // Red line
            if (count > 196) {                           // plot array full
                for (int i = 0; i <= 192; i += 4) {      // Shift all y-plot points one timestep
                    plotData2[i+1] = plotData2[i + 5];
                    plotData2[i+3] = plotData2[i + 7];
                }
                count = 196;
            }

            // Green line
            if (count == 0) {                           // First time, fake first reading
                PlotCourseFragment.CoursePlot.Point p = getPoint(0, yCmd);
                plotData1[count] = p.x;
                plotData1[count+1] = p.y;
            } else {
                plotData1[count] = plotData1[count-2];    // and for the rest, copy last reading
                plotData1[count + 1] = plotData1[count-1];
            }

            float x = (float)count/20;                  // Add new reading
            PlotCourseFragment.CoursePlot.Point p = getPoint(x, yCmd);
            plotData1[count+2] =p.x;
            plotData1[count+3] = p.y;

            // Red line
            if (count == 0) {                           // First time, fake first reading
                p = getPoint(0, yIs);
                plotData2[count] = p.x;
                plotData2[count+1] = p.y;
            } else {
                plotData2[count] = plotData2[count-2];    // and for the rest, copy last reading
                plotData2[count + 1] = plotData2[count-1];
            }

            x = (float)count/20;                  // Add new reading
            p = getPoint(x, yIs);
            plotData2[count+2] =p.x;
            plotData2[count+3] = p.y;

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

        public CoursePlot.Point getPoint (float x, float y) {
            float margin = 2;       // percent
            float xOffset = 10;     //
            float yOffset = 10;     //
            CoursePlot.Point pt = new CoursePlot.Point();

            pt.x = width*((100-2*margin-xOffset)/(xMax-xMin)*(x-xMin)+margin+xOffset)/100;
            pt.y = height*((100-2*margin-yOffset)/(yMax-yMin)*(yMax-y)+margin)/100;

            return pt;
        }
    }
}
