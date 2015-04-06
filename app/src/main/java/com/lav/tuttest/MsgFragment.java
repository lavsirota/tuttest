package com.lav.tuttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MsgFragment extends Fragment {

    public static final String TAG = "MsgFragment";
    public static final String ARG_CUR_TIME = "ARG_CUR_TIME";

    private String mCurTime;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCurTime = getArguments().getString(ARG_CUR_TIME);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_msg, container, false);

        TextView tvTime = (TextView) view.findViewById(android.R.id.text1);
        tvTime.setText(mCurTime);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.msg_fragment_title);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
