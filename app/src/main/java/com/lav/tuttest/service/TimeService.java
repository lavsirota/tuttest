package com.lav.tuttest.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;

import com.lav.tuttest.db.DbProvider;
import com.lav.tuttest.pref.PrefHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TimeService extends Service {

	public static final String SERVICE_ACTION = "com.lav.tuttest.service.TimeService";

	public static final String ACTION_TICK = "TUT_TEST_ACTION_TICK";
	public static final String EXTRA_KEY_CNT_TICK = "EXTRA_KEY_CNT_TICK";

	private static final long TICK_INTERVAL = 1 * 60;

	private int mInterval;
	private volatile int mCounter;
	private ScheduledExecutorService mService;


	@Override
	public void onCreate() {

		super.onCreate();

		mInterval = PrefHelper.getClearDataInterval(getApplicationContext());
		mCounter = 0;

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				mCounter++;

				if (mCounter >= mInterval) {
					mCounter = 0;
					clearData();
				}

				sendTick(mCounter);
			}
		};

		mService = Executors.newSingleThreadScheduledExecutor();
		mService.scheduleAtFixedRate(runnable, TICK_INTERVAL, TICK_INTERVAL, TimeUnit.SECONDS);
	}


	private void sendTick(int counter) {
		Intent intent = new Intent(ACTION_TICK);
		intent.putExtra(EXTRA_KEY_CNT_TICK, counter);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}


	private void clearData() {

		try {
			((Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE)).vibrate(50);
		}
		finally {
		}

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		PrefHelper.setLastClearTime(getApplicationContext(), sdf.format(new Date()));

		Uri uri = Uri.parse("content://" + DbProvider.PROVIDER_AUTHORITIES + "/timelist");
		getApplicationContext().getContentResolver().delete(uri, null, null);
	}


	@Override
	public void onDestroy()	{
		if (!mService.isTerminated() && !mService.isShutdown()) {
			mService.shutdown();
		}
		super.onDestroy();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return new TimeServiceBinder();
	}


	public class TimeServiceBinder extends Binder {
		public TimeService getService() {
			return TimeService.this;
		}
	}

	public int getCounter() {
		return mCounter;
	}
}
