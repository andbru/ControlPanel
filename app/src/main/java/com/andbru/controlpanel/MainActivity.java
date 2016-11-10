package com.andbru.controlpanel;

import android.os.Handler;
//import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.os.AsyncTask;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**

     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private PilotFragment mPilotFragment;
    static public TextView pilotLabel;
    private CompassFragment mCompassFragment;
    private SettingsFragment mSettingsFragment;

    private TCPClient mTcpClient;

    private int mInterval = 5000;
    private Handler mHandler;

    //we create a TCPClient object and
    //mTcpClient = new TCPClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mPilotFragment = new PilotFragment();
        mCompassFragment = new CompassFragment();
        mSettingsFragment = new SettingsFragment();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mTcpClient.sendMessage("$GET ");
            }
        });

        // connect to the server
        new connectTask().execute("");

        mHandler = new Handler();
        startRepeatingTask();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class PilotFragment extends Fragment {

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
            pilotLabel.setText("Pilot 2");
            return mPilotView;
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        public void setTextLabel(String mText) {
            if(pilotLabel != null)pilotLabel.setText(mText);
        }
    }


    public static class CompassFragment extends Fragment {

        public CompassFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_compass, container, false);
        }

        @Override
        public void onPause() {
            super.onPause();
        }
    }


    public static class SettingsFragment extends Fragment {

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_settings, container, false);
        }

        @Override
        public void onPause() {
            super.onPause();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return mPilotFragment;
                case 1:
                    return mCompassFragment;
                case 2:
                    return mSettingsFragment;
                default:
                    return mPilotFragment;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pilot";
                case 1:
                    return "Compass";
                case 2:
                    return "Settings";
                default:
                    return null;
            }

        }
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            //arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            //mAdapter.notifyDataSetChanged();

            int pnr = mViewPager.getCurrentItem();
            if(pnr == 0) {
                mPilotFragment.setTextLabel(values[0]);
            }
        }
    }


    Runnable mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    //updateStatus(); //this function can change value of mInterval.
                    int pnr = mViewPager.getCurrentItem();

                    //mPilotFragment.setTextLabel("Fantastiskt");
                    if(mTcpClient != null) mTcpClient.sendMessage("$GET ");

                } finally {
                    // 100% guarantee that this always happens, even if
                    // your update method throws an exception
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }
            }
        };

        void startRepeatingTask() {
            mStatusChecker.run();
        }

        void stopRepeatingTask() {
            mHandler.removeCallbacks(mStatusChecker);
        }
}

