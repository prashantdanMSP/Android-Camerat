package com.camrate.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

@SuppressLint("SetJavaScriptEnabled")
public class ShowSettingContent extends Activity {
	private TextView lblTitle;
	private WebView web;
	ProgressBar progress;
	Button btnBack;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_settings);

		progress = (ProgressBar) findViewById(R.id.prWeb);
		btnBack = (Button) findViewById(R.id.btnBack);
		lblTitle = (TextView) findViewById(R.id.lblInviteFriTitle);
		lblTitle.setText(getIntent().getStringExtra("title"));
		lblTitle.setTypeface(SplashScreen.ProxiNova_Bold);
		String url = getIntent().getStringExtra("url");
		web = (WebView) findViewById(R.id.webSettings);
		web.loadUrl(url);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setUseWideViewPort(true);
		// web.getSettings().setPluginsEnabled(true);
		web.getSettings().setPluginState(WebSettings.PluginState.ON);
		web.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);

		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				web.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}
		});
		web.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				progress.setProgress(newProgress);
			}

		});
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

}
