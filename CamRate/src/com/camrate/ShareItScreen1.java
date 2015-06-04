package com.camrate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.TwitterReceiver.updateTwitterMedia;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.GPSTracker;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.share.ListViewActivity1;
import com.camrate.tabs.TabSample;
import com.camrate.tools.ImageDownloaderOnly.OnTaskCompleted;
import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tumblr.jumblr.JumblrClient;

public class ShareItScreen1 extends Activity {

	Button btnBack, btnShare;
	ImageView imgSharePost, imgisVideo, imgsharerate;
	Button btnPost;
	TextView tvTitle, tvGeoTag, tvAutoSave;
	EditText edtTag, edtDesc;
	ToggleButton fbSwitch, twSwitch, tumblrSwitch, btnGeoTag, btnAutoSaveToPhone;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	ArrayList<HashMap<String, String>> PostCategoryData;
	String ParseCategory;
	Constant con = new Constant();
	JSONParser parser = new JSONParser();
	Spinner spCategory;
	private SimpleAdapter categoryAdapter;
	Function fun;
	String catID = "", User_ID, Address;
	int CatID, isfb = 0, istw = 0, istumblr;
	GPSTracker gps;
	int lat, longt;
	SharedPreferences prefs;
	double latitude, longitude;
	LinearLayout linearFB, linearTumblr, LinearTW;
	RelativeLayout relativeEmail;
	boolean isshareclick = false;
	Bitmap postImage;
	String path = "";
	ProgressBar pd;
	String Response;
	checkInternet chkInternet;
	RelativeLayout.LayoutParams paramImage;
	String isVideo = "";
	int rate = 0;
	String emailArrayList = "[]";
	Bitmap sharepostimage = null;
	static String ImageName;
	private OnTaskCompleted listener;
	public final static int REQUEST_CAMERA = 2;
	public static String IMAGE_DIRECTORY_NAME = "CamRate_Images";
	public static File mediaFile;;
	public static Uri fileuri;
	checkInternet chknet;
	private String userId;
	private Session currentSession;

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
	static final String TWITTER_CALLBACK_URL = "shaeroauth1://sharesample1";

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
	private static final String CALLBACK_URL = "tumblrauth1://tumblrok1"; // for this example, it would be "example://ok"
	private static final String FILE_LOCATION = "/CamRate_Images";

	String authUrl;

	CommonsHttpOAuthConsumer consumer;
	CommonsHttpOAuthProvider provider;

	private String item;
	Dialog dialog;
	LinearLayout autosave;
	Constant c;
	HashMap<String, Object> mySelectedFeedDetail;
	public static Activity classAinstance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shareit);

		Log.e("onCreate", "onCreate");

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();

	}

	public void init() {

		if (ShareItScreen1.classAinstance == null) {

			ShareItScreen1.classAinstance = this;

		} else {

			ShareItScreen1.classAinstance.finish();
		}

		classAinstance = this;
		initProcessDialog();
		c = new Constant();
		mySelectedFeedDetail = (HashMap<String, Object>) getIntent().getSerializableExtra("PostData");
		facebook = new Facebook(getResources().getString(R.string.app_id));
		mAsyncRunner = new AsyncFacebookRunner(facebook);
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
		PostCategoryData = new ArrayList<HashMap<String, String>>();
		btnBack = (Button) findViewById(R.id.button2);
		tvTitle = (TextView) findViewById(R.id.tvRateName);
		btnPost = (Button) findViewById(R.id.button1);
		btnGeoTag = (ToggleButton) findViewById(R.id.btnGeoTag);
		btnAutoSaveToPhone = (ToggleButton) findViewById(R.id.btnSaveToGallery);
		tvGeoTag = (TextView) findViewById(R.id.txtlblGeoTag);
		tvAutoSave = (TextView) findViewById(R.id.txtlblAutoSave);
		imgSharePost = (ImageView) findViewById(R.id.imgsharePost);
		imgsharerate = (ImageView) findViewById(R.id.imagerate);
		edtTag = (EditText) findViewById(R.id.edtTag);
		edtDesc = (EditText) findViewById(R.id.edtDesc);
		imageLoader.init(ImageLoaderConfiguration.createDefault(ShareItScreen1.this));
		spCategory = (Spinner) findViewById(R.id.spCategory);
		btnShare = (Button) findViewById(R.id.linearShare);
		linearFB = (LinearLayout) findViewById(R.id.linearfb);
		linearTumblr = (LinearLayout) findViewById(R.id.lineartumblr);
		LinearTW = (LinearLayout) findViewById(R.id.lineartwitter);
		relativeEmail = (RelativeLayout) findViewById(R.id.linearemail);
		fbSwitch = (ToggleButton) findViewById(R.id.fbswitch);
		twSwitch = (ToggleButton) findViewById(R.id.twitterswitch);
		tumblrSwitch = (ToggleButton) findViewById(R.id.tumblrswitch);
		imgisVideo = (ImageView) findViewById(R.id.imguserpostvideo);
		autosave = (LinearLayout) findViewById(R.id.linearAutoShavePhoneGallery);
		Display display = getWindowManager().getDefaultDisplay();
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		fun = new Function(ShareItScreen1.this);
		chkInternet = new checkInternet(ShareItScreen1.this);
		tvTitle.setText("Rate Info");
		autosave.setVisibility(View.GONE);
		int width = display.getWidth();
		int height = display.getHeight();
		int round = (width - 20) / 3;
		tvTitle.setTypeface(SplashScreen.ProxiNova_Bold);
		paramImage = new RelativeLayout.LayoutParams((width) / 4, (width) / 4);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.imageloading).showImageForEmptyUri(R.drawable.imageloading).showImageOnFail(R.drawable.imageloading).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

		ImageName = timeStamp + ".jpg";

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editPrefs = prefs.edit();
				editPrefs.putString("temptag", "");
				editPrefs.putString("tempdesc", "");
				editPrefs.commit();
				finish();
				// sharepostimage.recycle();

			}
		});
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "");
		Intent intent = getIntent();

		isVideo = intent.getStringExtra("image_video");
		rate = intent.getIntExtra("rate", 3);
		Editor editPrefs = prefs.edit();
		editPrefs.putString("image_video", isVideo);
		editPrefs.putInt("rate", rate);
		// editPrefs.putString("temptag", edtTag.getText().toString());
		// editPrefs.putString("tempdesc", edtDesc.getText().toString());
		editPrefs.commit();
		setRating(rate);

		imgSharePost.setLayoutParams(paramImage);
		edtTag.setTypeface(SplashScreen.ProxiNova_Regular);
		edtDesc.setTypeface(SplashScreen.ProxiNova_Regular);

		edtTag.setText(prefs.getString("temptag", ""));
		edtDesc.setText(prefs.getString("tempdesc", ""));

		tvGeoTag.setTypeface(SplashScreen.ProxiNova_Regular);
		tvAutoSave.setTypeface(SplashScreen.ProxiNova_Regular);
		btnPost.setTypeface(SplashScreen.ProxiNova_Bold);

		if (prefs.getBoolean("geo_Tag", true)) {
			btnGeoTag.setChecked(true);
		} else {
			btnGeoTag.setChecked(false);
		}

		if (prefs.getBoolean("auto_save", true)) {
			btnAutoSaveToPhone.setChecked(true);
		} else {
			btnAutoSaveToPhone.setChecked(false);
		}
		btnGeoTag.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor editor = prefs.edit();

				if (isChecked) {
					editor.putBoolean("geo_Tag", isChecked);
					editor.commit();
				} else {

					editor.putBoolean("geo_Tag", isChecked);
					editor.commit();
				}
			}
		});
		btnAutoSaveToPhone.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor editor = prefs.edit();

				if (isChecked) {
					editor.putBoolean("auto_save", isChecked);
					editor.commit();
				} else {

					editor.putBoolean("auto_save", isChecked);
					editor.commit();
				}
			}
		});

		btnPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (edtTag.getText().toString().length() == 0) {
					fun.SimpleAlert("CamRate", "Please enter Title");
				}
				if (edtDesc.getText().toString().length() == 0) {
					fun.SimpleAlert("CamRate", "Please enter Description");
				} else {

					try {

						reshare();
						// createMap();

					} catch (Exception e) {

					}

				}

			}
		});

		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!isshareclick) {
					btnShare.setBackgroundResource(R.drawable.sharebutton_sel);
					linearFB.setVisibility(View.VISIBLE);
					LinearTW.setVisibility(View.VISIBLE);
					linearTumblr.setVisibility(View.VISIBLE);
					relativeEmail.setVisibility(View.VISIBLE);
					isshareclick = true;
				} else {
					btnShare.setBackgroundResource(R.drawable.sharebutton);
					linearFB.setVisibility(View.GONE);
					LinearTW.setVisibility(View.GONE);
					linearTumblr.setVisibility(View.GONE);
					relativeEmail.setVisibility(View.GONE);
					isshareclick = false;
				}
			}
		});
		fbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					isfb = 1;
					Editor edit1 = prefs.edit();
					edit1.putBoolean("isfbon", true);
					edit1.commit();
					if (chkInternet.isNetworkConnected()) {
						loginToFacebook();
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				} else {
					isfb = 0;
					Editor edit1 = prefs.edit();
					edit1.putString("Facebok_Access_Tocken", "");
					edit1.putString("Facebok_Active_Session", "");
					edit1.putBoolean("isfbon", false);
					edit1.commit();
				}
			}
		});

		tumblrSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					istumblr = 1;
					String theToken = prefs.getString("Tumblr_myToken", "");
					String theSecret = prefs.getString("Tumblr_mySecret", "");
					Editor edit1 = prefs.edit();
					edit1.putBoolean("isTumblron", true);
					edit1.commit();
					if (chkInternet.isNetworkConnected()) {
						if (theToken.equals("") || theSecret.equals("")) {
							loginToTumblr();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					istumblr = 0;
					Editor edit1 = prefs.edit();
					edit1.putString("Tumblr_requestSecret", "");
					edit1.putString("Tumblr_requestToken", "");
					edit1.putString("Tumblr_myToken", "");
					edit1.putString("Tumblr_mySecret", "");
					edit1.putBoolean("isTumblron", false);
					edit1.putBoolean("Tumblr_gettingTokens", false);
					edit1.commit();

				}
			}
		});
		twSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					istw = 1;
					Editor edit1 = prefs.edit();
					edit1.putBoolean("isTwitteron", true);
					edit1.commit();
					if (chkInternet.isNetworkConnected()) {
						loginToTwitter();
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					istw = 0;
					Editor e = prefs.edit();
					e.putString(PREF_KEY_OAUTH_TOKEN, "");
					e.putString(PREF_KEY_OAUTH_SECRET, "");
					// Store login status - true
					e.putBoolean("isTwitteron", false);
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
					e.commit();
				}
			}
		});
		relativeEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShareItScreen1.this, ListViewActivity1.class);
				startActivityForResult(intent, 1);
			}
		});
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
			istumblr = 1;
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
		checkAndSetToggleButton();
		mySelectedFeedDetail = RateItScreen1.mySelectedFeedDetail;
		isVideo = prefs.getString("image_video", "0");
		// editPrefs.putString("temptag", edtTag.getText().toString());
		// editPrefs.putString("tempdesc", edtDesc.getText().toString());
		edtTag.setText(prefs.getString("temptag", ""));
		edtDesc.setText(prefs.getString("tempdesc", ""));
		if (isVideo.equalsIgnoreCase("0")) {
			twSwitch.setEnabled(true);
			try {

				// path = "file:///mnt/sdcard/CamRate_Images/" + ImageName;
				System.out.println("PAth===>" + path);
				sharepostimage = RateItScreen1.bmp;
				saveToInternalSorage(sharepostimage, ImageName, 0);
				// imageLoader.displayImage(path, imgSharePost, options);
				imgSharePost.setImageBitmap(sharepostimage);
			} catch (Exception e) {

			}

			// imgSharePost.invalidate();
		} else {
			twSwitch.setEnabled(false);
			try {
				sharepostimage = RateItScreen1.bmp;
				saveToInternalSorage(sharepostimage, ImageName, 0);
				imgSharePost.setImageBitmap(sharepostimage);
				imgisVideo.setVisibility(View.VISIBLE);
			} catch (Exception e) {

			}
		}

		spCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				catID = PostCategoryData.get(position).get("Category_ID");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		if (chkInternet.isNetworkConnected()) {
			new Loading().execute();
		} else {
			latitude = 0;
			longitude = 0;
			Address = "";
		}

	}

	private Bitmap rotateBitmap(Bitmap srcBitmap, int angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		srcBitmap.recycle();
		return rotatedBitmap;
	}

	public class Loading extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {

			downloadAndSetCategoryItem();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissDialog();

			categoryAdapter = new SimpleAdapter(ShareItScreen1.this, PostCategoryData, R.layout.sp_item, new String[] { "Category_Name", "Category_ID" }, new int[] { R.id.text1, R.id.text2 });
			spCategory.setAdapter(categoryAdapter);
			try {
				gps = new GPSTracker(ShareItScreen1.this);
				if (gps.canGetLocation()) {
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
					String city = gps.getLocality(ShareItScreen1.this);
					String country = gps.getCountryName(ShareItScreen1.this);
					if (city != null || country != null) {
						if (city.length() > 0 && country.length() > 0) {
							Address = city + "," + country;
						}
					} else {
						Address = "";
					}
				} else {
					if (prefs.getBoolean("isGpsDialog|_Visible", true)) {
						gps.showSettingsAlert();
					} else {
						Address = "";
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private void downloadAndSetCategoryItem() {

		String url = con.GetBaseUrl() + "api=GetCategory";

		// Log.d("Tag","url "+url);
		try {
			ParseCategory = parser.getStringFromUrl(url);
			HashMap<String, String> tempmap = new HashMap<String, String>();
			tempmap.put("Category_ID", "0");
			tempmap.put("Category_Name", "Select Category (Optional)");

			PostCategoryData.add(tempmap);
			JSONArray jsonMyFeed = new JSONArray(ParseCategory);
			for (int s = 0; s < jsonMyFeed.length(); s++) {
				JSONObject objComment = jsonMyFeed.getJSONObject(s);
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("Category_ID", objComment.getString("Category_ID"));
				map1.put("Category_Name", objComment.getString("Category_Name"));

				PostCategoryData.add(map1);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setRating(int ratcount) {
		// int ratcount = Integer.valueOf(valrate);
		switch (ratcount) {
		case 5:
			// holder.txtRatelbl.setText("LOVE");
			imgsharerate.setImageResource(R.drawable.fivestar_rate);
			break;
		case 4:
			// holder.txtRatelbl.setText("LIKE");
			imgsharerate.setImageResource(R.drawable.fourstar_rate);
			break;
		case 3:
			// holder.txtRatelbl.setText("WHATEVER");
			imgsharerate.setImageResource(R.drawable.threestar_rate);
			break;
		case 2:
			// holder.txtRatelbl.setText("DISLIKE");
			imgsharerate.setImageResource(R.drawable.twostar_rate);
			break;
		case 1:
			// holder.txtRatelbl.setText("HATE");
			imgsharerate.setImageResource(R.drawable.onestar_rate);
			break;
		}
	}

	public void createMap() {
		if (catID.length() > 0) {
			CatID = Integer.parseInt(catID);
		}
		HashMap<String, Object> objpost = new HashMap<String, Object>();
		objpost.put("Post_twitter", istw);
		objpost.put("Post_tumblr", istumblr);
		objpost.put("Post_facebook", isfb);
		if (isVideo.equalsIgnoreCase("1")) {
			objpost.put("Post_videoname", SecondCameraTab.VideoName);
			objpost.put("Post_imagename", "1");
		} else {
			objpost.put("Post_videoname", "1");
			objpost.put("Post_imagename", RateItScreen.ImageName);
		}
		objpost.put("Post_location", Address);
		objpost.put("Post_userid", Integer.parseInt(User_ID));
		objpost.put("Post_emailList", "text");
		objpost.put("Post_latitude", String.valueOf(latitude));
		objpost.put("Post_longitude", String.valueOf(longitude));
		objpost.put("Post_ratingRate", String.valueOf(rate));
		objpost.put("Post_title", edtTag.getText().toString());
		objpost.put("Post_description", edtDesc.getText().toString());
		objpost.put("Post_categoryid", CatID);
		objpost.put("isUploaded", "0");
		objpost.put("Post_emailList", emailArrayList);
		// System.out.println("HashMap==>" + objpost);
		DataBaseAdapter mDbHelper = new DataBaseAdapter(getApplicationContext());
		try {
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		mDbHelper.insertValuesInDatabase(objpost, "tblPost");
	}

	public void reshare() {

		String json = "";
		String url = c.GetBaseUrl() + "api=ReSharePost";

		String Post_ID = mySelectedFeedDetail.get("Post_ID").toString();
		String postTitle = edtTag.getText().toString();
		String postDesc = edtDesc.getText().toString();
		String postLoc = Address;
		String postlat = String.valueOf(latitude);
		String postlong = String.valueOf(longitude);
		String postTags = edtTag.getText().toString();
		String ratingrate = String.valueOf(rate);
		String emailList = emailArrayList;
		String Post_CategoryID = mySelectedFeedDetail.get("Post_CategoryID").toString();

		String Post_FacebookFlag = Integer.toString(isfb);
		String Post_FB_accessToken;
		if (isfb != 0) {
			Post_FB_accessToken = prefs.getString("Facebook_Access_Tocken", "");
		} else {
			Post_FB_accessToken = "0";
		}

		String Post_TwitterFlag = Integer.toString(istw);
		if (istw != 0) {

		} else {

		}

		String Post_TumblerFlag = Integer.toString(istumblr);

		String Post_Tumbler_consumer_key;
		String Post_Tumbler_consumer_secret;
		String Post_Tumbler_access_token;
		String Post_Tumbler_access_secret;
		String Post_Tumbler_Username;

		if (istumblr != 0) {
			Post_Tumbler_consumer_key = CONSUMER_KEY;
			Post_Tumbler_consumer_secret = CONSUMER_SECRET;
			Post_Tumbler_access_token = prefs.getString("Tumblr_myToken", "");
			Post_Tumbler_access_secret = prefs.getString("Tumblr_mySecret", "");

			final JumblrClient client = new JumblrClient(CONSUMER_KEY, CONSUMER_SECRET);
			client.setToken(Post_Tumbler_access_token, Post_Tumbler_access_secret);
			final String blog = client.user().getBlogs().get(0).getName();
			Post_Tumbler_Username = blog;
		} else {
			Post_Tumbler_consumer_key = "0";
			Post_Tumbler_consumer_secret = "0";
			Post_Tumbler_access_token = "0";
			Post_Tumbler_access_secret = "0";
			Post_Tumbler_Username = "0";
		}

		try {

			InputStream is = null;

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			entity.addPart("User_ID", new StringBody(User_ID));
			entity.addPart("Post_ID", new StringBody(Post_ID));
			entity.addPart("Post_Title", new StringBody(postTitle));
			entity.addPart("Post_Content", new StringBody(postDesc));
			entity.addPart("Post_Location", new StringBody(postLoc));
			entity.addPart("Post_Latitude", new StringBody(postlat));
			entity.addPart("Post_Longitude", new StringBody(postlong));
			entity.addPart("Post_Tags", new StringBody(postTags));
			entity.addPart("Rating_Rate", new StringBody(ratingrate));
			entity.addPart("Email_List", new StringBody(emailList));
			entity.addPart("Post_CategoryID", new StringBody(Post_CategoryID));

			entity.addPart("Post_FacebookFlag", new StringBody(Post_FacebookFlag));
			entity.addPart("Post_FB_accessToken", new StringBody(Post_FB_accessToken));

			entity.addPart("Post_TwitterFlag", new StringBody(emailList));

			entity.addPart("Post_Tumbler_consumer_key", new StringBody(Post_Tumbler_consumer_key));
			entity.addPart("Post_Tumbler_consumer_secret", new StringBody(Post_Tumbler_consumer_secret));
			entity.addPart("Post_TumblerFlag", new StringBody(Post_TumblerFlag));
			entity.addPart("Post_Tumbler_access_token", new StringBody(Post_Tumbler_access_token));
			entity.addPart("Post_Tumbler_access_secret", new StringBody(Post_Tumbler_access_secret));
			entity.addPart("Post_Tumbler_Username", new StringBody(Post_Tumbler_Username));

			httpPost.setEntity(entity);

			// new
			HttpParams httpParameters = httpPost.getParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 10000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			// new
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				// Log.d("TAG", "IN BACKGROUND" + json);
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (istw != 0) {
			new updateTwitterMedia().execute(postTags, ImageName);
		} else {
			Intent intent = new Intent(ShareItScreen1.this, TabSample.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isFor", "myfeed");
			startActivity(intent);
			overridePendingTransition(0, 0);
			finish();
		}

	}

	public static byte[] convertFileToByteArray(File f) {
		byte[] byteArray = null;
		try {
			InputStream inputStream = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024 * 8];
			int bytesRead = 0;

			while ((bytesRead = inputStream.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}

			byteArray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.e("onActivityResult", "onActivityResult");

		facebook.authorizeCallback(requestCode, resultCode, data);
		if (resultCode == 1) {

			if (data != null) {

				if (!data.getStringExtra("emaillist").toString().equalsIgnoreCase("")) {

					emailArrayList = data.getStringExtra("emaillist").toString();

				}

			}

		}

	}

	private void saveToInternalSorage(Bitmap result, String string, int type) {

		if (type == 0) {
			String dir = Environment.getExternalStorageDirectory().toString() + "/CamRate_Images";
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}

			File filenew = new File(dir, string);
			try {
				if (filenew.createNewFile()) {
					try {
						filenew.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(filenew);
				result.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String dir = Environment.getExternalStorageDirectory().toString() + "/CamRate_Video";
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}

			File filenew = new File(dir, string);
			try {
				if (filenew.createNewFile()) {
					try {
						filenew.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(filenew);
				result.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	protected void onResume() {
		super.onResume();
		// uiHelper.onResume();

		Log.e("onResume", "onResume");

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("onPause", "onPause");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("onDestroy", "onDestroy");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("onStop", "onStop");
		// finish();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e("onRestart", "onRestart");
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

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {

		Editor editPrefs = prefs.edit();
		editPrefs.putString("temptag", edtTag.getText().toString());
		editPrefs.putString("tempdesc", edtDesc.getText().toString());
		editPrefs.commit();

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
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(requestToken.getAuthenticationURL()));
				startActivityForResult(i, 100);
				startActivity(i);
				finish();

				// ShareItScreen1.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
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

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return prefs.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	public boolean isTumblrInAlready() {
		istumblr = 1;
		return prefs.getBoolean("Tumblr_gettingTokens", false);
	}

	public void checkAndSetToggleButton() {
		// set fb switch
		if (isfb == 0) {
			fbSwitch.setChecked(false);
		} else {
			fbSwitch.setChecked(true);
		}

		// set twitter switch
		if (istw == 0) {
			twSwitch.setChecked(false);
		} else {
			twSwitch.setChecked(true);
		}

		// set tumblr switch
		if (istumblr == 0) {
			tumblrSwitch.setChecked(false);
		} else {
			tumblrSwitch.setChecked(true);
		}
	}

	public void checkTotalSocialSharingCountEnable() {
		if (isfb == 1) {
			System.out.println("Fb sharing on");
		}
		if (istw == 1) {
			System.out.println("Twitter sharing is on");
		}
		if (istumblr == 1) {
			System.out.println("Tumblr sharing is on");
		}
	}

	public void loginToTumblr() {

		Editor editPrefs = prefs.edit();
		editPrefs.putString("temptag", edtTag.getText().toString());
		editPrefs.putString("tempdesc", edtDesc.getText().toString());
		editPrefs.commit();

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

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(ShareItScreen1.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(ShareItScreen1.this, android.R.style.Theme_Light_NoTitleBar);
		}

		dialog = new Dialog(themedContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		dialog.setCancelable(false);

	}

	public void showDialog() {
		try {
			if (dialog != null && (!dialog.isShowing())) {
				dialog.show();
			}
		} catch (Exception e) {

		}
	}

	public void dismissDialog() {
		try {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		} catch (Exception e) {
		}
	}

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
			String ImageName = args[1];
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

			// Access Token
			String access_token = prefs.getString(PREF_KEY_OAUTH_TOKEN, "");
			// Access Token Secret
			String access_token_secret = prefs.getString(PREF_KEY_OAUTH_SECRET, "");

			AccessToken accessToken = new AccessToken(access_token, access_token_secret);
			Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
			System.out.println("FinalName===>" + ImageName);
			String filepath = Environment.getExternalStorageDirectory() + "/CamRate_Images";
			File file = new File(filepath, ImageName);
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
			}

			return null;
		}

		protected void onPostExecute(String file_url) {

			Intent intent = new Intent(ShareItScreen1.this, TabSample.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isFor", "myfeed");
			startActivity(intent);
			overridePendingTransition(0, 0);
			finish();

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.e("onBackPressed", "onBackPressed");
		Editor editPrefs = prefs.edit();
		editPrefs.putString("temptag", "");
		editPrefs.putString("tempdesc", "");
		editPrefs.commit();
		finish();
	}
}
