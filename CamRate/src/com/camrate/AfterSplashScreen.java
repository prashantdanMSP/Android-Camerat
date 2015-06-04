package com.camrate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tabs.TabSample;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.Request.GraphUserCallback;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AfterSplashScreen extends Activity {

	Button btnfb, btn_SignIN, btn_Register;
	JSONObject profile;
	Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	String fbID, Name, Email;
	SharedPreferences prefs;
	ProgressDialog pd;
	JSONParser jparser = new JSONParser();
	private Session.StatusCallback sessionStatusCallback;
	private Session currentSession;
	UiLifecycleHelper uiHelper;
	checkInternet chkNet;
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
		////This isc lkvldkl;dkl;kvl;kvl;sdfk,;',f,;,f;,
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.prelogin);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		uiHelper = new UiLifecycleHelper(this, sessionStatusCallback);
		uiHelper.onCreate(savedInstanceState);
		context = AfterSplashScreen.this;
        //test by prashantdan
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		btn_SignIN = (Button) findViewById(R.id.btn_SignIN);
		btn_Register = (Button) findViewById(R.id.btn_Register);
		btnfb = (Button) findViewById(R.id.btn_SignInFb);
		facebook = new Facebook(getResources().getString(R.string.app_id));
		chkNet = new checkInternet(AfterSplashScreen.this);
		pd1 = (ProgressBar) findViewById(R.id.progressBar1);
		btn_SignIN.setTypeface(SplashScreen.ProxiNova_Bold);
		btn_Register.setTypeface(SplashScreen.ProxiNova_Bold);
		btn_SignIN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(AfterSplashScreen.this, Login.class);
				startActivity(intent);
				overridePendingTransition(0, 0);

			}
		});

		btn_Register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(AfterSplashScreen.this, Register.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
		});
		btnfb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connectToFB();
			}
		});

		sessionStatusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception);

			}
		};
	}

	public void GcmRegister() {
		GCMRegistrar.checkDevice(AfterSplashScreen.this);
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

	// Get Facebook profile information who is loggedIn

	public void getProfileInformation() {
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				Log.d("Profile", response);

				String json = response;

				try {
					// Facebook Profile JSON data
					profile = new JSONObject(json);

					// getting id of the user
					fbID = profile.getString("id");

					// getting name of the user
					Name = profile.getString("first_name");

					// getting LastName of the user
					Email = profile.getString("email");
					Log.d("Tag ", "Email " + Email);
					prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

					SharedPreferences.Editor edit1 = prefs.edit();
					edit1.putBoolean("isfirsttime", Boolean.TRUE);
					edit1.putString("Email", Email);
					edit1.putString("Name", Name);
					edit1.putString("fbID", fbID);
					edit1.commit();

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (chkNet.isNetworkConnected()) {

								new checkLogin().execute("");
								// Intent intent=new Intent(BeforeLogin.this,EpForumHome.class);
								// startActivity(intent);

							} else {
								Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
							}
							// Toast.makeText(getApplicationContext(), "Name: " + Name + "\nEmail: " +Email, Toast.LENGTH_LONG).show();

						}

					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}

		});

	}

	public void loginToFacebook() {
		prefs = getPreferences(MODE_PRIVATE);
		String access_token = prefs.getString("access_token", null);
		long expires = prefs.getLong("access_expires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "email", "publish_stream" }, new DialogListener() {

				@Override
				public void onCancel() {
					// Function to handle cancel event
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires", facebook.getAccessExpires());
					editor.commit();
					getProfileInformation();
				}

				@Override
				public void onError(DialogError error) {
					// Function to handle error

				}

				@Override
				public void onFacebookError(FacebookError fberror) {
					// Function to handle Facebook errors

				}

			});
		}
	}

	/**
	 * Connects the user to facebook
	 */
	public void connectToFB() {

		List<String> permissions = new ArrayList<String>();
		permissions.add("email");
		permissions.add("publish_actions");

		currentSession = new Session.Builder(this).build();
		currentSession.addCallback(sessionStatusCallback);

		Session.OpenRequest openRequest = new Session.OpenRequest(AfterSplashScreen.this);
		openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
		openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
		openRequest.setPermissions(permissions);
		currentSession.openForPublish(openRequest);

	}

	/**
	 * this method is used by the facebook API
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (currentSession != null) {
			currentSession.onActivityResult(this, requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Session.getActiveSession().isOpened();
		System.out.println("Login Resume");
	}

	public void parse_setUserData(JSONObject json) {
		try {

			// getting id of the user
			fbID = json.getString("id");

			// getting name of the user
			Name = json.getString("first_name");

			// getting LastName of the user
			Email = json.getString("email");
			Log.d("Tag ", "Email " + Email);
			new checkLogin().execute("");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session != currentSession) {
			return;
		}

		if (state.isOpened()) {
			// Log in just happened.
			// Toast.makeText(getApplicationContext(), "session opened", Toast.LENGTH_SHORT).show();
			Request.executeMeRequestAsync(session, new GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					// TODO Auto-generated method stub
					Log.d("Response", "val===>" + response);
					Log.d("User", "val===>" + user.getInnerJSONObject());
					parse_setUserData(user.getInnerJSONObject());

				}
			});
		} else if (state.isClosed()) {
			// Log out just happened. Update the UI.
			// Toast.makeText(getApplicationContext(), "session closed", Toast.LENGTH_SHORT).show();
		}
	}

	class checkLogin extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			// pd = ProgressDialog.show(Login.this, "","Validating User",true);
			pd1.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=CheckLoginFBandroid&User_Name=" + Name + "&User_Email=" + Email + "&FBUserID=" + fbID + "";
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd1.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				GcmRegister();
				Intent intent = new Intent(AfterSplashScreen.this, TabSample.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isFor", "newest");
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
			if (status.equals("0")) {
				Toast.makeText(getApplicationContext(), "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

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
