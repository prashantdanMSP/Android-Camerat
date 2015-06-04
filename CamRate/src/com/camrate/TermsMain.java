package com.camrate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.explore.SlidingDrawerActivity;
import com.camrate.global.Constant;
import com.camrate.global.checkInternet;
import com.camrate.tabs.TabSample;
import com.camrate.tools.HttpClient;
import com.google.android.gcm.GCMRegistrar;

public class TermsMain extends Activity {

	String UserID;
	Button btnTerms, btnPrivacy, btnCode, btnAgree, btnCancel;
	Fragment fragment = null;
	ProgressBar pd;
	String UserName, UserEmail, UserPass, UserImgPic, countryID, cityName, Gender_ID, Date = "";
	String Response;
	Constant con = new Constant();
	checkInternet chkNet;
	String path;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.termsmain);
		btnTerms = (Button) findViewById(R.id.chkTerms);
		btnPrivacy = (Button) findViewById(R.id.chkPrivacy);
		btnCode = (Button) findViewById(R.id.chkCode);
		btnAgree = (Button) findViewById(R.id.btnAgree);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		if (savedInstanceState == null) {

			btnTerms.setBackgroundResource(R.color.Theme_Color);
			btnPrivacy.setBackgroundResource(R.color.btnsearchunsel);
			btnCode.setBackgroundResource(R.color.btnsearchunsel);
			fragment = new Terms_Condition();
			Bundle bundle = new Bundle();
			bundle.putString("url", "http://camrate.com/main/frmAppTOS.php");
			fragment.setArguments(bundle);
			// btnTerms.setBackgroundResource(R.drawable.btnterms_sel);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		}
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Legal Information");
		Button Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.INVISIBLE);

		Button Settings = (Button) findViewById(R.id.button1);
		Settings.setVisibility(View.INVISIBLE);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		chkNet = new checkInternet(TermsMain.this);
		Intent intent = getIntent();
		UserImgPic = intent.getStringExtra("UserImgPic");
		UserEmail = intent.getStringExtra("User_Email");
		UserPass = intent.getStringExtra("User_Password");
		UserName = intent.getStringExtra("User_Name");
		countryID = intent.getStringExtra("countryID");
		cityName = intent.getStringExtra("cityName");
		Gender_ID = intent.getStringExtra("gender");
		Date = intent.getStringExtra("Birthdate");
		btnAgree.setTypeface(SplashScreen.ProxiNova_Bold);
		btnCancel.setTypeface(SplashScreen.ProxiNova_Bold);
		btnTerms.setTypeface(SplashScreen.ProxiNova_Bold);
		btnCode.setTypeface(SplashScreen.ProxiNova_Bold);
		btnPrivacy.setTypeface(SplashScreen.ProxiNova_Bold);
		btnTerms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				btnTerms.setBackgroundResource(R.color.Theme_Color);
				btnPrivacy.setBackgroundResource(R.color.btnsearchunsel);
				btnCode.setBackgroundResource(R.color.btnsearchunsel);
				fragment = new Terms_Condition();
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://camrate.com/main/frmAppTOS.php");
				fragment.setArguments(bundle);
				/*
				 * btnTerms.setBackgroundResource(R.drawable.btnterms_sel); btnPrivacy.setBackgroundResource(R.drawable.btnprivacy); btnCode.setBackgroundResource(R.drawable.btncode);
				 */
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			}
		});
		btnPrivacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				btnTerms.setBackgroundResource(R.color.btnsearchunsel);
				btnPrivacy.setBackgroundResource(R.color.Theme_Color);
				btnCode.setBackgroundResource(R.color.btnsearchunsel);
				fragment = new Terms_Condition();
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://camrate.com/main/frmAppPCP.php");
				fragment.setArguments(bundle);
				/*
				 * btnTerms.setBackgroundResource(R.drawable.btnterms); btnPrivacy.setBackgroundResource(R.drawable.btnprivacy_sel); btnCode.setBackgroundResource(R.drawable.btncode);
				 */
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			}
		});

		btnCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnTerms.setBackgroundResource(R.color.btnsearchunsel);
				btnPrivacy.setBackgroundResource(R.color.btnsearchunsel);
				btnCode.setBackgroundResource(R.color.Theme_Color);
				fragment = new Terms_Condition();
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://camrate.com/main/frmAppCOC.php");
				fragment.setArguments(bundle);
				/*
				 * btnTerms.setBackgroundResource(R.drawable.btnterms); btnPrivacy.setBackgroundResource(R.drawable.btnprivacy); btnCode.setBackgroundResource(R.drawable.btncode_sel);
				 */
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			}
		});

		btnAgree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (chkNet.isNetworkConnected()) {
					// String url = "http://camrate.com/main/frmlovehateApi.php?api=Register";
					String url = con.GetBaseUrl() + "api=Register";
					System.out.println("Api==>" + url);
					new RegisterUser().execute(url);
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();
				overridePendingTransition(0, 0);

			}
		});
		path = "file:///mnt/sdcard/CamRate_Images/" + Register.picImage;
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
				Intent intent = new Intent(TermsMain.this, TabSample.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				overridePendingTransition(0, 0);

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private class RegisterUser extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {
			String Result = null;
			String url = params[0];
			ByteArrayOutputStream baos = null;
			if (UserImgPic.length() > 0) {
				String filepath = Environment.getExternalStorageDirectory() + "/CamRate_Images/" + UserImgPic;
				System.out.println("filepath-------" + filepath);
				Bitmap b = BitmapFactory.decodeFile(UserImgPic); // BitmapFactory.decodeResource(UploadActivity.this.getResources(), R.drawable.logo);
				// 12-04 14:47:39.744: I/System.out(22909): PAth===>file:///mnt/sdcard/CamRate_Images//image_20141204_144735.jpg

				baos = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 70, baos);
			}
			try {

				HttpClient client = new HttpClient(url);
				client.connectForMultipart();
				client.addFormPart("User_Name", UserName);
				client.addFormPart("User_Password", UserPass);
				client.addFormPart("User_Email", UserEmail);
				client.addFormPart("countryID", countryID);
				client.addFormPart("cityName", cityName);
				client.addFormPart("gender", Gender_ID);
				if (UserImgPic.length() > 0) {
					client.addFilePart("User_Imagepath", UserImgPic, baos.toByteArray());
				}
				client.finishMultipart();
				String data = client.getResponse();
				Response = data.toString();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			JSONObject jsonobject;
			try {
				jsonobject = new JSONObject(Response);
				Result = jsonobject.getString("result");
				if (Result.equalsIgnoreCase("1")) {
					JSONObject user = jsonobject.getJSONObject("user");
					String UserID = user.getString("User_ID");
					String User_Image = user.getString("User_Imagepath");
					String User_Email = user.getString("User_Email");
					String User_Name = user.getString("User_Name");
					SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
					SharedPreferences.Editor edit1 = prefs.edit();
					edit1.putString("UserID", UserID);
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
			} catch (JSONException e) {
				Log.d("the xceptions ", "Xcep in posting status messages are : " + e.getMessage());
			}

			return Result;
		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {

				GcmRegister();
				SimpleAlert("CamRate", "Register Successfully");

			}
			if (status.equals("0")) {
				// pd.dismiss();
				Toast.makeText(getApplicationContext(), "Registration Unsuccessful! Please try again.", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				// pd.dismiss();
				Toast.makeText(getApplicationContext(), "This email is already registered. Please use different email address.", Toast.LENGTH_SHORT).show();
			}

		}

	}

	public void GcmRegister() {
		GCMRegistrar.checkDevice(TermsMain.this);
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
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
