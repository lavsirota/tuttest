package com.lav.tuttest.pref;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lav.tuttest.R;


public class DlgThemePref extends DialogFragment implements DialogInterface.OnClickListener {

	private IDlgCallback mListener = null;
	private String mTitle;
	private int mValue;
	private ListView mLv;
	

	public void setListener(IDlgCallback listener) {
		mListener = listener;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (savedInstanceState == null) {
			savedInstanceState = getArguments();
		}

		mTitle = savedInstanceState.getString(SupportPreferenceFragment.PREF_TITLE);
		mValue = savedInstanceState.getInt(SupportPreferenceFragment.PREF_VALUE);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dlg_theme_list, null);

		String[] arr = getResources().getStringArray(R.array.themes_title);
		final ArrAdapter adapter = new ArrAdapter(getActivity(), R.layout.pref_list_theme_item, arr);

		mLv = (ListView) view.findViewById(android.R.id.list);

		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mLv.setItemChecked(position, true);
				mValue = position;
			}
		});

		mLv.setAdapter(adapter);
		mLv.setItemChecked(mValue, true);

		return new AlertDialog.Builder(getActivity())
				.setTitle(mTitle)
				.setPositiveButton(getString(android.R.string.ok), this)
				.setNegativeButton(getString(android.R.string.cancel), null)
				.setView(view)
				.create();
	}
	
	
	public void onClick(DialogInterface dialog, int which) {
		if (mListener != null) {
			mListener.onPreferenceChange(mValue, getTag());
		}
		dismiss();
	}


	private class ArrAdapter extends ArrayAdapter<String> {

		public ArrAdapter(Context context, int resource, String[] arr) {
			super(context, resource, arr);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View row = super.getView(position, convertView, parent);

			if (position == mValue) {
				row.setBackgroundResource(R.drawable.pref_list_selected_item);
			}
			else {
				row.setBackgroundColor(Color.TRANSPARENT);
			}

			return row;
		}
	}
}
