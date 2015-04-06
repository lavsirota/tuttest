package com.lav.tuttest.pref;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lav.tuttest.R;
import com.lav.tuttest.service.TimeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SupportPreferenceFragment extends Fragment
        implements AdapterView.OnItemClickListener, IDlgCallback {

    public static final String PREF_KEY = "key";
    public static final String PREF_VALUE = "value";
    public static final String PREF_TITLE = "title";
    public static final String PREF_SUMMARY = "summary";
    public static final String PREF_DEFAULT = "default";

    public static final String INPUT_TYPE = "inputtype";
    public static final String INPUT_GRAVITY = "gravity";


    private ListView mLv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pref, container, false);

        mLv = (ListView) view.findViewById(android.R.id.list);
        addPreferencesFromResource();

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.navigation_title_settings);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void addPreferencesFromResource() {

        List<Map<String, Object>> prefArr = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put(PREF_KEY, getString(R.string.pref_cleaning_data_frequency_key));
        map.put(PREF_TITLE, getString(R.string.pref_cleaning_data_frequency_title));
        map.put(PREF_SUMMARY, getString(R.string.pref_cleaning_data_frequency_summary));
        map.put(PREF_DEFAULT, getString(R.string.pref_cleaning_data_frequency_default));

        prefArr.add(map);

        map = new HashMap<>();
        map.put(PREF_KEY, getString(R.string.pref_theme_key));
        map.put(PREF_TITLE, getString(R.string.pref_theme_title));
        map.put(PREF_SUMMARY, getString(R.string.pref_theme_summary));
        map.put(PREF_DEFAULT, getString(R.string.pref_theme_default));

        prefArr.add(map);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), prefArr
                , R.layout.preference_item
                , new String[] { PREF_TITLE, PREF_SUMMARY }
                , new int[] { android.R.id.title, android.R.id.summary });

        mLv.setAdapter(adapter);
        mLv.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(position);
        String key = map.get(PREF_KEY).toString();

        if (key.equals(getString(R.string.pref_cleaning_data_frequency_key))) {

            int value = PrefHelper.getClearDataInterval(getActivity());

            DlgTextPref dlg = new DlgTextPref();
            dlg.setListener(this);
            Bundle args = new Bundle();

            args.putString(PREF_TITLE, map.get(PREF_TITLE).toString());
            args.putString(PREF_SUMMARY, map.get(PREF_SUMMARY).toString());
            args.putString(PREF_VALUE, String.valueOf(value));
            args.putInt(INPUT_TYPE, InputType.TYPE_CLASS_NUMBER);
            args.putInt(INPUT_GRAVITY, Gravity.END);

            dlg.setArguments(args);
            dlg.show(getFragmentManager(), key);

        }
        else if (key.equals(getString(R.string.pref_theme_key))) {

            DlgThemePref dlg = new DlgThemePref();
            dlg.setListener(this);
            Bundle args = new Bundle();

            args.putString(PREF_TITLE, map.get(PREF_TITLE).toString());
            args.putInt(PREF_VALUE, PrefHelper.getThemeId(getActivity()));

            dlg.setArguments(args);
            dlg.show(getFragmentManager(), key);
        }
    }


    @Override
    public void onPreferenceChange(Object value, String tag) {

        if (getString(R.string.pref_cleaning_data_frequency_key).equals(tag)) {

            int val;

            try {
                val = Integer.parseInt(value.toString());
            }
            catch (NumberFormatException e) {
                val = 5;
            }
            if (val < 1) {
                val = 1;
            }

            PrefHelper.setClearDataInterval(getActivity(), val);

            getActivity().stopService(new Intent(TimeService.SERVICE_ACTION));
            getActivity().startService(new Intent(TimeService.SERVICE_ACTION));
        }
        else if (getString(R.string.pref_theme_key).equals(tag)) {

            int val;

            try {
                val = Integer.parseInt(value.toString());
            }
            catch (NumberFormatException e) {
                val = 0;
            }

            PrefHelper.setThemeId(getActivity(), val);
            PrefHelper.setSettingRequest(getActivity(), true);

            FragmentActivity activity = getActivity();
            activity.finish();
            activity.startActivity(new Intent(activity, activity.getClass()));
        }
    }
}
