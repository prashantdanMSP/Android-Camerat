package com.camrate.share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;

public class ManuallyAdd extends Activity {

	EditText editTextEmail, editTextName;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manuallyadd);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// for custome title
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Add email address");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Button btnRefresh = (Button) findViewById(R.id.button2);
		btnRefresh.setVisibility(View.VISIBLE);
		btnRefresh.setText("Cancel");
		btnRefresh.setTextColor(Color.parseColor("#26BDBE"));
		btnRefresh.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		Button btnAdd = (Button) findViewById(R.id.button1);
		btnAdd.setVisibility(View.VISIBLE);
		btnAdd.setText("Done");
		//

		editTextEmail = (EditText) findViewById(R.id.editText_email);
		editTextName = (EditText) findViewById(R.id.editText_name);
		
		editTextEmail.setTypeface(SplashScreen.ProxiNova_Regular);
		editTextName.setTypeface(SplashScreen.ProxiNova_Regular);
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (editTextEmail.getText().toString().trim().equalsIgnoreCase("")) {
					Toast.makeText(ManuallyAdd.this, "Please enter email", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.putExtra("email", editTextEmail.getText().toString().trim());
					intent.putExtra("name", editTextName.getText().toString().trim());
					setResult(0, intent);
					finish();
				}
			}
		});

		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

}
