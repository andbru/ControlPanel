package com.andbru.controlpanel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Anders on 2016-11-11.
 */

public class PilotFragment extends Fragment {

    static public TextView pilotLabel;

    public PilotFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mPilotView = inflater.inflate(R.layout.fragment_pilot, container, false);
        pilotLabel = (TextView) mPilotView.findViewById(R.id.pilot_label);
        //pilotLabel.setText("Pilot 2");
        pilotLabel.setText("xxx");
        return mPilotView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setTextLabel(String mText) {
        if(pilotLabel != null)pilotLabel.setText(mText);
    }

    public void update(PilotData mPD) {
        if(pilotLabel != null)pilotLabel.setText(mPD.yawIs);
    }
}
