package com.lav.tuttest.pref;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lav.tuttest.R;


public class DlgTextPref extends DialogFragment implements DialogInterface.OnClickListener {

	private String mTitle, mSummary, mValue;
	private int mInputType, mGravity;
	private EditText mEtValue;

	private IDlgCallback mListener = null;


	public void setListener(IDlgCallback listener) {
		mListener = listener;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)	{

		if (savedInstanceState == null) {
			savedInstanceState = getArguments();
		}

		mTitle     = savedInstanceState.getString(SupportPreferenceFragment.PREF_TITLE);
		mSummary   = savedInstanceState.getString(SupportPreferenceFragment.PREF_SUMMARY);
		mValue     = savedInstanceState.getString(SupportPreferenceFragment.PREF_VALUE);
		mInputType = savedInstanceState.getInt(SupportPreferenceFragment.INPUT_TYPE);
		mGravity   = savedInstanceState.getInt(SupportPreferenceFragment.INPUT_GRAVITY);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dlg_get_text, null);

		mEtValue = (EditText) view.findViewById(android.R.id.edit);
		mEtValue.setInputType(mInputType);
		mEtValue.setGravity(mGravity);
		mEtValue.setText(mValue);

		return new AlertDialog.Builder(getActivity())
				.setTitle(mTitle)
				.setMessage(mSummary)
				.setPositiveButton(getString(android.R.string.ok), this)
				.setNegativeButton(getString(android.R.string.cancel), null)
				.setView(view)
				.create();
	}


	public void onClick(DialogInterface dialog, int which) {
		if (mListener != null) {
			mListener.onPreferenceChange(mEtValue.getText().toString(), getTag());
		}
		dismiss();
	}
}
