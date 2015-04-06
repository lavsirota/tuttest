package com.lav.tuttest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lav.tuttest.db.DbHelper;
import com.lav.tuttest.db.DbProvider;
import com.lav.tuttest.pref.PrefHelper;
import com.lav.tuttest.service.TimeService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainFragment extends Fragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_TIME_LIST = 1;
    private static final String SORT = DbHelper.Timelist.TABLE_NAME + "." + DbHelper.Timelist.DC.TIME;
    private static final String[] PROJECTION = new String[] {
            DbHelper.Timelist.TABLE_NAME + "." + DbHelper.Timelist.DC.ID,
            DbHelper.Timelist.TABLE_NAME + "." + DbHelper.Timelist.DC.TIME
    };
    private static final Uri CONTENT_URI = Uri.parse("content://" + DbProvider.PROVIDER_AUTHORITIES + "/timelist/");

    private SimpleCursorAdapter mAdapter;

    private BroadcastReceiver mTimeReceiver;
    private ServiceConnection mServiceConn;

    private TextView mTvTickCnt, mTvLeftCnt;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mTimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int cntTick = intent.getIntExtra(TimeService.EXTRA_KEY_CNT_TICK, 0);
                showTimeData(cntTick);
            }
        };
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.navigation_title_main);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void showTimeData(int cntTick) {

        int interval = PrefHelper.getClearDataInterval(getActivity());
        int cntLeft = interval - cntTick == 0 ? interval : interval - cntTick;

        String lastClearTime = PrefHelper.getLastClearTime(getActivity());

        mTvTickCnt.setText(lastClearTime);
        mTvLeftCnt.setText(String.valueOf(cntLeft));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button btn = (Button) view.findViewById(android.R.id.button1);
        btn.setOnClickListener(this);

        mTvTickCnt = (TextView) view.findViewById(R.id.tv_last_clear_time);
        mTvLeftCnt = (TextView) view.findViewById(R.id.tv_next_clear_time);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                TimeService service = ((TimeService.TimeServiceBinder) binder).getService();
                int cntTick = service.getCounter();
                getActivity().unbindService(this);
                showTimeData(cntTick);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) { }
        };

        mAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[] { DbHelper.Timelist.DC.TIME },
                new int[] { android.R.id.text1 },
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                String time = cursor.getString(1);
                showMsg(time);
            }
        });

        getLoaderManager().initLoader(LOADER_TIME_LIST, null, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // забиндится на сервис, чтоб сразу получить данные
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTimeReceiver, new IntentFilter(TimeService.ACTION_TICK));
        getActivity().bindService(new Intent(TimeService.SERVICE_ACTION), mServiceConn, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mTimeReceiver);
        super.onPause();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)	{
        return new CursorLoader(getActivity(), CONTENT_URI, PROJECTION, null, null, SORT);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {

            case LOADER_TIME_LIST:
                mAdapter.swapCursor(cursor);
                break;

            default:
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case android.R.id.button1:

                String time = getCurrentTime();
                saveTime(time);
                showMsg(time);
                break;

            default:
                break;
        }
    }


    private void saveTime(String time) {

        Uri uri = Uri.parse("content://" + DbProvider.PROVIDER_AUTHORITIES + "/timelist");

        ContentValues values = new ContentValues();
        values.put(DbHelper.Timelist.DC.TIME, time);

        Context context = getActivity().getApplicationContext();

        context.getContentResolver().insert(uri, values);
        getLoaderManager().getLoader(LOADER_TIME_LIST).forceLoad();
    }


    private void showMsg(String time) {

        Fragment f = new MsgFragment();

        Bundle args = new Bundle();
        args.putString(MsgFragment.ARG_CUR_TIME, time);
        f.setArguments(args);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, f, MsgFragment.TAG)
                .addToBackStack(MsgFragment.TAG)
                .commit();
    }


    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
