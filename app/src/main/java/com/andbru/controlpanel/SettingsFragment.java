package com.andbru.controlpanel;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Created by Anders on 2016-11-11.
 */

public class SettingsFragment extends Fragment implements OnClickListener {

    TextView Kp;
    TextView Kd;
    TextView Ki;
    TextView Km;

    PassCmd cmdPasser;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mSettingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        Kp = (TextView) mSettingsView.findViewById(R.id.Kp);

        Button KpDec = (Button)mSettingsView.findViewById(R.id.bKpDec);
        Button KpInc = (Button)mSettingsView.findViewById(R.id.bKpInc);
        KpDec.setOnClickListener(this);
        KpInc.setOnClickListener(this);

        Kd = (TextView) mSettingsView.findViewById(R.id.Kd);

        Button KdDec = (Button)mSettingsView.findViewById(R.id.bKdDec);
        Button KdInc = (Button)mSettingsView.findViewById(R.id.bKdInc);
        KdDec.setOnClickListener(this);
        KdInc.setOnClickListener(this);

        Ki = (TextView) mSettingsView.findViewById(R.id.Ki);

        Button KiDec = (Button)mSettingsView.findViewById(R.id.bKiDec);
        Button KiInc = (Button)mSettingsView.findViewById(R.id.bKiInc);
        KiDec.setOnClickListener(this);
        KiInc.setOnClickListener(this);

        Km = (TextView) mSettingsView.findViewById(R.id.Km);

        Button KmDec = (Button)mSettingsView.findViewById(R.id.bKmDec);
        Button KmInc = (Button)mSettingsView.findViewById(R.id.bKmInc);
        KmDec.setOnClickListener(this);
        KmInc.setOnClickListener(this);

        return mSettingsView;
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
        if(Kp != null) Kp.setText(mPD.Kp);
        if(Kd != null) Kd.setText(mPD.Kd);
        if(Ki != null) Ki.setText(mPD.Ki);
        if(Km != null) Km.setText(mPD.Km);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bKpDec:
                cmdPasser.passCmd("bKpDec");
                break;
            case R.id.bKpInc:
                cmdPasser.passCmd("bKpInc");
                break;
            case R.id.bKdDec:
                cmdPasser.passCmd("bKdDec");
                break;
            case R.id.bKdInc:
                cmdPasser.passCmd("bKdInc");
                break;
            case R.id.bKiDec:
                cmdPasser.passCmd("bKiDec");
                break;
            case R.id.bKiInc:
                cmdPasser.passCmd("bKiInc");
                break;
            case R.id.bKmDec:
                cmdPasser.passCmd("bKmDec");
                break;
            case R.id.bKmInc:
                cmdPasser.passCmd("bKmInc");
                break;
        }
    }
}
