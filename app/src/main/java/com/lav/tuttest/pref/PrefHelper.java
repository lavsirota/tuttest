package com.lav.tuttest.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lav.tuttest.R;


public class PrefHelper {

	public static int getClearDataInterval(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(pref.getString(context.getString(R.string.pref_cleaning_data_frequency_key)
				, context.getString(R.string.pref_cleaning_data_frequency_default)));
	}
	
	
	public static void setClearDataInterval(Context context, int value) {
		SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putString(context.getString(R.string.pref_cleaning_data_frequency_key), String.valueOf(value));

		prefEditor.commit();
	}


	public static String getLastClearTime(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(context.getString(R.string.pref_last_clear_time_key)
				, context.getString(R.string.pref_last_clear_time_default));
	}


	public static void setLastClearTime(Context context, String value) {
		SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putString(context.getString(R.string.pref_last_clear_time_key), value);

		prefEditor.commit();
	}


	public static int getThemeId(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Integer.parseInt(pref.getString(context.getString(R.string.pref_theme_key)
				, context.getString(R.string.pref_theme_default)));
	}


	public static void setThemeId(Context context, int value) {
		SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putString(context.getString(R.string.pref_theme_key), String.valueOf(value));

		prefEditor.commit();
	}


	public static int getThemeResId(Context context) {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String theme = pref.getString(context.getString(R.string.pref_theme_key)
				, context.getString(R.string.pref_theme_default));

		if (theme.equals(context.getString(R.string.theme_blue))) {
			return R.style.AppThemeBlue;
		}
		else if (theme.equals(context.getString(R.string.theme_orange))) {
			return R.style.AppThemeOrange;
		}

		return R.style.AppThemeBlue;
	}


	public static boolean isSettingRequest(Context context)	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getString(R.string.pref_requerst_setting_key)
				, Boolean.parseBoolean(context.getString(R.string.pref_requerst_setting_default)));
	}

	public static void setSettingRequest(Context context, boolean value) {
		SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putBoolean(context.getString(R.string.pref_requerst_setting_key), value);

		prefEditor.commit();
	}
}
