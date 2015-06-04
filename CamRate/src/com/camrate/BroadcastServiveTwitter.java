package com.camrate;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class BroadcastServiveTwitter extends Service {

	private final static String TAG = "BroadcastService";

	public static final String COUNTDOWN_BR = "com.camrate.Twitter_share";
	Intent bi = new Intent(COUNTDOWN_BR);

	@Override
	public void onCreate() {
		super.onCreate();
		// / int pos = bi.getIntExtra("position", 0);
		bi.putExtra("position", 0);
		sendBroadcast(bi);
	}

	@Override
	public void onDestroy() {

		Log.i(TAG, "Timer cancelled");

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
