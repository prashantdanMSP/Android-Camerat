package com.camrate;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.MyGridView;
import com.camrate.global.checkInternet;
import com.camrate.profile.EditProfile;
import com.camrate.profile.Followers;
import com.camrate.profile.Following;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.profile.Notification;
import com.camrate.profile.UserProfile;
import com.camrate.settings.SettingsActivity;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class UserProfileChild extends Activity {
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONArray objUSerDetail;
	String UserParse;
	Constant c = new Constant();
	checkInternet chknet;
	String UserID = "", Checked_ID = "";
	int width, height;
	Boolean isLoading;
	int startIndex;
	int RatingRate = 0;
	ArrayList<HashMap<String, String>> UserProfileData;
	ImageView imgUserProfile, imgUserLevel, userInvestorBadge;
	TextView txtUserName, txtUserLocation, txtUserStatus, txtUserRates, txtUserFollowings, txtUserFollowers, txtUserReRates, txtUserNotification;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options, options1;
	String No_of_Following, No_of_Followers, Rates_Count, Rerate_Count, UserBadge;
	Button btnDislike, btnWhatever, btnHate, btnLike, btnLove, btnNone;

	RelativeLayout.LayoutParams paramImage, paramImage1, paramImage2;
	ArrayList<HashMap<String, Object>> TagPost;
	ArrayList<HashMap<String, String>> TagPostCommentsData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> TagPostRateData = new ArrayList<HashMap<String, String>>();

	ImageAdapter objImageAdapter;
	AbsListView listView;
	String NewestParse;
	String SearchUser_ID = "";
	TextView tv14;
	String User_Name = "", User_LastName = "", User_Desc = "", User_Gender = "", User_Age = "", Country_Name = "", User_ImagePath = "", User_City = "", User_FirstName = "";
	TextView txt_ErrorShow;
	RelativeLayout rel_Error;
	int ProfilePercentage = 0, Per_First_Name = 0, Per_Last_Name = 0, Per_Profpic = 0, Per_city = 0, Per_cntry = 0, Per_desc = 0, Per_age = 0, Per_gen = 0;

	boolean isrerate = false;
	public static final String TAG = UserProfileChild.class.getSimpleName();
	TextView txtFilterBy;
	int ACITIVITY_RESULT_POSTDETAIL = 1, ACITIVITY_RESULT_FOLLOWING = 2, ACITIVITY_RESULT_FOLLOWERS = 3;
	LinearLayout linear_posts, linear_rates;
	int postIndex;
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		init();

		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (mPullRefreshScrollView.getCurrentMode() == Mode.PULL_FROM_START) {

					startIndex = 0;
					TagPost.clear();
					if (chknet.isNetworkConnected()) {

						new GetUserDetail().execute();

						if (isrerate) {
							new GetPrivateGalleryRate().execute();
						} else {
							new GetPrivateGalleryPost().execute();
						}

						new getNotification().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				} else if (mPullRefreshScrollView.getCurrentMode() == Mode.PULL_FROM_END) {

					if (chknet.isNetworkConnected()) {

						if (isrerate) {
							new GetPrivateGalleryRate().execute();
						} else {
							new GetPrivateGalleryPost().execute();
						}

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				}

			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();

		if (chknet.isNetworkConnected()) {
			TagPost.clear();
			new GetUserDetail().execute();
			new GetPrivateGalleryPost().execute();
			new getNotification().execute();
		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();

		}

	}

	public void init() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Constant.arrActivity.add(UserProfileChild.this);
		initProcessDialog();
		TagPost = new ArrayList<HashMap<String, Object>>();
		objImageAdapter = new ImageAdapter(TagPost);
		tv14 = (TextView) findViewById(R.id.textView1);
		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button btnEditProfile = (Button) findViewById(R.id.btnEditProfile);
		Button Settings = (Button) findViewById(R.id.button1);
		Settings.setVisibility(View.VISIBLE);
		// Settings.setTypeface(SplashScreen.ProxiNova_Regular, Typeface.BOLD);

		imgUserProfile = (ImageView) findViewById(R.id.imgUserProfilePic);
		txtUserName = (TextView) findViewById(R.id.txtValueProfileUserName);
		txtUserLocation = (TextView) findViewById(R.id.txtValueProfileUserLocation);
		txtUserStatus = (TextView) findViewById(R.id.txtValueProfileUserStatus);
		txtUserRates = (TextView) findViewById(R.id.txtValueRates);
		txtUserFollowings = (TextView) findViewById(R.id.txtValueFollowing);
		txtUserReRates = (TextView) findViewById(R.id.txtValueReRates);
		txtUserFollowers = (TextView) findViewById(R.id.txtValueFollowers);
		txtUserNotification = (TextView) findViewById(R.id.txtValueNotification);
		txtFilterBy = (TextView) findViewById(R.id.txtlblFilter);
		chknet = new checkInternet(UserProfileChild.this);
		btnHate = (Button) findViewById(R.id.btnProfileHate);
		btnDislike = (Button) findViewById(R.id.btnProfileDislike);
		btnWhatever = (Button) findViewById(R.id.btnProfileWhatever);
		btnLike = (Button) findViewById(R.id.btnProfileLike);
		btnLove = (Button) findViewById(R.id.btnProfileLove);
		btnNone = (Button) findViewById(R.id.btnProfileNone);
		btnHate.setOnClickListener(rateclicklister);
		btnDislike.setOnClickListener(rateclicklister);
		btnLike.setOnClickListener(rateclicklister);
		btnLove.setOnClickListener(rateclicklister);
		btnNone.setOnClickListener(rateclicklister);
		btnWhatever.setOnClickListener(rateclicklister);
		linear_posts = (LinearLayout) findViewById(R.id.linear_posts);
		linear_rates = (LinearLayout) findViewById(R.id.linear_rates);
		listView = (MyGridView) findViewById(R.id.gridview);
		imgUserLevel = (ImageView) findViewById(R.id.imgValueLevel);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		// userInvestorBadge=(ImageView)findViewById(R.id.imginvestorbadge);
		rel_Error = (RelativeLayout) findViewById(R.id.relative1);
		txt_ErrorShow = (TextView) findViewById(R.id.txtErrorShow);

		/*
		 * Display display = getWindowManager().getDefaultDisplay(); Point size = new Point(); display.getSize(size); width = size.x; height = size.y; paramImage = new LayoutParams((width - 360) / 7, (width - 360) / 7); paramImage.setMargins((width - 360) / 7, 5, 0, 0);
		 * 
		 * btnDislike.setLayoutParams(paramImage); btnLike.setLayoutParams(paramImage); btnLove.setLayoutParams(paramImage); btnNone.setLayoutParams(paramImage); btnWhatever.setLayoutParams(paramImage); btnHate.setLayoutParams(paramImage);
		 */
		Display display = getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		System.out.println("Width--->" + width);
		System.out.println("Height--->" + height);
		System.out.println("IMageview Height and width--->" + (width - 20) / 3);
		paramImage = new RelativeLayout.LayoutParams((width - 20) / 3, (width - 20) / 3);
		paramImage1 = new RelativeLayout.LayoutParams((width - 20) / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		paramImage2 = new RelativeLayout.LayoutParams((width - 20) / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		paramImage1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		int round = (width - 20) / 3;

		imageLoader.init(ImageLoaderConfiguration.createDefault(UserProfileChild.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).resetViewBeforeLoading(true).build();
		options1 = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.imageloadingnew).showImageForEmptyUri(R.drawable.imageloadingnew).showImageOnFail(R.drawable.imageloadingnew).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).resetViewBeforeLoading(true).build();
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");
		// UserID = "109";
		User_FirstName = prefs.getString("User_FirstName", "");
		User_Name = prefs.getString("User_Name", "");
		User_LastName = prefs.getString("User_LastName", "");
		User_Desc = prefs.getString("User_Desc", "");
		User_Gender = prefs.getString("User_Gender", "");
		User_Age = prefs.getString("User_Age", "");
		User_ImagePath = prefs.getString("User_Imagepath", "");
		User_City = prefs.getString("User_City", "");
		Country_Name = prefs.getString("User_CountryID", "");
		/*
		 * System.out.println("User_FirstName" + User_FirstName); System.out.println("User_LastName-->" + User_LastName); System.out.println("User_Desc-->" + User_Desc); System.out.println("User_Gender-->" + User_Gender); System.out.println("User_Age-->" + User_Age);
		 * System.out.println("Country_Name-->" + Country_Name); System.out.println("User_City-->" + User_City); System.out.println("User_ImagePath-->" + User_ImagePath);
		 */
		// GetProfilePercentage(User_ImagePath, User_FirstName, User_LastName, User_Desc, User_Age, User_Gender, Country_Name, User_City);

		btnEditProfile.setTypeface(SplashScreen.ProxiNova_SemiBold);
		txtFilterBy.setTypeface(SplashScreen.ProxiNova_SemiBold);

		btnEditProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getParent(), EditProfile.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("EditProfile", intent);
			}
		});
		Settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getParent(), SettingsActivity.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("SettingsActivity", intent);
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("TAG", "selected pos" + position);
				if (isrerate) {
					if (TagPost.get(position).get("IsPrivateToViewer").toString().equalsIgnoreCase("1")) {
						SimpleAlert("CamRate", "This user has gone private now.\n Would you like to follow", position);
					} else {
						Intent intent = new Intent(getParent(), PostDetail.class);
						intent.putExtra("PostData", TagPost);
						intent.putExtra("CheckedIndex", position);
						intent.putExtra("SelectedPostData", TagPost.get(position));
						intent.putExtra("TotalCheckedIndex", TagPost.size());
						intent.putExtra("progress", 0);
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startChildActivity("PostDetail", intent);
					}
				} else {
					Intent intent = new Intent(getParent(), PostDetail.class);
					intent.putExtra("PostData", TagPost);
					intent.putExtra("CheckedIndex", position);
					intent.putExtra("SelectedPostData", TagPost.get(position));
					intent.putExtra("TotalCheckedIndex", TagPost.size());
					intent.putExtra("progress", 0);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("PostDetail", intent);
				}

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivityManage.lastSeletedTab = 4;
		if (ActivityManage.isFromPostDetail) {

			try {

				if (ActivityManage.isForDelete) {
					TagPost.remove(ActivityManage.deletePostion);
					objImageAdapter.notifyDataSetChanged();
				}
			} catch (Exception e) {

			}
		}

		if (chknet.isNetworkConnected()) {
			new GetUserDetail().execute();
			new getNotification().execute();
		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		c.TagmyPref = getSharedPreferences(c.TAG_PREF, Context.MODE_WORLD_WRITEABLE);
		Editor edt = c.TagmyPref.edit();
		edt.putString(c.TAG_KEY, TAG);
		edt.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public class GetUserDetail extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url = c.GetBaseUrl() + "api=ProfileDetail&User_ID=" + UserID + "&start=0&end=12&totalrec=9";
			Log.d("Tag", "url " + url);
			UserProfileData = new ArrayList<HashMap<String, String>>();
			try {
				UserParse = parser.getStringFromUrl(url);
				// System.out.println("myFeedParseJson-->"+myFeedParse);
				// System.out.println("myFeedParseJson length-->"+myFeedParse.length());
				JSONObject jsonUser = new JSONObject(UserParse);
				for (int s = 0; s < jsonUser.length(); s++) {
					JSONObject objJsonUser = jsonUser.getJSONObject("User");
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
					String User_Birthdate = objJsonUser.getString("User_Birthdate").toString();
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

					HashMap<String, String> map = new HashMap<String, String>();
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
					map.put("User_Birthdate", User_Birthdate);
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
					UserProfileData.add(map);

					No_of_Following = jsonUser.getString("No_of_Following").toString();
					No_of_Followers = jsonUser.getString("No_of_Followers").toString();
					Rates_Count = jsonUser.getString("Rates_Count").toString();
					Rerate_Count = jsonUser.getString("Rerate_Count").toString();

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return UserProfileData;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// pd.dismiss();
			if (result.size() == 0) {
				Toast.makeText(getApplicationContext(), "No More Post To Load", Toast.LENGTH_SHORT).show();
			} else {
				setProfileData(result);

			}
		}

	}

	public void setProfileData(ArrayList<HashMap<String, String>> result) {
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		SharedPreferences.Editor edit1 = prefs.edit();
		edit1.putString("UserID", result.get(0).get("User_ID"));
		edit1.putString("User_Imagepath", result.get(0).get("User_Imagepath"));
		edit1.putString("User_Email", result.get(0).get("User_Email"));
		edit1.putString("User_Name", result.get(0).get("User_Name"));
		edit1.putString("User_FirstName", result.get(0).get("User_FirstName"));
		edit1.putString("User_LastName", result.get(0).get("User_LastName"));
		edit1.putString("User_CountryID", result.get(0).get("User_CountryID"));
		edit1.putString("User_City", result.get(0).get("User_City"));
		edit1.putString("User_Gender", result.get(0).get("User_Gender"));
		edit1.putString("User_Birthdate", result.get(0).get("User_Birthdate"));
		edit1.putString("User_Age", result.get(0).get("User_Age"));
		edit1.putString("User_AgeRange", result.get(0).get("User_AgeRange"));
		edit1.putString("User_Desc", result.get(0).get("User_Desc"));
		edit1.putString("User_PushNotification", result.get(0).get("User_PushNotification"));
		edit1.putString("User_Location", result.get(0).get("User_Location"));
		edit1.putString("User_Badge", result.get(0).get("User_Badge"));
		edit1.putString("User_Status", result.get(0).get("User_Status"));
		edit1.putString("User_IsPrivate", result.get(0).get("User_IsPrivate"));
		edit1.putString("User_IsActive", result.get(0).get("User_IsActive"));
		edit1.putString("User_LastSession", result.get(0).get("User_LastSession"));
		edit1.putString("User_Date", result.get(0).get("User_Date"));
		edit1.commit();

		imageLoader.displayImage(result.get(0).get("User_Imagepath"), imgUserProfile, options);
		tv14.setText(result.get(0).get("User_Name"));
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Checked_ID = result.get(0).get("User_ID");

		String User_FirstName = result.get(0).get("User_FirstName");
		String User_LastName = result.get(0).get("User_LastName");
		if (User_FirstName.length() > 0 && User_LastName.length() > 0) {
			txtUserName.setText(User_FirstName + " " + User_LastName);
			txtUserName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		} else {
			txtUserName.setText(result.get(0).get("User_Name"));
			txtUserName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		}
		String cntry_name = getCountryName(result.get(0).get("User_CountryID"));
		String city_name = result.get(0).get("User_City");
		if (cntry_name != null && cntry_name.length() > 0 && city_name != null && city_name.length() > 0) {
			txtUserLocation.setText(city_name + ", " + cntry_name);
		} else if (cntry_name != null && cntry_name.length() > 0) {
			txtUserLocation.setText(cntry_name);
		} else if (city_name != null && city_name.length() > 0) {
			txtUserLocation.setText(city_name);
		} else {
			txtUserLocation.setText("");
		}
		txtUserLocation.setTypeface(SplashScreen.ProxiNova_Regular);
		txtUserStatus.setText(result.get(0).get("User_Desc"));
		txtUserRates.setText(Rates_Count + "\n" + "Posts");
		txtUserReRates.setText(Rerate_Count + "\n" + "Rates");
		txtUserFollowers.setText(No_of_Followers + "\n" + "Followers");
		txtUserFollowings.setText(No_of_Following + "\n" + "Following");

		txtUserRates.setTypeface(SplashScreen.ProxiNova_SemiBold, Typeface.BOLD);
		txtUserReRates.setTypeface(SplashScreen.ProxiNova_SemiBold, Typeface.BOLD);
		txtUserFollowings.setTypeface(SplashScreen.ProxiNova_SemiBold, Typeface.BOLD);
		txtUserFollowers.setTypeface(SplashScreen.ProxiNova_SemiBold, Typeface.BOLD);
		txtUserNotification.setTypeface(SplashScreen.ProxiNova_SemiBold, Typeface.BOLD);

		txtUserRates.setOnClickListener(mclickListner);
		txtUserReRates.setOnClickListener(mclickListner);
		txtUserFollowings.setOnClickListener(mclickListner);
		txtUserFollowers.setOnClickListener(mclickListner);
		txtUserNotification.setOnClickListener(mclickListner);
		imgUserLevel.setOnClickListener(mclickListner);

	}

	public String getCountryName(String Country_ID) {
		int country_id = Integer.parseInt(Country_ID);
		DataBaseAdapter mDbHelper = new DataBaseAdapter(getApplicationContext());
		try {
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		String country_Name = mDbHelper.getCountryName(country_id);
		return country_Name;
	}

	static class ViewHolder {
		ImageView imageViewRate, imageViewPost, imageViewGradient, imgViewPostVideo;
		ProgressBar progressBar;
		TextView txtRatelbl, txtRateCount;
		RelativeLayout relGradient;
	}

	public OnClickListener rateclicklister = new OnClickListener() {

		@Override
		public void onClick(View v) {

			startIndex = 0;
			TagPost.clear();

			switch (v.getId()) {
			case R.id.btnProfileLove:
				RatingRate = 5;
				if (isrerate) {
					startIndex = 0;
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryRate().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				} else {

					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryPost().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				}
				btnLove.setBackgroundResource(R.drawable.filter_5_sel);
				btnLike.setBackgroundResource(R.drawable.filter_4);
				btnWhatever.setBackgroundResource(R.drawable.filter_3);
				btnDislike.setBackgroundResource(R.drawable.filter_2);
				btnHate.setBackgroundResource(R.drawable.filter_1);
				btnNone.setBackgroundResource(R.drawable.none);
				break;

			case R.id.btnProfileLike:
				RatingRate = 4;
				if (isrerate) {
					// callrerate Api
					// System.out.println("Rerate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryRate().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					// call rate api
					// System.out.println("Rate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryPost().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

				btnLove.setBackgroundResource(R.drawable.filter_5);
				btnLike.setBackgroundResource(R.drawable.filter_4_sel);
				btnWhatever.setBackgroundResource(R.drawable.filter_3);
				btnDislike.setBackgroundResource(R.drawable.filter_2);
				btnHate.setBackgroundResource(R.drawable.filter_1);
				btnNone.setBackgroundResource(R.drawable.none);
				break;
			case R.id.btnProfileWhatever:
				RatingRate = 3;
				if (isrerate) {
					// callrerate Api
					// System.out.println("Rerate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryRate().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					// call rate api
					// System.out.println("Rate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryPost().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

				btnLove.setBackgroundResource(R.drawable.filter_5);
				btnLike.setBackgroundResource(R.drawable.filter_4);
				btnWhatever.setBackgroundResource(R.drawable.filter_3_sel);
				btnDislike.setBackgroundResource(R.drawable.filter_2);
				btnHate.setBackgroundResource(R.drawable.filter_1);
				btnNone.setBackgroundResource(R.drawable.none);
				break;
			case R.id.btnProfileDislike:
				RatingRate = 2;
				if (isrerate) {
					// callrerate Api
					// System.out.println("Rerate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryRate().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					// call rate api
					// System.out.println("Rate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryPost().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

				btnLove.setBackgroundResource(R.drawable.filter_5);
				btnLike.setBackgroundResource(R.drawable.filter_4);
				btnWhatever.setBackgroundResource(R.drawable.filter_3);
				btnDislike.setBackgroundResource(R.drawable.filter_2_sel);
				btnHate.setBackgroundResource(R.drawable.filter_1);
				btnNone.setBackgroundResource(R.drawable.none);
				break;

			case R.id.btnProfileHate:
				RatingRate = 1;
				if (isrerate) {
					// callrerate Api
					// System.out.println("Rerate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryRate().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				} else {
					// call rate api
					// System.out.println("Rate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryPost().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

				btnLove.setBackgroundResource(R.drawable.filter_5);
				btnLike.setBackgroundResource(R.drawable.filter_4);
				btnWhatever.setBackgroundResource(R.drawable.filter_3);
				btnDislike.setBackgroundResource(R.drawable.filter_2);
				btnHate.setBackgroundResource(R.drawable.filter_1_sel);
				btnNone.setBackgroundResource(R.drawable.none);
				break;
			case R.id.btnProfileNone:
				RatingRate = 0;
				if (isrerate) {
					// callrerate Api
					// System.out.println("Rerate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryRate().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

				else {
					// call rate api
					// System.out.println("Rate");
					if (chknet.isNetworkConnected()) {
						new GetPrivateGalleryPost().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}

				btnLove.setBackgroundResource(R.drawable.filter_5);
				btnLike.setBackgroundResource(R.drawable.filter_4);
				btnWhatever.setBackgroundResource(R.drawable.filter_3);
				btnDislike.setBackgroundResource(R.drawable.filter_2);
				btnHate.setBackgroundResource(R.drawable.filter_1);
				btnNone.setBackgroundResource(R.drawable.none);
				break;
			}
		}
	};

	public OnClickListener mclickListner = new OnClickListener() {

		@Override
		public void onClick(View v) throws NullPointerException {
			// TODO Auto-generated method stub
			int colorClick = Color.parseColor("#ffffff");
			int colorUnClick = Color.parseColor("#7d7d7d");
			Intent intent;
			switch (v.getId()) {
			case R.id.txtValueRates:
				TagPost.clear();
				startIndex = 0;
				isrerate = false;
				txtUserRates.setTextColor(colorClick);
				txtUserReRates.setTextColor(colorUnClick);

				linear_posts.setBackgroundColor(getResources().getColor(R.color.Theme_Color));
				linear_rates.setBackgroundColor(getResources().getColor(R.color.lbl_profile_ratebg));
				if (chknet.isNetworkConnected()) {
					new GetPrivateGalleryPost().execute();
					System.out.println("Click...");
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.txtValueFollowing:

				intent = new Intent(getParent(), Following.class);
				intent.putExtra("Following_Type", "0");
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Following", intent);
				break;
			case R.id.txtValueFollowers:
				intent = new Intent(getParent(), Followers.class);
				intent.putExtra("Followers_Type", "0");
				TabGroupActivity parentActivity1 = (TabGroupActivity) getParent();
				parentActivity1.startChildActivity("Followers", intent);
				break;
			case R.id.txtValueReRates:
				TagPost.clear();
				startIndex = 0;
				isrerate = true;
				txtUserRates.setTextColor(colorUnClick);
				txtUserReRates.setTextColor(colorClick);

				linear_posts.setBackgroundColor(getResources().getColor(R.color.lbl_profile_ratebg));
				linear_rates.setBackgroundColor(getResources().getColor(R.color.Theme_Color));
				if (chknet.isNetworkConnected()) {
					new GetPrivateGalleryRate().execute("");
					System.out.println("Click...");
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
				break;

			case R.id.txtValueNotification:
				intent = new Intent(getParent(), Notification.class);
				TabGroupActivity parentActivity2 = (TabGroupActivity) getParent();
				parentActivity2.startChildActivity("Notification", intent);
				break;

			}
		}
	};

	public class GetPrivateGalleryPost extends AsyncTask<String, Void, ArrayList<HashMap<String, Object>>> {

		@Override
		protected void onPreExecute() {

			isLoading = true;

			showDialog();
		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(String... params) {

			String url = c.GetBaseUrl() + "api=PrivateGallery&User_ID=" + UserID + "&Rating_Rate=" + RatingRate + "&start=" + startIndex + "&end=15&totalrec=15&View_User=" + UserID;
			Log.d("Tag", "url " + url);

			try {
				NewestParse = parser.getStringFromUrl(url);
				JSONArray jsonMyFeed = new JSONArray(NewestParse);
				if (jsonMyFeed.length() > 0) {

					startIndex += 15;

					for (int s = 0; s < jsonMyFeed.length(); s++) {
						ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
						ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
						JSONObject myFeedData = jsonMyFeed.getJSONObject(s);
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
						String Rating_Rate = myFeedData.getString("Rating_Rate").toString();

						/*** UserDetail ***********/
						JSONObject objJsonUser = myFeedData.getJSONObject("User");
						// System.out.println("user-->" + objJsonUser);
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
						map.put("Rating_Rate", Rating_Rate);
						map.put("Average_Rating", Average_Rating);
						map.put("CommentsCount", CommentsCount);
						map.put("User_Rated", User_Rated);

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
						TagPost.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("TAG", "TAG Post ARRAY SIze " + TagPost.size());
			return TagPost;
		}

		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, Object>> menuItems) {

			mPullRefreshScrollView.onRefreshComplete();
			dismissDialog();

			isLoading = false;
			setTheAdapterWithArray();
		}
	}

	public void setTheAdapterWithArray() {

		listView.setVisibility(View.VISIBLE);

		if (objImageAdapter.getCount() <= 15) {
			objImageAdapter = new ImageAdapter(TagPost);
			((GridView) listView).invalidateViews();
			((GridView) listView).setAdapter(objImageAdapter);
			mScrollView.smoothScrollTo(0, 0);
			System.out.println("Adapter First===");
		} else {
			objImageAdapter.notifyDataSetChanged();
			System.out.println("Adapter secondTime");
		}

	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, Object>> items;

		ImageAdapter(ArrayList<HashMap<String, Object>> items) {
			this.items = items;
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return items.size();

		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.explorer_pager_list, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageViewRate = (ImageView) view.findViewById(R.id.imagerate);
				holder.imageViewGradient = (ImageView) view.findViewById(R.id.imagegradient);
				holder.imageViewPost = (ImageView) view.findViewById(R.id.imguserpost);
				holder.imageViewPost.setId(position);
				holder.imgViewPostVideo = (ImageView) view.findViewById(R.id.imguserpostvideo);
				holder.relGradient = (RelativeLayout) view.findViewById(R.id.rel_Gradient);
				holder.imageViewPost.setLayoutParams(paramImage);

				paramImage2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				// holder.imageViewGradient.setLayoutParams(paramImage2);

				// holder.imageViewRate.setLayoutParams(paramImage);
				holder.imageViewPost.setTag(new Integer(position));
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.txtRatelbl = (TextView) view.findViewById(R.id.txtratelbl);
				holder.txtRateCount = (TextView) view.findViewById(R.id.txtratecountlbl);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (holder.imageViewPost != null) {
				// imageLoader.displayImage(items.get(position).get("Post_ImageSquare").toString(), holder.imageViewPost, options1);
				String User_IsPrivate = items.get(position).get("User_IsPrivate").toString();
				System.out.println("User_IsPrivate==>" + User_IsPrivate);

				// if (isrerate) {
				// if (User_IsPrivate.equalsIgnoreCase("1")) {
				// holder.imageViewPost.setImageResource(R.drawable.lockicon);
				// } else {
				// imageLoader.displayImage(items.get(position).get("Post_ImageSquare").toString(), holder.imageViewPost, options1);
				// }
				// } else {
				// imageLoader.displayImage(items.get(position).get("Post_ImageSquare").toString(), holder.imageViewPost, options1);
				// }
				if (isrerate) {
					if (items.get(position).get("IsPrivateToViewer").toString().equalsIgnoreCase("1")) {
						holder.imageViewPost.setImageResource(R.drawable.lockicon);
					} else {
						imageLoader.displayImage(items.get(position).get("Post_ImageSquare").toString(), holder.imageViewPost, options1);
					}
				} else {
					imageLoader.displayImage(items.get(position).get("Post_ImageSquare").toString(), holder.imageViewPost, options1);
				}
			}

			if (holder.imgViewPostVideo != null) {

				String video = (items.get(position).get("Post_IsVideo").toString());
				// String
				// Video_Url=(items.get(position).get("Post_VideoURL").toString());
				if (video.equalsIgnoreCase("1")) {
					holder.imgViewPostVideo.setVisibility(View.VISIBLE);
				} else {
					holder.imgViewPostVideo.setVisibility(View.INVISIBLE);
				}

			}
			if (holder.imageViewRate != null) {
				int ratcount;
				if (isrerate) {
					ratcount = Integer.valueOf(items.get(position).get("Rating_RateMain").toString());
				} else {
					ratcount = Integer.valueOf(items.get(position).get("Rating_Rate").toString());
				}
				switch (ratcount) {
				case 5:
					// holder.txtRatelbl.setText("LOVE");
					holder.imageViewRate.setImageResource(R.drawable.five_star_grid);
					break;
				case 4:
					// holder.txtRatelbl.setText("LIKE");
					holder.imageViewRate.setImageResource(R.drawable.four_star_grid);
					break;
				case 3:
					// holder.txtRatelbl.setText("WHATEVER");
					holder.imageViewRate.setImageResource(R.drawable.three_star_grid);
					break;
				case 2:
					// holder.txtRatelbl.setText("DISLIKE");
					holder.imageViewRate.setImageResource(R.drawable.two_star_grid);
					break;
				case 1:
					// holder.txtRatelbl.setText("HATE");
					holder.imageViewRate.setImageResource(R.drawable.one_star_grid);
					break;
				}

			}
			if (holder.txtRateCount != null) {
				String count_rate = items.get(position).get("Count_Rating").toString();
				String rating = "";
				if (count_rate.equalsIgnoreCase("1")) {
					rating = count_rate + " Rate";
				} else {
					rating = count_rate + " Rates";
				}
				holder.txtRateCount.setText(rating);
				holder.txtRateCount.setTypeface(SplashScreen.ProxiNova_Regular);

			}

			return view;
		}
	}

	public class GetPrivateGalleryRate extends AsyncTask<String, Void, ArrayList<HashMap<String, Object>>> {

		@Override
		protected void onPreExecute() {

			isLoading = true;
			showDialog();

		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(String... params) {

			String url = c.GetBaseUrl() + "api=ReRatePost2&User_ID=" + UserID + "&Rating_Rate=" + RatingRate + "&start=" + startIndex + "&end=15&totalrec=15&View_User=" + UserID;
			Log.d("TAG", "Url Rerate " + url);
			try {
				NewestParse = parser.getStringFromUrl(url);
				JSONArray jsonMyFeed = new JSONArray(NewestParse);

				if (jsonMyFeed.length() > 0) {

					startIndex += 15;
					for (int s = 0; s < jsonMyFeed.length(); s++) {
						ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
						ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
						JSONObject myFeedData = jsonMyFeed.getJSONObject(s);
						/*** Post **/
						String Post_ID = myFeedData.getString("Post_ID").toString();
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
						String Rating_Rate = myFeedData.getString("Rating_Rate").toString();
						String Rating_RateMain = myFeedData.getString("Rating_RateMain").toString();
						String IsPrivateToViewer = myFeedData.getString("IsPrivateToViewer").toString();
						/*** UserDetail ***********/

						JSONObject objJsonUser = myFeedData.getJSONObject("User");
						// System.out.println("user-->" + objJsonUser);
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
						map.put("Rating_Rate", Rating_Rate);
						map.put("Rating_RateMain", Rating_RateMain);
						map.put("IsPrivateToViewer", IsPrivateToViewer);
						map.put("CommentsCount", CommentsCount);
						map.put("User_Rated", User_Rated);
						// TagPost.add(map);
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
						map.put("Rate", myFeedRateData1);
						map.put("Comments", myFeedCommentsData1);
						TagPost.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("TAG", "TAGPOST array " + TagPost.size());
			return TagPost;
		}

		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, Object>> menuItems) {
			dismissDialog();
			mPullRefreshScrollView.onRefreshComplete();

			isLoading = false;
			setTheAdapterWithArray();
		}
	}

	public void GetProfilePercentage(String ProfilePic, String FirstName, String LastName, String Desc, String Age, String Gender, String Country, String City) {
		if (ProfilePic.length() > 0) {
			Per_Profpic = 20;
		}
		if (ProfilePic.contentEquals("noImageAvailable.jpg")) {
			Per_Profpic = 0;
		}
		if (FirstName.length() > 0) {
			Per_First_Name = 10;
		}
		if (LastName.length() > 0) {
			Per_Last_Name = 10;
		}

		if (Desc.length() > 0) {
			Per_desc = 10;
		}
		if (Gender.length() > 0) {
			Per_gen = 10;
		}
		if (Age.length() > 0) {
			Per_age = 10;
		}
		if (Country.length() > 0) {
			Per_cntry = 15;
		}
		if (City.length() > 0) {
			Per_city = 15;
		}
		ProfilePercentage = Per_Profpic + Per_First_Name + Per_Last_Name + Per_desc + Per_gen + Per_age + Per_cntry + Per_city;
		System.out.println("Profileper-->" + ProfilePercentage);
		System.out.println("Per_First_Name" + Per_First_Name);
		System.out.println("Per_Last_Name-->" + Per_Last_Name);
		System.out.println("Per_desc-->" + Per_desc);
		System.out.println("Per_gen-->" + Per_gen);
		System.out.println("Per_cntry-->" + Per_cntry);
		System.out.println("Per_city-->" + Per_city);

		if (ProfilePercentage > 0) {
			rel_Error.setVisibility(View.VISIBLE);
			txt_ErrorShow.setText("Profile " + ProfilePercentage + "% Complete.Tab here to update!");
			rel_Error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getParent(), EditProfile.class);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("EditProfile", intent);
				}
			});
		} else {
			rel_Error.setVisibility(View.GONE);
		}
		if (ProfilePercentage == 100) {
			rel_Error.setVisibility(View.GONE);
		}
	}

	class getNotification extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			Constant cc = new Constant();
			// http://camrate.it/main/frmlovehateApi.php?api=AcceptRequest&Follow_UserID=337&Follow_Following=109&NotificationID=2913
			String url = cc.GetBaseUrl() + "api=GetNotificationCountgcm&User_ID=" + UserID;
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = parser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result1 = json.getString("count");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result1);

			return Result1;

		}

		@Override
		protected void onPostExecute(String status) {

			txtUserNotification.setText(status + "\n" + "Notifications");
			txtUserNotification.setTypeface(SplashScreen.ProxiNova_Regular);

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// Log.d("TAG", "Activity Result");
	// if (requestCode == 1) {
	//
	// if (data != null) {
	// TagPost.remove(data.getIntExtra("deletePostion", 0));
	// objImageAdapter.notifyDataSetChanged();
	// }
	//
	// }
	//
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("TAG", "Activity Result");
		if (requestCode == 1) {

			if (data != null) {
				try {
					TagPost.remove(data.getIntExtra("deletePostion", 0));
					objImageAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		} else if (requestCode == ACITIVITY_RESULT_FOLLOWING) {
			if (data != null) {
				try {
					Log.d("TAG", "ischange " + data.getBooleanExtra("isChange", false));
					new GetUserDetail().execute();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else if (requestCode == ACITIVITY_RESULT_FOLLOWERS) {
			if (data != null) {
				try {
					Log.d("TAG", "ischange " + data.getBooleanExtra("isChange", false));
					new GetUserDetail().execute();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	public void SimpleAlert(String t, String b, final int position) {
		Builder builder = new AlertDialog.Builder(UserProfileChild.this);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(getParent(), GeneralUserProfile.class);
				intent.putExtra("User_ID", TagPost.get(position).get("User_ID").toString());
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("GeneralUserProfile", intent);
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

}
