package com.camrate.settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.camrate.AfterSplashScreen;
import com.camrate.R;
import com.camrate.ServerUtilities;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.pojo.PojoFacebookFriends;
import com.camrate.tabs.TabGroupActivity;
import com.camrate.tools.ShowSettingContent;
import com.camrate.twitter.TwitterApp;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gcm.GCMRegistrar;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener {

	static ArrayList<PojoFacebookFriends> arrlstFri = new ArrayList<PojoFacebookFriends>();

	private Preference prefInviteFri;
	private Preference prefTermService;
	private Preference prefPolicy;
	private Preference prefCodeOfConduct;
	private Preference prefChangePass;
	private Preference prefFaq;
	private Preference prefConact;
	private Preference prefFeedback;
	private Preference prefSeeVideo;
	private Preference prefLogOut;
	private Preference prefAboutUs;
	private Preference prefRateUs;
	private Preference prefManageBlockList;

	TogglePreference toggle_Facebook, toggle_Twitter, toggle_Tumbler, prefPushNoti, prefPrivacy, prefAutosave;
	private static Facebook fb;
	private TwitterApp mTwitter;

	ToggleButton tglprivacy;
	checkInternet chk;
	SharedPreferences prefs;
	// Fb share
	Facebook facebook;
	AsyncFacebookRunner mAsyncRunner;

	// Twitter share
	// place your cosumer key here
	static String TWITTER_CONSUMER_KEY = "Yi91A6xPiHQfxjV5NzWBw";
	// place your consumer secret here
	static String TWITTER_CONSUMER_SECRET = "NJkHm22eWgJROQmdfaod8zVuKfkEnyYmfcmrJu5k";

	// Progress dialog
	ProgressDialog pDialog;
	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String TWITTER_CALLBACK_URL = "settingshaeroauth://settingsharesample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Tumblr share
	private static final String REQUEST_TOKEN_URL = "https://www.tumblr.com/oauth/request_token";
	private static final String ACCESS_TOKEN_URL = "https://www.tumblr.com/oauth/access_token";
	private static final String AUTH_URL = "https://www.tumblr.com/oauth/authorize";

	// Get these from Tumblr app registration
	private static final String CONSUMER_KEY = "7hdNr0AwrsJEH9t5TMpyTEchoyMf5XVSqvKK5ehQmPY0MypXd5";
	private static final String CONSUMER_SECRET = "5Jg6I1IBSaAennLPi6Ht2uuYtVMvPdqds7rPypwCzSvXeFs0gv";

	// Set the callback url in the manifest. There are a few more things you must include in the manifest, so check it!
	private static final String CALLBACK_URL = "settingtumblrauth://settingtumblrok"; // for this example, it would be "example://ok"
	private static final String FILE_LOCATION = "/CamRate_Images";

	String authUrl;

	CommonsHttpOAuthConsumer consumer;
	CommonsHttpOAuthProvider provider;
	String User_ID, User_Isprivate;
	JSONParser parser = new JSONParser();
	ProgressBar pd;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);

		chk = new checkInternet(SettingsActivity.this);
		addPreferencesFromResource(R.xml.activity_settings);
		setContentView(R.layout.setting_title);

		findViewById(R.id.btnContactsBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		init();
		setClickListener();
		TextView txtmain = (TextView) findViewById(R.id.lblInviteFriTitle);
		txtmain.setText("Settings");
		txtmain.setTypeface(SplashScreen.ProxiNova_Bold);
		pd = (ProgressBar) findViewById(R.id.progressBar1);

	}

	private void setClickListener() {
		prefInviteFri.setOnPreferenceClickListener(this);
		prefTermService.setOnPreferenceClickListener(this);
		prefPolicy.setOnPreferenceClickListener(this);
		prefChangePass.setOnPreferenceClickListener(this);
		prefFaq.setOnPreferenceClickListener(this);
		prefConact.setOnPreferenceClickListener(this);
		prefFeedback.setOnPreferenceClickListener(this);
		prefLogOut.setOnPreferenceClickListener(this);
		prefAboutUs.setOnPreferenceClickListener(this);
		prefSeeVideo.setOnPreferenceClickListener(this);
		prefManageBlockList.setOnPreferenceClickListener(this);
		prefCodeOfConduct.setOnPreferenceClickListener(this);
		prefRateUs.setOnPreferenceClickListener(this);
		prefPrivacy.setOnPreferenceClickListener(this);

	}

	private void init() {
		facebook = new Facebook(getResources().getString(R.string.app_id));
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		prefInviteFri = (Preference) findPreference("prefInviteFriends");
		prefTermService = (Preference) findPreference("prefTofService");
		prefPolicy = (Preference) findPreference("prefPrivacyPolicy");
		prefChangePass = (Preference) findPreference("prefChangePass");
		prefFaq = (Preference) findPreference("prefFAQ");
		prefConact = (Preference) findPreference("prefContactUs");
		prefFeedback = (Preference) findPreference("prefFeedback");
		prefSeeVideo = (Preference) findPreference("prefSeeVideo");
		prefLogOut = (Preference) findPreference("prefLogOut");
		prefAboutUs = (Preference) findPreference("prefAboutUs");
		prefCodeOfConduct = (Preference) findPreference("prefCodeofconduct");
		prefManageBlockList = (Preference) findPreference("prefManageBlockList");
		prefRateUs = (Preference) findPreference("prefRateUS");
		// if (prefs.getBoolean("push_notification", true)) {
		// prefPushNoti.setChecked(true);
		// } else {
		// prefPushNoti.setChecked(false);
		// }
		//
		// if (prefs.getString("User_IsPrivate", "0").equalsIgnoreCase("1")) {
		// prefPrivacy.setChecked(true);
		// } else {
		// prefPrivacy.setChecked(false);
		// }
		User_ID = prefs.getString("UserID", "");
		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					// Shared Preferences
					Editor e = prefs.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.d("Twitter OAuth Token", "> " + accessToken.getToken());

					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();

				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}
		String theToken = prefs.getString("Tumblr_myToken", "");
		String theSecret = prefs.getString("Tumblr_mySecret", "");
		boolean needsQuery = prefs.getBoolean("Tumblr_gettingTokens", false);

		if ((theToken.equals("") || theSecret.equals("")) && needsQuery) {
			Uri uri = this.getIntent().getData();

			String token;
			try {
				token = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
			} catch (NullPointerException e) {
				Log.e("DrawLog", "Registration not complete");
				return;
			}
			final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

			// The consumer object was lost because the browser got into foreground, need to instantiate it again with your apps token and secret.
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			String rToken = prefs.getString("Tumblr_requestToken", "");
			String rSecret = prefs.getString("Tumblr_requestSecret", "");

			// Set the requestToken and the tokenSecret that you got earlier by calling retrieveRequestToken.
			consumer.setTokenWithSecret(rToken, rSecret);

			// The provider object is lost, too, so instantiate it again.
			provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTH_URL);
			// Now that's really important. Because you don't perform the retrieveRequestToken method at this moment, the OAuth method is not detected automatically (there is no communication with Tumblr). So, the default is 1.0 which is wrong because the initial request was performed with 1.0a.
			provider.setOAuth10a(true); // WHAT??!

			try {
				provider.retrieveAccessToken(consumer, verifier);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("Tumblr_myToken", consumer.getToken());
				editor.putString("Tumblr_mySecret", consumer.getTokenSecret());
				editor.putBoolean("Tumblr_gettingTokens", false);
				editor.commit();
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		toggle_Facebook = (TogglePreference) findPreference("toggle_facebookshare");
		toggle_Facebook.setExternalListener(new TogglePreference.ExternalListener() {
			@Override
			public void onPreferenceClick() {
				if (toggle_Facebook.isChecked()) {
					if (chk.isNetworkConnected()) {
						loginToFacebook();
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
					Editor edit1 = prefs.edit();
					edit1.putString("Facebok_Access_Tocken", "");
					edit1.putString("Facebok_Active_Session", "");
					edit1.commit();
				}

			}
		});

		toggle_Twitter = (TogglePreference) findPreference("toggle_TwitShare");
		toggle_Twitter.setExternalListener(new TogglePreference.ExternalListener() {
			@Override
			public void onPreferenceClick() {
				if (toggle_Twitter.isChecked()) {
					if (chk.isNetworkConnected()) {
						loginToTwitter();
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					Editor e = prefs.edit();
					e.putString(PREF_KEY_OAUTH_TOKEN, "");
					e.putString(PREF_KEY_OAUTH_SECRET, "");
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
					e.commit();
				}

			}
		});

		toggle_Tumbler = (TogglePreference) findPreference("toggle_TumbShare");
		toggle_Tumbler.setExternalListener(new TogglePreference.ExternalListener() {
			@Override
			public void onPreferenceClick() {

				if (toggle_Tumbler.isChecked()) {
					String theToken = prefs.getString("Tumblr_myToken", "");
					String theSecret = prefs.getString("Tumblr_mySecret", "");

					if (chk.isNetworkConnected()) {
						if (theToken.equals("") || theSecret.equals("")) {
							loginToTumblr();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					Editor edit1 = prefs.edit();
					edit1.putString("Tumblr_requestSecret", "");
					edit1.putString("Tumblr_requestToken", "");
					edit1.putString("Tumblr_myToken", "");
					edit1.putString("Tumblr_mySecret", "");
					edit1.putBoolean("Tumblr_gettingTokens", false);
					edit1.commit();

				}

			}
		});
		prefPushNoti = (TogglePreference) findPreference("prefPushNotif");
		SharedPreferences.Editor editor = prefs.edit();
		boolean prefpush = prefs.getBoolean("User_Pushnotification", true);

		prefPushNoti.setExternalListener(new TogglePreference.ExternalListener() {
			@Override
			public void onPreferenceClick() {

				SharedPreferences.Editor editor = prefs.edit();

				if (prefPushNoti.isChecked()) {
					editor.putBoolean("User_Pushnotification", prefPushNoti.isChecked());
					editor.commit();
				} else {

					editor.putBoolean("User_Pushnotification", prefPushNoti.isChecked());
					editor.commit();
				}
			}
		});

		prefPrivacy = (TogglePreference) findPreference("prefprivacy");
		prefPrivacy.setExternalListener(new TogglePreference.ExternalListener() {
			@Override
			public void onPreferenceClick() {

				SharedPreferences.Editor editor = prefs.edit();

				if (prefPrivacy.isChecked()) {
					User_Isprivate = "1";
					editor.putString("User_IsPrivate", "1");
					editor.commit();
					if (chk.isNetworkConnected()) {
						new checkPrivacy().execute("");
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					User_Isprivate = "0";
					editor.putString("User_IsPrivate", "0");
					editor.commit();
					if (chk.isNetworkConnected()) {
						new checkPrivacy().execute("");
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		prefAutosave = (TogglePreference) findPreference("prefAutoSave");
		prefAutosave.setExternalListener(new TogglePreference.ExternalListener() {
			@Override
			public void onPreferenceClick() {

				Log.d("TAG", prefAutosave.isChecked() + " toggle_Tumbler");
				SharedPreferences.Editor editor = prefs.edit();

				if (prefAutosave.isChecked()) {
					editor.putBoolean("auto_save", prefAutosave.isChecked());
					editor.commit();
				} else {

					editor.putBoolean("auto_save", prefAutosave.isChecked());
					editor.commit();
				}

			}
		});

		if (prefs.getBoolean("push_notification", true)) {
			prefPushNoti.setChecked(true);
		} else {
			prefPushNoti.setChecked(false);
		}

		if (prefs.getString("User_IsPrivate", "0").equalsIgnoreCase("1")) {
			prefPrivacy.setChecked(true);
		} else {
			prefPrivacy.setChecked(false);
		}

		if (prefs.getBoolean("auto_save", false)) {
			prefAutosave.setChecked(true);
		} else {
			prefAutosave.setChecked(false);
		}

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String selectedItem = preference.getKey();
		if (selectedItem.equals("prefInviteFriends")) {

			Intent intent = new Intent(getParent(), InviteFriendsActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("InviteFriendsActivity", intent);

		} else if (selectedItem.equals("prefTofService")) {
			openWeb("http://camrate.com/main/frmAppTOS.php", "Terms of Service");
		} else if (selectedItem.equals("prefPrivacyPolicy")) {
			openWeb("http://camrate.com/main/frmAppPCP.php", "Privacy Policy");
		} else if (selectedItem.equals("prefCodeofconduct")) {
			openWeb("http://camrate.com/main/frmAppCOC.php", "Code of Conduct");
		} else if (selectedItem.equals("prefChangePass")) {

			Intent intent = new Intent(getParent(), ChangePasswordActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("ChangePasswordActivity", intent);

		} else if (selectedItem.equals("prefFAQ")) {
			openWeb("http://camrate.com/main/FAQdemo.php", "FAQ");
		} else if (selectedItem.equals("prefRateUS")) {
			System.out.println("Rate US==>");
			openWeb("https://play.google.com/store?hl=en", "Rate Us");
		} else if (selectedItem.equals("prefContactUs")) {

			Intent intent = new Intent(getParent(), ContactUsActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("ContactUsActivity", intent);

		} else if (selectedItem.equals("prefFeedback")) {

			Intent intent = new Intent(getParent(), FeedbackQuestionActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("FeedbackQuestionActivity", intent);
		} else if (selectedItem.equals("prefSeeVideo")) {

			Intent intent = new Intent(getParent(), SeeVideoActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("SeeVideoActivity", intent);

		} else if (selectedItem.equals("prefLogOut")) {

			if (chk.isNetworkConnected()) {

				//
				final String regId = GCMRegistrar.getRegistrationId(this);
				ServerUtilities.unregister(SettingsActivity.this, regId, SplashScreen.deviceID, SplashScreen.deviceName, SplashScreen.App_name, SplashScreen.clientid, SplashScreen.app_version);
				//
				clearPrefs();
				Intent intent = new Intent(SettingsActivity.this, AfterSplashScreen.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				SettingsActivity.this.getParent().startActivity(intent);
				overridePendingTransition(0, 0);
				getParent().finish();
			} else {
				Toast.makeText(getParent(), "Check your internet", Toast.LENGTH_SHORT).show();
			}
		} else if (selectedItem.equals("prefAboutUs")) {
			openWeb("http://camrate.com/TestCamRate/Lovehate/aboutus", "About Us");
		} else if (selectedItem.equals("prefManageBlockList")) {

			Intent intent = new Intent(getParent(), BlockListActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("BlockListActivity", intent);

		} else if (selectedItem.equals("prefprivacy")) {

		}
		return false;
	}

	private void clearPrefs() {
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		Editor edit1 = prefs.edit();
		edit1.putString("UserID", "");
		edit1.putBoolean("KeepLoggedIn", Boolean.FALSE);
		edit1.putString("User_Imagepath", "");
		edit1.putString("User_Email", "");
		edit1.putString("User_Name", "");
		edit1.putString("User_FirstName", "");
		edit1.putString("User_LastName", "");
		edit1.putString("User_CountryID", "");
		edit1.putString("User_City", "");
		edit1.putString("User_Gender", "");
		edit1.putString("User_Age", "");
		edit1.putString("User_AgeRange", "");
		edit1.putString("User_Desc", "");
		edit1.putString("User_PushNotification", "");
		edit1.putString("User_Location", "");
		edit1.putString("User_Badge", "");
		edit1.putString("User_Status", "");
		edit1.putString("User_IsPrivate", "");
		edit1.putString("User_IsActive", "");
		edit1.putString("User_LastSession", "");
		edit1.putString("User_Date", "");
		edit1.putBoolean("camera_flash", false);
		edit1.putBoolean("geo_Tag", false);
		edit1.putBoolean("auto_save", false);
		edit1.putString("Facebok_Access_Tocken", "");
		edit1.putString("Facebok_Active_Session", "");
		edit1.putString("Tumblr_requestSecret", "");
		edit1.putString("Tumblr_requestToken", "");
		edit1.putString("Tumblr_myToken", "");
		edit1.putString("Tumblr_mySecret", "");
		edit1.putBoolean("Tumblr_gettingTokens", false);
		edit1.putString(PREF_KEY_OAUTH_TOKEN, "");
		edit1.putString(PREF_KEY_OAUTH_SECRET, "");

		edit1.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
		edit1.putBoolean("User_Pushnotification", false);
		edit1.clear();
		edit1.commit();
		Log.d("UserID", "====" + prefs.getString("UserID", ""));

	}

	// private void startActivity(String string) {
	// Intent intent = new Intent("com.camrate.settings." + string);
	// startActivity(intent);
	// }

	private void openWeb(String url, String title) {
		Intent intent = new Intent(getParent(), ShowSettingContent.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		TabGroupActivity parentActivity = (TabGroupActivity) getParent();
		parentActivity.startChildActivity("ShowSettingContent", intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// @Override
	// public boolean onPreferenceChange(Preference preference, Object newValue) {
	// System.out.println("prefchange");
	// if (preference.getKey().equals("prefFacebookSharing")) {
	//
	// } else if (preference.getKey().equals("prefTwitShare")) {
	//
	// } else if (preference.getKey().equals("prefprivacy")) {
	//
	// }
	// return true;
	// }

	private void postFacebookMessage() {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(fb);
		Bundle bundle = new Bundle();
		bundle.putString("fields", "id, name, picture");
		runner.request("me/friends", bundle, new FriendsRequestListener());
	}

	public static void sendRequest(Context con, String ids) {
		Bundle bundle = new Bundle();
		bundle.putString("id", ids);
		fb.dialog(con, "friends", bundle, new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(DialogError e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}
		});
	}

	private class FriendsRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			saveJSONToList(response);
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
		public void onMalformedURLException(MalformedURLException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

	}

	public void saveJSONToList(String response) {
		try {

			JSONObject jsonObj = new JSONObject(response);
			JSONArray arrJSON = jsonObj.getJSONArray("data");
			for (int i = 0; i < arrJSON.length(); i++) {
				JSONObject obj = arrJSON.getJSONObject(i);
				String id = obj.getString("id");
				String name = obj.getString("name");

				JSONObject objSecond = obj.getJSONObject("picture");
				JSONObject objThird = objSecond.getJSONObject("data");
				String url = objThird.getString("url");

				PojoFacebookFriends pojo = new PojoFacebookFriends();
				pojo.setId(id);
				pojo.setName(name);
				pojo.setUrl(url);

				arrlstFri.add(pojo);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<PojoFacebookFriends> getPojoList() {
		return arrlstFri;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}

	public void loginToFacebook() {
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		String access_token = prefs.getString("Facebook_Access_Tocken", null);
		long expires = prefs.getLong("Facebook_Access_expire", 0);
		Log.d("pref_tocken", "---->" + access_token);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "publish_actions" }, new DialogListener() {

				@Override
				public void onCancel() {
					// Function to handle cancel event
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					Log.d("tttoocken", "---->" + facebook.getAccessToken());
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("Facebook_Access_Tocken", facebook.getAccessToken());
					editor.putLong("Facebook_Access_expire", facebook.getAccessExpires());
					editor.commit();
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

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return prefs.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			// Toast.makeText(getApplicationContext(),
			// "Already Logged into twitter", Toast.LENGTH_LONG).show();
			System.out.println("twitter user log in alredy");

		}
	}

	public void loginToTumblr() {
		consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

		provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTH_URL);
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URL);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("Tumblr_requestToken", consumer.getToken());
					editor.putString("Tumblr_requestSecret", consumer.getTokenSecret());
					editor.putBoolean("Tumblr_gettingTokens", true);
					Log.e("Token", "-->" + consumer.getToken());
					Log.e("requestedSecret", "--->" + consumer.getTokenSecret());
					editor.commit();
					startActivity(new Intent("android.intent.action.VIEW", Uri.parse(authUrl)));

				} catch (OAuthMessageSignerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OAuthNotAuthorizedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OAuthExpectationFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OAuthCommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();

	}

	class checkPrivacy extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			// pd = ProgressDialog.show(Login.this, "","Validating User",true);
			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=ChangeUserPrivacy&User_ID=" + User_ID + "&Privacy_Value=" + User_Isprivate;
			System.out.println(url);
			// cc.GetUrl(url);
			JSONObject json = parser.getJSONFromUrl(url.toString());
			System.out.println(json);
			try {
				Result1 = json.getString("result");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return Result1;
		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

}
