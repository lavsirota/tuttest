package com.lav.tuttest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.lav.tuttest.pref.PrefHelper;
import com.lav.tuttest.pref.SupportPreferenceFragment;
import com.lav.tuttest.service.TimeService;


public class ActivityMain extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private static boolean isChangeConfig; // признак изменения конфигурации - чтобы не стопать сервис в onDestroy

    Fragment mFragmentMain, mFragmentSupportPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int themeResId = PrefHelper.getThemeResId(this);
        setTheme(themeResId);
        getApplication().setTheme(themeResId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer
                , (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AppFragment())
                    .commit();
        } else {
            mNavigationDrawerFragment.setActionBarArrowDependingOnFragmentsBackStack();
        }

        if (!isServiceRunning(TimeService.class)) {
            startService(new Intent(TimeService.SERVICE_ACTION));
        }

        // после применения темы - показать фрагмент настроек;
        if (PrefHelper.isSettingRequest(getApplicationContext())) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SupportPreferenceFragment())
                    .addToBackStack(null)
                    .commit();
        }
        isChangeConfig = false;
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isChangeConfig = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {

        if (!isChangeConfig && !PrefHelper.isSettingRequest(this)) {
            stopService(new Intent(TimeService.SERVICE_ACTION));
        }
        PrefHelper.setSettingRequest(this, false);

        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.getDrawerLayout().closeDrawers();
        }
        else {
            super.onBackPressed();
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch (position) {

            case 0:

                if (mFragmentMain == null) {

                    mFragmentMain = new MainFragment();

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, mFragmentMain)
                            .addToBackStack(null)
                            .commit();
                }
                else {

                    if (!mFragmentMain.isAdded()) {
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, mFragmentMain)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if (mFragmentSupportPreference != null) {
                        getSupportFragmentManager().beginTransaction()
                                .remove(mFragmentSupportPreference)
                                .commit();

                        mFragmentSupportPreference = null;
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                }

                break;

            case 1:

                if (mFragmentSupportPreference == null) {

                    mFragmentSupportPreference = new SupportPreferenceFragment();

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, mFragmentSupportPreference)
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    if (!mFragmentSupportPreference.isAdded()) {
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, mFragmentSupportPreference)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if (mFragmentMain != null) {
                        getSupportFragmentManager().beginTransaction()
                                .remove(mFragmentMain)
                                .commit();

                        mFragmentMain = null;
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                }

                break;

            default:
                break;
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_logo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
}
