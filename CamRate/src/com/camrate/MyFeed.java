package com.camrate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.camrate.tools.VolleyJsonParser;
import com.camrate.tools.VolleyJsonParser.VolleyCallback;
import com.camrate.twitter.AlertDialogManager;
import com.facebook.UiLifecycleHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyFeed extends Activity {

	ListView myFeedPostFailed;
	Constant c = new Constant();
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONObject tempJsonobj = null;
	JSONArray objmyFeedJson;
	String myFeedParse;
	String UserID;
	MyFeedAdapter customAdapter;
	ImageView imglevelTag;
	public static ArrayList<HashMap<String, Object>> myFeedPostData;
	ArrayList<HashMap<String, String>> myFeedCommentsData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> myFeedRateData = new ArrayList<HashMap<String, String>>();
	int startindex;
	ImageLoader imageLoader = ImageLoader.getInstance();
	checkInternet chknet;
	int width, height;
	RelativeLayout.LayoutParams paramImage;
	private static final String LIST_STATE = "listState";
	private Parcelable mListState = null;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("email");
	private static final List<String> PERMISSIONS2 = Arrays.asList("publish_actions");
	ViewGroup loadMorefooter;

	// consumer key
	static String TWITTER_CONSUMER_KEY = "Yi91A6xPiHQfxjV5NzWBw";
	// consumer secret key
	static String TWITTER_CONSUMER_SECRET = "NJkHm22eWgJROQmdfaod8zVuKfkEnyYmfcmrJu5k";

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	static final String TWITTER_CALLBACK_URL = "twitAuth://camrate";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	// Progress dialog
	ProgressDialog pDialog;

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// TextView txtFooterView;
	private static final String TAG = MyFeed.class.getSimpleName();
	public static Typeface Kbrayant_med;
	String Response;
	ArrayList<HashMap<String, String>> failedPost;
	PostFailedMyFeedAdapter objpostFailed;
	Dialog rateDialog;
	RelativeLayout relRatePosted;
	int timeoutConnection = 60000;
	int timeoutSocket = 60000;
	int ACITIVITY_RESULT_POSTDETAIL = 1;
	private PullToRefreshListView mPullRefreshListView;
	ListView listView;
	int delPosition = 0;
	boolean isFbon = false, isTwitteron = false, isTumblron = false;
	ArrayList<HashMap<String, Object>> myFeedPostDetailRefresh;
	int ACTIVITY_RESULT_COMMENT = 0;
	int postIndex;
	boolean isdelItemavailable = false;
	Dialog dialog;
	SharedPreferences prefs;
	Function fun;
	TextView txtInviteFriends;
	String ImageName = "";
	VolleyJsonParser volleyParser;
	Context context;
	HashMap<String, String> params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homefeed);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Log.d("TAG", "ONCREATE");

		if ((savedInstanceState != null) && (savedInstanceState.getSerializable("data") != null)) {
			System.out.println("hi");
		} else {
			init();

		}
	}

	public void init() {
		initProcessDialog();
		myFeedPostData = new ArrayList<HashMap<String, Object>>();

		Constant.arrActivity.add(MyFeed.this);
		TextView tv14 = (TextView) findViewById(R.id.tvRateName);
		tv14.setText("My Feed");
		ImageView btnmyFriend = (ImageView) findViewById(R.id.button1);
		btnmyFriend.setVisibility(View.INVISIBLE);
		txtInviteFriends = (TextView) findViewById(R.id.button2);
		customAdapter = new MyFeedAdapter(MyFeed.this, R.layout.myfeed_list, myFeedPostData);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listMyFeedList);
		listView = mPullRefreshListView.getRefreshableView();
		myFeedPostFailed = (ListView) findViewById(R.id.listMyFeedPostFailed);
		relRatePosted = (RelativeLayout) findViewById(R.id.rateposted);
		chknet = new checkInternet(getApplicationContext());
		context = MyFeed.this;
		fun = new Function(context);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		volleyParser = new VolleyJsonParser(getParent());
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");
		checkPostInDB();

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

		System.out.println("UserID--->" + UserID);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		System.out.println("Width--->" + width);
		System.out.println("Height--->" + height);
		System.out.println("IMageview Height and width--->" + (width - 20) / 3);
		paramImage = new RelativeLayout.LayoutParams((width - 20) / 3, (width - 20) / 3);
		int round = (width - 20) / 3;
		System.out.println("MyFeedList==>" + myFeedPostData);

		/*** ReferFriend */
		btnmyFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SimpleAlert(MyFeed.this, "Invite Feature Coming Soon - Will be available when app is launched on PlayStore");
			}
		});
		/** Refresh Api */

		System.out.println("oncreate");

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (mPullRefreshListView.getCurrentMode() == Mode.PULL_FROM_START) {
					myFeedPostData.clear();
					startindex = 0;
					params = new HashMap<String, String>();
					params.put("api", "MyFeedtest");
					params.put("User_ID", UserID);
					params.put("start", String.valueOf(startindex));
					params.put("end", "15");
					params.put("totalrec", "15");
					// new GetMyFeed().execute();
					if (chknet.isNetworkConnected()) {
						// new GetMyFeed().execute();
						// volleyParser.makeStringReq(url, params, volleyParser.mcallback);
						volleyParser.makeStringReq(Constant.Main_URL, params, v);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					// new GetMyFeed().execute();
					if (chknet.isNetworkConnected()) {
						// new GetMyFeed().execute();
						params = new HashMap<String, String>();
						params.put("api", "MyFeedtest");
						params.put("User_ID", UserID);
						params.put("start", String.valueOf(startindex));
						params.put("end", "15");
						params.put("totalrec", "15");
						// volleyParser.makeStringReq(url, params, volleyParser.mcallback);
						volleyParser.makeStringReq(Constant.Main_URL, params, v);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		txtInviteFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), InviteFriends.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("InviteFriends", intent);
			}
		});
		if (chknet.isNetworkConnected()) {
			params = new HashMap<String, String>();
			params.put("api", "MyFeedtest");
			params.put("User_ID", UserID);
			params.put("start", String.valueOf(startindex));
			params.put("end", "15");
			params.put("totalrec", "15");
			volleyParser.makeStringReq(Constant.Main_URL, params, v);
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}

	}

	VolleyCallback v = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			parse_setData(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};

	VolleyCallback vpost = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			parse_setDataAfterPost(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		ActivityManage.lastSeletedTab = 0;
		Log.d("TAG", "onresume");
		Log.d("TAG", "Volley===>" + myFeedParse);
		// parse_setData(arrParse);

		if (ActivityManage.isFromPostDetail) {

			try {

				if (ActivityManage.isForDelete) {
					myFeedPostData.remove(ActivityManage.deletePostion);
					customAdapter.notifyDataSetChanged();
				} else {
					ActivityManage.isFromPostDetail = false;
					postIndex = ActivityManage.postIndex;
					String Post_ID = ActivityManage.Post_ID;
					if (chknet.isNetworkConnected()) {
						params = new HashMap<String, String>();
						params.put("api", "PostDetail");
						params.put("User_ID", UserID);
						params.put("Post_ID", Post_ID);
						volleyParser.makeStringReq(Constant.Main_URL, params, vpost);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
					// new getPostAfterRate().execute(postID);
				}
			} catch (Exception e) {

			}
		} else if (ActivityManage.isFromComment) {
			try {
				ActivityManage.isFromComment = false;
				postIndex = ActivityManage.postIndex;
				String postID = ActivityManage.Post_ID;
				if (chknet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "PostDetail");
					params.put("User_ID", UserID);
					params.put("Post_ID", postID);
					volleyParser.makeStringReq(Constant.Main_URL, params, vpost);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		c.TagmyPref = getSharedPreferences(c.TAG_PREF, Context.MODE_WORLD_WRITEABLE);
		Editor edt = c.TagmyPref.edit();
		edt.putString(c.TAG_KEY, TAG);
		edt.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(MyFeed.this, BroadcastServiveFB.class));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		System.out.println("onSaveFeed");

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		System.out.println("onRestore");

	}

	public void setTheAdapterWithArray() {

		Log.d("TAG", "set adapter");

		if (listView.getAdapter() == null) {
			customAdapter = new MyFeedAdapter(getParent(), R.layout.myfeed_list, myFeedPostData);
			listView.setAdapter(customAdapter);
		} else {
			customAdapter.notifyDataSetChanged();
			System.out.println("Adapter secondTime");
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position - 1;

				Intent intent = new Intent(getParent(), PostDetail.class);
				intent.putExtra("PostData", myFeedPostData);
				intent.putExtra("CheckedIndex", position);
				intent.putExtra("SelectedPostData", myFeedPostData.get(position));
				intent.putExtra("TotalCheckedIndex", myFeedPostData.size());
				intent.putExtra("progress", 0);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("PostDetail", intent);

			}
		});
	}

	public void SimpleAlert(Context context, String b) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("CamRate");
		builder.setMessage(b);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// @Override
	// public void onBackPressed() {
	// super.onBackPressed();
	// footer.calledBack(MyFeed.this);
	// }

	public void checkPostInDB() {

		Log.d("TAG", "CHECK IN DB");

		DataBaseAdapter mDbHelper = new DataBaseAdapter(getApplicationContext());
		try {
			Log.d("TAG", "create db");
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		failedPost = mDbHelper.getAllPost();

		Log.d("TAG", "POST SIZE " + failedPost.size());

		objpostFailed = new PostFailedMyFeedAdapter(MyFeed.this, R.layout.post_failed, failedPost);
		if (failedPost.size() > 0) {
			myFeedPostFailed.setVisibility(View.VISIBLE);
			myFeedPostFailed.setAdapter(objpostFailed);

			if (chknet.isNetworkConnected()) {

				for (int i = 0; i < failedPost.size(); i++) {
					postData(i, failedPost);
				}
				if (failedPost.size() == 1) {
					isdelItemavailable = true;
				} else {
					isdelItemavailable = false;
				}
			} else {

			}
		} else {
			myFeedPostFailed.setVisibility(View.GONE);
		}
	}

	public void postDataFromAdapter(int position) {
		postData(position, failedPost);
	}

	public void postData(int pos, ArrayList<HashMap<String, String>> items) {

		String url = c.GetBaseUrl() + "api=Post5android";
		String User_ID = String.valueOf(items.get(pos).get("Post_userid"));// int
		String postTitle = items.get(pos).get("Post_title");
		String postDesc = items.get(pos).get("Post_description");
		String postLoc = items.get(pos).get("Post_location");
		String postlat = items.get(pos).get("Post_latitude");
		String postlong = items.get(pos).get("Post_longitude");
		String postTags = items.get(pos).get("Post_title");
		String ratingrate = items.get(pos).get("Post_ratingRate");
		String catid = String.valueOf(items.get(pos).get("Post_categoryid"));// int
		String image = items.get(pos).get("Post_imagename");
		ImageName = image;
		String Video_Name = items.get(pos).get("Post_videoname");
		String emailList = items.get(pos).get("Post_emailList");
		String post_Twitter = items.get(pos).get("Post_twitter");
		String Post_tumblr = items.get(pos).get("Post_tumblr");
		String Post_facebook = items.get(pos).get("Post_facebook");
		isFbon = prefs.getBoolean("isfbon", false);
		isTumblron = prefs.getBoolean("isTumblron", false);
		isTwitteron = prefs.getBoolean("isTwitteron", false);
		String[] params = new String[] { url, User_ID, postTitle, postDesc, postLoc, postlat, postlong, postTags, ratingrate, catid, image, Video_Name, emailList };

		Log.d("TAG", "START TO SUBMIT");
		if (chknet.isNetworkConnected()) {
			new submitPost(pos).execute(params);
		} else {
			Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
		}

	}

	private class submitPost extends AsyncTask<String, Void, String> {
		int position;

		public submitPost(int pos) {
			position = pos;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			Log.d("TAG", "SUBMITING");

			String json = "";

			String Result = null;
			String url = params[0];
			String User_ID = params[1];
			String postTitle = params[2];
			String postDesc = params[3];
			String postLoc = params[4];
			String postlat = params[5];
			String postlong = params[6];
			String postTags = params[7];
			String ratingrate = params[8];
			String catid = params[9];
			String image = params[10];
			String videoName = params[11];
			String emailList = params[12];
			String filepath = "";
			String ImageName = "";
			ByteArrayOutputStream baos = null;
			byte[] videodata = null;
			if (videoName.equalsIgnoreCase("1")) {
				ImageName = image.replaceAll("/", "");
				System.out.println("FinalName===>" + ImageName);
				filepath = Environment.getExternalStorageDirectory() + "/CamRate_Images/" + ImageName;
				System.out.println("filepath-------" + filepath);
				Bitmap b = BitmapFactory.decodeFile(filepath);
				baos = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 70, baos);
			} else {

				ImageName = videoName.replaceAll("/", "");
				String path = Environment.getExternalStorageDirectory() + "/CamRate_Video/covervideo.jpg";
				Bitmap b = BitmapFactory.decodeFile(path);
				baos = new ByteArrayOutputStream();
				b.compress(CompressFormat.JPEG, 70, baos);
				filepath = Environment.getExternalStorageDirectory() + "/CamRate_Video/" + ImageName;
				System.out.println("filepath-------" + filepath);
				File file = new File(filepath);
				videodata = convertFileToByteArray(file);
				// System.out.println("filepath-------" + filepath);
			}

			try {

				InputStream is = null;

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				entity.addPart("User_ID", new StringBody(User_ID));
				entity.addPart("Post_Title", new StringBody(postTitle));
				entity.addPart("Post_Content", new StringBody(postDesc));
				entity.addPart("Post_Location", new StringBody(postLoc));
				entity.addPart("Post_Latitude", new StringBody(postlat));
				entity.addPart("Post_Longitude", new StringBody(postlong));
				entity.addPart("Post_Tags", new StringBody(postTags));
				entity.addPart("Rating_Rate", new StringBody(ratingrate));
				entity.addPart("Email_List", new StringBody(emailList));
				entity.addPart("Post_CategoryID", new StringBody(catid));

				if (videoName.equalsIgnoreCase("1")) {
					entity.addPart("Post_IsVideo", new StringBody("0"));
					entity.addPart("Post_Image", new ByteArrayBody(baos.toByteArray(), ImageName));

				} else {
					entity.addPart("Post_IsVideo", new StringBody("1"));
					entity.addPart("Post_Image", new ByteArrayBody(baos.toByteArray(), "covervideo.jpg"));
					entity.addPart("Post_Video", new ByteArrayBody(videodata, ImageName));

				}

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

			return json;

		}

		@Override
		protected void onPostExecute(String status) {

			// Log.d("TAG", "POST RESULET" + status);
			JSONObject objPost;
			try {
				objPost = new JSONObject(status);

				if (objPost.getString("result").equalsIgnoreCase("1")) {

					setParsePost(position, objPost);
					Log.d("TAG", "Post Posted successfully");
				} else {
					Toast.makeText(MyFeed.this, "no post", Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(MyFeed.this, "Please try later", Toast.LENGTH_SHORT).show();
			}

		}
	}

	public void setParsePost(int pos, JSONObject myFeedData) throws JSONException {
		ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
		String Post_ID = myFeedData.getString("Post_ID").toString();
		String Post_CategoryID = myFeedData.getString("Post_CategoryID").toString();
		String Post_Title = myFeedData.getString("Post_Title").toString();
		String Post_ImageSquare = myFeedData.getString("Post_ImageSquare").toString();
		String Post_Location = myFeedData.getString("Post_Location").toString();
		String Post_Date = myFeedData.getString("Post_Date").toString();
		String Post_Tags = myFeedData.getString("Post_Tags").toString();
		String Post_Latitude = myFeedData.getString("Post_Latitude").toString();
		String Post_Longitude = myFeedData.getString("Post_Longitude").toString();
		String Post_RecentlyModifiedDate = myFeedData.getString("Post_RecentlyModifiedDate").toString();
		String Post_VideoURL = myFeedData.getString("Post_VideoURL").toString();
		String Post_IsVideo = myFeedData.getString("Post_IsVideo").toString();
		String Post_Content = myFeedData.getString("Post_Content").toString();

		/*** UserDetail ***********/
		JSONObject objJsonUser = myFeedData.getJSONObject("User");

		String User_ID = objJsonUser.getString("User_ID").toString();
		String User_FirstName = objJsonUser.getString("User_FirstName").toString();
		String User_LastName = objJsonUser.getString("User_LastName").toString();
		String User_Name = objJsonUser.getString("User_Name").toString();
		String User_Password = objJsonUser.getString("User_Password").toString();
		String User_Imagepath = objJsonUser.getString("User_Imagepath").toString();
		String User_Email = objJsonUser.getString("User_Email").toString();
		String User_CountryID = objJsonUser.getString("User_CountryID").toString();
		String User_City = objJsonUser.getString("User_City").toString();
		String User_Gender = objJsonUser.getString("User_Gender").toString();
		String User_Age = objJsonUser.getString("User_Age").toString();
		String User_AgeRange = objJsonUser.getString("User_AgeRange").toString();
		String User_Desc = objJsonUser.getString("User_Desc").toString();
		String User_PushNotification = objJsonUser.getString("User_PushNotification").toString();
		String User_Location = objJsonUser.getString("User_Location").toString();
		String User_Badge = objJsonUser.getString("User_Badge").toString();
		String User_Status = objJsonUser.getString("User_Status").toString();
		String User_IsPrivate = objJsonUser.getString("User_IsPrivate").toString();
		String User_IsActive = objJsonUser.getString("User_IsActive").toString();
		String User_LastSession = objJsonUser.getString("User_LastSession").toString();
		String User_Date = objJsonUser.getString("User_Date").toString();
		String PostCount = objJsonUser.getString("PostCount").toString();

		/*** Post **/
		String Count_Rating = myFeedData.getString("Count_Rating").toString();
		String Sum_Rating = myFeedData.getString("Sum_Rating").toString();
		String Average_Rating = myFeedData.getString("Average_Rating").toString();
		String CommentsCount = myFeedData.getString("CommentsCount").toString();
		String User_Rated = myFeedData.getString("User_Rated").toString();

		/** Putting PostData **/
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Post_ID", Post_ID);
		map.put("Post_CategoryID", Post_CategoryID);
		map.put("Post_Title", Post_Title);
		map.put("Post_ImageSquare", Post_ImageSquare);
		map.put("Post_Location", Post_Location);
		map.put("Post_Date", Post_Date);
		map.put("Post_Tags", Post_Tags);
		map.put("Post_Latitude", Post_Latitude);
		map.put("Post_Longitude", Post_Longitude);
		map.put("Post_RecentlyModifiedDate", Post_RecentlyModifiedDate);
		map.put("Post_VideoURL", Post_VideoURL);
		map.put("Post_IsVideo", Post_IsVideo);
		map.put("Post_Content", Post_Content);
		map.put("User_ID", User_ID);
		map.put("User_FirstName", User_FirstName);
		map.put("User_LastName", User_LastName);
		map.put("User_Name", User_Name);
		map.put("User_Password", User_Password);
		map.put("User_Imagepath", User_Imagepath);
		map.put("User_Email", User_Email);
		map.put("User_CountryID", User_CountryID);
		map.put("User_City", User_City);
		map.put("User_Gender", User_Gender);
		map.put("User_Imagepath", User_Imagepath);
		map.put("User_Age", User_Age);
		map.put("User_AgeRange", User_AgeRange);
		map.put("User_Desc", User_Desc);
		map.put("User_PushNotification", User_PushNotification);
		map.put("User_Location", User_Location);
		map.put("User_Badge", User_Badge);
		map.put("User_Status", User_Status);
		map.put("User_IsPrivate", User_IsPrivate);
		map.put("User_IsActive", User_IsActive);
		map.put("User_LastSession", User_LastSession);
		map.put("User_Date", User_Date);
		map.put("Count_Rating", Count_Rating);
		map.put("Sum_Rating", Sum_Rating);
		map.put("PostCount", PostCount);
		map.put("Average_Rating", Average_Rating);
		map.put("CommentsCount", CommentsCount);
		map.put("User_Rated", User_Rated);
		// System.out.println("myFeedPostData-->"+myFeedPostData);
		/** Comments **/
		JSONArray Comment = myFeedData.getJSONArray("Comments");
		// System.out.println("JsonComment-->"+Comment);

		if (Comment.length() > 0) {
			for (int j = 0; j < Comment.length(); j++) {
				JSONObject objComment = Comment.getJSONObject(j);
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("Comment_ID", objComment.getString("Comment_ID"));
				map1.put("Comment_PostID", objComment.getString("Comment_PostID"));
				map1.put("Comment_UserID", objComment.getString("Comment_UserID"));
				map1.put("Comment_Content", objComment.getString("Comment_Content"));
				map1.put("Comment_Approved", objComment.getString("Comment_Approved"));
				map1.put("Comment_Date", objComment.getString("Comment_Date"));
				map1.put("User_ID", objComment.getString("User_ID"));
				map1.put("User_FirstName", objComment.getString("User_FirstName"));
				map1.put("User_LastName", objComment.getString("User_LastName"));
				map1.put("User_Name", objComment.getString("User_Name"));
				map1.put("User_Imagepath", objComment.getString("User_Imagepath"));
				map1.put("User_Email", objComment.getString("User_Email"));
				map1.put("User_CountryID", objComment.getString("User_CountryID"));
				map1.put("User_City", objComment.getString("User_City"));
				map1.put("User_Gender", objComment.getString("User_Gender"));
				map1.put("User_Age", objComment.getString("User_Age"));
				map1.put("User_AgeRange", objComment.getString("User_AgeRange"));
				map1.put("User_Desc", objComment.getString("User_Desc"));
				map1.put("User_PushNotification", objComment.getString("User_PushNotification"));
				map1.put("User_Location", objComment.getString("User_Location"));
				map1.put("User_Badge", objComment.getString("User_Badge"));
				map1.put("User_Status", objComment.getString("User_Status"));
				map1.put("User_IsPrivate", objComment.getString("User_IsPrivate"));
				map1.put("User_IsActive", objComment.getString("User_IsActive"));
				map1.put("User_LastSession", objComment.getString("User_LastSession"));
				map1.put("User_Date", objComment.getString("User_Date"));
				myFeedCommentsData1.add(map1);
			}
		}

		/** Rate **/
		JSONArray Rate = myFeedData.getJSONArray("Rate");
		// System.out.println("Rate-->"+Rate);
		if (Rate.length() > 0) {
			for (int k = 0; k < Rate.length(); k++) {
				JSONObject objRate = Rate.getJSONObject(k);
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("User_ID", objRate.getString("User_ID"));
				map1.put("User_FirstName", objRate.getString("User_FirstName"));
				map1.put("User_LastName", objRate.getString("User_LastName"));
				map1.put("User_Name", objRate.getString("User_Name"));
				map1.put("User_Password", objRate.getString("User_Password"));
				map1.put("User_Imagepath", objRate.getString("User_Imagepath"));
				map1.put("User_Email", objRate.getString("User_Email"));
				map1.put("User_CountryID", objRate.getString("User_CountryID"));
				map1.put("User_City", objRate.getString("User_City"));
				map1.put("User_Gender", objRate.getString("User_Gender"));
				map1.put("User_Age", objRate.getString("User_Age"));
				map1.put("User_AgeRange", objRate.getString("User_AgeRange"));
				map1.put("User_Desc", objRate.getString("User_Desc"));
				map1.put("User_PushNotification", objRate.getString("User_PushNotification"));
				map1.put("User_Location", objRate.getString("User_Location"));
				map1.put("User_Badge", objRate.getString("User_Badge"));
				map1.put("User_Status", objRate.getString("User_Status"));
				map1.put("User_IsPrivate", objRate.getString("User_IsPrivate"));
				map1.put("User_IsActive", objRate.getString("User_IsActive"));
				map1.put("User_LastSession", objRate.getString("User_LastSession"));
				map1.put("User_Date", objRate.getString("User_Date"));
				map1.put("Rating_ID", objRate.getString("Rating_ID"));
				map1.put("Rating_PostID", objRate.getString("Rating_PostID"));
				map1.put("Rating_UserID", objRate.getString("Rating_UserID"));
				map1.put("Rating_Rate", objRate.getString("Rating_Rate"));
				map1.put("Rating_Date", objRate.getString("Rating_Date"));
				myFeedRateData1.add(map1);
			}
		}
		map.put("flag", false);
		map.put("Rate", myFeedRateData1);
		map.put("Comments", myFeedCommentsData1);
		myFeedPostData.add(0, map);
		if (isFbon || isTumblron || isTwitteron) {
			onShareItem(Post_Tags, Post_Content, Average_Rating, Count_Rating, Post_ID, ImageName);
		}
		//
		if (isFbon) {
			isFbon = false;
			if (isFBServiceRunning()) {
				stopService(new Intent(MyFeed.this, BroadcastServiveFB.class));
				startService(new Intent(MyFeed.this, BroadcastServiveFB.class).putExtra("position", pos));
			} else {
				startService(new Intent(MyFeed.this, BroadcastServiveFB.class).putExtra("position", pos));
			}
		}
		if (isTwitteron) {
			isTwitteron = false;
			if (isTWTServiceRunning()) {
				stopService(new Intent(MyFeed.this, BroadcastServiveTwitter.class));
				startService(new Intent(MyFeed.this, BroadcastServiveTwitter.class).putExtra("position", pos));
			} else {
				startService(new Intent(MyFeed.this, BroadcastServiveTwitter.class).putExtra("position", pos));
			}
		}
		if (isTumblron) {
			isTumblron = false;
			if (isTumblrServiceRunning()) {
				stopService(new Intent(MyFeed.this, BroadcastServiveTumblr.class));
				startService(new Intent(MyFeed.this, BroadcastServiveTumblr.class).putExtra("position", pos));
			} else {
				startService(new Intent(MyFeed.this, BroadcastServiveTumblr.class).putExtra("position", pos));
			}
		}
		delPosition = pos;
		//

		String[] params = new String[] { UserID, Post_ID };
		new changestatus().execute(params);

	}

	public void deletPost(int pos, ArrayList<HashMap<String, String>> items) {
		DataBaseAdapter mDbHelper = new DataBaseAdapter(MyFeed.this);
		try {
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		boolean isDel = mDbHelper.deletePost(Integer.parseInt(items.get(pos).get("Post_id")));

		if (isDel) {
			Log.d("TAG", "deleted " + pos);
		} else {
			Log.d("TAG", "Not deleted " + pos);
		}

	}

	public static byte[] convertFileToByteArray(File f) {
		byte[] byteArray = null;
		try {
			InputStream inputStream = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024 * 1024];
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

	class changestatus extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			String Result = "";
			String User_ID = params[0];
			String Post_ID = params[1];
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=ChangePendingPostStatus&User_ID=" + User_ID + "&Post_ID=" + Post_ID + "";
			System.out.println(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = parser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			if (chknet.isNetworkConnected()) {
				new showPostRatedScreen().execute();
			} else {
				Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
			}

		}

	}

	private class showPostRatedScreen extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			relRatePosted.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

				Thread.sleep(2000);

			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isdelItemavailable) {
				deletPost(delPosition, failedPost);
				failedPost.remove(delPosition);
				isdelItemavailable = false;
			}
			relRatePosted.setVisibility(View.GONE);
			objpostFailed.notifyDataSetChanged();
			customAdapter.notifyDataSetChanged();

		}

	}

	private boolean isFBServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.camrate.BroadcastServiveFB".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isTWTServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.camrate.BroadcastServiveTwitter".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isTumblrServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.camrate.BroadcastServiveTumblr".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Log.d("TAG", "Activity Result");
		//
		// if (requestCode == ACITIVITY_RESULT_POSTDETAIL) {
		// try {
		// if (data != null) {
		//
		// if (data.getBooleanExtra("isForDelete", false)) {
		//
		// myFeedPostData.remove(data.getIntExtra("deletePostion", 0));
		// customAdapter.notifyDataSetChanged();
		//
		// } else {
		// postIndex = data.getIntExtra("postIndex", 0);
		// String postID = data.getStringExtra("Post_ID");
		// Log.d("TAG", "postIndex " + postIndex + " postID " + postID);
		// new getPostAfterRate().execute(postID);
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// } else if (requestCode == ACTIVITY_RESULT_COMMENT) {
		//
		// try {
		// postIndex = data.getIntExtra("postIndex", 0);
		// String postID = data.getStringExtra("Post_ID");
		// Log.d("TAG", "postIndex " + postIndex + " postID " + postID);
		// new getPostAfterRate().execute(postID);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
	}

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(getParent(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(getParent(), android.R.style.Theme_Light_NoTitleBar);
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
				Log.d("TAG", "SHOW DIALOG");
				dialog.show();
			}
		} catch (Exception e) {

		}
	}

	public void dismissDialog() {
		try {
			if (dialog != null && dialog.isShowing()) {
				Log.d("TAG", "DISMISS DIALOG");
				dialog.dismiss();
			}
		} catch (Exception e) {
		}
	}

	// Can be triggered by a view event such as a button press
	public void onShareItem(String Post_Tag, String Post_Content, String avgRating, String CountRating, String PostID, String ImageName) {
		// Get access to bitmap image from view
		// Get access to the URI for the bitmap

		String NewImageName = ImageName.replaceAll("/", "");

		Uri bmpUri = getLocalBitmapUri(avgRating, CountRating, NewImageName);

		String mainUrl = "http://camrate.com";
		String FBBody = Post_Tag + "\n" + Post_Content + "\n\n" + mainUrl + "\n";

		String encodepostid = fun.encodePostID(PostID);
		String TwitterBodyLink = Post_Tag + "\n\n" + Post_Content + "\n\n" + "http://camrate.com/TestCamRate/Lovehate/postdetail/" + encodepostid;
	}

	// Returns the URI path to the Bitmap displayed in specified ImageView
	public Uri getLocalBitmapUri(String avgRating, String countRating, String ImageName) {

		// Store image to default external storage directory
		Bitmap bmp = null;
		String filepath = Environment.getExternalStorageDirectory() + "/CamRate_Images";
		File file1 = new File(filepath, ImageName);
		bmp = BitmapFactory.decodeFile(file1.getAbsolutePath());
		Bitmap topBar = BitmapFactory.decodeResource(context.getResources(), R.drawable.fbposttopbar);
		Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), R.drawable.fbpostoverlay);
		Bitmap updatedBitmap = fun.joinImages(bmp, topBar, overlay, avgRating, countRating);
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), ImageName);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			updatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.close();
			bmpUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmpUri;
	}

	public void parse_setData(String response) {
		int size = myFeedPostData.size();
		try {
			JSONArray jsonMyFeed = new JSONArray(response);
			if (jsonMyFeed.length() > 0) {

				startindex += 15;

				for (int s = 0; s < jsonMyFeed.length(); s++) {
					ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
					ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
					ArrayList<HashMap<String, String>> myFeedUserData = new ArrayList<HashMap<String, String>>();

					JSONObject myFeedData = jsonMyFeed.getJSONObject(s);
					tempJsonobj = jsonMyFeed.getJSONObject(s);
					/*** Post **/
					String position = String.valueOf(s);
					String Post_ID = myFeedData.getString("Post_ID").toString();
					String Post_CategoryID = myFeedData.getString("Post_CategoryID").toString();
					String Post_Title = myFeedData.getString("Post_Title").toString();
					String Post_ImageSquare = myFeedData.getString("Post_ImageSquare").toString();
					String Post_Location = myFeedData.getString("Post_Location").toString();
					String Post_Date = myFeedData.getString("Post_Date").toString();
					String Post_Tags = myFeedData.getString("Post_Tags").toString();
					String Post_Latitude = myFeedData.getString("Post_Latitude").toString();
					String Post_Longitude = myFeedData.getString("Post_Longitude").toString();
					String Post_RecentlyModifiedDate = myFeedData.getString("Post_RecentlyModifiedDate").toString();
					String Post_VideoURL = myFeedData.getString("Post_VideoURL").toString();
					String Post_IsVideo = myFeedData.getString("Post_IsVideo").toString();
					String Post_Content = myFeedData.getString("Post_Content").toString();

					/*** UserDetail ***********/
					JSONObject objJsonUser = myFeedData.getJSONObject("User");
					String User_ID = objJsonUser.getString("User_ID").toString();
					String User_FirstName = objJsonUser.getString("User_FirstName").toString();
					String User_LastName = objJsonUser.getString("User_LastName").toString();
					String User_Name = objJsonUser.getString("User_Name").toString();
					String User_Password = objJsonUser.getString("User_Password").toString();
					String User_Imagepath = objJsonUser.getString("User_Imagepath").toString();
					String User_Email = objJsonUser.getString("User_Email").toString();
					String User_CountryID = objJsonUser.getString("User_CountryID").toString();
					String User_City = objJsonUser.getString("User_City").toString();
					String User_Gender = objJsonUser.getString("User_Gender").toString();
					String User_Age = objJsonUser.getString("User_Age").toString();
					String User_AgeRange = objJsonUser.getString("User_AgeRange").toString();
					String User_Desc = objJsonUser.getString("User_Desc").toString();
					String User_PushNotification = objJsonUser.getString("User_PushNotification").toString();
					String User_Location = objJsonUser.getString("User_Location").toString();
					String User_Badge = objJsonUser.getString("User_Badge").toString();
					String User_Status = objJsonUser.getString("User_Status").toString();
					String User_IsPrivate = objJsonUser.getString("User_IsPrivate").toString();
					String User_IsActive = objJsonUser.getString("User_IsActive").toString();
					String User_LastSession = objJsonUser.getString("User_LastSession").toString();
					String User_Date = objJsonUser.getString("User_Date").toString();
					String PostCount = objJsonUser.getString("PostCount").toString();

					/*** Post **/
					String Count_Rating = myFeedData.getString("Count_Rating").toString();
					String Sum_Rating = myFeedData.getString("Sum_Rating").toString();
					String Average_Rating = myFeedData.getString("Average_Rating").toString();
					String CommentsCount = myFeedData.getString("CommentsCount").toString();
					String User_Rated = myFeedData.getString("User_Rated").toString();

					/** Putting PostData **/
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("Position", position);
					map.put("Post_ID", Post_ID);
					map.put("Post_CategoryID", Post_CategoryID);
					map.put("Post_Title", Post_Title);
					map.put("Post_ImageSquare", Post_ImageSquare);
					map.put("Post_Location", Post_Location);
					map.put("Post_Date", Post_Date);
					map.put("Post_Tags", Post_Tags);
					map.put("Post_Latitude", Post_Latitude);
					map.put("Post_Longitude", Post_Longitude);
					map.put("Post_RecentlyModifiedDate", Post_RecentlyModifiedDate);
					map.put("Post_VideoURL", Post_VideoURL);
					map.put("Post_IsVideo", Post_IsVideo);
					map.put("Post_Content", Post_Content);
					map.put("User_ID", User_ID);
					map.put("User_FirstName", User_FirstName);
					map.put("User_LastName", User_LastName);
					map.put("User_Name", User_Name);
					map.put("User_Password", User_Password);
					map.put("User_Imagepath", User_Imagepath);
					map.put("User_Email", User_Email);
					map.put("User_CountryID", User_CountryID);
					map.put("User_City", User_City);
					map.put("User_Gender", User_Gender);
					map.put("User_Imagepath", User_Imagepath);
					map.put("User_Age", User_Age);
					map.put("User_AgeRange", User_AgeRange);
					map.put("User_Desc", User_Desc);
					map.put("User_PushNotification", User_PushNotification);
					map.put("User_Location", User_Location);
					map.put("User_Badge", User_Badge);
					map.put("User_Status", User_Status);
					map.put("User_IsPrivate", User_IsPrivate);
					map.put("User_IsActive", User_IsActive);
					map.put("User_LastSession", User_LastSession);
					map.put("User_Date", User_Date);
					map.put("Count_Rating", Count_Rating);
					map.put("Sum_Rating", Sum_Rating);
					map.put("PostCount", PostCount);
					map.put("Average_Rating", Average_Rating);
					map.put("CommentsCount", CommentsCount);
					map.put("User_Rated", User_Rated);

					// System.out.println("myFeedPostData-->"+myFeedPostData);
					/** Comments **/
					JSONArray Comment = myFeedData.getJSONArray("Comments");
					// System.out.println("JsonComment-->"+Comment);

					if (Comment.length() > 0) {
						for (int j = 0; j < Comment.length(); j++) {
							JSONObject objComment = Comment.getJSONObject(j);
							HashMap<String, String> map1 = new HashMap<String, String>();
							map1.put("Comment_ID", objComment.getString("Comment_ID"));
							map1.put("Comment_PostID", objComment.getString("Comment_PostID"));
							map1.put("Comment_UserID", objComment.getString("Comment_UserID"));
							map1.put("Comment_Content", objComment.getString("Comment_Content"));
							map1.put("Comment_Approved", objComment.getString("Comment_Approved"));
							map1.put("Comment_Date", objComment.getString("Comment_Date"));
							map1.put("User_ID", objComment.getString("User_ID"));
							map1.put("User_FirstName", objComment.getString("User_FirstName"));
							map1.put("User_LastName", objComment.getString("User_LastName"));
							map1.put("User_Name", objComment.getString("User_Name"));
							map1.put("User_Imagepath", objComment.getString("User_Imagepath"));
							map1.put("User_Email", objComment.getString("User_Email"));
							map1.put("User_CountryID", objComment.getString("User_CountryID"));
							map1.put("User_City", objComment.getString("User_City"));
							map1.put("User_Gender", objComment.getString("User_Gender"));
							map1.put("User_Age", objComment.getString("User_Age"));
							map1.put("User_AgeRange", objComment.getString("User_AgeRange"));
							map1.put("User_Desc", objComment.getString("User_Desc"));
							map1.put("User_PushNotification", objComment.getString("User_PushNotification"));
							map1.put("User_Location", objComment.getString("User_Location"));
							map1.put("User_Badge", objComment.getString("User_Badge"));
							map1.put("User_Status", objComment.getString("User_Status"));
							map1.put("User_IsPrivate", objComment.getString("User_IsPrivate"));
							map1.put("User_IsActive", objComment.getString("User_IsActive"));
							map1.put("User_LastSession", objComment.getString("User_LastSession"));
							map1.put("User_Date", objComment.getString("User_Date"));
							myFeedCommentsData1.add(map1);
						}
					}

					/** Rate **/
					JSONArray Rate = myFeedData.getJSONArray("Rate");
					// System.out.println("Rate-->"+Rate);
					if (Rate.length() > 0) {
						for (int k = 0; k < Rate.length(); k++) {
							JSONObject objRate = Rate.getJSONObject(k);
							HashMap<String, String> map1 = new HashMap<String, String>();
							map1.put("User_ID", objRate.getString("User_ID"));
							map1.put("User_FirstName", objRate.getString("User_FirstName"));
							map1.put("User_LastName", objRate.getString("User_LastName"));
							map1.put("User_Name", objRate.getString("User_Name"));
							map1.put("User_Password", objRate.getString("User_Password"));
							map1.put("User_Imagepath", objRate.getString("User_Imagepath"));
							map1.put("User_Email", objRate.getString("User_Email"));
							map1.put("User_CountryID", objRate.getString("User_CountryID"));
							map1.put("User_City", objRate.getString("User_City"));
							map1.put("User_Gender", objRate.getString("User_Gender"));
							map1.put("User_Age", objRate.getString("User_Age"));
							map1.put("User_AgeRange", objRate.getString("User_AgeRange"));
							map1.put("User_Desc", objRate.getString("User_Desc"));
							map1.put("User_PushNotification", objRate.getString("User_PushNotification"));
							map1.put("User_Location", objRate.getString("User_Location"));
							map1.put("User_Badge", objRate.getString("User_Badge"));
							map1.put("User_Status", objRate.getString("User_Status"));
							map1.put("User_IsPrivate", objRate.getString("User_IsPrivate"));
							map1.put("User_IsActive", objRate.getString("User_IsActive"));
							map1.put("User_LastSession", objRate.getString("User_LastSession"));
							map1.put("User_Date", objRate.getString("User_Date"));
							map1.put("Rating_ID", objRate.getString("Rating_ID"));
							map1.put("Rating_PostID", objRate.getString("Rating_PostID"));
							map1.put("Rating_UserID", objRate.getString("Rating_UserID"));
							map1.put("Rating_Rate", objRate.getString("Rating_Rate"));
							map1.put("Rating_Date", objRate.getString("Rating_Date"));

							myFeedRateData1.add(map1);
						}
					}
					map.put("flag", false);
					map.put("isVideoPlaying", false);
					map.put("Rate", myFeedRateData1);
					map.put("Comments", myFeedCommentsData1);
					myFeedPostData.add(map);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mPullRefreshListView.onRefreshComplete();
		setTheAdapterWithArray();
	}

	public void parse_setDataAfterPost(String refresh_PostData) {

		myFeedPostDetailRefresh = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
		try {
			JSONObject objRefreshData = new JSONObject(refresh_PostData);
			String position = "1";
			String Post_ID = objRefreshData.getString("Post_ID").toString();
			String Post_CategoryID = objRefreshData.getString("Post_CategoryID").toString();
			String Post_Title = objRefreshData.getString("Post_Title").toString();
			String Post_ImageSquare = objRefreshData.getString("Post_ImageSquare").toString();
			String Post_Location = objRefreshData.getString("Post_Location").toString();
			String Post_Date = objRefreshData.getString("Post_Date").toString();
			String Post_Tags = objRefreshData.getString("Post_Tags").toString();
			String Post_Latitude = objRefreshData.getString("Post_Latitude").toString();
			String Post_Longitude = objRefreshData.getString("Post_Longitude").toString();
			String Post_RecentlyModifiedDate = objRefreshData.getString("Post_RecentlyModifiedDate").toString();
			String Post_VideoURL = objRefreshData.getString("Post_VideoURL").toString();
			String Post_IsVideo = objRefreshData.getString("Post_IsVideo").toString();
			String Post_Content = objRefreshData.getString("Post_Content").toString();

			/*** UserDetail ***********/
			JSONObject objJsonUser = objRefreshData.getJSONObject("User");
			// System.out.println("user-->"+objJsonUser);
			/*
			 * String User_Name=user.getString("User_Name").toString(); String User_Imagepath=user.getString("User_Imagepath").toString ();
			 */
			String User_ID = objJsonUser.getString("User_ID").toString();
			String User_FirstName = objJsonUser.getString("User_FirstName").toString();
			String User_LastName = objJsonUser.getString("User_LastName").toString();
			String User_Name = objJsonUser.getString("User_Name").toString();
			String User_Password = objJsonUser.getString("User_Password").toString();
			String User_Imagepath = objJsonUser.getString("User_Imagepath").toString();
			String User_Email = objJsonUser.getString("User_Email").toString();
			String User_CountryID = objJsonUser.getString("User_CountryID").toString();
			String User_City = objJsonUser.getString("User_City").toString();
			String User_Gender = objJsonUser.getString("User_Gender").toString();
			String User_Age = objJsonUser.getString("User_Age").toString();
			String User_AgeRange = objJsonUser.getString("User_AgeRange").toString();
			String User_Desc = objJsonUser.getString("User_Desc").toString();
			String User_PushNotification = objJsonUser.getString("User_PushNotification").toString();
			String User_Location = objJsonUser.getString("User_Location").toString();
			String User_Badge = objJsonUser.getString("User_Badge").toString();
			String User_Status = objJsonUser.getString("User_Status").toString();
			String User_IsPrivate = objJsonUser.getString("User_IsPrivate").toString();
			String User_IsActive = objJsonUser.getString("User_IsActive").toString();
			String User_LastSession = objJsonUser.getString("User_LastSession").toString();
			String User_Date = objJsonUser.getString("User_Date").toString();
			String PostCount = objJsonUser.getString("PostCount").toString();

			/*** Post **/
			String Count_Rating = objRefreshData.getString("Count_Rating").toString();
			String Sum_Rating = objRefreshData.getString("Sum_Rating").toString();
			String Average_Rating = objRefreshData.getString("Average_Rating").toString();
			String CommentsCount = objRefreshData.getString("CommentsCount").toString();
			String User_Rated = objRefreshData.getString("User_Rated").toString();

			/** Putting PostData **/
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Position", position);
			map.put("Post_ID", Post_ID);
			map.put("Post_CategoryID", Post_CategoryID);
			map.put("Post_Title", Post_Title);
			map.put("Post_ImageSquare", Post_ImageSquare);
			map.put("Post_Location", Post_Location);
			map.put("Post_Date", Post_Date);
			map.put("Post_Tags", Post_Tags);
			map.put("Post_Latitude", Post_Latitude);
			map.put("Post_Longitude", Post_Longitude);
			map.put("Post_RecentlyModifiedDate", Post_RecentlyModifiedDate);
			map.put("Post_VideoURL", Post_VideoURL);
			map.put("Post_IsVideo", Post_IsVideo);
			map.put("Post_Content", Post_Content);
			map.put("User_ID", User_ID);
			map.put("User_FirstName", User_FirstName);
			map.put("User_LastName", User_LastName);
			map.put("User_Name", User_Name);
			map.put("User_Password", User_Password);
			map.put("User_Imagepath", User_Imagepath);
			map.put("User_Email", User_Email);
			map.put("User_CountryID", User_CountryID);
			map.put("User_City", User_City);
			map.put("User_Gender", User_Gender);
			map.put("User_Imagepath", User_Imagepath);
			map.put("User_Age", User_Age);
			map.put("User_AgeRange", User_AgeRange);
			map.put("User_Desc", User_Desc);
			map.put("User_PushNotification", User_PushNotification);
			map.put("User_Location", User_Location);
			map.put("User_Badge", User_Badge);
			map.put("User_Status", User_Status);
			map.put("User_IsPrivate", User_IsPrivate);
			map.put("User_IsActive", User_IsActive);
			map.put("User_LastSession", User_LastSession);
			map.put("User_Date", User_Date);
			map.put("Count_Rating", Count_Rating);
			map.put("Sum_Rating", Sum_Rating);
			map.put("PostCount", PostCount);
			map.put("Average_Rating", Average_Rating);
			map.put("CommentsCount", CommentsCount);
			map.put("User_Rated", User_Rated);

			// System.out.println("myFeedPostData-->"+myFeedPostData);
			/** Comments **/
			JSONArray Comment = objRefreshData.getJSONArray("Comments");
			// System.out.println("JsonComment-->"+Comment);

			if (Comment.length() > 0) {
				for (int j = 0; j < Comment.length(); j++) {
					JSONObject objComment = Comment.getJSONObject(j);
					HashMap<String, String> map1 = new HashMap<String, String>();
					map1.put("Comment_ID", objComment.getString("Comment_ID"));
					map1.put("Comment_PostID", objComment.getString("Comment_PostID"));
					map1.put("Comment_UserID", objComment.getString("Comment_UserID"));
					map1.put("Comment_Content", objComment.getString("Comment_Content"));
					map1.put("Comment_Approved", objComment.getString("Comment_Approved"));
					map1.put("Comment_Date", objComment.getString("Comment_Date"));
					map1.put("User_ID", objComment.getString("User_ID"));
					map1.put("User_FirstName", objComment.getString("User_FirstName"));
					map1.put("User_LastName", objComment.getString("User_LastName"));
					map1.put("User_Name", objComment.getString("User_Name"));
					map1.put("User_Imagepath", objComment.getString("User_Imagepath"));
					map1.put("User_Email", objComment.getString("User_Email"));
					map1.put("User_CountryID", objComment.getString("User_CountryID"));
					map1.put("User_City", objComment.getString("User_City"));
					map1.put("User_Gender", objComment.getString("User_Gender"));
					map1.put("User_Age", objComment.getString("User_Age"));
					map1.put("User_AgeRange", objComment.getString("User_AgeRange"));
					map1.put("User_Desc", objComment.getString("User_Desc"));
					map1.put("User_PushNotification", objComment.getString("User_PushNotification"));
					map1.put("User_Location", objComment.getString("User_Location"));
					map1.put("User_Badge", objComment.getString("User_Badge"));
					map1.put("User_Status", objComment.getString("User_Status"));
					map1.put("User_IsPrivate", objComment.getString("User_IsPrivate"));
					map1.put("User_IsActive", objComment.getString("User_IsActive"));
					map1.put("User_LastSession", objComment.getString("User_LastSession"));
					map1.put("User_Date", objComment.getString("User_Date"));
					myFeedCommentsData1.add(map1);
				}
			}

			/** Rate **/
			JSONArray Rate = objRefreshData.getJSONArray("Rate");
			// System.out.println("Rate-->"+Rate);
			if (Rate.length() > 0) {
				for (int k = 0; k < Rate.length(); k++) {
					JSONObject objRate = Rate.getJSONObject(k);
					HashMap<String, String> map1 = new HashMap<String, String>();
					map1.put("User_ID", objRate.getString("User_ID"));
					map1.put("User_FirstName", objRate.getString("User_FirstName"));
					map1.put("User_LastName", objRate.getString("User_LastName"));
					map1.put("User_Name", objRate.getString("User_Name"));
					map1.put("User_Password", objRate.getString("User_Password"));
					map1.put("User_Imagepath", objRate.getString("User_Imagepath"));
					map1.put("User_Email", objRate.getString("User_Email"));
					map1.put("User_CountryID", objRate.getString("User_CountryID"));
					map1.put("User_City", objRate.getString("User_City"));
					map1.put("User_Gender", objRate.getString("User_Gender"));
					map1.put("User_Age", objRate.getString("User_Age"));
					map1.put("User_AgeRange", objRate.getString("User_AgeRange"));
					map1.put("User_Desc", objRate.getString("User_Desc"));
					map1.put("User_PushNotification", objRate.getString("User_PushNotification"));
					map1.put("User_Location", objRate.getString("User_Location"));
					map1.put("User_Badge", objRate.getString("User_Badge"));
					map1.put("User_Status", objRate.getString("User_Status"));
					map1.put("User_IsPrivate", objRate.getString("User_IsPrivate"));
					map1.put("User_IsActive", objRate.getString("User_IsActive"));
					map1.put("User_LastSession", objRate.getString("User_LastSession"));
					map1.put("User_Date", objRate.getString("User_Date"));
					map1.put("Rating_ID", objRate.getString("Rating_ID"));
					map1.put("Rating_PostID", objRate.getString("Rating_PostID"));
					map1.put("Rating_UserID", objRate.getString("Rating_UserID"));
					map1.put("Rating_Rate", objRate.getString("Rating_Rate"));
					map1.put("Rating_Date", objRate.getString("Rating_Date"));

					myFeedRateData1.add(map1);
				}
			}
			map.put("flag", false);
			map.put("Rate", myFeedRateData1);
			map.put("Comments", myFeedCommentsData1);
			myFeedPostData.set(postIndex, map);
			Log.d("TAG", "post index " + postIndex);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		customAdapter.notifyDataSetChanged();

	}
}
