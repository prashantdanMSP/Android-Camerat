package com.camrate.search;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.PostDetail;
import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.camrate.tools.VolleyJsonParser;
import com.camrate.tools.VolleyJsonParser.VolleyCallback;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class SearchTagsDetail extends Activity {

	ImageView ivIcon;
	TextView tvItemName;
	Constant c = new Constant();
	String User_ID;
	int width, height;
	int startIndex = 0;

	DisplayImageOptions options;
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONArray objmyFeedJson;
	String NewestParse, SelectedTag, SelectedCount, SelectedSearch, TempTag;
	RelativeLayout.LayoutParams paramImage, paramImage2;
	ArrayList<HashMap<String, Object>> TagPost = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, String>> TagPostCommentsData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> TagPostRateData = new ArrayList<HashMap<String, String>>();
	ImageAdapter objImageAdapter;
	ImageLoader imageLoader = ImageLoader.getInstance();
	String url1;
	int ACITIVITY_RESULT_POSTDETAIL = 1;
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView gridView;
	private static final String TAG = SearchTagsDetail.class.getSimpleName();
	VolleyJsonParser volleyParser;
	Context context;
	HashMap<String, String> params;
	checkInternet chkNet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newestgrid1);

		init();
	}

	public void init() {
		context = getParent();
		chkNet = new checkInternet(context);
		volleyParser = new VolleyJsonParser(context);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		Button Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.VISIBLE);
		Button btnStats = (Button) findViewById(R.id.button1);
		btnStats.setVisibility(View.VISIBLE);
		btnStats.setText("Stats");
		// btnStats.setTypeface(SplashScreen.ProxiNova_Regular);

		objImageAdapter = new ImageAdapter(TagPost);
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gridview);
		gridView = mPullRefreshGridView.getRefreshableView();

		pd = (ProgressBar) findViewById(R.id.progressBar1);
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
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
		int round = (width - 20) / 3;
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "");
		paramImage2 = new RelativeLayout.LayoutParams((width - 20) / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		System.out.println("UserID--->" + User_ID);
		// Bundle bundle = getIntent().getExtras();
		// imageUrls = bundle.getStringArray(Extra.IMAGES);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.imageloadingnew).showImageForEmptyUri(R.drawable.imageloadingnew).showImageOnFail(R.drawable.imageloadingnew).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		Intent intent = getIntent();

		SelectedTag = intent.getStringExtra("Key_Tag");
		SelectedSearch = intent.getStringExtra("Key_search");
		SelectedCount = intent.getStringExtra("Key_count");
		tv14.setText(SelectedTag);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		TempTag = SelectedTag.replace(" ", "%20");
		System.out.println("Tempt" + TempTag);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, 0);
			}
		});
		if (chkNet.isNetworkConnected()) {
			params = new HashMap<String, String>();
			params.put("api", "TagsDetail2");
			params.put("Tags", TempTag);
			params.put("SearchTerm", SelectedSearch);
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			volleyParser.makeStringReq(Constant.Main_URL, params, vTag);
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
		btnStats.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchTagsDetail.this, StatsScreen.class);
				intent.putExtra("Key_Tag", SelectedTag);
				intent.putExtra("Key_search", SelectedSearch);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("StatsScreen", intent);
			}
		});

		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {

				startIndex = 0;
				TagPost.clear();
				// new GetTagData().execute();
				if (chkNet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "TagsDetail2");
					params.put("Tags", TempTag);
					params.put("SearchTerm", SelectedSearch);
					params.put("User_ID", User_ID);
					params.put("start", String.valueOf(startIndex));
					params.put("end", "15");
					params.put("totalrec", "15");
					volleyParser.makeStringReq(Constant.Main_URL, params, vTag);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// new GetTagData().execute();
				if (chkNet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "TagsDetail2");
					params.put("Tags", TempTag);
					params.put("SearchTerm", SelectedSearch);
					params.put("User_ID", User_ID);
					params.put("start", String.valueOf(startIndex));
					params.put("end", "15");
					params.put("totalrec", "15");
					volleyParser.makeStringReq(Constant.Main_URL, params, vTag);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}

		});

	}

	VolleyCallback vTag = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			parse_setDataTagDetail(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};

	static class ViewHolder {
		ImageView imageViewRate, imageViewPost, imageViewGradient, imgViewPostVideo;
		ProgressBar progressBar;
		TextView txtRatelbl, txtRateCount;
		RelativeLayout relGradient;
	}

	public void setTheAdapterWithArray(final ArrayList<HashMap<String, Object>> arrayList) {

		if (objImageAdapter.getCount() <= 15) {
			gridView.setAdapter(objImageAdapter);
			System.out.println("Adapter First===");
		} else {
			objImageAdapter.notifyDataSetChanged();
			System.out.println("Adapter secondTime");
		}

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("TAG", "selected pos" + position);
				Intent intent = new Intent(SearchTagsDetail.this, PostDetail.class);
				intent.putExtra("PostData", TagPost);
				intent.putExtra("CheckedIndex", position);
				intent.putExtra("SelectedPostData", TagPost.get(position));
				intent.putExtra("TotalCheckedIndex", TagPost.size());
				intent.putExtra("progress", 0);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("PostDetail", intent);

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityManage.isFromPostDetail) {

			try {

				if (ActivityManage.isForDelete) {

					TagPost.remove(ActivityManage.deletePostion);
					objImageAdapter.notifyDataSetChanged();

				}

			} catch (Exception e) {

			}
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
				imageLoader.displayImage(items.get(position).get("Post_ImageSquare").toString(), holder.imageViewPost, options);
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

				// imageLoader.displayImage(img, userPostImage, options1,
				// animateFirstListener);
			}
			/*
			 * 1 -Hate 2-Dislike 3-Whatever 4-Like 5-Love
			 */
			if (holder.imageViewRate != null) {
				int ratcount = Integer.valueOf(items.get(position).get("Average_Rating").toString());
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
				holder.txtRateCount.setText(rating + " Rates");
				holder.txtRateCount.setTypeface(SplashScreen.ProxiNova_Regular);

			}

			return view;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("TAG", "Activity Result");
		if (requestCode == ACITIVITY_RESULT_POSTDETAIL) {

			if (data != null) {
				TagPost.remove(data.getIntExtra("deletePostion", 0));
				objImageAdapter.notifyDataSetChanged();
			}

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

	public void parse_setDataTagDetail(String NewestParse) {
		try {
			JSONArray jsonMyFeed = new JSONArray(NewestParse);
			if (jsonMyFeed.length() > 0) {

				startIndex += 15;

				for (int s = 0; s < jsonMyFeed.length(); s++) {
					ArrayList<HashMap<String, String>> NewestPostCommentsData1 = new ArrayList<HashMap<String, String>>();
					ArrayList<HashMap<String, String>> NewestPostRateData1 = new ArrayList<HashMap<String, String>>();
					JSONObject myFeedData = jsonMyFeed.getJSONObject(s);
					// System.out.println("myFeed-->"+myFeedData);
					/*** Post **/
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
					// System.out.println("user-->"+user);
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
					map.put("Post_VideoURL", Post_VideoURL);
					map.put("Post_IsVideo", Post_IsVideo);
					map.put("Post_Content", Post_Content);
					map.put("Post_RecentlyModifiedDate", Post_RecentlyModifiedDate);
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
					map.put("Count_Rating", Count_Rating);
					map.put("Sum_Rating", Sum_Rating);
					map.put("Average_Rating", Average_Rating);
					map.put("CommentsCount", CommentsCount);
					map.put("User_Rated", User_Rated);

					// NewestPost.add(map);
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
							NewestPostCommentsData1.add(map1);
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
							NewestPostRateData1.add(map1);
						}
					}
					map.put("flag", false);
					map.put("isVideoPlaying", false);
					map.put("Rate", NewestPostRateData1);
					map.put("Comments", NewestPostCommentsData1);
					TagPost.add(map);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mPullRefreshGridView.onRefreshComplete();

		if (TagPost.size() == 0) {
			Toast.makeText(getApplicationContext(), "No More Post To Load", Toast.LENGTH_SHORT).show();
		} else {
			setTheAdapterWithArray(TagPost);
		}
	}
}
