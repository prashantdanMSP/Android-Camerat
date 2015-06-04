package com.camrate;

import java.io.InputStream;
import java.util.ArrayList;

import com.camrate.tabs.TabSample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class SplashScreen extends Activity {

	VideoView videoview;

	private Handler mHandler = new Handler();
	private Thread mSplashThread;
	private boolean isBackPressed = false;
	// public Integer[] ImgArray = { R.drawable.splash, R.drawable.animated_splash };
	InputStream stream = null;
	public static Typeface ProxiNova_Regular, ProxiNova_SemiBold, ProxiNova_Bold;

	public static String deviceID = "", deviceName = "", App_name = "", clientid = "", app_version = "";
	Uri VideoURL = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		videoview = (VideoView) findViewById(R.id.VideoView);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		final boolean KeepLoggedIn = prefs.getBoolean("KeepLoggedIn", false);
		ProxiNova_Regular = Typeface.createFromAsset(this.getAssets(), "ProximaNova-Regular.otf");
		ProxiNova_SemiBold = Typeface.createFromAsset(this.getAssets(), "ProximaNova-Semibold.otf");
		ProxiNova_Bold = Typeface.createFromAsset(this.getAssets(), "proxi_bold.ttf");
		System.out.println("KeepLoggedIn--->" + KeepLoggedIn);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float density = metrics.density;
		if (density >= 4.0) {
			// return "xxhdpi";
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new1);
		} else if (density >= 3.0) {
			// return "xhdpi";
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new);
		} else if (density >= 2.0) {
			// return "xhdpi";
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new720);
		} else if (density >= 1.5) {
			// return "hdpi";
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new480);
		} else if (density >= 1.0) {
			// return "mdpi";
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new480);
		} else if (density >= 0.75) {
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new480);
		} else {
			VideoURL = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.animated_new720);
		}

		clientid = prefs.getString("UserID", "");
		deviceID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		deviceName = android.os.Build.MODEL;
		App_name = getResources().getString(R.string.app_name);
		PackageManager manager = getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
			app_version = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// Start the MediaController
			MediaController mediacontroller = new MediaController(SplashScreen.this);
			mediacontroller.setAnchorView(videoview);
			// Get the URL from String VideoURL
			// Uri video = Uri.parse(video);
			// videoview.setMediaController(mediacontroller);
			videoview.setMediaController(null);
			videoview.setVideoURI(VideoURL);

		} catch (Exception e) {
			e.printStackTrace();
		}

		videoview.requestFocus();
		videoview.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {

				videoview.start();
			}
		});
		videoview.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if (KeepLoggedIn) {
					Intent i = new Intent(SplashScreen.this, TabSample.class);
					i.putExtra("isFor", "newest");
					startActivity(i);
					overridePendingTransition(0, 0);
					finish();
				} else {
					Intent i = new Intent(SplashScreen.this, AfterSplashScreen.class);
					startActivity(i);
					overridePendingTransition(0, 0);
					finish();
				}
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			isBackPressed = true;
			finish();
		}
		return super.onKeyDown(keyCode, event);

	}

}
