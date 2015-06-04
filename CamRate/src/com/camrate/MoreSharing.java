package com.camrate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.twitter.AlertDialogManager;
import com.camrate.twitter.ConnectionDetector;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MoreSharing extends Activity {
	TextView txtDelete, txtAddtoFav, txtReportInappropriate, txtCancel;
	/***
	 * Register your here app https://dev.twitter.com/apps/new and get your consumer key and secret
	 */
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

	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	// Internet Connection detector
	private ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	String Post_ID, Post_Tag, Post_Content, shareUser_ID, shareUser_Name, FBBody, strSub, mainUrl, strBody;

	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = new ArrayList<String>();
	private static final List<String> PERMISSIONS2 = Arrays.asList("publish_actions");
	Constant con = new Constant();
	checkInternet chknet;
	JSONParser parser = new JSONParser();
	private Session.StatusCallback statusCallback;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		cd = new ConnectionDetector(getApplicationContext());
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		init();

	}

	public void init() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.camrate", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
		// Check if Internet present
		/*
		 * if (!cd.isConnectingToInternet()) { // Internet Connection is not present alert.showAlertDialog(SocialSharing.this, "Internet Connection Error", "Please connect to working Internet connection", false); // stop executing code by return return; }
		 */

		PERMISSIONS.add("email");
		PERMISSIONS.add("publish_actions");
		// Check if twitter keys are set
		if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
			// Internet Connection is not present
			alert.showAlertDialog(MoreSharing.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
			// stop executing code by return
			return;
		}

		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		this.userId = prefs.getString("UserID", "");
		/**
		 * This if conditions is tested once is redirected from twitter page. Parse the uri to get oAuth Verifier
		 * */
		Intent shareIntent = getIntent();
		Post_ID = shareIntent.getStringExtra("Post_ID");
		shareUser_ID = shareIntent.getStringExtra("User_ID");
		shareUser_Name = shareIntent.getStringExtra("User_Name");
		Post_Tag = shareIntent.getStringExtra("Post_Tags");
		Post_Content = shareIntent.getStringExtra("Post_Content");
		strSub = "CamRate App";
		mainUrl = "http://camrate.com";
		strBody = shareUser_Name + " just Shared a CamRate photo with you.\n\n" + Post_Tag + "\n" + Post_Content + "\n\n" + mainUrl + "\n";
		FBBody = Post_Tag + "\n" + Post_Content + "\n\n" + mainUrl + "\n";
		System.out.println("fbbosy" + FBBody);

		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

					// Hide login button

					// Getting user details from twitter
					// For now i am getting his name only
					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();

					new updateTwitterMedia().execute(FBBody);
					// Displaying in xml ui

				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}

		// txtTwitter = (TextView) findViewById(R.id.txtShareTW);
		txtDelete = (TextView) findViewById(R.id.txtDelete);
		// txtFB = (TextView) findViewById(R.id.txtShareFB);
		// txtEmail = (TextView) findViewById(R.id.txtShareEmail);
		txtAddtoFav = (TextView) findViewById(R.id.txtAddtoFav);
		txtCancel = (TextView) findViewById(R.id.txtCancel);
		txtReportInappropriate = (TextView) findViewById(R.id.txtReportInAppropriate);
		if (isFavorite(userId, Post_ID)) {
			txtAddtoFav.setText("Remove from Favorite");
		} else {
			txtAddtoFav.setText("Add to Favorite");

		}
		System.out.println("isFav==>" + isFavorite(userId, Post_ID));
		System.out.println("isAuthor==>" + isAuthor(shareUser_ID));
		if (isAuthor(shareUser_ID)) {
			txtDelete.setVisibility(View.VISIBLE);
		} else {
			txtDelete.setVisibility(View.GONE);
		}
		// txtTwitter.setOnClickListener(Shareclicklister);
		txtDelete.setOnClickListener(Shareclicklister);
		// txtFB.setOnClickListener(Shareclicklister);
		// txtEmail.setOnClickListener(Shareclicklister);
		txtAddtoFav.setOnClickListener(Shareclicklister);
		txtCancel.setOnClickListener(Shareclicklister);
		txtReportInappropriate.setOnClickListener(Shareclicklister);

		txtAddtoFav.setTypeface(SplashScreen.ProxiNova_Regular, Typeface.BOLD);
		txtCancel.setTypeface(SplashScreen.ProxiNova_Regular, Typeface.BOLD);
		txtDelete.setTypeface(SplashScreen.ProxiNova_Regular, Typeface.BOLD);
		txtReportInappropriate.setTypeface(SplashScreen.ProxiNova_Regular, Typeface.BOLD);
		statusCallback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				/*
				 * if (state.isOpened()) {
				 * 
				 * Log.d("FacebookSampleActivity", "Facebook session opened"); } else if (state.isClosed()) {
				 * 
				 * Log.d("FacebookSampleActivity", "Facebook session closed"); }
				 */
				// onSessionStateChange(session, state, exception);
				requestPermissions();
			}
		};
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
			new updateTwitterMedia().execute(FBBody);
		}
	}

	/**
	 * Function to update status
	 * */
	class updateTwitterMedia extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(SocialSharing.this); pDialog.setMessage("Updating to twitter..."); pDialog.setIndeterminate(false); pDialog.setCancelable(false); pDialog.show();
			 */
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			Log.d("Tweet Text", "> " + args[0]);
			String status = args[0];
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

			// Access Token
			String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
			// Access Token Secret
			String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

			AccessToken accessToken = new AccessToken(access_token, access_token_secret);
			Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
			String dir = "sdcard/Share_Camrate_Images/";
			String name = "shareImg.jpg";
			File file = new File(dir, name);

			try {
				twitter4j.UploadedMedia twits = twitter.uploadMedia(file);
				long mediaID = twits.getMediaId();
				long[] longarr = new long[1];
				longarr[0] = mediaID;
				StatusUpdate su = new StatusUpdate(status);
				su.setMediaIds(longarr);
				twitter.updateStatus(su);

				Log.d("Status", "> " + twits.getMediaId());
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}/*
			 * try { uploadPic(file, "Test", twitter); } catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 */

			// }
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show the data in UI Always use runOnUiThread(new Runnable()) to update UI from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			// pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Pic tweeted successfully", Toast.LENGTH_SHORT).show();
					// Clearing EditText field
					// txtUpdate.setText("");
				}
			});
		}

	}

	/**
	 * Check user already logged in your application using twitter Login flag is fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	protected void onResume() {
		super.onResume();
		// uiHelper.onResume();
		Session.getActiveSession().isOpened();
		System.out.println("socialshare_resumecalled");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

		uiHelper.onSaveInstanceState(outState);

	}

	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains(PERMISSIONS);
		} else {
			return false;
		}

	}

	public void requestPermissions() {
		Session s = Session.getActiveSession();
		// final PostDetail activity = (PostDetail) context;
		// final Activity activity = (Activity) context;
		if (s != null) {
			System.out.println("session here------" + s.isOpened());
			if (s.isOpened()) {
				System.out.println("true to proceed");
				s.requestNewPublishPermissions(new Session.NewPermissionsRequest(MoreSharing.this, PERMISSIONS));

				proceed1(s);
			} else {
				Session.openActiveSession(MoreSharing.this, true, new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if (session.isOpened()) {
							System.out.println("now the session is opened");

							session.requestNewReadPermissions(new Session.NewPermissionsRequest(MoreSharing.this, PERMISSIONS));
							proceed1(session);
						}
					}
				});

			}
		}
	}

	void proceed() {
		System.out.println("now we can proceed");
	}

	private void proceed1(Session sesion) {
		// TODO Auto-generated method stub
		System.out.println("now we can proceed1");
		publishStory();
	}

	/**
	 * Publishes story on the logged user's wall
	 */
	public void publishStory() {
		String dir = "sdcard/Share_Camrate_Images/";
		String name = "shareImg.jpg";
		File file = new File(dir, name);

		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			Bitmap bi = BitmapFactory.decodeStream(fis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("onCreate", "debug error  e = " + e.toString());
		}
		Bundle params = new Bundle();
		// params.putString("name", "test_name");
		// params.putString (Facebook.TOKEN, sesion.getAccessToken());
		params.putString("message", FBBody);
		params.putByteArray("picture", data);
		Request.Callback callback = new Request.Callback() {
			public void onCompleted(Response response) {
				FacebookRequestError error = response.getError();

				if (error != null) {
					Toast.makeText(MoreSharing.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
					System.out.println("Error==>" + error.getErrorMessage());
				} else {
					Toast.makeText(MoreSharing.this, "Posted successful on your wall", Toast.LENGTH_SHORT).show();
					// System.out.println("Post on wall");
				}
			}
		};

		Request request = new Request(Session.getActiveSession(), "me/photos", params, HttpMethod.POST, callback);
		RequestAsyncTask task = new RequestAsyncTask(request);
		task.execute();
	}

	protected void shareWithEmail(String strSub, String strBody) {
		// for file
		String dir = "sdcard/Share_Camrate_Images/";
		String name = "shareImg.jpg";
		File file = new File(dir, name);

		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("image/jpeg");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, strSub);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strBody);
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		startActivity(Intent.createChooser(emailIntent, "Sharing Options"));
	}

	private boolean isAuthor(String strPostAuthor2) {
		if (strPostAuthor2.equals(userId)) {
			return true;
		} else {
			return false;
		}
	}

	protected void deletePost(String postId2) {
		String url = con.GetBaseUrl() + "api=PostDelete&Post_ID=" + postId2;
		System.out.println(url);
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			if (res.equals("0")) {
				SimpleAlert(MoreSharing.this, "Post has not been deleted. Please try again");
			} else {
				SimpleAlert(MoreSharing.this, "Post has been deleted successfully");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void reportInAppropriate(String userId2, String strPostId) {
		String url = con.GetBaseUrl() + "api=PostStatus&User_ID=" + userId2 + "&Post_ID=" + strPostId;
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			if (res.equals("0")) {
				SimpleAlert(MoreSharing.this, "Post has not been reported as inappropriate. Please try again");
			} else {
				SimpleAlert(MoreSharing.this, "Post has been reported as inappropriate successfully");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void SimpleAlert(Context context, String b) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("CamRate");
		builder.setMessage(b);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// setVisible(false);
				finish();
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean isFavorite(String userId, String postId) {
		String url = con.GetBaseUrl() + "api=GetFavouriteList&User_ID=" + userId;
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONArray jsonMyFeed = new JSONArray(myFeedParse);
			for (int i = 0; i < jsonMyFeed.length(); i++) {
				String postIds = jsonMyFeed.get(i).toString();
				if (postIds.equals(postId)) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	protected void deleteFromFavorite(String userId2, String strPostId) {
		String url = con.GetBaseUrl() + "api=RemoveFavourite&User_ID=" + userId2 + "&Post_ID=" + strPostId;
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			// setVisible(false);
			finish();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void addToFavorite(String userId2, String strPostId) {
		String url = con.GetBaseUrl() + "api=AddMyFavouritePost&User_ID=" + userId2 + "&Post_ID=" + strPostId;
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			System.out.println("res==>" + res);
			// setVisible(false);
			finish();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void SimpleAlert(String t) {
		Builder builder = new AlertDialog.Builder(MoreSharing.this);
		builder.setTitle(t);
		builder.setMessage("Are you sure you want to delete this post?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deletePost(Post_ID);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public OnClickListener Shareclicklister = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.txtDelete:
				// deletePost(Post_ID);
				SimpleAlert("CamRate");
				break;
			case R.id.txtAddtoFav:
				System.out.println("Fav==>" + txtAddtoFav.getText().toString());
				if (txtAddtoFav.getText().toString().equals("Add to Favorite")) {
					System.out.println("iffav");
					addToFavorite(userId, Post_ID);
				} else {
					System.out.println("elsefav");
					deleteFromFavorite(userId, Post_ID);
				}

				break;
			case R.id.txtReportInAppropriate:
				reportInAppropriate(userId, Post_ID);
				break;
			case R.id.txtCancel:
				// setVisible(false);
				finish();
				break;
			default:
				break;
			}
		}

	};
}
