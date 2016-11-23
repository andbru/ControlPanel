package com.andbru.controlpanel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Anders on 2016-11-22.
 */

public class PlotRegFragment extends Fragment {

    RegPlot mPlot;

    public PlotRegFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View mPlotRegView = inflater.inflate(R.layout.fragment_plotreg, container, false);

        mPlot = new RegPlot(container.getContext());
        mPlot.setBackgroundColor(0xff000000);

        RelativeLayout insertPlot = (RelativeLayout)  mPlotRegView.findViewById(R.id.psurface);

        insertPlot.addView(mPlot);

        return mPlotRegView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void update(PilotData mPD) {
        // Update the plot
        double dYawCmd = new Double(mPD.yawCmd);
        double dYawIs = new Double(mPD.yawIs);
        if(mPlot != null) mPlot.plot(dYawCmd, dYawIs);
        if(mPlot != null) mPlot.invalidate();
    }
}
