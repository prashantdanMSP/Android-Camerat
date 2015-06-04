package com.camrate;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;

public class ForgotPassword extends Activity {

	EditText edtemail;
	Constant cc = new Constant();
	ProgressDialog pd;
	JSONParser jparser = new JSONParser();
	String Result = null;
	checkInternet chkNet;
	TextView txtlbldntpanic,txtlbldesc;
	Function fun;
	Button btnSubmit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Reset Password");
		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.INVISIBLE);
		edtemail = (EditText) findViewById(R.id.edt_frgt_email);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, 0);
			}
		});
		txtlbldntpanic=(TextView)findViewById(R.id.txtlblAlert);
		txtlbldesc=(TextView)findViewById(R.id.txtlblDesc);
		btnSubmit=(Button)findViewById(R.id.btnSubmit);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		btnSubmit.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbldntpanic.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbldesc.setTypeface(SplashScreen.ProxiNova_Regular);
		edtemail.setTypeface(SplashScreen.ProxiNova_Regular);
		chkNet = new checkInternet(ForgotPassword.this);
		fun = new Function(ForgotPassword.this);
		/*next.setBackgroundResource(R.drawable.btnsubmit);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edtemail.getText().toString().length() == 0) {
					fun.SimpleAlert("CamRate", "Please enter email");
				} else if ((!edtemail.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+"))) {
					fun.SimpleAlert("CamRate", "Please enter valid email");
				} else {
					if (chkNet.isNetworkConnected()) {
						new ResetPassword().execute("");
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			}
		});*/
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edtemail.getText().toString().length() == 0) {
					fun.SimpleAlert("CamRate", "Please enter email");
				} else if ((!edtemail.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+"))) {
					fun.SimpleAlert("CamRate", "Please enter valid email");
				} else {
					if (chkNet.isNetworkConnected()) {
						new ResetPassword().execute("");
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	class ResetPassword extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd = ProgressDialog.show(ForgotPassword.this, "", "", true);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=forgotpassword&User_Email=" + edtemail.getText().toString().trim();
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");
				/*
				 * JSONArray json1=json.getJSONArray("Login"); for(int s=0;s<json1.length();s++) { JSONObject pubdetail=json1.getJSONObject(s); UserID=pubdetail.getString("User_ID"); Log.d("Tag udid", "val"+UserID); } SharedPreferences prefs =
				 * PreferenceManager.getDefaultSharedPreferences(getBaseContext()); SharedPreferences.Editor edit1 = prefs.edit(); edit1.putString("userid", UserID); edit1.commit();
				 */

				/*
				 * Result1=json.getString("userid"); Log.d("Tag udid", "val"+Result1); SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				 * 
				 * SharedPreferences.Editor edit1 = prefs.edit(); edit1.putString("userid", Result1); edit1.commit();
				 */
			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.dismiss();
			if (status.equals("1")) {
				SimpleAlert("CamRate", "Your password has been sent to your email address. Please check and login again.");
				// Toast.makeText(getApplicationContext(),"Successfully Register", Toast.LENGTH_SHORT).show();
				// Intent intent =new Intent(ForgotPassword.this,Tutorial.class);
				// startActivity(intent);
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Username or email does not exist!");
			}
			if (status.equalsIgnoreCase("2")) {
				AlertSend("CamRate", "Looks like you haven't confirmed your account. Would you like us to resend a confirmation mail?");	
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	class ResendEmail extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd = ProgressDialog.show(ForgotPassword.this, "", "", true);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=ResendEmail&User_Email=" + edtemail.getText().toString().trim();
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.dismiss();
			if (status.equals("1")) {
				SimpleAlert("CamRate", "Confirmation email has been sent to your email account. Please check your email to confirm your account.");
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Username or email does not exist!");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	// show alert
	public void SimpleAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// show alert
	public void AlertSend(String t, String b) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Resend", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				//dialog.dismiss();
				new ResendEmail().execute("");
			}
		});
		builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

}
