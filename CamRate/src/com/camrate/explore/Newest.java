package com.camrate.explore;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.MyFeedAdapter;
import com.camrate.PostDetail;
import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.GPSTracker;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.camrate.tools.VolleyJsonParser;
import com.camrate.tools.VolleyJsonParser.VolleyCallback;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Newest extends Fragment {

	Context context;

	public static final String Category_ID = "Category_ID";
	public static final String Category_Name = "Category_Name";
	ImageView ivIcon;
	TextView tvItemName;
	Constant c = new Constant();
	String User_ID;
	int width, height;
	int startIndex;
	DisplayImageOptions options;
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONArray objmyFeedJson;
	String NewestParse;
	RelativeLayout.LayoutParams paramImage, paramImage1, paramImage2;
	ArrayList<HashMap<String, Object>> NewestPost;
	ArrayList<HashMap<String, String>> NewestPostCommentsData = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> NewestPostRateData = new ArrayList<HashMap<String, String>>();

	ImageLoader imageLoader = ImageLoader.getInstance();

	GPSTracker gps;

	private int TAG = 0;
	TextView txtFooterView;
	double latitude, longitude;
	int radius = 0;
	SharedPreferences prefs;
	int ACITIVITY_RESULT_POSTDETAIL = 1;

	private PullToRefreshGridView mPullRefreshGridView;
	private GridView gridView;
	private PullToRefreshListView mPullRefreshListView;
	ListView listView;
	ImageAdapter gridViewAdapter;
	MyFeedAdapter listViewAdapter;
	LinearLayout linear_list, linear_grid;
	int postIndex;
	Dialog dialog;
	VolleyJsonParser volleyParser;
	checkInternet chkNet;
	HashMap<String, String> params;

	public Newest() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.newestgrid, container, false);
		context = getActivity().getParent();

		linear_list = (LinearLayout) view.findViewById(R.id.linear_list);
		linear_grid = (LinearLayout) view.findViewById(R.id.linear_grid);

		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.listGridOff);
		listView = mPullRefreshListView.getRefreshableView();
		pd = (ProgressBar) view.findViewById(R.id.progressBar1);
		txtFooterView = (TextView) view.findViewById(R.id.txtfooter);
		TAG = getArguments().getInt("tag");

		mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.gridview);
		gridView = mPullRefreshGridView.getRefreshableView();

		init();
		boolean myGridIsOn = getArguments().getBoolean("gridon");
		if (myGridIsOn) {
			linear_list.setVisibility(View.GONE);
			linear_grid.setVisibility(View.VISIBLE);
		} else {
			linear_list.setVisibility(View.VISIBLE);
			linear_grid.setVisibility(View.GONE);
		}
		gridView.invalidate();
		listView.invalidate();

		return view;
	}

	public void init() {
		NewestPost = new ArrayList<HashMap<String, Object>>();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		Display display = ((SlidingDrawerActivity) getActivity()).getWindowManager().getDefaultDisplay();

		width = display.getWidth();
		height = display.getHeight();
		paramImage = new RelativeLayout.LayoutParams((width - 20) / 3, (width - 20) / 3);
		paramImage1 = new RelativeLayout.LayoutParams((width - 20) / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		paramImage2 = new RelativeLayout.LayoutParams((width - 20) / 3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		int round = (width - 20) / 3;
		prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "");
		gps = new GPSTracker(context);
		radius = prefs.getInt("radius", 5);

		listViewAdapter = new MyFeedAdapter(context, R.layout.myfeed_list, NewestPost);
		gridViewAdapter = new ImageAdapter(NewestPost);
		volleyParser = new VolleyJsonParser(context);
		chkNet = new checkInternet(context);
		if (chkNet.isNetworkConnected()) {
			try {
				if (gps.canGetLocation()) {
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
				} else {

					if (prefs.getBoolean("isGpsDialog|_Visible", true)) {
						gps.showSettingsAlert();
					} else {
						latitude = 23.00;
						longitude = 72.00;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.imageloadingnew).showImageForEmptyUri(R.drawable.imageloadingnew).showImageOnFail(R.drawable.imageloadingnew).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				System.out.println("checkedIndex===>" + position);

				Intent intent = new Intent(getActivity(), PostDetail.class);
				intent.putExtra("PostData", NewestPost);
				intent.putExtra("CheckedIndex", position);
				intent.putExtra("SelectedPostData", NewestPost.get(position));
				intent.putExtra("TotalCheckedIndex", NewestPost.size());
				intent.putExtra("progress", "0");
				TabGroupActivity parentActivity = (TabGroupActivity) getActivity().getParent();
				parentActivity.startChildActivity("PostDetail", intent);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position - 1;
				Log.d("TAG", "selected pos" + position);
				Intent intent = new Intent(getActivity(), PostDetail.class);
				intent.putExtra("PostData", NewestPost);
				intent.putExtra("CheckedIndex", position);
				intent.putExtra("SelectedPostData", NewestPost.get(position));
				intent.putExtra("TotalCheckedIndex", NewestPost.size());
				intent.putExtra("progress", 0);

				TabGroupActivity parentActivity = (TabGroupActivity) getActivity().getParent();
				parentActivity.startChildActivity("PostDetail", intent);

			}
		});

		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {

				startIndex = 0;
				NewestPost.clear();
				// new GetNewestData().execute();
				params = getApiParams(TAG);
				if (chkNet.isNetworkConnected()) {
					volleyParser.makeStringReq(Constant.Main_URL, params, v);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// new GetNewestData().execute();
				params = getApiParams(TAG);
				if (chkNet.isNetworkConnected()) {
					volleyParser.makeStringReq(Constant.Main_URL, params, v);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}

		});

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (mPullRefreshListView.getCurrentMode() == Mode.PULL_FROM_START) {
					startIndex = 0;
					NewestPost.clear();
					// new GetNewestData().execute();
					params = getApiParams(TAG);
					if (chkNet.isNetworkConnected()) {
						volleyParser.makeStringReq(Constant.Main_URL, params, v);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				} else {
					// new GetNewestData().execute();
					params = getApiParams(TAG);
					if (chkNet.isNetworkConnected()) {
						volleyParser.makeStringReq(Constant.Main_URL, params, v);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				}

			}
		});

		// new GetNewestData().execute();
		params = getApiParams(TAG);
		if (chkNet.isNetworkConnected()) {
			volleyParser.makeStringReq(Constant.Main_URL, params, v);
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		ActivityManage.lastSeletedTab = 1;
		radius = prefs.getInt("radius", 5);
		System.out.println("radius==>" + radius);

		if (ActivityManage.isFromPostDetail) {
			try {

				if (ActivityManage.isForDelete) {
					NewestPost.remove(ActivityManage.deletePostion);
					listViewAdapter.notifyDataSetChanged();
					gridViewAdapter.notifyDataSetChanged();
				} else {
					ActivityManage.isFromPostDetail = false;
					postIndex = ActivityManage.postIndex;
					String postID = ActivityManage.Post_ID;
					// new getPostAfterRate().execute(postID);
					if (chkNet.isNetworkConnected()) {
						params = new HashMap<String, String>();
						params.put("api", "PostDetail");
						params.put("User_ID", User_ID);
						params.put("Post_ID", postID);
						volleyParser.makeStringReq(Constant.Main_URL, params, vpost);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {

			}
		} else if (ActivityManage.isFromComment) {
			try {
				ActivityManage.isFromComment = false;
				postIndex = ActivityManage.postIndex;
				String postID = ActivityManage.Post_ID;
				// new getPostAfterRate().execute(postID);
				if (chkNet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "PostDetail");
					params.put("User_ID", User_ID);
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
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		System.out.println("onSave");
		// outState.putSerializable("exploredata", NewestPost);
	}

	static class ViewHolder {
		ImageView imageViewRate, imageViewPost, imageViewGradient, imgViewPostVideo;
		ProgressBar progressBar;
		TextView txtRatelbl, txtRateCount;
		RelativeLayout relGradient;
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

	public void setTheAdapterWithArrayInList() {

		if (listViewAdapter.getCount() <= 15) {
			listViewAdapter = new MyFeedAdapter(context, R.layout.myfeed_list, NewestPost);
			listView.setAdapter(listViewAdapter);
		} else {
			listViewAdapter.notifyDataSetChanged();
			System.out.println("Adapter secondTime");
		}

	}

	public void setTheAdapterWithArray() {

		if (gridViewAdapter.getCount() <= 15) {
			gridViewAdapter = new ImageAdapter(NewestPost);
			((GridView) gridView).setAdapter(gridViewAdapter);
		} else {
			gridViewAdapter.notifyDataSetChanged();
			System.out.println("Adapter secondTime");
		}

	}

	public void setGridOn(boolean isGridOn) {
		if (isGridOn) {
			linear_list.setVisibility(View.GONE);
			linear_grid.setVisibility(View.VISIBLE);
		} else {
			linear_list.setVisibility(View.VISIBLE);
			linear_grid.setVisibility(View.GONE);
		}
	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, Object>> items;

		ImageAdapter(ArrayList<HashMap<String, Object>> items) {
			this.items = items;
			inflater = ((Activity) context).getLayoutInflater();
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
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
				view = ((SlidingDrawerActivity) getActivity()).getLayoutInflater().inflate(R.layout.explorer_pager_list, parent, false);
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
				if (video.equalsIgnoreCase("1")) {
					holder.imgViewPostVideo.setVisibility(View.VISIBLE);
				} else {
					holder.imgViewPostVideo.setVisibility(View.INVISIBLE);
				}

			}
			if (holder.imageViewRate != null) {
				int ratcount = Integer.valueOf(items.get(position).get("Average_Rating").toString());
				switch (ratcount) {
				case 5:
					holder.imageViewRate.setImageResource(R.drawable.five_star_grid);
					break;
				case 4:
					holder.imageViewRate.setImageResource(R.drawable.four_star_grid);
					break;
				case 3:
					holder.imageViewRate.setImageResource(R.drawable.three_star_grid);
					break;
				case 2:
					holder.imageViewRate.setImageResource(R.drawable.two_star_grid);
					break;
				case 1:
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("TAG", "Activity Result");
		if (requestCode == ACITIVITY_RESULT_POSTDETAIL) {

			if (data != null) {
				try {
					NewestPost.remove(data.getIntExtra("deletePostion", 0));
					listViewAdapter.notifyDataSetChanged();
					gridViewAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

	public HashMap<String, String> getApiParams(int Tag) {
		HashMap<String, String> params = new HashMap<String, String>();
		switch (TAG) {
		case 0:
			params.put("api", "Trending");
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			return params;
		case 1:
			params.put("api", "Newest");
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			return params;
		case 2:
			params.put("api", "MostReRated");
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			return params;
		case 3:
			params.put("api", "NearBy");
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			params.put("latitude", String.valueOf(latitude));
			params.put("longitude", String.valueOf(longitude));
			params.put("unit", String.valueOf(radius));
			return params;
		case 5:
			params.put("api", "GetMyFavouritePost");
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			return params;
		default:
			params.put("api", "Trending");
			params.put("User_ID", User_ID);
			params.put("start", String.valueOf(startIndex));
			params.put("end", "15");
			params.put("totalrec", "15");
			return params;
		}
	}

	public void parse_setData(String NewestParse) {
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
					NewestPost.add(map);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mPullRefreshGridView.onRefreshComplete();
		mPullRefreshListView.onRefreshComplete();
		try {
			((SlidingDrawerActivity) getActivity()).dismissDialog();
		} catch (Exception e) {

		}
		setTheAdapterWithArrayInList();
		setTheAdapterWithArray();
	}

	public void parse_setDataAfterPost(String refresh_PostData) {
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
			NewestPost.set(postIndex, map);
			Log.d("TAG", "post index " + postIndex);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		listViewAdapter.notifyDataSetChanged();
		gridViewAdapter.notifyDataSetChanged();
	}
}
