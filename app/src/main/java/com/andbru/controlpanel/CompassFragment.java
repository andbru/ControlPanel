package com.andbru.controlpanel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
}
