package com.camrate.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

public class SeeVideoActivity extends Activity implements OnTouchListener {
	WebView web;
	ProgressBar progress;
	TextView txtlblMain;
	Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_video);
		init();

		findViewById(R.id.btnSeeVideoBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void init() {
		progress = (ProgressBar) findViewById(R.id.prVideoWeb);
		web = (WebView) findViewById(R.id.webSeeVideo);
		txtlblMain = (TextView) findViewById(R.id.lblInviteFriTitle);
		btnBack = (Button) findViewById(R.id.btnSeeVideoBack);
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setPluginState(WebSettings.PluginState.ON);
		// web.getSettings().setUserAgent(WebSettings);
		web.setWebChromeClient(new WebChromeClient() {
		});
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		String html = getHTML("oArVMSd4Zkk");
		web.loadDataWithBaseURL("", html, mimeType, encoding, "");
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public String getHTML(String videoId) {

		String html = "<iframe class=\"youtube-player\" " + "style=\"border: 0; width: 100%; height: 95%;" + "padding:0px; margin:0px\" " + "id=\"ytplayer\" type=\"text/html\" " + "src=\"http://www.youtube.com/embed/" + videoId + "?fs=0\" frameborder=\"0\" " + "allowfullscreen autobuffer "
				+ "controls onclick=\"this.play()\">\n" + "</iframe>\n";

		return html;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		web.onPause();
	}
}
