package com.camrate.profile;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.tabs.TabGroupActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Following extends Activity {

	private static final String TAG = Following.class.getSimpleName();
	Constant c = new Constant();
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONArray objmyFeedJson;
	String jsonString;
	String UserID, User_ID;
	int startindex = 0;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ArrayList<HashMap<String, String>> FollowingUserData;
	String UnFollow_User_ID, Login_User_ID;
	setFollowingData objUserAdapter;
	JSONParser jparser = new JSONParser();
	String Result = null;
	int checkIsFollow = 0;
	String checkProfileType = "0";
	private PullToRefreshListView mPullRefreshListView;
	ListView listView;
	SharedPreferences prefs;
	boolean isChange;
	int ACITIVITY_RESULT_FOLLOWING = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homefollow);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();
	}

	public void init() {

		imageLoader.init(ImageLoaderConfiguration.createDefault(Following.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Following");
		Button Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.VISIBLE);
		Button Next = (Button) findViewById(R.id.button1);
		Next.setVisibility(View.INVISIBLE);
		FollowingUserData = new ArrayList<HashMap<String, String>>();
		objUserAdapter = new setFollowingData(FollowingUserData);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listMyFeed);
		listView = mPullRefreshListView.getRefreshableView();
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onback();
			}
		});

		Intent intent = getIntent();
		checkProfileType = intent.getStringExtra("Following_Type");
		if (checkProfileType.equalsIgnoreCase("0")) {

			UserID = prefs.getString("UserID", "");
		} else {
			UserID = intent.getStringExtra("User_ID");

		}

		Login_User_ID = prefs.getString("UserID", "");

		System.out.println("UserID--->" + UserID);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		if (isNetworkConnected()) {
			new GetFollowingUser().execute();

		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position - 1;
				if (isAuthor(FollowingUserData.get(position).get("User_ID"))) {

					Intent intent = new Intent(getParent(), UserProfile.class);
					intent.putExtra("User_ID", FollowingUserData.get(position).get("User_ID"));
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("UserProfile", intent);

				} else {
					Intent intent = new Intent(getParent(), GeneralUserProfile.class);
					intent.putExtra("User_ID", FollowingUserData.get(position).get("User_ID"));
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("GeneralUserProfile", intent);
				}

			}
		});

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (mPullRefreshListView.getCurrentMode() == Mode.PULL_FROM_START) {
					FollowingUserData.clear();
					startindex = 0;
					new GetFollowingUser().execute();
				} else {
					new GetFollowingUser().execute();
				}

			}
		});

	}

	private void onback() {

		// if (checkProfileType.equalsIgnoreCase("0")) {
		// Intent intent = new Intent(Followers.this, UserProfile.class);
		// startActivity(intent);
		//
		// } else {
		// Intent intent = new Intent(Followers.this, GeneralUserProfile.class);
		// intent.putExtra("User_ID", UserID);
		// startActivity(intent);
		// }

		Intent intent = new Intent();
		intent.putExtra("isChange", isChange);
		setResult(ACITIVITY_RESULT_FOLLOWING, intent);
		finish();
		overridePendingTransition(0, 0);
	}

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} else
			return true;
	}

	public class GetFollowingUser extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

			String finalUrl = c.GetBaseUrl() + "api=GetFollowing&User_ID=" + UserID + "&View_ID=" + Login_User_ID + "&start=" + startindex + "&end=15";
			System.out.println("url-->" + finalUrl);
			Log.d("TAG", "finalUrl " + finalUrl);
			try {
				jsonString = parser.getStringFromUrl(finalUrl);
				Log.d("TAG", "Json String " + jsonString.toString());
				JSONArray json = new JSONArray(jsonString);
				// System.out.println("JsonArrayLength==>"+json.length());

				if (json.length() > 0) {

					startindex += 15;

					for (int s = 0; s < json.length(); s++) {
						JSONObject object = json.getJSONObject(s);
						String User_ID = object.getString("User_ID");
						String User_FirstName = object.getString("User_FirstName");
						String User_LastName = object.getString("User_LastName");
						String User_Name = object.getString("User_Name");
						String User_Imagepath = object.getString("User_Imagepath");
						String User_Password = object.getString("User_Password");
						String User_Email = object.getString("User_Email");
						String User_CountryID = object.getString("User_CountryID");
						String User_City = object.getString("User_City");
						String User_Gender = object.getString("User_Gender");
						String User_Age = object.getString("User_Age");
						String User_AgeRange = object.getString("User_AgeRange");
						String User_Desc = object.getString("User_Desc");
						String User_PushNotification = object.getString("User_PushNotification");
						String User_Location = object.getString("User_Location");
						String User_Badge = object.getString("User_Badge");
						String User_Status = object.getString("User_Status");
						String User_IsPrivate = object.getString("User_IsPrivate");
						String User_IsActive = object.getString("User_IsActive");
						String User_LastSession = object.getString("User_LastSession");
						String User_Date = object.getString("User_Date");
						String Follow_Following = object.getString("Follow_Following");
						String Is_Follow = object.getString("Is_Follow");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("User_ID", User_ID);
						map.put("User_FirstName", User_FirstName);
						map.put("User_LastName", User_LastName);
						map.put("User_Name", User_Name);
						map.put("User_Imagepath", User_Imagepath);
						map.put("User_Password", User_Password);
						map.put("User_Email", User_Email);
						map.put("User_CountryID", User_CountryID);
						map.put("User_City", User_City);
						map.put("User_Gender", User_Gender);
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
						map.put("Follow_Following", Follow_Following);
						map.put("Is_Follow", Is_Follow);
						FollowingUserData.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return FollowingUserData;
		}

		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);
			mPullRefreshListView.onRefreshComplete();
			setTheAdapterWithArray();

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
	protected void onResume() {
		super.onResume();
		if (objUserAdapter != null) {
			objUserAdapter.notifyDataSetChanged();
		}

	}

	public void setTheAdapterWithArray() {

		if (objUserAdapter.getCount() <= 15) {
			objUserAdapter = new setFollowingData(FollowingUserData);
			listView.setAdapter(objUserAdapter);
		} else {
			objUserAdapter.notifyDataSetChanged();
		}

	}

	static class ViewHolder {
		ImageView imgSearchUSer;
		TextView tvSearchUser;
		TextView imgisFollow;

	}

	/** Adapter */
	public class setFollowingData extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> items;
		String DecodedComment;

		setFollowingData(ArrayList<HashMap<String, String>> items) {
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
				view = getLayoutInflater().inflate(R.layout.following, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imgSearchUSer = (ImageView) view.findViewById(R.id.imgFollowingUser);
				holder.tvSearchUser = (TextView) view.findViewById(R.id.txtValueFollwingUserName);
				holder.imgisFollow = (TextView) view.findViewById(R.id.btnFollowing);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (isAuthor(items.get(position).get("User_ID"))) {
				holder.imgisFollow.setVisibility(View.GONE);
			} else {
				holder.imgisFollow.setVisibility(View.VISIBLE);
			}

			if (holder.imgSearchUSer != null) {

				String img = (items.get(position).get("User_Imagepath"));

				imageLoader.displayImage(img, holder.imgSearchUSer, options);
			}
			if (holder.tvSearchUser != null) {
				holder.tvSearchUser.setText(items.get(position).get("User_Name"));
				holder.tvSearchUser.setTypeface(SplashScreen.ProxiNova_Regular);
			}
			if (holder.imgisFollow != null) {
				int isfollow = Integer.valueOf(items.get(position).get("Is_Follow").toString());
				System.out.println("item position==>" + position);
				switch (isfollow) {
				case 2:
					holder.imgisFollow.setText("Requested");
					holder.imgisFollow.setBackgroundResource(R.drawable.btn_requestedbg);
					holder.imgisFollow.setTypeface(SplashScreen.ProxiNova_Regular);
					checkIsFollow = 2;
					holder.imgisFollow.setTextColor(Color.parseColor("#ffffff"));
					break;
				case 1:
					holder.imgisFollow.setText("Following");
					holder.imgisFollow.setBackgroundResource(R.drawable.btn_followingbg);
					holder.imgisFollow.setTypeface(SplashScreen.ProxiNova_Regular);
					holder.imgisFollow.setTextColor(Color.parseColor("#ffffff"));
					checkIsFollow = 1;
					break;
				case 0:
					checkIsFollow = 0;
					holder.imgisFollow.setText("Follow");
					holder.imgisFollow.setBackgroundResource(R.drawable.btnbg);
					holder.imgisFollow.setTypeface(SplashScreen.ProxiNova_Regular);
					holder.imgisFollow.setTextColor(R.color.Theme_Color);
					break;
				}
			}
			holder.imgisFollow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UnFollow_User_ID = items.get(position).get("User_ID");

					int isfollow = Integer.valueOf(items.get(position).get("Is_Follow").toString());

					switch (isfollow) {
					case 0:
						System.out.println("case 0");
						if (isNetworkConnected()) {
							new FollowingUserRequest().execute();
						} else {
							Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
						}
						break;
					case 1:
						System.out.println("case 1");
						if (isNetworkConnected()) {
							new UnFollowUser().execute();
						} else {
							Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
						}
						break;

					case 2:
						System.out.println("case 2");
						if (isNetworkConnected()) {
							new UnFollowUser().execute();
						} else {
							Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
						}
						break;

					}
				}
			});

			return view;
		}
	}

	/** UnFollow Api */

	class UnFollowUser extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			isChange = true;
			String Result1 = null;
			Constant cc = new Constant();
			String finalUrl = cc.GetBaseUrl() + "api=Unfollow&User_ID=" + Login_User_ID + "&Follow_Following=" + UnFollow_User_ID;
			Log.d("TAG", finalUrl);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(finalUrl);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			Log.d("TAG", "Json String " + json.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				FollowingUserData.clear();
				startindex = 0;
				new GetFollowingUser().execute();
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Server Error Please try again");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/* Following Api */

	class FollowingUserRequest extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			isChange = true;
			String Result1 = null;
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=Following&User_ID=" + Login_User_ID + "&Follow_Following=" + UnFollow_User_ID;
			System.out.println(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			Log.d("TAG", "Json String " + json.toString());
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
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				FollowingUserData.clear();
				startindex = 0;
				new GetFollowingUser().execute();
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Server Error Please try again");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

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

	public OnClickListener checkIsFollowListner = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (checkIsFollow) {
			case 0:
				if (isNetworkConnected()) {
					new FollowingUserRequest().execute();
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
				break;
			case 1:
				if (isNetworkConnected()) {
					new UnFollowUser().execute();
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
				break;
			case 2:
				if (isNetworkConnected()) {
					new UnFollowUser().execute();
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
				break;

			}

		}
	};

	private boolean isAuthor(String strPostAuthor2) {
		if (strPostAuthor2.equals(Login_User_ID)) {
			return true;
		} else {
			return false;
		}
	}

	public void onBackPressed() {
		onback();
	};
}
