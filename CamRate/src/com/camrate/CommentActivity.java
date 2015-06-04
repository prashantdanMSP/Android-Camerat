package com.camrate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.camrate.global.Base64;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.profile.UserProfile;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.google.android.gms.internal.ie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CommentActivity extends Activity {

	ArrayList<Setter> items;
	ArrayList<Setter> itemsTemp = new ArrayList<Setter>();
	String JsonString = null;
	CustomAdapter adapter;

	EditText edt_comments;
	Button btn_SendComments;
	String UserID, Post_ID;
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	ArrayList<HashMap<String, String>> PostCommentData;
	String ParseComment;
	Constant c = new Constant();

	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	String EncodedComment;
	byte[] encodeComment;
	Boolean isLoading;
	int count = 0;
	Function fun;
	checkInternet chkNet;
	private static final String TAG = CommentActivity.class.getSimpleName();
	private SwipeMenuListView lstComments;
	CommentsAdapter objCommentadapter;
	Constant con;
	String comment_ID;
	int ACTIVITY_RESULT_COMMENT = 0;
	int postIndex;
	ListView list_followers;
	String ClickedUserName;
	String searchString;
	boolean isStartAT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();
	}

	private void init() {

		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Comments");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.INVISIBLE);
		next.setText("");
		items = new ArrayList<Setter>();
		lstComments = (SwipeMenuListView) findViewById(R.id.listComment);
		con = new Constant();
		list_followers = (ListView) findViewById(R.id.listfollowers);
		list_followers.setItemsCanFocus(true);

		edt_comments = (EditText) findViewById(R.id.edt_comments);
		btn_SendComments = (Button) findViewById(R.id.btn_sendComments);
		edt_comments.setTypeface(SplashScreen.ProxiNova_Regular);

		btn_SendComments.setTypeface(SplashScreen.ProxiNova_Bold);
		chkNet = new checkInternet(CommentActivity.this);
		fun = new Function(getParent());
		pd = (ProgressBar) findViewById(R.id.progressBar1);

		adapter = new CustomAdapter(getParent());

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		try {
			postIndex = Integer.parseInt(getIntent().getStringExtra("postindex"));
			Log.d("TAG", "position.. " + postIndex);
		} catch (Exception e) {

		}

		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");

		Intent intent = getIntent();
		Post_ID = intent.getStringExtra("Post_ID");
		System.out.println("Post_ID--->" + Post_ID);

		if (chkNet.isNetworkConnected()) {
			new GetComments().execute();

		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
		btn_SendComments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt_comments.getText().toString().trim().length() == 0) {
					SimpleAlert("Please enter your comment");
				} else {
					if (chkNet.isNetworkConnected()) {
						new SubmitComment().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		imageLoader.init(ImageLoaderConfiguration.createDefault(CommentActivity.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
				case 0:
					createMenu2(menu);
					break;
				case 1:
					createMenu3(menu);
					break;
				}
			}

			private void createMenu2(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
				item1.setBackground(new ColorDrawable(Color.RED));
				item1.setWidth(dp2px(90));
				item1.setIcon(R.drawable.deleteicon);
				menu.addMenuItem(item1);

			}

			private void createMenu3(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
				item1.setBackground(new ColorDrawable(Color.RED));
				item1.setWidth(dp2px(90));
				item1.setIcon(R.drawable.moreicon);
				menu.addMenuItem(item1);

			}
		};
		// set creator
		lstComments.setMenuCreator(creator);

		// step 2. listener item click event
		lstComments.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

				Log.d("TAg", objCommentadapter.getItemViewType(position) + "");

				comment_ID = PostCommentData.get(position).get("Comment_ID");

				if (objCommentadapter.getItemViewType(position) == 0) {

					comment_ID = PostCommentData.get(position).get("Comment_ID");
					new DeleteLoading(position).execute();
					// delete
				} else {

					// info

					Intent intent = new Intent(CommentActivity.this, ResonForReport.class);
					intent.putExtra("reasonType", "1"); // 0 for post items
					intent.putExtra("Post_ID", Post_ID);
					intent.putExtra("Comment_ID", comment_ID);
					startActivity(intent);
					overridePendingTransition(0, 0);

				}

				return false;
			}
		});

		edt_comments.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				try {

					String lastChar = edt_comments.getText().toString().substring(edt_comments.getText().toString().length() - 1, edt_comments.getText().toString().length());
					Log.d("TAG", "last Char " + lastChar);
					if (isStartAT) {
						list_followers.setVisibility(View.VISIBLE);
						searchString = searchString + lastChar;
						Log.d("TAG", "Search " + searchString);
						performSearch(searchString);
					} else {
						list_followers.setVisibility(View.INVISIBLE);
					}

					if (lastChar.equalsIgnoreCase("@")) {
						searchString = "";
						isStartAT = true;
						list_followers.setVisibility(View.VISIBLE);
						performSearch("@");
					} else if (lastChar.equalsIgnoreCase(" ")) {
						isStartAT = false;
						list_followers.setVisibility(View.GONE);
					}

					if (edt_comments.getText().toString().equalsIgnoreCase("")) {
						list_followers.setVisibility(View.INVISIBLE);
					}

					// Log.d("TAG", "Last Char " + lastChar);

					// hideSoftKeyboard();

				} catch (Exception e) {
					searchString = "";
					isStartAT = false;
					list_followers.setVisibility(View.GONE);
				}
			}
		});

		list_followers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("TAG", "Clicked Item " + items.get(position).getUser_Name() + "");
				String clickedUserName = items.get(position).getUser_Name();
				String strTemp = edt_comments.getText().toString();

				strTemp = strTemp.substring(0, strTemp.length() - searchString.length());
				edt_comments.setText(strTemp);

				edt_comments.setText(edt_comments.getText().toString() + "" + clickedUserName + " ");
				list_followers.setVisibility(View.INVISIBLE);
				edt_comments.setSelection(edt_comments.getText().toString().length());
				isStartAT = false;

			}
		});

		new GetFollowersUser().execute();
	}

	public void performSearch(String searchString) {

		items.clear();

		if (searchString.equalsIgnoreCase("@")) {
			for (int i = 0; i < itemsTemp.size(); i++) {
				items.add(itemsTemp.get(i));

			}
		} else if (!searchString.equalsIgnoreCase("")) {
			for (int i = 0; i < itemsTemp.size(); i++) {
				if (itemsTemp.get(i).getUser_Name().startsWith(searchString)) {
					items.add(itemsTemp.get(i));
				}

			}
		} else {
			for (int i = 0; i < itemsTemp.size(); i++) {
				items.add(itemsTemp.get(i));

			}

		}
		Log.d("TAG", "item size " + items.size());
		adapter.notifyDataSetChanged();

	}

	public void SimpleAlert(String b) {
		Builder builder = new AlertDialog.Builder(CommentActivity.this);
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

	static class ViewHolder {
		ImageView imageViewUser;
		TextView txtUserComment, txtLastSeen, txtUserName;

	}

	class SubmitComment extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result = null;
			// TODO Auto-generated method stub
			String comment = edt_comments.getText().toString().trim();

			try {
				encodeComment = comment.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			EncodedComment = Base64.encodeBytes(encodeComment);
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=PostComment&Comment_Content=" + EncodedComment + "&Post_ID=" + Post_ID + "&User_ID=" + UserID + "";
			System.out.println(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = parser.getJSONFromUrl(encoded_url.toString());
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
				edt_comments.setText("");
				if (!isLoading) {

					System.out.println("here it comes");
					if (chkNet.isNetworkConnected()) {
						new GetComments().execute();

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
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

	public class GetComments extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			isLoading = true;
			if (count == 0) {
				pd.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url = c.GetBaseUrl() + "api=GetComment&Post_ID=" + Post_ID;
			count = 1;
			// Log.d("Tag","url "+url);
			try {
				ParseComment = parser.getStringFromUrl(url);
				JSONArray jsonMyFeed = new JSONArray(ParseComment);
				PostCommentData = new ArrayList<HashMap<String, String>>();
				for (int s = 0; s < jsonMyFeed.length(); s++) {
					JSONObject objComment = jsonMyFeed.getJSONObject(s);
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
					map1.put("User_Desc", objComment.getString("User_Desc"));
					map1.put("User_Location", objComment.getString("User_Location"));
					map1.put("User_Status", objComment.getString("User_Status"));
					map1.put("User_IsPrivate", objComment.getString("User_IsPrivate"));
					map1.put("User_Date", objComment.getString("User_Date"));
					PostCommentData.add(map1);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			System.out.println("PostCommentData -->" + PostCommentData);
			return PostCommentData;
		}

		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, String>> menuItems) {
			if (count == 1) {
				pd.setVisibility(View.INVISIBLE);
			}
			isLoading = false;

			if (menuItems.size() == 0) {
				Toast.makeText(getApplicationContext(), "No comments Found", Toast.LENGTH_SHORT).show();

			} else {
				setTheAdapterWithArray(menuItems);
			}
		}
	}

	public void setTheAdapterWithArray(final ArrayList<HashMap<String, String>> arrayList) {
		objCommentadapter = new CommentsAdapter(arrayList);
		lstComments.setAdapter(objCommentadapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivityManage.isFromComment = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		c.TagmyPref = getSharedPreferences(c.TAG_PREF, Context.MODE_WORLD_WRITEABLE);
		Editor edt = c.TagmyPref.edit();
		edt.putString(c.TAG_KEY, TAG);
		edt.commit();

	}

	public class CommentsAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> items;
		String DecodedComment;

		CommentsAdapter(ArrayList<HashMap<String, String>> items) {
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

		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {

			if (items.get(position).get("Comment_UserID").equalsIgnoreCase(UserID)) {
				return 0;
			} else {
				return 1;
			}

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.comment_list, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageViewUser = (ImageView) view.findViewById(R.id.userimg);
				holder.txtUserName = (TextView) view.findViewById(R.id.tvusername);
				holder.txtUserComment = (TextView) view.findViewById(R.id.tvcommentdesc);
				holder.txtLastSeen = (TextView) view.findViewById(R.id.tvlastseen);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (holder.imageViewUser != null) {
				String img = (items.get(position).get("User_Imagepath"));
				imageLoader.displayImage(img, holder.imageViewUser, options, null);
			}
			if (holder.txtUserName != null) {
				String UserName = (items.get(position).get("User_Name"));
				holder.txtUserName.setText(UserName);
			}

			holder.txtUserComment.setMovementMethod(LinkMovementMethod.getInstance());
			if (holder.txtUserComment != null) {
				String Comment_Content = (items.get(position).get("Comment_Content"));
				byte[] Data;

				try {
					Data = Base64.decode(Comment_Content.getBytes());
					DecodedComment = new String(Data);
					System.out.println("Decoded data-->" + DecodedComment);
				} catch (IOException e) {
					e.printStackTrace();
				}

				SpannableStringBuilder comments = getclickableText(DecodedComment);
				holder.txtUserComment.setText(comments, BufferType.SPANNABLE);
			}

			if (holder.txtLastSeen != null) {
				String Comment_Date = (items.get(position).get("Comment_Date"));

				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date postDate = simpleDateFormat.parse((items.get(position).get("Comment_Date")).toString());
					String diffVal = fun.checkDate_Diffrences(postDate);
					Log.d("Diff", "val==>" + diffVal);
					holder.txtLastSeen.setText(diffVal);
					holder.txtLastSeen.setTypeface(SplashScreen.ProxiNova_Regular);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return view;
		}
	}

	public SpannableStringBuilder getclickableText(String comment) {

		String stringBuilder = "";
		SpannableStringBuilder builder = new SpannableStringBuilder();

		for (int i = 0; i < comment.length(); i++) {

			String later = comment.substring(i, i + 1);
			Log.d("TAG", "Char " + later);

			if (later.equalsIgnoreCase("@")) {

				String remailString = comment.substring(i, comment.length());

				String aTString = "";

				for (int j = 0; j < remailString.length(); j++) {

					if (!remailString.substring(j, j + 1).equalsIgnoreCase(" ")) {
						aTString = aTString + remailString.substring(j, j + 1);
					} else {
						break;
					}

				}
				// stringBuilder = stringBuilder + aTString;
				i = i + aTString.length() - 1;

				SpannableString ss = new SpannableString(aTString);

				ClickableSpan clickableSpan = new ClickableSpan() {
					@Override
					public void onClick(View textView) {

					}
				};
				ss.setSpan(new MyClickableSpan(aTString), 0, aTString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				builder.append(ss);

				stringBuilder = stringBuilder + ss;

			} else {
				stringBuilder = stringBuilder + later;
				SpannableString ss = new SpannableString(later);
				builder.append(ss);
			}

		}

		return builder;
	}

	class MyClickableSpan extends ClickableSpan {// extend ClickableSpan

		String clicked;

		public MyClickableSpan(String string) {
			// TODO Auto-generated constructor stub
			super();
			clicked = string;
		}

		public void onClick(View textView) {
			TextView tv = (TextView) textView;
			Spanned s = (Spanned) tv.getText();
			int start = s.getSpanStart(this);
			int end = s.getSpanEnd(this);
			Log.d(TAG, "onClick [" + s.subSequence(start, end) + "]");

			String userName = s.subSequence(start + 1, end).toString();
			ClickedUserName = userName;

			new CheckUserName().execute();

		}

		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#26BDBE"));
			ds.setUnderlineText(false);

		}

	}

	private boolean isAuthor(String strPostAuthor2) {
		if (strPostAuthor2.equals(UserID)) {
			return true;
		} else {
			return false;
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	public class DeleteLoading extends AsyncTask<Void, Void, String> {

		int index;

		public DeleteLoading(int index) {
			this.index = index;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {

			String finalUrl = con.GetBaseUrl() + "api=DeleteComment&Comment_ID=" + comment_ID;
			Log.d("TAG", finalUrl);
			JSONParser jsonParser = new JSONParser();

			String jsonString = jsonParser.getStringFromUrl(finalUrl);

			return jsonString;
		}

		@Override
		protected void onPostExecute(String jsonString) {
			super.onPostExecute(jsonString);
			pd.setVisibility(View.INVISIBLE);
			Log.d("TAG", "Result " + jsonString);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(jsonString);
				String result = jsonObject.getString("result");

				if (result.equalsIgnoreCase("1")) {

					fun.SimpleAlert("CamRate", "Comment has been deleted successfull");
					PostCommentData.remove(index);
					objCommentadapter.notifyDataSetChanged();
				} else {

					fun.SimpleAlert("CamRate", "Comment has not been deleted. Please try again");

					Toast.makeText(CommentActivity.this, "Please try later", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}

		}

	}

	public class GetFollowersUser extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			items.clear();
			itemsTemp.clear();
			String finalUrl = c.GetBaseUrl() + "api=Followinglist&User_ID=" + UserID + "&Follow_Request=1";

			Log.d("TAG", "finalUrl " + finalUrl);
			try {
				JsonString = parser.getStringFromUrl(finalUrl);
				JSONArray json = new JSONArray(JsonString);

				if (json.length() > 0) {

					System.out.println("JsonArrayLength==>" + json.length());
					for (int i = 0; i < json.length(); i++) {
						JSONObject object = json.getJSONObject(i);
						String User_ID = object.getString("User_ID");
						String User_FirstName = object.getString("User_FirstName");
						String User_LastName = object.getString("User_LastName");
						String User_Name = object.getString("User_Name");
						String User_Imagepath = object.getString("User_Imagepath");

						Setter s = new Setter();
						s.setUser_ID(User_ID);
						s.setUser_FirstName(User_FirstName);
						s.setUser_LastName(User_LastName);
						s.setUser_Name(User_Name);
						s.setUser_Imagepath(User_Imagepath);
						items.add(s);
						itemsTemp.add(s);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			list_followers.setAdapter(adapter);
		}
	}

	public class CheckUserName extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {

			String finalUrl = c.GetBaseUrl() + "api=CheckUserExist&User_Name=" + ClickedUserName;
			Log.d("TAG", "finalUrl " + finalUrl);
			try {
				JsonString = parser.getStringFromUrl(finalUrl);

			} catch (Exception e) {

			}
			return JsonString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);

			try {
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("result").equalsIgnoreCase("1")) {
					String User_ID = jsonObject.getString("User_ID").toString();

					if (isAuthor(User_ID)) {
						Intent intent = new Intent(getParent(), UserProfileChild.class);
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startChildActivity("UserProfileChild", intent);

					} else {
						Intent intent = new Intent(getParent(), GeneralUserProfile.class);
						intent.putExtra("User_ID", User_ID);
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startChildActivity("GeneralUserProfile", intent);
					}

				} else {
					Toast.makeText(getParent(), "User does not exist", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
			}

		}
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getParent().getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	private class Setter {
		private boolean isChecked;
		String User_ID;
		String User_FirstName;
		String User_LastName;
		String User_Name;
		String User_Imagepath;

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public String getUser_ID() {
			return User_ID;
		}

		public void setUser_ID(String user_ID) {
			User_ID = user_ID;
		}

		public String getUser_FirstName() {
			return User_FirstName;
		}

		public void setUser_FirstName(String user_FirstName) {
			User_FirstName = user_FirstName;
		}

		public String getUser_LastName() {
			return User_LastName;
		}

		public void setUser_LastName(String user_LastName) {
			User_LastName = user_LastName;
		}

		public String getUser_Name() {
			return User_Name;
		}

		public void setUser_Name(String user_Name) {
			User_Name = user_Name;
		}

		public String getUser_Imagepath() {
			return User_Imagepath;
		}

		public void setUser_Imagepath(String user_Imagepath) {
			User_Imagepath = user_Imagepath;
		}

	}

	public class CustomAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;

		public CustomAdapter(Context context) {
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public class Holder {

			private TextView title;
			private ImageView imageView;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Holder holder;

			if (convertView == null) {
				holder = new Holder();
				v = layoutInflater.inflate(R.layout.item_alertfollowerlist, null, false);
				holder.title = (TextView) v.findViewById(R.id.textView1);
				holder.imageView = (ImageView) v.findViewById(R.id.imgFollowingUser);
				v.setTag(holder);
			} else {
				holder = (Holder) v.getTag();
			}

			Setter s = items.get(position);

			holder.title.setText(s.getUser_Name());

			try {

				imageLoader.displayImage(s.getUser_Imagepath(), holder.imageView, options);

			} catch (Exception e) {
				// TODO: handle exception
			}

			return v;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManage.Post_ID = Post_ID;
		ActivityManage.postIndex = postIndex;
		Log.d("tag", "ON DESTROY COMMENTACTIVITY");
	}

	// @Override
	// public void onBackPressed() {
	// Log.d("TAG", "on destroy");
	// Intent intent = new Intent();
	// intent.putExtra("Post_ID", Post_ID);
	// intent.putExtra("postIndex", postIndex);
	// setResult(ACTIVITY_RESULT_COMMENT, intent);
	// super.onBackPressed();
	// }

}
