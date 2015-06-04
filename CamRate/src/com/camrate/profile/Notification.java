package com.camrate.profile;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tabs.TabGroupActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Notification extends Activity {

	private static final String TAG = Notification.class.getSimpleName();
	Constant c = new Constant();
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONArray objmyFeedJson;
	String jsonString;
	String UserID;
	int startindex = 0;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ArrayList<HashMap<String, String>> NotificationData;
	String UnFollow_User_ID, NotificationID;
	// FollowingAdapter objUserAdapter;
	setNotificationData objNotificationAdapter;
	JSONParser jparser = new JSONParser();
	String Result = null;
	int checkIsFollow = 0;
	checkInternet chk;
	Function fun;
	private PullToRefreshListView mPullRefreshListView;
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homefollow);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();
	}

	public void init() {

		imageLoader.init(ImageLoaderConfiguration.createDefault(Notification.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Notifications");
		Button Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.VISIBLE);
		Button btnReadAll = (Button) findViewById(R.id.button1);
		btnReadAll.setVisibility(View.VISIBLE);
		btnReadAll.setText("Read All");
		NotificationData = new ArrayList<HashMap<String, String>>();
		objNotificationAdapter = new setNotificationData(NotificationData);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listMyFeed);
		listView = mPullRefreshListView.getRefreshableView();

		pd = (ProgressBar) findViewById(R.id.progressBar1);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), UserProfile.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("UserProfile", intent);
			}
		});
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");

		System.out.println("UserID--->" + UserID);
		chk = new checkInternet(Notification.this);
		fun = new Function(getParent());
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		btnReadAll.setTypeface(SplashScreen.ProxiNova_SemiBold);
		if (chk.isNetworkConnected()) {
			new GetNotifications().execute();

		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
		btnReadAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (chk.isNetworkConnected()) {
					new ReadAll().execute();
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (mPullRefreshListView.getCurrentMode() == Mode.PULL_FROM_START) {
					NotificationData.clear();
					startindex = 0;
					new GetNotifications().execute();
				} else {
					new GetNotifications().execute();
				}

			}
		});

	}

	public class GetNotifications extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {

			String finalUrl = c.GetBaseUrl() + "api=GetNotificationsgcm&User_ID=" + UserID + "&start=" + startindex + "&end=15";
			Log.d("TAG", "finalurl " + finalUrl);

			try {
				jsonString = parser.getStringFromUrl(finalUrl);
				Log.d("TAG", "jsonString " + jsonString);
				JSONArray json = new JSONArray(jsonString);

				if (json.length() > 0) {

					startindex += 15;

					System.out.println("JsonArrayLength==>" + json.length());

					for (int s = 0; s < json.length(); s++) {
						JSONObject object = json.getJSONObject(s);
						String Notifications_ID = object.getString("Notificationsgcm_ID");
						String Notifications_apns_msgID = object.getString("Notificationsgcm_gcm_msgID");
						String Notifications_UserID = object.getString("Notificationsgcm_UserID");
						String Notifications_PostID = object.getString("Notificationsgcm_PostID");
						String Notifications_SenderID = object.getString("Notificationsgcm_SenderID");
						String Notifications_Flag = object.getString("Notificationsgcm_Flag");
						String Notifications_Date = object.getString("Notificationsgcm_Date");
						String pid = object.getString("pid");
						String clientid = object.getString("clientid");
						String fk_device = object.getString("fk_device");
						String msgString = object.getString("message");
						String multicast_id = object.getString("multicast_id");
						String delivery = object.getString("delivery");
						String status = object.getString("status");
						String created = object.getString("created");
						String modified = object.getString("modified");
						String User_Imagepath = object.getString("User_Imagepath");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("Notificationsgcm_ID", Notifications_ID);
						map.put("Notificationsgcm_gcm_msgID", Notifications_apns_msgID);
						map.put("Notificationsgcm_UserID", Notifications_UserID);

						map.put("Notificationsgcm_PostID", Notifications_PostID);
						map.put("Notificationsgcm_SenderID", Notifications_SenderID);
						map.put("Notificationsgcm_Flag", Notifications_Flag);
						map.put("Notificationsgcm_Date", Notifications_Date);
						map.put("pid", pid);
						map.put("clientid", clientid);
						map.put("fk_device", fk_device);
						map.put("message", msgString);
						map.put("multicast_id", multicast_id);
						map.put("delivery", delivery);
						map.put("status", status);
						map.put("created", created);
						map.put("modified", modified);
						map.put("User_Imagepath", User_Imagepath);

						NotificationData.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("TAG", "NotificationData " + NotificationData.size());
			return NotificationData;
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
		// TODO Auto-generated method stub
		super.onResume();
		/*
		 * if(objUserAdapter!=null) { objUserAdapter.notifyDataSetChanged(); }
		 */
		// c.TagmyPref = getSharedPreferences(c.TAG_PREF,
		// Context.MODE_WORLD_WRITEABLE);
		// String actName = c.TagmyPref.getString(c.TAG_KEY, "null");
		// System.out.println("Activity NAme--->" + actName);

	}

	public void setTheAdapterWithArray() {

		if (objNotificationAdapter.getCount() <= 15) {
			objNotificationAdapter = new setNotificationData(NotificationData);
			listView.setAdapter(objNotificationAdapter);
		} else {
			objNotificationAdapter.notifyDataSetChanged();
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position - 1;

				if (chk.isNetworkConnected()) {
					new UpdateNotication(position).execute();
				}

				String msgCheck = NotificationData.get(position).get("message");
				NotificationID = NotificationData.get(position).get("Notificationsgcm_ID");
				UnFollow_User_ID = NotificationData.get(position).get("Notificationsgcm_UserID");

				if (msgCheck.contains("requested")) {
					AlertNotification("CamRate", msgCheck);
				} else if (msgCheck.contains("started")) {
					// general profile
					System.out.println("USer_ID==>" + NotificationData.get(position).get("Notificationsgcm_SenderID"));
					Intent intent = new Intent(getParent(), GeneralUserProfile.class);
					intent.putExtra("User_ID", NotificationData.get(position).get("Notificationsgcm_SenderID"));
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("GeneralUserProfile", intent);

				} else if (msgCheck.contains("commented")) {

					Intent intent = new Intent(getParent(), NotificationPostDetail.class);
					intent.putExtra("Post_ID", NotificationData.get(position).get("Notificationsgcm_PostID"));
					intent.putExtra("isFromComment", true);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("NotificationPostDetail", intent);

				} else if (msgCheck.contains("Rated")) {
					Intent intent = new Intent(getParent(), NotificationPostDetail.class);
					intent.putExtra("Post_ID", NotificationData.get(position).get("Notificationsgcm_PostID"));
					intent.putExtra("isFromComment", false);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("NotificationPostDetail", intent);
				}

			}
		});
	}

	static class ViewHolder {
		ImageView imgAlertUser;
		TextView tvAlertMessageUser, tvAlertMessage, tvAlertDate;
		RelativeLayout relNotifacationUnread;

	}

	/** Adapter */
	public class setNotificationData extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> items;
		String DecodedComment;

		setNotificationData(ArrayList<HashMap<String, String>> items) {
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
				view = getLayoutInflater().inflate(R.layout.notification, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imgAlertUser = (ImageView) view.findViewById(R.id.imgAlertUser);
				holder.tvAlertMessageUser = (TextView) view.findViewById(R.id.txtValueForAlert);

				holder.tvAlertDate = (TextView) view.findViewById(R.id.txtValueFordate);
				holder.relNotifacationUnread = (RelativeLayout) view.findViewById(R.id.relativecmnt);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (holder.relNotifacationUnread != null) {
				String Notification_flag = items.get(position).get("Notificationsgcm_Flag");
				if (Notification_flag.equalsIgnoreCase("0")) {
					holder.relNotifacationUnread.setBackgroundResource(R.color.lblunreadnotification);

				} else {
					holder.relNotifacationUnread.setBackgroundResource(R.color.White);
				}
			}
			if (holder.imgAlertUser != null) {

				String img = (items.get(position).get("User_Imagepath"));

				imageLoader.displayImage(img, holder.imgAlertUser, options);
			}
			if (holder.tvAlertMessageUser != null) {

				String Notifications_Flag = items.get(position).get("Notificationsgcm_Flag");
				String[] tempAlertarray = items.get(position).get("message").split("has");
				String UserName = tempAlertarray[0];
				String nextdata = tempAlertarray[1];
				// System.out.println("UsernAme==>" + UserName);
				// System.out.println("alert==>" + nextdata);

				// String text = "<font color=#5cacee>" + UserName +
				// "</font> <font color=#000000>" + "has" + nextdata +
				// "</font>";
				String text = UserName + "has" + nextdata;
				if (Notifications_Flag.equalsIgnoreCase("1")) {

					holder.tvAlertMessageUser.setText(Html.fromHtml(text));
					holder.tvAlertMessageUser.setTypeface(SplashScreen.ProxiNova_Regular);
					/*
					 * holder.tvAlertMessageUser.setText(UserName); holder.tvAlertMessage.setText("has" +nextdata); holder.tvAlertMessage.setAlpha(0.8f); holder.tvAlertMessage .setTextColor(Color.parseColor("#000000"));
					 */

				} else {
					holder.tvAlertMessageUser.setText(Html.fromHtml(text));
					holder.tvAlertMessageUser.setTypeface(SplashScreen.ProxiNova_Regular);
					/*
					 * holder.tvAlertMessageUser.setText(UserName); holder.tvAlertMessage.setText("has" +nextdata); holder.tvAlertMessage.setAlpha(0.8f); holder.tvAlertMessage .setTextColor(Color.parseColor("#00E6E6"));
					 */
				}

			}
			if (holder.tvAlertDate != null) {
				// holder.tvAlertDate.setText(items.get(position).get("Notifications_Date"));
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date postDate = simpleDateFormat.parse((items.get(position).get("Notificationsgcm_Date")).toString());
					String diffVal = fun.checkDate_Diffrences(postDate);
					Log.d("Diff", "val==>" + diffVal);
					holder.tvAlertDate.setText(diffVal);
					holder.tvAlertDate.setTypeface(SplashScreen.ProxiNova_Regular);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			return view;
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

	public void AlertNotification(String t, String b) {
		Builder builder = new AlertDialog.Builder(getParent());
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (chk.isNetworkConnected()) {
					new AcceptRequest().execute();

				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (chk.isNetworkConnected()) {
					new DeclineRequest().execute();

				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		builder.setNeutralButton("Later", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (chk.isNetworkConnected()) {
					new LaterRequest().execute();

				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/** AcceptRequest Api */
	class AcceptRequest extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			// http://camrate.it/main/frmlovehateApi.php?api=AcceptRequest&Follow_UserID=337&Follow_Following=109&NotificationID=2913
			String url = cc.GetBaseUrl() + "api=AcceptRequest&Follow_UserID=" + UserID + "&Follow_Following=" + UnFollow_User_ID + "&NotificationID=" + NotificationID;
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
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				startindex += 15;
				NotificationData.clear();
				new GetNotifications().execute();
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Invitation not accepted.Please try again");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/** DeclineRequest Api */
	class DeclineRequest extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=DeclineRequest&Follow_UserID=" + UserID + "&Follow_Following=" + UnFollow_User_ID + "&NotificationID=" + NotificationID;
			System.out.println(url);
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
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				startindex += 15;
				NotificationData.clear();
				new GetNotifications().execute();
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Invitation not accepted.Please try again");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/** LaterRequest Api */
	class LaterRequest extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			Constant cc = new Constant();
			String finalUrl = cc.GetBaseUrl() + "api=UpdateNotificationFlaggcm&Notification_ID=" + NotificationID;
			Log.d("TAG", "finalUrl " + finalUrl);
			URI encoded_url = cc.Geturl(finalUrl);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			Log.d("TAG", "jsonString " + json.toString());
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
				startindex += 15;
				NotificationData.clear();
				new GetNotifications().execute();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/** ReadAll Api */
	class ReadAll extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			Constant cc = new Constant();
			String finalUrl = cc.GetBaseUrl() + "api=ReadAllNotificationsgcm&User_ID=" + UserID;
			Log.d("TAG", "finalUrl" + finalUrl);

			URI encoded_url = cc.Geturl(finalUrl);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			Log.d("TAG", "jsonString " + json.toString());
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
				startindex = 0;
				NotificationData.clear();
				new GetNotifications().execute();
			}
			if (status.equals("0")) {
				SimpleAlert("CamRate", "Invitation not accepted.Please try again");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	class UpdateNotication extends AsyncTask<String, Void, String> {

		int index;

		public UpdateNotication(int index) {
			this.index = index;
		}

		@Override
		protected void onPreExecute() {

			System.out.println();
		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			Constant cc = new Constant();
			String finalUrl = cc.GetBaseUrl() + "api=UpdateNotificationFlaggcm&Notification_ID=" + NotificationID;
			Log.d("TAG", "finalUrl " + finalUrl);
			URI encoded_url = cc.Geturl(finalUrl);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);
			Log.d("TAG", "Result " + Result);
			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			if (status.equals("1")) {

				NotificationData.get(index).put("Notificationsgcm_Flag", "1");
				objNotificationAdapter.notifyDataSetChanged();
			}

		}
	}
}
