package com.andbru.controlpanel;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity implements SettingsFragment.PassCmd, PilotFragment.PassCmd{

    /**     Control Panel for Raspberry Pi 3 autopilot
     *
     * This app is based on the "Swipe View with Tabs" default app generation in Android Studio.
     * It uses ViewPager and FragmentPagerAdapter. The fragments are defined in separate files.
     * I have a TCPClient class in a separate file set up on a background thread with ActiveSync
     * in this file. The TCPClient calls to poll for new data regularly with Handler.postDelayed in
     * runnable mStatusChecker.
     *
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private PilotFragment mPilotFragment;
    private CompassFragment mCompassFragment;
    private PlotCourseFragment mPlotCourseFragment;
    private PlotGyroFragment mPlotRegFragment;
    private SettingsFragment mSettingsFragment;

    private TCPClient mTcpClient;

    private int mInterval = 200;
    private Handler mHandler;

    connectTask cT;

    PilotData mPilotData = new PilotData();

    public void relayCmd(String txt){
        mTcpClient.pilotCmd(txt);
    }

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
        mPlotCourseFragment = new PlotCourseFragment();
        mPlotRegFragment = new PlotGyroFragment();
        mSettingsFragment = new SettingsFragment();

        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // connect to the server
        cT = (connectTask) new connectTask().execute();
        startRepeatingTask();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Kill TcpClient
        mTcpClient.stopClient();
        cT.cancel(true);

        stopRepeatingTask();
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
    public void passCmd(String cmd) {
        mTcpClient.pilotCmd(cmd);
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
                    return mPlotCourseFragment;
                case 3:
                    return mPlotRegFragment;
                case 4:
                    return mSettingsFragment;
                default:
                    return mPilotFragment;
            }

        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pilot";
                case 1:
                    return "Compass";
                case 2:
                    return "Course Plot";
                case 3:
                    return "Gyro Plot";
                case 4:
                    return "Settings";
                default:
                    return null;
            }

        }
    }

    public class connectTask extends AsyncTask<String,PilotData,TCPClient> {

        PilotData mPD = new PilotData();

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(PilotData mPD) {

                    //this method calls the onProgressUpdate
                    publishProgress(mPD);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(PilotData... values) {
            super.onProgressUpdate(values);

            // Don't update invisible fragments. Restart plot when it becomes visible.

            if(mViewPager.getCurrentItem() == 0) mPilotFragment.update(values[0]);

            if(mViewPager.getCurrentItem() == 1) mCompassFragment.update(values[0]);

            if(mViewPager.getCurrentItem() == 2) mPlotCourseFragment.update(values[0]);
                else {
                if(mPlotCourseFragment.mPlot != null) mPlotCourseFragment.mPlot.count = 0;
            }

            if(mViewPager.getCurrentItem() == 3) mPlotRegFragment.update(values[0]);
                else {
                if(mPlotRegFragment.mPlot != null) mPlotRegFragment.mPlot.count = 0;
            }

            if(mViewPager.getCurrentItem() == 4) mSettingsFragment.update(values[0]);
        }
    }


    Runnable mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {

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

