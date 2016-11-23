package com.andbru.controlpanel;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Anders on 2016-11-11.
 */

public class PilotFragment extends Fragment implements View.OnClickListener{

    TextView rudder;
    TextView speed;
    Button biStdby;
    Button biHeadingHold;
    Button biGotoWpt;

    PassCmd cmdPasser;

    Compass mCompass = null;

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

        mCompass = new Compass(container.getContext());
        mCompass.setBackgroundColor(0xffffffff);
        RelativeLayout insertCompass = (RelativeLayout)  mPilotView.findViewById(R.id.csurface);
        insertCompass.addView(mCompass);

        rudder = (TextView) mPilotView.findViewById(R.id.textRudder);
        speed = (TextView) mPilotView.findViewById(R.id.textSpeed);
        biStdby = (Button) mPilotView.findViewById(R.id.biStdby);
        biHeadingHold = (Button) mPilotView.findViewById(R.id.biHeadingHold);
        biGotoWpt = (Button) mPilotView.findViewById(R.id.biGotoWpt);

        Button bHH = (Button)mPilotView.findViewById(R.id.bHeadingHold);
        bHH.setOnClickListener(this);
        Button bStdby = (Button)mPilotView.findViewById(R.id.bStdby);
        bStdby.setOnClickListener(this);
        Button bMinus5 = (Button)mPilotView.findViewById(R.id.bMinus5);
        bMinus5.setOnClickListener(this);
        Button bMinus1 = (Button)mPilotView.findViewById(R.id.bMinus1);
        bMinus1.setOnClickListener(this);
        Button bPlus1 = (Button)mPilotView.findViewById(R.id.bPlus1);
        bPlus1.setOnClickListener(this);
        Button bPlus5 = (Button)mPilotView.findViewById(R.id.bPlus5);
        bPlus5.setOnClickListener(this);

        return mPilotView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        cmdPasser = (PassCmd) c;
    }

    public interface PassCmd {
        public void passCmd(String cmd);
    }

    public void update(PilotData mPD) {

        // Update the compass
        double dYawCmd = new Double(mPD.yawCmd);
        double dYawIs = new Double(mPD.yawIs);
        mCompass.setCourses(dYawCmd, dYawIs);
        mCompass.invalidate();

        // Update measured values and status indications
        double dRudder = new Double(mPD.rudderIs);
        double dSpeed = new Double(mPD.gpsSpeed);
        if(rudder != null) rudder.setText(String.format( "%.1f", dRudder ));
        if(speed != null) speed.setText(String.format( "%.1f", dSpeed ));

        int s = 0x00000000;
        int h = 0x00000000;
        int w = 0x00000000;

        switch (mPD.mode) {
            case "1":
                s = 0xFF00FF00;
                h = 0xFFFFFF00;
                w = 0xFFFF0000;
                break;
            case "2":
                s = 0xFFFFFF00;
                h = 0xFF00FF00;
                w = 0xFFFF0000;
                break;
            case "3":
                s = 0xFFFF0000;
                h = 0xFFFF0000;
                w = 0xFFFF0000;
                break;
            default:
                s = 0xFFFFFF00;
                h = 0xFFFFFF00;
                w = 0xFF00FF00;
        }

        try {
            biStdby.setBackgroundColor(s);
            biHeadingHold.setBackgroundColor(h);
            biGotoWpt.setBackgroundColor(w);
        } catch ( NullPointerException e) {
            // Ignore, the fragment has been replaced by another before call has been invoked.
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bHeadingHold:
                cmdPasser.passCmd("bHH");
                break;
            case R.id.bStdby:
                cmdPasser.passCmd("bStdby");
                break;
            case R.id.bMinus5:
                cmdPasser.passCmd("bMinus5");
                break;
            case R.id.bMinus1:
                cmdPasser.passCmd("bMinus1");
                break;
            case R.id.bPlus1:
                cmdPasser.passCmd("bPlus1");
                break;
            case R.id.bPlus5:
                cmdPasser.passCmd("bPlus5");
                break;
        }
    }
}

