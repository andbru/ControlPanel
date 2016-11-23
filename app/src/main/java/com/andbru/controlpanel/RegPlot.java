package com.andbru.controlpanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Created by Anders on 2016-11-22.
 */

public class RegPlot extends SurfaceView implements SurfaceHolder.Callback {

    Paint ptText = new Paint();
    Paint ptGrid = new Paint();
    Paint ptLine = new Paint();
    float width;
    float height;
    float xMin = 0;
    float xMax = 5;        // Grid physical size
    float yMin = -2;
    float yMax = 2;
    float[] plotData = new float[100];
    int count = 0;

    public RegPlot(Context ctext) {
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

        Point p1 = new Point();
        Point p2 = new Point();

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

        canvas.drawLines(plotData, ptLine);

    }

    public void plot(double yCmd, double yIs) {
        Float diff = (float)(yCmd - yIs);

        if (count > 96) {                           // plot array full
            for (int i = 0; i <= 92; i += 4) {      // Shift all y-plot points one timestep
                plotData[i+1] = plotData[i + 5];
                plotData[i+3] = plotData[i + 7];
            }
            count = 96;
        }

        if (count == 0) {                           // First time, fake first reading
            Point p = getPoint(0, 0);
            plotData[count] = p.x;
            plotData[count+1] = p.y;
        } else {
            plotData[count] = plotData[count-2];    // and for the rest, copy last reading
            plotData[count + 1] = plotData[count-1];
        }

        float x = (float)count/20;                  // Add new reading
        Point p = getPoint(x, diff);
        plotData[count+2] =p.x;
        plotData[count+3] = p.y;

        count += 4;


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

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

    public Point getPoint (float x, float y) {
        float margin = 2;       // percent
        float xOffset = 10;     //
        float yOffset = 10;     //
        Point pt = new Point();

        pt.x = width*((100-2*margin-xOffset)/(xMax-xMin)*(x-xMin)+margin+xOffset)/100;
        pt.y = height*((100-2*margin-yOffset)/(yMax-yMin)*(-y-yMin)+margin)/100;

        return pt;
    }
}
