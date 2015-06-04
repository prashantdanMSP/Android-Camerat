package com.camrate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.global.Constant;
import com.camrate.tabs.ActivityManage;

public class ThankYou extends Activity implements OnClickListener {

	private Context context;
	ProgressBar progressBar;
	TextView txtThankyouText, txtlblThankYou;
	Constant con;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thankyou);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		context = ThankYou.this;

		init();
	}

	private void init() {
		ActivityManage.isFromThankyou = true;
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("");
		Button btnBack = (Button) findViewById(R.id.button_left);
		btnBack.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		txtThankyouText = (TextView) findViewById(R.id.txtvalThankYou);
		txtlblThankYou = (TextView) findViewById(R.id.txtlblThankYou);
		con = new Constant();

		Intent intent = getIntent();
		String ThankYou_Type = intent.getStringExtra("ThankYou_Type");
		if (ThankYou_Type.equalsIgnoreCase("0")) {
			txtThankyouText.setText("We take your reports seriously. We look into every issue and take action when people violate our code of conduct.");
		} else if (ThankYou_Type.equalsIgnoreCase("1")) {
			txtThankyouText.setText("Thank you for your report. We will remove this photo or comment if it violates our code of conduct.");

		}
		txtThankyouText.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblThankYou.setTypeface(SplashScreen.ProxiNova_Bold);
		btnBack.setTypeface(SplashScreen.ProxiNova_Bold);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button_left) {

			finish();
		}

	}

}
