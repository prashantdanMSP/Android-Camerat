package com.camrate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.explore.SlidingDrawerActivity;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.tabs.TabSample;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;

public class Login extends Activity {

	JSONObject profile;
	Facebook facebook;
	Button btnfb, btnLogin, btnfrgt_pwd;
	private AsyncFacebookRunner mAsyncRunner;
	String fbID, Name, Email;
	SharedPreferences prefs;
	ProgressDialog pd;
	JSONParser jparser = new JSONParser();
	String UserID;
	String Result = null;
	EditText edtName, edtPass;
	ProgressBar pd1;

	// for GCM
	AsyncTask<Void, Void, Void> mRegisterTask;
	public static String name;
	public static String email;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		context = Login.this;
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Log In");
		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.INVISIBLE);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Login.this, AfterSplashScreen.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
		});
		btnLogin = (Button) findViewById(R.id.btn_SignIN);

		edtName = (EditText) findViewById(R.id.edt_username);
		pd1 = (ProgressBar) findViewById(R.id.progressBar1);
		edtPass = (EditText) findViewById(R.id.edt_pass);
		btnfrgt_pwd = (Button) findViewById(R.id.btn_frgt_pwd);
		btnfrgt_pwd.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		edtName.setTypeface(SplashScreen.ProxiNova_Regular);
		edtPass.setTypeface(SplashScreen.ProxiNova_Regular);
		btnLogin.setTypeface(SplashScreen.ProxiNova_Bold);
		for (Activity activity : Constant.arrActivity) {
			activity.finish();
		}
		Constant.arrActivity.clear();

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isNetworkConnected()) {

					new LoginUser().execute("");
					// Intent intent=new Intent(BeforeLogin.this,EpForumHome.class);
					// startActivity(intent);

				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		btnfrgt_pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Login.this, ForgotPassword.class);
				startActivity(intent);
			}
		});

	}

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	public void GcmRegister() {
		GCMRegistrar.checkDevice(Login.this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);
		// ServerUtilities.unregister(context, regId, deviceID, deviceName, App_name, clientid, app_version);
		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				// Toast.makeText(getApplicationContext(),
				// "Already registered with GCM", Toast.LENGTH_LONG)
				// .show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user

						Log.d("TAG", "=====FOR GCM ======");
						Log.d("TAG", "regId " + regId);
						Log.d("TAG", "deviceID " + SplashScreen.deviceID);
						Log.d("TAG", "deviceName " + SplashScreen.deviceName);
						Log.d("TAG", "App_name " + SplashScreen.App_name);
						Log.d("TAG", "clientid " + SplashScreen.clientid);
						Log.d("TAG", "app_version " + SplashScreen.app_version);

						ServerUtilities.register(context, regId, SplashScreen.deviceID, SplashScreen.deviceName, SplashScreen.App_name, SplashScreen.clientid, SplashScreen.app_version);

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}



	class LoginUser extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			// pd = ProgressDialog.show(Login.this, "","Validating User",true);
			pd1.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=CheckLogin&User_Name=" + edtName.getText().toString().trim() + "&User_Password=" + edtPass.getText().toString().trim() + "";
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");
				if (Result.equalsIgnoreCase("1")) {
					JSONObject user = json.getJSONObject("user");
					String UserID = user.getString("User_ID");
					String User_Image = user.getString("User_Imagepath");
					String User_Email = user.getString("User_Email");
					String User_Name = user.getString("User_Name");
					SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
					SharedPreferences.Editor edit1 = prefs.edit();
					edit1.putString("UserID", UserID);
					SplashScreen.clientid = UserID;
					edit1.putBoolean("KeepLoggedIn", Boolean.TRUE);
					edit1.putString("User_Imagepath", User_Image);
					edit1.putString("User_Email", User_Email);
					edit1.putString("User_Name", User_Name);
					edit1.putString("User_FirstName", user.getString("User_FirstName"));
					edit1.putString("User_LastName", user.getString("User_LastName"));
					edit1.putString("User_CountryID", user.getString("User_CountryID"));
					edit1.putString("User_City", user.getString("User_City"));
					edit1.putString("User_Gender", user.getString("User_Gender"));
					edit1.putString("User_Birthdate", user.getString("User_Birthdate"));
					edit1.putString("User_Age", user.getString("User_Age"));
					edit1.putString("User_AgeRange", user.getString("User_AgeRange"));
					edit1.putString("User_Desc", user.getString("User_Desc"));
					edit1.putString("User_PushNotification", user.getString("User_PushNotification"));
					edit1.putString("User_Location", user.getString("User_Location"));
					edit1.putString("User_Badge", user.getString("User_Badge"));
					edit1.putString("User_Status", user.getString("User_Status"));
					edit1.putString("User_IsPrivate", user.getString("User_IsPrivate"));
					edit1.putString("User_IsActive", user.getString("User_IsActive"));
					edit1.putString("User_LastSession", user.getString("User_LastSession"));
					edit1.putString("User_Date", user.getString("User_Date"));
					edit1.commit();
				}
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
			// pd.dismiss();
			pd1.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				GcmRegister();
				Intent intent = new Intent(Login.this, TabSample.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isFor", "newest");
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();

			}
			if (status.equals("0")) {

				SimpleAlert("CamRate", "Login Unsuccessfully");
			}
			if (status.equals("2")) {

				SimpleAlert("CamRate", "Invalid User/Password..Please Try Again");
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
	public void SimpleAlert1(String t, String b) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Intent intent = new Intent(Login.this, SlidingDrawerActivity.class);
				// startActivity(intent);
				// overridePendingTransition(0, 0);
				// finish();
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// for GCM

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app requirement For now i am just displaying it on the screen
			 * */

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
		}
		super.onDestroy();
	}

}
