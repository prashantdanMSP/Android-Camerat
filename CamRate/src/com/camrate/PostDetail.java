package com.camrate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.camrate.global.Base64;
import com.camrate.global.CircleDisplay;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.camrate.tools.ImageDownloader;
import com.camrate.tools.ImageDownloader.OnTaskCompleted;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PostDetail extends Activity {

	ArrayList<HashMap<String, Object>> myFeedPostDetail;
	ArrayList<HashMap<String, Object>> myFeedPostDetailRefresh;
	HashMap<String, Object> mySelectedFeedDetail;
	ArrayList<HashMap<String, String>> RateDetail;
	ArrayList<HashMap<String, String>> CommentsDetail;
	DisplayImageOptions options, options1;
	ImageLoader imageLoader = ImageLoader.getInstance();
	String Avg_Rate;
	private Resources mRes;
	int TotalRate, AvgRate;
	TextView tvUserName, tvUserLoc, tvPostName, tvPostDesc, tvRating, tvlastseen, tvCommentUserName, tvCommentDesc, tvTotalCommentlbl, tvisRated, txttapratetoexpand, txtlblpostdetail;
	ImageView userImage, userPostImage, userLevel, userRate, userInvestorBadge, userisPostVideo;
	Button btnRate, btnComment, btnStat, btnShare, btnmore;
	int LoveCount = 0, LikeCount = 0, DislikeCount = 0, WhateverCount = 0, HateCount = 0;
	ExpandableListView ratingListView;
	ExpandableListAdapter exptAdpater;
	ListView lstComments;
	String DecodedComment;;
	int postIndex = 0, TotalIndex = 0;
	Button btnEdit;
	JSONParser parser = new JSONParser();
	Function fun;
	String UserID;
	int width, height;
	ProgressBar progressBar;
	RelativeLayout.LayoutParams paramImage;
	private UiLifecycleHelper uiHelper;
	private GestureDetector gestureDetector, gestureDetector1;
	private static final List<String> PERMISSIONS = Arrays.asList("email");
	private static final List<String> PERMISSIONS2 = Arrays.asList("publish_actions");
	Context context;
	boolean isPostDetailExpanded = true;
	String strUserId;

	String strPostId;
	String userName;
	String strPostTag;
	String strPostContent;
	Constant c = new Constant();
	private static final String TAG = PostDetail.class.getSimpleName();
	private CircleDisplay mCircleDisplay;
	int prog = 0;
	double lat = 0;
	double lng = 0;
	RelativeLayout.LayoutParams paramimage;
	RelativeLayout.LayoutParams paramSeekBar;
	LinearLayout linearRating;
	int[] a = new int[] { 0, 25, 50, 75, 100 };
	int rating_rate = 3;
	ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
	ImageView imgBubble1, imgSubmitRate, imgisRated;
	SeekBar seekBar;
	RelativeLayout relSlider;
	float x = 0;
	float y = 0;
	float center_point = 0;
	String postid = "";
	ProgressBar pd;
	Constant con = new Constant();
	RelativeLayout relative_TapToExpand;
	ImageView imgExpandArrow, imgpauseVideo;
	int ACTIVITY_RESULT_COMMENT = 0, ACTIVITY_RESULT_EDITPOST = 2;
	boolean isFromDelete;
	VideoView videoview;
	int videoposition = 1;
	int pos;
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		init();
		// share = new Sharing(PostDetail.this);

	}

	public void init() {
		initProcessDialog();
		ActivityManage.isFromPostDetail = true;
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Post Stats");

		Button btnBack = (Button) findViewById(R.id.button2);
		btnBack.setVisibility(View.VISIBLE);
		btnEdit = (Button) findViewById(R.id.button1);

		mCircleDisplay = (CircleDisplay) findViewById(R.id.circleDisplay);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		Display display = getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		System.out.println("Width--->" + width);
		System.out.println("Height--->" + height);
		System.out.println("IMageview Height and width--->" + (width - 10));
		paramImage = new RelativeLayout.LayoutParams((width - 10), (width - 10));
		int round = (width - 20) / 2;

		context = getApplicationContext();
		fun = new Function(getParent());
		Intent intent = getIntent();

		gestureDetector1 = new GestureDetector(new SwipeGestureDetector());
		// gestureDetector = new GestureDetector(simpleOnGestureListener);
		myFeedPostDetail = new ArrayList<HashMap<String, Object>>();
		mySelectedFeedDetail = new HashMap<String, Object>();
		myFeedPostDetail = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("PostData");
		mySelectedFeedDetail = (HashMap<String, Object>) intent.getSerializableExtra("SelectedPostData");
		pos = getIntent().getIntExtra("CheckedIndex", 0);
		postIndex = pos;
		TotalIndex = intent.getIntExtra("TotalCheckedIndex", 0);
		prog = intent.getIntExtra("progress", 0);
		System.out.println("progress======>" + prog);
		mCircleDisplay.showValue(prog, 100, false);
		if (prog == 100) {
			mCircleDisplay.setVisibility(View.GONE);
		}
		// postIndex=TotalIndex-1;
		System.out.println("Total-->" + TotalIndex);
		// System.out.println("FeedPost-->" + myFeedPostDetail);
		System.out.println("SelectedPost-->" + mySelectedFeedDetail);
		System.out.println("DePosition-->" + pos);
		String isVideo = mySelectedFeedDetail.get("Post_IsVideo").toString();
		System.out.println("isvideo==>" + isVideo);

		imageLoader.init(ImageLoaderConfiguration.createDefault(getParent()));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		options1 = new DisplayImageOptions.Builder().showImageOnLoading(null).showImageForEmptyUri(null).showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		tvUserName = (TextView) findViewById(R.id.tvusername);
		tvUserLoc = (TextView) findViewById(R.id.tvuserlocation);
		tvPostName = (TextView) findViewById(R.id.tvpostname);
		tvPostDesc = (TextView) findViewById(R.id.tvpostdesc);
		// tvCommentCount = (TextView) findViewById(R.id.tvTotalCommentsFeed);
		tvRating = (TextView) findViewById(R.id.tvRateName);
		tvlastseen = (TextView) findViewById(R.id.tvlastseen);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		// mPie = (PieDonutChart) findViewById(R.id.imguserrate1);
		tvisRated = (TextView) findViewById(R.id.txtlblisRated);
		imgisRated = (ImageView) findViewById(R.id.imgisRated);
		// mPie.setLayoutParams(paramImage);
		userImage = (ImageView) findViewById(R.id.userimg);
		userPostImage = (ImageView) findViewById(R.id.imguserpost);
		// userPostImage.setLayoutParams(paramsImage1);
		userLevel = (ImageView) findViewById(R.id.img_levelTag);
		userRate = (ImageView) findViewById(R.id.imguserrate);
		btnRate = (Button) findViewById(R.id.bntRateFeed);
		btnComment = (Button) findViewById(R.id.btnCommentFeed);
		btnStat = (Button) findViewById(R.id.btnStatsFeed);
		btnShare = (Button) findViewById(R.id.btnShareFeed);
		btnmore = (Button) findViewById(R.id.btnmorefeed);
		btnComment.setTypeface(SplashScreen.ProxiNova_Bold);
		btnStat.setTypeface(SplashScreen.ProxiNova_Bold);
		btnShare.setTypeface(SplashScreen.ProxiNova_Bold);
		tvCommentUserName = (TextView) findViewById(R.id.tvcommentusername);
		tvCommentDesc = (TextView) findViewById(R.id.tvdesccomment);
		tvTotalCommentlbl = (TextView) findViewById(R.id.tvTotalcomment);
		// userInvestorBadge=(ImageView)findViewById(R.id.imginvestorbadge);
		userisPostVideo = (ImageView) findViewById(R.id.imguserpostvideo);
		txttapratetoexpand = (TextView) findViewById(R.id.txtlblTapratetoexpand);
		txtlblpostdetail = (TextView) findViewById(R.id.txtlblPostdetail);
		ratingListView = (ExpandableListView) findViewById(R.id.lvExp);
		lstComments = (ListView) findViewById(R.id.listComment);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		imgStar1 = (ImageView) findViewById(R.id.imgStar1);
		imgStar2 = (ImageView) findViewById(R.id.imgStar2);
		imgStar3 = (ImageView) findViewById(R.id.imgStar3);
		imgStar4 = (ImageView) findViewById(R.id.imgStar4);
		imgStar5 = (ImageView) findViewById(R.id.imgStar5);
		imgBubble1 = (ImageView) findViewById(R.id.imgbubble1);
		imgSubmitRate = (ImageView) findViewById(R.id.imgSubmitRated);
		linearRating = (LinearLayout) findViewById(R.id.ratingStar);

		relSlider = (RelativeLayout) findViewById(R.id.relative_slider);
		relative_TapToExpand = (RelativeLayout) findViewById(R.id.relativepost);
		imgExpandArrow = (ImageView) findViewById(R.id.imgPostdetailarrow);
		videoview = (VideoView) findViewById(R.id.VideoView);
		imgpauseVideo = (ImageView) findViewById(R.id.imguserpausevideo);
		// imgSubmitRate.setVisibility(View.GONE);
		imgStar1.setId(0);
		imgStar2.setId(1);
		imgStar3.setId(2);
		imgStar4.setId(3);
		imgStar5.setId(4);
		// x=206;\
		String user_rate = mySelectedFeedDetail.get("User_Rated").toString();
		ViewTreeObserver vto = null;
		if (user_rate.equalsIgnoreCase("1")) {
			vto = imgStar1.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar1.getId());
			imgBubble1.setImageResource(R.drawable.bubble1);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star_unsel);
			imgStar3.setImageResource(R.drawable.star_unsel);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);
		} else if (user_rate.equalsIgnoreCase("2")) {
			vto = imgStar2.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar2.getId());
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star_unsel);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);
			imgBubble1.setImageResource(R.drawable.bubble2);
		} else if (user_rate.equalsIgnoreCase("3")) {
			vto = imgStar3.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar3.getId());
			imgBubble1.setImageResource(R.drawable.bubble3);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);
		} else if (user_rate.equalsIgnoreCase("4")) {
			vto = imgStar4.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar4.getId());
			imgBubble1.setImageResource(R.drawable.bubble4);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star);
			imgStar5.setImageResource(R.drawable.star_unsel);
		} else if (user_rate.equalsIgnoreCase("5")) {
			vto = imgStar5.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar5.getId());
			imgBubble1.setImageResource(R.drawable.bubble5);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star);
			imgStar5.setImageResource(R.drawable.star);
		} else {
			vto = imgStar3.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar3.getId());
			imgBubble1.setImageResource(R.drawable.bubble3);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);

		}
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {

				paramSeekBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				paramSeekBar.addRule(RelativeLayout.BELOW, linearRating.getId());
				paramSeekBar.addRule(RelativeLayout.CENTER_HORIZONTAL);
				paramSeekBar.setMargins(0, 10, 0, 0);
				seekBar.setLayoutParams(paramSeekBar);
				// x = imgStar3.getLeft();
				// setImageXY(x, imgStar3.getWidth());
				switch (seekBar.getProgress()) {
				case 0:
					setImageXY(imgStar1.getLeft(), imgStar1.getMeasuredWidth());
					break;
				case 25:
					setImageXY(imgStar2.getLeft(), imgStar2.getMeasuredWidth());
					break;
				case 50:
					setImageXY(imgStar3.getLeft(), imgStar3.getMeasuredWidth());
					break;
				case 75:
					setImageXY(imgStar4.getLeft(), imgStar4.getMeasuredWidth());
					break;
				case 100:
					setImageXY(imgStar5.getLeft(), imgStar5.getMeasuredWidth());
					break;
				default:
					break;
				}
				return true;
			}
		});

		mRes = getResources();
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");
		Avg_Rate = mySelectedFeedDetail.get("Average_Rating").toString();
		if (checkUser_ID(UserID, mySelectedFeedDetail.get("User_ID").toString())) {
			btnEdit.setText("Edit");
			btnEdit.setVisibility(View.VISIBLE);
			// btnEdit.setTypeface(SplashScreen.ProxiNova_Bold);
		} else {
			btnEdit.setVisibility(View.GONE);
		}
		if (isVideo.equalsIgnoreCase("1")) {
			userisPostVideo.setVisibility(View.VISIBLE);
			userisPostVideo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Intent intent1 = new Intent(PostDetail.this, VideoViewActivity.class);
					// intent1.putExtra("Video_URL", mySelectedFeedDetail.get("Post_VideoURL").toString());
					// startActivity(intent1);
					// overridePendingTransition(0, 0);
					userisPostVideo.setVisibility(View.INVISIBLE);
					userPostImage.setVisibility(View.INVISIBLE);
					videoview.setVisibility(View.VISIBLE);
					String Video_Url = mySelectedFeedDetail.get("Post_VideoURL").toString();
					new playingVideo().execute(Video_Url);
				}
			});

		} else {
			userisPostVideo.setVisibility(View.INVISIBLE);
		}
		userPostImage.setLayoutParams(paramImage);
		videoview.setLayoutParams(paramImage);
		/*
		 * mPie.addItem(0, mRes.getColor(R.color.Love)); mPie.addItem(20, mRes.getColor(R.color.Like)); mPie.addItem(20, mRes.getColor(R.color.whatever)); mPie.addItem(10, mRes.getColor(R.color.Dislike)); mPie.addItem(40, mRes.getColor(R.color.hate));
		 */
		tvUserName.setText(mySelectedFeedDetail.get("User_Name").toString());
		tvUserLoc.setText(mySelectedFeedDetail.get("Post_Location").toString());
		tvPostName.setText(mySelectedFeedDetail.get("Post_Title").toString());
		String strPostDesc = mySelectedFeedDetail.get("Post_Content").toString();
		tvPostDesc.setText(strPostDesc);
		// tvCommentCount.setText(mySelectedFeedDetail.get("CommentsCount").toString());
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		tvUserName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		tvPostName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		tvPostDesc.setTypeface(SplashScreen.ProxiNova_Regular);
		txttapratetoexpand.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblpostdetail.setTypeface(SplashScreen.ProxiNova_Regular);
		String commentscount = mySelectedFeedDetail.get("CommentsCount").toString();
		// btnComment.setText("Comments (" + commentscount + ")");
		btnComment.setText("Comments");
		CommentsDetail = new ArrayList<HashMap<String, String>>();
		CommentsDetail = (ArrayList<HashMap<String, String>>) mySelectedFeedDetail.get("Comments");
		if (!mySelectedFeedDetail.get("CommentsCount").toString().equalsIgnoreCase("0")) {
			int TotalComments = Integer.parseInt(mySelectedFeedDetail.get("CommentsCount").toString());

			tvTotalCommentlbl.setText(TotalComments + " Comments");
			if (TotalComments == 0) {
				tvTotalCommentlbl.setVisibility(View.INVISIBLE);
				lstComments.setVisibility(View.GONE);
			}
			if (TotalComments > 2) {
				lstComments.setVisibility(View.VISIBLE);
				if (CommentsDetail.size() > 0) {
					setTheAdapterWithArray(CommentsDetail);
				}
				// tvTotalCommentlbl.setVisibility(View.VISIBLE);

			} else {
				lstComments.setVisibility(View.VISIBLE);
				// tvTotalCommentlbl.setVisibility(View.INVISIBLE);
				setTheAdapterWithArray(CommentsDetail);

			}
			// tvTotalCommentlbl.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Intent intent = new Intent(PostDetail.this,
			// CommentActivity.class);
			// intent.putExtra("Post_ID",
			// mySelectedFeedDetail.get("Post_ID").toString());
			// startActivity(intent);
			// overridePendingTransition(0, 0);
			// }
			// });

		}
		tvUserName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fun.isAuthor(mySelectedFeedDetail.get("User_ID").toString(), UserID)) {
					Intent intent = new Intent(getParent(), UserProfileChild.class);
					intent.putExtra("User_ID", mySelectedFeedDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("UserProfileChild", intent);
				} else {
					Intent intent = new Intent(getParent(), GeneralUserProfile.class);
					intent.putExtra("User_ID", mySelectedFeedDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("GeneralUserProfile", intent);
				}
			}
		});
		userImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fun.isAuthor(mySelectedFeedDetail.get("User_ID").toString(), UserID)) {
					Intent intent = new Intent(getParent(), UserProfileChild.class);
					intent.putExtra("User_ID", mySelectedFeedDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("UserProfileChild", intent);
				} else {
					Intent intent = new Intent(getParent(), GeneralUserProfile.class);
					intent.putExtra("User_ID", mySelectedFeedDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("GeneralUserProfile", intent);
				}
			}
		});
		tvUserLoc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String Post_lat = mySelectedFeedDetail.get("Post_Latitude").toString();
				String Post_long = mySelectedFeedDetail.get("Post_Longitude").toString();
				String Post_date = mySelectedFeedDetail.get("Post_Date").toString();
				String Post_tag = mySelectedFeedDetail.get("Post_Tags").toString();
				String Post_location = mySelectedFeedDetail.get("Post_Location").toString();
				String Average_Rating = mySelectedFeedDetail.get("Average_Rating").toString();
				String Post_ImageSquare = mySelectedFeedDetail.get("Post_ImageSquare").toString();
				String User_Name = mySelectedFeedDetail.get("User_Name").toString();

				if (!(Post_lat.equals("") && Post_long.equals(""))) {
					lat = Double.parseDouble(Post_lat);

					lng = Double.parseDouble(Post_long);

					Intent i = new Intent(getParent(), PostLocation_Detail.class);
					i.putExtra("Post_Latitude", lat);
					i.putExtra("Post_Longitude", lng);
					i.putExtra("Post_Date", Post_date);
					i.putExtra("Post_Tags", Post_tag);
					i.putExtra("Post_Location", Post_location);
					i.putExtra("User_Name", User_Name);
					i.putExtra("Post_ImageSquare", Post_ImageSquare);
					i.putExtra("Average_Rating", Average_Rating);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("PostLocation_Detail", i);
				}

				else {
					Toast.makeText(c, "Please enter latitude and longitude", Toast.LENGTH_SHORT).show();
				}

			}
		});
		String count_rate = mySelectedFeedDetail.get("Count_Rating").toString();
		// String rating = count_rate + " Rates";
		String rating = "";
		if (count_rate.equalsIgnoreCase("1")) {
			rating = count_rate + " Rate";
		} else {
			rating = count_rate + " Rates";
		}
		tvRating.setText(rating);
		tvRating.setTypeface(SplashScreen.ProxiNova_Regular);

		String img = (mySelectedFeedDetail.get("User_Imagepath").toString());

		imageLoader.displayImage(img, userImage, options);
		String img1 = (mySelectedFeedDetail.get("Post_ImageSquare").toString());
		String isRated = mySelectedFeedDetail.get("User_Rated").toString();
		if (isRated.equalsIgnoreCase("0")) {
			tvisRated.setVisibility(View.INVISIBLE);
			// holder.tvisRated.setTypeface(SplashScreen.ProxiNova_Regular);
			imgisRated.setVisibility(View.INVISIBLE);
		} else {
			tvisRated.setVisibility(View.VISIBLE);
			tvisRated.setTypeface(SplashScreen.ProxiNova_Regular);
			imgisRated.setVisibility(View.VISIBLE);
		}
		imageLoader.displayImage(img1, userPostImage, options1, new SimpleImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				/*
				 * progressBar.setProgress(0); progressBar.setVisibility(View.VISIBLE);
				 */
				mCircleDisplay.showValue(prog, 100, false);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// progressBar.setVisibility(View.GONE);
				mCircleDisplay.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// progressBar.setVisibility(View.GONE);
				mCircleDisplay.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				// progressBar.setProgress(Math.round(100.0f * current/
				// total));
				mCircleDisplay.showValue(Math.round(100.0f * prog / total), 100, true);
			}
		});
		btnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), CommentActivity.class);
				intent.putExtra("Post_ID", mySelectedFeedDetail.get("Post_ID").toString());

				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("CommentActivity", intent);
			}
		});

		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ShareDialog();
				String imgPath = mySelectedFeedDetail.get("Post_ImageSquare").toString();
				String countRating = mySelectedFeedDetail.get("Count_Rating").toString();
				String AvgRating = mySelectedFeedDetail.get("Average_Rating").toString();
				strUserId = mySelectedFeedDetail.get("User_ID").toString();
				strPostId = mySelectedFeedDetail.get("Post_ID").toString();
				userName = mySelectedFeedDetail.get("User_Name").toString();
				strPostTag = mySelectedFeedDetail.get("Post_Tags").toString();
				strPostContent = mySelectedFeedDetail.get("Post_Content").toString();
				onShareItem(strPostTag, strPostContent, userPostImage, AvgRating, countRating, strPostId);

			}
		});
		btnmore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String imgPath = mySelectedFeedDetail.get("Post_ImageSquare").toString();
				String countRating = mySelectedFeedDetail.get("Count_Rating").toString();
				String AvgRating = mySelectedFeedDetail.get("Average_Rating").toString();

				final String postid = myFeedPostDetail.get(postIndex).get("Post_ID").toString();
				final String userId = myFeedPostDetail.get(postIndex).get("User_ID").toString();

				strUserId = mySelectedFeedDetail.get("User_ID").toString();
				strPostId = mySelectedFeedDetail.get("Post_ID").toString();
				userName = mySelectedFeedDetail.get("User_Name").toString();
				strPostTag = mySelectedFeedDetail.get("Post_Tags").toString();
				strPostContent = mySelectedFeedDetail.get("Post_Content").toString();
				final Dialog dialog = new Dialog(getParent());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.more);

				final TextView textview_fav = (TextView) dialog.findViewById(R.id.txtAddtoFav);

				if (isFavorite(userId, postid)) {
					textview_fav.setText("Remove from Favorite");
				} else {

				}
				LinearLayout linear_delete = (LinearLayout) dialog.findViewById(R.id.linearDelete);
				LinearLayout linear_reportinapp = (LinearLayout) dialog.findViewById(R.id.linearReportInapproriate);
				LinearLayout linear_addtofav = (LinearLayout) dialog.findViewById(R.id.linearAddtoFav);
				LinearLayout linear_alertFollowers = (LinearLayout) dialog.findViewById(R.id.linearAlertFollowers);
				LinearLayout linear_reShare = (LinearLayout) dialog.findViewById(R.id.linearReshare);
				LinearLayout linear_cancel = (LinearLayout) dialog.findViewById(R.id.linearCancel);

				if (isAuthor(userId)) {
					linear_delete.setVisibility(View.VISIBLE);
					linear_reportinapp.setVisibility(View.GONE);
				} else {
					linear_delete.setVisibility(View.GONE);
					linear_reportinapp.setVisibility(View.VISIBLE);
				}
				linear_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						SimpleAlert("CamRate");
					}
				});

				linear_reportinapp.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();

						Intent intent = new Intent(getParent(), ResonForReport.class);
						intent.putExtra("reasonType", "0"); // 0 for post items
						intent.putExtra("Post_ID", mySelectedFeedDetail.get("Post_ID").toString());
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startChildActivity("ResonForReport", intent);

					}
				});
				linear_addtofav.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						String postid = myFeedPostDetail.get(postIndex).get("Post_ID").toString();
						String userId = myFeedPostDetail.get(postIndex).get("User_ID").toString();

						if (textview_fav.getText().toString().equals("Remove from Favorite")) {

							deleteFromFavorite(userId, postid);
						} else {
							addToFavorite(userId, postid);
						}
					}
				});
				linear_alertFollowers.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();

						String postid = myFeedPostDetail.get(postIndex).get("Post_ID").toString();
						String userId = myFeedPostDetail.get(postIndex).get("User_ID").toString();

						Intent intent = new Intent(getParent(), AlertFollowers.class);
						intent.putExtra("User_ID", userId);
						intent.putExtra("Post_ID", postid);
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startActivity(intent);

					}
				});
				linear_reShare.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();

						Intent intent = new Intent(getParent(), RateItScreen1.class);
						intent.putExtra("is_web", "0");
						intent.putExtra("PostData", myFeedPostDetail);
						intent.putExtra("CheckedIndex", pos);
						intent.putExtra("SelectedPostData", mySelectedFeedDetail);
						intent.putExtra("TotalCheckedIndex", myFeedPostDetail.size());
						intent.putExtra("progress", prog);
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startActivity(intent);

					}
				});
				linear_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		});
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), EditPost.class);
				intent.putExtra("editPost_ID", mySelectedFeedDetail.get("Post_ID").toString());
				intent.putExtra("editTag", mySelectedFeedDetail.get("Post_Title").toString());
				intent.putExtra("editDesc", mySelectedFeedDetail.get("Post_Content").toString());
				intent.putExtra("editCategory", mySelectedFeedDetail.get("Post_CategoryID").toString());

				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("EditPost", intent);
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		// imgStar1.setOnClickListener(mclickListner);
		imgStar1.setOnClickListener(mclickListner);
		imgStar2.setOnClickListener(mclickListner);
		imgStar3.setOnClickListener(mclickListner);
		imgStar4.setOnClickListener(mclickListner);
		imgStar5.setOnClickListener(mclickListner);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				setSliderPosition();
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

				Log.e("Progress:", "::>" + arg1);
				seekBar.setProgress(arg1);
				Log.d("Center_Point", ":>" + center_point);

				if (arg1 >= 0 && arg1 <= 12) {
					imgBubble1.setImageResource(R.drawable.bubble1);
					imgStar1.setImageResource(R.drawable.star);
					// seekBar.setProgress(2);
					imgStar2.setImageResource(R.drawable.star_unsel);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 1;
					x = imgStar1.getLeft();
					setImageXY(x, imgStar1.getWidth());
				} else if (arg1 >= 13 && arg1 <= 37) {
					// seekBar.setProgress(18);
					imgBubble1.setImageResource(R.drawable.bubble2);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 2;
					x = imgStar2.getLeft();
					setImageXY(x, imgStar2.getWidth());
				} else if (arg1 >= 38 && arg1 <= 63) {
					// seekBar.setProgress(35);
					imgBubble1.setImageResource(R.drawable.bubble3);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 3;
					x = imgStar3.getLeft();
					setImageXY(x, imgStar3.getWidth());
				} else if (arg1 >= 64 && arg1 <= 89) {
					// seekBar.setProgress(50);
					imgBubble1.setImageResource(R.drawable.bubble4);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 4;
					x = imgStar4.getLeft();
					setImageXY(x, imgStar4.getWidth());
				} else if (arg1 >= 90) {
					// seekBar.setProgress(65);
					imgBubble1.setImageResource(R.drawable.bubble5);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star);
					rating_rate = 5;
					x = imgStar5.getLeft();
					setImageXY(x, imgStar5.getWidth());
				}
				// textview.setText(arg1 + "");

			}
		});

		imgSubmitRate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String post_ID = mySelectedFeedDetail.get("Post_ID").toString();
				new submitRate().execute(post_ID);
			}
		});
		RateDetail = new ArrayList<HashMap<String, String>>();
		RateDetail = (ArrayList<HashMap<String, String>>) mySelectedFeedDetail.get("Rate");
		// System.out.println("ratedetail-->" + RateDetail);
		if (Avg_Rate != null) {
			int ratcount = Integer.valueOf(mySelectedFeedDetail.get("Average_Rating").toString());
			switch (ratcount) {
			case 5:
				userRate.setImageResource(R.drawable.five_star_detail);
				break;
			case 4:
				userRate.setImageResource(R.drawable.four_star_detail);
				break;
			case 3:
				userRate.setImageResource(R.drawable.three_star_detail);
				break;
			case 2:
				userRate.setImageResource(R.drawable.two_star_detail);
				break;
			case 1:
				userRate.setImageResource(R.drawable.one_star_detail);
				break;
			}

		}
		String RateCount = mySelectedFeedDetail.get("Count_Rating").toString();
		if (RateCount != null) {
			TotalRate = Integer.parseInt(RateCount);
			System.out.println("Count_Rating---> " + TotalRate);

		}

		if (RateDetail.size() > 0) {
			for (int i = 0; i < RateDetail.size(); i++) {
				String RatingCount = RateDetail.get(i).get("Rating_Rate").toString();
				if (RatingCount != null) {
					int count = Integer.valueOf(RatingCount);
					switch (count) {
					case 5:
						LoveCount++;
						break;
					case 4:
						LikeCount++;
						break;
					case 3:
						WhateverCount++;
						break;
					case 2:
						DislikeCount++;
						break;
					case 1:
						HateCount++;
						break;
					}

				}
				// RateCalculation(LoveCount, LikeCount, WhateverCount,
				// DislikeCount, HateCount, TotalRate);
			}

		}
		/*
		 * Investor Badge code if(userInvestorBadge!=null) { String User_BagdeValue=mySelectedFeedDetail.get("User_Badge").toString(); if(!User_BagdeValue.equals("NA")) {
		 * 
		 * userInvestorBadge.setVisibility(View.VISIBLE); if(User_BagdeValue.equalsIgnoreCase("SILVER")) { userInvestorBadge.setImageResource(R.drawable.investor_silver); } else if(User_BagdeValue.equalsIgnoreCase("BLACK")) { userInvestorBadge.setImageResource(R.drawable.investor_black); } else
		 * if(User_BagdeValue.equalsIgnoreCase("BRONZE")) { userInvestorBadge.setImageResource(R.drawable.investor_bronze); } else if(User_BagdeValue.equalsIgnoreCase("GOLD")) { userInvestorBadge.setImageResource(R.drawable.investor_gold); }
		 * 
		 * 
		 * } else { userInvestorBadge.setVisibility(View.INVISIBLE); } }
		 */
		// System.out.println("mypie" + mPie.getWidth() + "Height" +
		// mPie.getHeight());
		/*
		 * 0 Love: #ED1F24 Like :#F8991D whatever:#A2C83A Dislike:#28B1E6 hate:#265BA9
		 */

		exptAdpater = new ExpandableListAdapter(getParent(), RateDetail, mySelectedFeedDetail, createParentList(RateDetail));
		// exptAdpater =new ExpandableListAdapter(getApplicationContext(),
		// objPostDetail ,RateDetail);
		ratingListView.setAdapter(exptAdpater);
		// Helper.getExpListViewSize(ratingListView);
		new Helper(getParent()).getExpListViewSize(ratingListView);

		// exptAdpater =new ExpandableListAdapter(getApplicationContext(),
		// mySelectedFeedDetail,RateDetail);
		// ratingListView.setAdapter(exptAdpater);
		// Helper.getListViewSize(ratingListView);
		userPostImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (gestureDetector1.onTouchEvent(event)) {
					return false;
				}
				return true;

			}
		});
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date myDate = simpleDateFormat.parse((mySelectedFeedDetail.get("Post_Date")).toString());
			String diffVal = fun.checkDate_Diffrences(myDate);
			tvlastseen.setText(diffVal);
			tvlastseen.setTypeface(SplashScreen.ProxiNova_Regular);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		relative_TapToExpand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!isPostDetailExpanded) {
					imgExpandArrow.setImageResource(R.drawable.uparrow);
					ratingListView.setVisibility(View.VISIBLE);
					isPostDetailExpanded = true;
				} else {
					imgExpandArrow.setImageResource(R.drawable.downarrow);
					ratingListView.setVisibility(View.INVISIBLE);
					isPostDetailExpanded = false;
				}
			}
		});
	}

	private String getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("mediaplayertmp", "dat");
			temp.deleteOnExit();
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
			byte buf[] = new byte[512];
			do {
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			} while (true);
			try {
				stream.close();
			} catch (IOException ex) {
				Log.e("CamRate", "error: " + ex.getMessage(), ex);
			}
			return tempPath;
		}
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

	private boolean isAuthor(String strPostAuthor2) {
		if (strPostAuthor2.equals(UserID)) {
			return true;
		} else {
			return false;
		}
	}

	protected void addToFavorite(String userId2, String strPostId) {
		String url = con.GetBaseUrl() + "api=AddMyFavouritePost&User_ID=" + userId2 + "&Post_ID=" + strPostId;
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			System.out.println("res==>" + res);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void deleteFromFavorite(String userId2, String strPostId) {
		String url = con.GetBaseUrl() + "api=RemoveFavourite&User_ID=" + userId2 + "&Post_ID=" + strPostId;
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void SimpleAlert(String t) {

		Log.d("TAG", "delete post index " + postIndex);

		Builder builder = new AlertDialog.Builder(getParent());
		builder.setTitle(t);
		builder.setMessage("Are you sure you want to delete this post?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				deletePost(postIndex);

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

	protected void deletePost(final int postindex) {
		String postid = myFeedPostDetail.get(postindex).get("Post_ID").toString();
		String url = con.GetBaseUrl() + "api=PostDelete&Post_ID=" + postid;
		System.out.println(url);
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			if (res.equals("0")) {

				Builder builder = new AlertDialog.Builder(getParent());
				builder.setTitle("CamRate");
				builder.setMessage("Post has not been deleted. Please try again");
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

			} else {

				Builder builder = new AlertDialog.Builder(getParent());
				builder.setTitle("CamRate");
				builder.setMessage("Post has been deleted successfully");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						isFromDelete = true;
						ActivityManage.deletePostion = postindex;
						ActivityManage.isForDelete = true;
						finish();

					}
				});
				AlertDialog alert = builder.create();
				alert.show();
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

	public OnClickListener mclickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case 0:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star_unsel);
				imgStar3.setImageResource(R.drawable.star_unsel);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar1.getId());
				rating_rate = 1;
				x = imgStar1.getLeft();
				setImageXY(x, imgStar1.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble1);
				break;
			case 1:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star_unsel);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar2.getId());
				rating_rate = 2;
				x = imgStar2.getLeft();
				setImageXY(x, imgStar2.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble2);
				break;
			case 2:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar3.getId());
				rating_rate = 3;
				x = imgStar3.getLeft();
				setImageXY(x, imgStar3.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble3);

				break;
			case 3:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar4.getId());
				rating_rate = 4;
				x = imgStar4.getLeft();
				setImageXY(x, imgStar4.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble4);
				break;

			case 4:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star);
				imgStar5.setImageResource(R.drawable.star);
				setBubblleAndSeekBar(imgStar5.getId());
				rating_rate = 5;
				x = imgStar5.getLeft();
				setImageXY(x, imgStar5.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble5);
				break;

			}
		}
	};

	public void setImageXY(float X, int width) {

		// Log.d("X", ":>" + X);
		int tempwidth = (width);
		// Log.d("Width", ":>" + tempwidth);
		center_point = X;
		center_point = center_point - (imgBubble1.getWidth() / 2) + (tempwidth / 2);
		imgBubble1.setX(center_point);

		imgBubble1.invalidate();
	}

	public void setSliderPosition() {

		int diff = 0;
		int finalValue = 0;
		int valueSeek = seekBar.getProgress();
		int storage = 100;
		for (int i = 0; i < a.length; i++) {
			int arrVal = a[i];
			diff = arrVal - valueSeek;
			if (diff < 0) {
				diff = -diff;
			}
			if (diff < storage) {
				storage = diff;
				finalValue = arrVal;
			}

		}

		seekBar.setProgress(finalValue);
	}

	public void setBubblleAndSeekBar(int id) {
		int tag = id * 25;
		seekBar.setProgress(tag);

	}

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {

				Log.d("FacebookSampleActivity", "Facebook session opened");

			} else if (state.isClosed()) {

				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		uiHelper.onResume();
		Session.getActiveSession().isOpened();
		System.out.println("onResume");

		if (ActivityManage.isFromComment) {
			ActivityManage.isFromComment = false;
			String postID = ActivityManage.Post_ID;
			new getPostAfterRate().execute(postID);
		} else if (ActivityManage.isFromEditPost) {
			ActivityManage.isFromEditPost = false;
			String postID = ActivityManage.Post_ID;
			new getPostAfterRate().execute(postID);
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
		uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
		try {

			if (!isFromDelete) {
				ActivityManage.Post_ID = mySelectedFeedDetail.get("Post_ID").toString();
				ActivityManage.postIndex = postIndex;
				ActivityManage.isForDelete = false;
			}
		} catch (Exception e) {

		}
	}

	private boolean checkUser_ID(String userID1, String userID2) {
		if (userID1.equalsIgnoreCase(userID2)) {
			return true;
		} else {
			return false;
		}
		// TODO Auto-generated method stub

	}

	public void ShareDialog() {

		final CharSequence[] items = { "Share Via Email", "Share Via Facebook", "Share Via Twitter", "Report inapropriate", "add to Favorite", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
		builder.setTitle("CamRate");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Share Via Email")) {
					/*
					 * Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); fileUri=getOutputMediaFileUri(REQUEST_CAMERA); intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					 * 
					 * 
					 * startActivityForResult(intent, REQUEST_CAMERA);
					 */
				} else if (items[item].equals("Share Via Facebook")) {
					/*
					 * Intent intent = new Intent(Intent.ACTION_PICK,android.provider .MediaStore.Images.Media.EXTERNAL_CONTENT_URI); intent.setType("image/*"); startActivityForResult(intent,SELECT_FILE);
					 */
				} else if (items[item].equals("Share Via Twitter")) {

				} else if (items[item].equals("Report inapropriate")) {

				} else if (items[item].equals("add to Favorite")) {

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}

	public void RateCalculation(int love, int like, int whatever, int dislike, int hate, int totalcount) {
		/*
		 * mPie.deleteItems(); // mPie.setRatioCircleInside(0.8f); if (totalcount == 0) { totalcount = 1; } mPie.addItem(((love * 100) / totalcount), mRes.getColor(R.color.Love)); mPie.addItem(((like * 100) / totalcount), mRes.getColor(R.color.Like)); mPie.addItem(((whatever * 100) / totalcount),
		 * mRes.getColor(R.color.whatever)); mPie.addItem(((dislike * 100) / totalcount), mRes.getColor(R.color.Dislike)); mPie.addItem(((hate * 100) / totalcount), mRes.getColor(R.color.hate));
		 */

		/*
		 * System.out.println("TotalCount-->" + totalcount); System.out.println("Love-->" + love); System.out.println("Like-->" + like); System.out.println("whatever-->" + whatever); System.out.println("dislike-->" + dislike); System.out.println("hate-->" + hate); System.out.println("Love-->" +
		 * ((love * 100) / totalcount)); System.out.println("Like-->" + ((like * 100) / totalcount)); System.out.println("whatever-->" + ((whatever * 100) / totalcount)); System.out.println("dislike-->" + ((dislike * 100) / totalcount)); System.out.println("hate-->" + ((hate * 100) / totalcount));
		 */
	}

	private class Helper {
		Context cntxt;

		public Helper(Context context) {
			// TODO Auto-generated constructor stub
			this.cntxt = context;
		}

		public void getExpListViewSize(ExpandableListView myListView) {
			ListAdapter myListAdapter = myListView.getAdapter();
			if (myListAdapter == null) {
				// do nothing return null
				return;
			}
			// set listAdapter in loop for getting final size
			int totalHeight = 0;
			for (int size = 0; size < myListAdapter.getCount(); size++) {
				View listItem = myListAdapter.getView(size, null, myListView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			// setting listview item in adapter
			ViewGroup.LayoutParams params = myListView.getLayoutParams();
			params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
			myListView.setLayoutParams(params);
		}

		public void getListViewSize(ListView myListView) {
			ListAdapter myListAdapter = myListView.getAdapter();
			if (myListAdapter == null) {
				// do nothing return null
				return;
			}
			// set listAdapter in loop for getting final size
			int totalHeight = 0;
			for (int size = 0; size < myListAdapter.getCount(); size++) {
				View listItem = myListAdapter.getView(size, null, myListView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			// setting listview item in adapter
			ViewGroup.LayoutParams params = myListView.getLayoutParams();
			// params.height = totalHeight + (myListView.getDividerHeight() *
			// (myListAdapter.getCount() - 1));

			int height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
			// DisplayMetrics displayMetrics =
			// cntxt.getResources().getDisplayMetrics();
			// int dp = Math.round(height / (displayMetrics.xdpi /
			// DisplayMetrics.DENSITY_DEFAULT));
			int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
			System.out.println("DP Height==>" + dp);
			// RelativeLayout.LayoutParams param = new
			// RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, dp);
			params.height = dp;
			myListView.setLayoutParams(params);
			// myListView.invalidate();
			// myListView.invalidateViews();
		}
	}

	public int pxToDp(int px) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			String swipe = "";
			float sensitvity = 5;

			// TODO Auto-generated method stub
			if ((e1.getX() - e2.getX()) > sensitvity) {
				// swipe += "Swipe Left\n";
				// System.out.println(swipe);
				swipeLeft();
			} else if ((e2.getX() - e1.getX()) > sensitvity) {
				// swipe += "Swipe Right\n";
				// System.out.println(swipe);
				swipeRight();
			} else {
				swipe += "\n";
			}

			if ((e1.getY() - e2.getY()) > sensitvity) {
				swipe += "Swipe Up\n";
				System.out.println(swipe);
			} else if ((e2.getY() - e1.getY()) > sensitvity) {
				swipe += "Swipe Down\n";
				System.out.println(swipe);
			} else {
				swipe += "\n";
			}

			// gestureEvent.setText(swipe);

			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};

	public void swipeLeft() {
		if (postIndex > 0) {
			postIndex--;
			System.out.println("left");
			HashMap<String, Object> tempPostDetail = new HashMap<String, Object>();
			tempPostDetail = myFeedPostDetail.get(postIndex);
			UpdateUIWithMyFeedPostDetail(tempPostDetail);
		}
	}

	public void swipeRight() {

		System.out.println("PostIndex===>" + postIndex);
		System.out.println("TotalIndex===>" + TotalIndex);
		if (postIndex < (TotalIndex - 1)) {
			postIndex++;
			System.out.println("right");
			HashMap<String, Object> tempPostDetail = new HashMap<String, Object>();
			tempPostDetail = myFeedPostDetail.get(postIndex);
			UpdateUIWithMyFeedPostDetail(tempPostDetail);
		}
	}

	public Void UpdateUIWithMyFeedPostDetail(final HashMap<String, Object> objPostDetail) {
		int updateLoveCount = 0, updateLikeCount = 0, updateDislikeCount = 0, updateWhateverCount = 0, updateHateCount = 0;
		String user_rate = objPostDetail.get("User_Rated").toString();
		ViewTreeObserver vto = null;
		if (user_rate.equalsIgnoreCase("1")) {
			vto = imgStar1.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar1.getId());
			imgBubble1.setImageResource(R.drawable.bubble1);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star_unsel);
			imgStar3.setImageResource(R.drawable.star_unsel);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);
		} else if (user_rate.equalsIgnoreCase("2")) {
			vto = imgStar2.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar2.getId());
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star_unsel);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);
			imgBubble1.setImageResource(R.drawable.bubble2);
		} else if (user_rate.equalsIgnoreCase("3")) {
			vto = imgStar3.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar3.getId());
			imgBubble1.setImageResource(R.drawable.bubble3);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);
		} else if (user_rate.equalsIgnoreCase("4")) {
			vto = imgStar4.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar4.getId());
			imgBubble1.setImageResource(R.drawable.bubble4);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star);
			imgStar5.setImageResource(R.drawable.star_unsel);
		} else if (user_rate.equalsIgnoreCase("5")) {
			vto = imgStar5.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar5.getId());
			imgBubble1.setImageResource(R.drawable.bubble5);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star);
			imgStar5.setImageResource(R.drawable.star);
		} else {
			vto = imgStar3.getViewTreeObserver();
			setBubblleAndSeekBar(imgStar3.getId());
			imgBubble1.setImageResource(R.drawable.bubble3);
			imgStar1.setImageResource(R.drawable.star);
			imgStar2.setImageResource(R.drawable.star);
			imgStar3.setImageResource(R.drawable.star);
			imgStar4.setImageResource(R.drawable.star_unsel);
			imgStar5.setImageResource(R.drawable.star_unsel);

		}
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {

				paramSeekBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				paramSeekBar.addRule(RelativeLayout.BELOW, linearRating.getId());
				paramSeekBar.addRule(RelativeLayout.CENTER_HORIZONTAL);
				paramSeekBar.setMargins(0, 10, 0, 0);
				seekBar.setLayoutParams(paramSeekBar);
				// x = imgStar3.getLeft();
				// setImageXY(x, imgStar3.getWidth());
				switch (seekBar.getProgress()) {
				case 0:
					setImageXY(imgStar1.getLeft(), imgStar1.getMeasuredWidth());
					break;
				case 25:
					setImageXY(imgStar2.getLeft(), imgStar2.getMeasuredWidth());
					break;
				case 50:
					setImageXY(imgStar3.getLeft(), imgStar3.getMeasuredWidth());
					break;
				case 75:
					setImageXY(imgStar4.getLeft(), imgStar4.getMeasuredWidth());
					break;
				case 100:
					setImageXY(imgStar5.getLeft(), imgStar5.getMeasuredWidth());
					break;
				default:
					break;
				}
				return true;
			}
		});

		Avg_Rate = objPostDetail.get("Average_Rating").toString();
		if (checkUser_ID(UserID, objPostDetail.get("User_ID").toString())) {
			btnEdit.setText("");
			btnEdit.setVisibility(View.VISIBLE);
		} else {
			btnEdit.setVisibility(View.GONE);
		}
		tvUserName.setText(objPostDetail.get("User_Name").toString());
		tvUserLoc.setText(objPostDetail.get("Post_Location").toString());
		tvPostName.setText(objPostDetail.get("Post_Title").toString());

		String strPostDesc = mySelectedFeedDetail.get("Post_Content").toString();
		tvPostDesc.setText(strPostDesc);
		// tvCommentCount.setText(objPostDetail.get("CommentsCount").toString());
		tvUserName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		tvPostName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		tvPostDesc.setTypeface(SplashScreen.ProxiNova_Regular);
		txttapratetoexpand.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblpostdetail.setTypeface(SplashScreen.ProxiNova_Regular);
		// tvRating.setText(objPostDetail.get("Average_Rating").toString());
		String count_rate = objPostDetail.get("Count_Rating").toString();
		String rating = "";
		if (count_rate.equalsIgnoreCase("1")) {
			rating = count_rate + " Rate";
		} else {
			rating = count_rate + " Rates";
		}
		tvRating.setText(rating);
		tvRating.setTypeface(SplashScreen.ProxiNova_Regular);
		String img = (objPostDetail.get("User_Imagepath").toString());

		imageLoader.displayImage(img, userImage, options);
		String img1 = (objPostDetail.get("Post_ImageSquare").toString());
		String isVideo = objPostDetail.get("Post_IsVideo").toString();
		if (checkUser_ID(UserID, objPostDetail.get("User_ID").toString())) {
			btnEdit.setText("Edit");
			btnEdit.setVisibility(View.VISIBLE);
			// btnEdit.setTypeface(SplashScreen.ProxiNova_Bold);
		} else {
			btnEdit.setVisibility(View.GONE);
		}
		if (isVideo.equalsIgnoreCase("1")) {
			userisPostVideo.setVisibility(View.VISIBLE);
			userisPostVideo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Intent intent1 = new Intent(getParent(), VideoViewActivity.class);
					// intent1.putExtra("Video_URL", objPostDetail.get("Post_VideoURL").toString());
					// TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					// parentActivity.startChildActivity("VideoViewActivity", intent1);
					userisPostVideo.setVisibility(View.INVISIBLE);
					userPostImage.setVisibility(View.INVISIBLE);
					videoview.setVisibility(View.VISIBLE);
					String Video_Url = mySelectedFeedDetail.get("Post_VideoURL").toString();
					new playingVideo().execute(Video_Url);
				}
			});

		} else {
			userisPostVideo.setVisibility(View.INVISIBLE);
		}
		tvUserName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fun.isAuthor(objPostDetail.get("User_ID").toString(), UserID)) {
					Intent intent = new Intent(getParent(), UserProfileChild.class);
					intent.putExtra("User_ID", objPostDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("UserProfileChild", intent);
				} else {
					Intent intent = new Intent(getParent(), GeneralUserProfile.class);
					intent.putExtra("User_ID", objPostDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("GeneralUserProfile", intent);
				}
			}
		});
		userImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fun.isAuthor(objPostDetail.get("User_ID").toString(), UserID)) {
					Intent intent = new Intent(getParent(), UserProfileChild.class);
					intent.putExtra("User_ID", objPostDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("UserProfileChild", intent);
				} else {
					Intent intent = new Intent(getParent(), GeneralUserProfile.class);
					intent.putExtra("User_ID", objPostDetail.get("User_ID").toString());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("GeneralUserProfile", intent);
				}
			}
		});
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), EditPost.class);
				intent.putExtra("editPost_ID", objPostDetail.get("Post_ID").toString());
				intent.putExtra("editTag", objPostDetail.get("Post_Title").toString());
				intent.putExtra("editDesc", objPostDetail.get("Post_Content").toString());
				intent.putExtra("editCategory", objPostDetail.get("Post_CategoryID").toString());
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("EditPost", intent);
			}
		});
		imageLoader.displayImage(img1, userPostImage, options1, new SimpleImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setProgress(0);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				progressBar.setProgress(Math.round(100.0f * current / total));
			}
		});
		String commentscount = objPostDetail.get("CommentsCount").toString();
		// btnComment.setText("Comments (" + commentscount + ")");
		CommentsDetail = new ArrayList<HashMap<String, String>>();
		CommentsDetail = (ArrayList<HashMap<String, String>>) objPostDetail.get("Comments");
		if (!objPostDetail.get("CommentsCount").toString().equalsIgnoreCase("0")) {
			int TotalComments = Integer.parseInt(objPostDetail.get("CommentsCount").toString());
			btnComment.setText("Comments (" + String.valueOf(TotalComments) + ")");
			if (TotalComments == 0) {
				tvTotalCommentlbl.setVisibility(View.INVISIBLE);
				lstComments.setVisibility(View.GONE);
			}
			tvTotalCommentlbl.setText(TotalComments + " Comments");
			if (TotalComments > 2) {
				lstComments.setVisibility(View.VISIBLE);
				if (CommentsDetail.size() > 0) {
					setTheAdapterWithArray(CommentsDetail);
				}
				// tvTotalCommentlbl.setVisibility(View.VISIBLE);

				// tvTotalCommentlbl.setText(TotalComments + " Comments");

				// tvTotalCommentlbl.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// Intent intent = new Intent(PostDetail.this,
				// CommentActivity.class);
				// intent.putExtra("Post_ID",
				// objPostDetail.get("Post_ID").toString());
				// startActivity(intent);
				// overridePendingTransition(0, 0);
				// }
				// });
			} else {
				lstComments.setVisibility(View.VISIBLE);
				// tvTotalCommentlbl.setVisibility(View.INVISIBLE);
				if (CommentsDetail.size() > 0) {
					setTheAdapterWithArray(CommentsDetail);
				}

			}

		}
		btnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), CommentActivity.class);
				intent.putExtra("Post_ID", objPostDetail.get("Post_ID").toString());
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("CommentActivity", intent);
			}
		});

		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ShareDialog();
				String imgPath = objPostDetail.get("Post_ImageSquare").toString();
				String countRating = objPostDetail.get("Count_Rating").toString();
				String AvgRating = objPostDetail.get("Average_Rating").toString();
				// // new ImageDownloader(context, countRating,
				// // AvgRating).execute(imgPath);
				// new ImageDownloader(PostDetail.this, countRating, AvgRating, new OnTaskCompleted() {
				//
				// @Override
				// public void onTaskCompleted() {
				// // TODO Auto-generated method stub
				// // share.ShareDialog(strUserId, strPostId,
				// // userName, strPostTag,strPostContent);
				// Intent intent = new Intent(PostDetail.this, SocialSharing.class);
				// intent.putExtra("User_ID", strUserId);
				// intent.putExtra("Post_ID", strPostId);
				// intent.putExtra("User_Name", userName);
				// intent.putExtra("Post_Tags", strPostTag);
				// intent.putExtra("Post_Content", strPostContent);
				// startActivity(intent);
				// overridePendingTransition(0, 0);
				//
				// }
				// }).execute(imgPath);

				strUserId = objPostDetail.get("User_ID").toString();
				strPostId = objPostDetail.get("Post_ID").toString();
				userName = objPostDetail.get("User_Name").toString();
				strPostTag = objPostDetail.get("Post_Tags").toString();
				strPostContent = objPostDetail.get("Post_Content").toString();
				onShareItem(strPostTag, strPostContent, userPostImage, AvgRating, countRating, strPostId);

			}
		});
		btnmore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String imgPath = objPostDetail.get("Post_ImageSquare").toString();
				String countRating = objPostDetail.get("Count_Rating").toString();
				String AvgRating = objPostDetail.get("Average_Rating").toString();
				// new ImageDownloader(context, countRating,
				// AvgRating).execute(imgPath);
				new ImageDownloader(getParent(), countRating, AvgRating, new OnTaskCompleted() {

					@Override
					public void onTaskCompleted() {
						// TODO Auto-generated method stub
						// share.ShareDialog(strUserId, strPostId,
						// userName, strPostTag,strPostContent);
						Intent intent = new Intent(getParent(), MoreSharing.class);
						intent.putExtra("User_ID", strUserId);
						intent.putExtra("Post_ID", strPostId);
						intent.putExtra("User_Name", userName);
						intent.putExtra("Post_Tags", strPostTag);
						intent.putExtra("Post_Content", strPostContent);
						TabGroupActivity parentActivity = (TabGroupActivity) getParent();
						parentActivity.startChildActivity("MoreSharing", intent);

					}
				}).execute(imgPath);

				strUserId = objPostDetail.get("User_ID").toString();
				strPostId = objPostDetail.get("Post_ID").toString();
				userName = objPostDetail.get("User_Name").toString();
				strPostTag = objPostDetail.get("Post_Tags").toString();
				strPostContent = objPostDetail.get("Post_Content").toString();

			}
		});
		RateDetail = new ArrayList<HashMap<String, String>>();
		RateDetail = (ArrayList<HashMap<String, String>>) objPostDetail.get("Rate");
		// System.out.println("ratedetail-->" + RateDetail);
		if (Avg_Rate != null) {
			int ratcount = Integer.valueOf(objPostDetail.get("Average_Rating").toString());
			switch (ratcount) {
			case 5:
				userRate.setImageResource(R.drawable.five_star_detail);
				break;
			case 4:
				userRate.setImageResource(R.drawable.four_star_detail);
				break;
			case 3:
				userRate.setImageResource(R.drawable.three_star_detail);
				break;
			case 2:
				userRate.setImageResource(R.drawable.two_star_detail);
				break;
			case 1:
				userRate.setImageResource(R.drawable.one_star_detail);
				break;
			}

		}
		String RateCount = objPostDetail.get("Count_Rating").toString();
		if (RateCount != null) {
			TotalRate = Integer.parseInt(RateCount);
			System.out.println("Count_Rating---> " + TotalRate);

		}

		if (RateDetail.size() > 0) {
			for (int i = 0; i < RateDetail.size(); i++) {
				String RatingCount = RateDetail.get(i).get("Rating_Rate").toString();
				if (RatingCount != null) {
					int count = Integer.valueOf(RatingCount);
					switch (count) {
					case 5:
						updateLoveCount++;
						break;
					case 4:
						updateLikeCount++;
						break;
					case 3:
						updateWhateverCount++;
						break;
					case 2:
						updateDislikeCount++;
						break;
					case 1:
						updateHateCount++;
						break;
					}
				}
				// RateCalculation(updateLoveCount, updateLikeCount,
				// updateWhateverCount, updateDislikeCount, updateHateCount,
				// TotalRate);
			}

		}
		userPostImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (gestureDetector1.onTouchEvent(event)) {
					return false;
				}
				return true;

			}
		});
		imgSubmitRate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String post_ID = objPostDetail.get("Post_ID").toString();
				new submitRate().execute(post_ID);
			}
		});
		String isRated = objPostDetail.get("User_Rated").toString();
		if (isRated.equalsIgnoreCase("0")) {
			tvisRated.setVisibility(View.INVISIBLE);
			// holder.tvisRated.setTypeface(SplashScreen.ProxiNova_Regular);
			imgisRated.setVisibility(View.INVISIBLE);
		} else {
			tvisRated.setVisibility(View.VISIBLE);
			tvisRated.setTypeface(SplashScreen.ProxiNova_Regular);
			imgisRated.setVisibility(View.VISIBLE);
		}

		/*
		 * Investor Badge Code if(userInvestorBadge!=null) { String User_BagdeValue=objPostDetail.get("User_Badge").toString(); if(!User_BagdeValue.equals("NA")) {
		 * 
		 * userInvestorBadge.setVisibility(View.VISIBLE); if(User_BagdeValue.equalsIgnoreCase("SILVER")) { userInvestorBadge.setImageResource(R.drawable.investor_silver); } else if(User_BagdeValue.equalsIgnoreCase("BLACK")) { userInvestorBadge.setImageResource(R.drawable.investor_black); } else
		 * if(User_BagdeValue.equalsIgnoreCase("BRONZE")) { userInvestorBadge.setImageResource(R.drawable.investor_bronze); } else if(User_BagdeValue.equalsIgnoreCase("GOLD")) { userInvestorBadge.setImageResource(R.drawable.investor_gold); }
		 * 
		 * 
		 * } else { userInvestorBadge.setVisibility(View.INVISIBLE); } }
		 */
		// System.out.println("mypie" + mPie.getWidth() + "Height"+
		// mPie.getHeight());

		// Log.e("list","" + objPostDetail.toString() + " \n\n\n " +
		// RateDetail.toString());
		exptAdpater = new ExpandableListAdapter(getParent(), RateDetail, objPostDetail, createParentList(RateDetail));
		// exptAdpater =new ExpandableListAdapter(getApplicationContext(),
		// objPostDetail ,RateDetail);
		ratingListView.setAdapter(exptAdpater);
		// Helper.getExpListViewSize(ratingListView);
		new Helper(getParent()).getExpListViewSize(ratingListView);
		/*
		 * userPostImage.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO Auto-generated method stub
		 * 
		 * if (gestureDetector.onTouchEvent(event)) { return false; } return true; } });
		 */
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				setSliderPosition();
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

				Log.e("Progress:", "::>" + arg1);
				seekBar.setProgress(arg1);
				Log.d("Center_Point", ":>" + center_point);

				if (arg1 >= 0 && arg1 <= 12) {
					imgBubble1.setImageResource(R.drawable.bubble1);
					imgStar1.setImageResource(R.drawable.star);
					// seekBar.setProgress(2);
					imgStar2.setImageResource(R.drawable.star_unsel);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 1;
					x = imgStar1.getLeft();
					setImageXY(x, imgStar1.getWidth());
				} else if (arg1 >= 13 && arg1 <= 37) {
					// seekBar.setProgress(18);
					imgBubble1.setImageResource(R.drawable.bubble2);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 2;
					x = imgStar2.getLeft();
					setImageXY(x, imgStar2.getWidth());
				} else if (arg1 >= 38 && arg1 <= 63) {
					// seekBar.setProgress(35);
					imgBubble1.setImageResource(R.drawable.bubble3);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 3;
					x = imgStar3.getLeft();
					setImageXY(x, imgStar3.getWidth());
				} else if (arg1 >= 64 && arg1 <= 89) {
					// seekBar.setProgress(50);
					imgBubble1.setImageResource(R.drawable.bubble4);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 4;
					x = imgStar4.getLeft();
					setImageXY(x, imgStar4.getWidth());
				} else if (arg1 >= 90) {
					// seekBar.setProgress(65);
					imgBubble1.setImageResource(R.drawable.bubble5);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star);
					rating_rate = 5;
					x = imgStar5.getLeft();
					setImageXY(x, imgStar5.getWidth());
				}
				// textview.setText(arg1 + "");

			}
		});

		imgStar1.setOnClickListener(mclickListner);
		imgStar2.setOnClickListener(mclickListner);
		imgStar3.setOnClickListener(mclickListner);
		imgStar4.setOnClickListener(mclickListner);
		imgStar5.setOnClickListener(mclickListner);

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date myDate = simpleDateFormat.parse((objPostDetail.get("Post_Date")).toString());
			String diffVal = fun.checkDate_Diffrences(myDate);
			tvlastseen.setText(diffVal);
			tvlastseen.setTypeface(SplashScreen.ProxiNova_Regular);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}

	static class ViewHolder {
		ImageView imageViewUser;
		TextView txtUserComment, txtLastSeen, txtUserName;

	}

	/* Comments Adapter */
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
			// return items.size();
			// return 2;
			if (items.size() >= 2) {
				return 2;
			} else {
				return 1;
			}
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
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			System.out.println("postion" + position);
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.postdetailcomment, parent, false);
				holder = new ViewHolder();
				assert view != null;
				// holder.imageViewUser = (ImageView)
				// view.findViewById(R.id.userimg);
				holder.txtUserName = (TextView) view.findViewById(R.id.tvusername);
				holder.txtUserComment = (TextView) view.findViewById(R.id.tvcommentdesc);
				// holder.txtLastSeen = (TextView)
				// view.findViewById(R.id.tvlastseen);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (items.size() > 0) {

				if (holder.txtUserName != null) {
					String UserName = (items.get(position).get("User_Name"));
					holder.txtUserName.setText(UserName);
				}
				if (holder.txtUserComment != null) {
					String Comment_Content = (items.get(position).get("Comment_Content"));
					byte[] Data;

					try {
						Data = Base64.decode(Comment_Content.getBytes());
						DecodedComment = new String(Data);
						System.out.println("Decoded data-->" + DecodedComment);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					holder.txtUserComment.setText(DecodedComment);
				}
			}
			return view;
		}
	}

	public void setTheAdapterWithArray(final ArrayList<HashMap<String, String>> arrayList) {
		CommentsAdapter objCommentadapter = new CommentsAdapter(arrayList);
		lstComments.setAdapter(objCommentadapter);
		// Helper.getListViewSize(lstComments);
		new Helper(getParent()).getListViewSize(lstComments);
		// setListViewHeightBasedOnItems(lstComments);

	}

	public ArrayList<ArrayList<HashMap<String, String>>> createParentList(ArrayList<HashMap<String, String>> RateDetail) {
		ArrayList<ArrayList<HashMap<String, String>>> parentArray = new ArrayList<ArrayList<HashMap<String, String>>>();

		ArrayList<HashMap<String, String>> firstList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> secondList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> thirdList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> fourthList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> fifthList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < RateDetail.size(); i++) {
			String ratingValue = RateDetail.get(i).get("Rating_Rate");

			if (ratingValue.equals("5")) {
				firstList.add(RateDetail.get(i));
			} else if (ratingValue.equals("4")) {
				secondList.add(RateDetail.get(i));
			} else if (ratingValue.equals("3")) {
				thirdList.add(RateDetail.get(i));
			} else if (ratingValue.equals("2")) {
				fourthList.add(RateDetail.get(i));
			} else if (ratingValue.equals("1")) {
				fifthList.add(RateDetail.get(i));
			}

		}

		parentArray.add(firstList);
		parentArray.add(secondList);
		parentArray.add(thirdList);
		parentArray.add(fourthList);
		parentArray.add(fifthList);

		return parentArray;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (requestCode == 2) {
				try {
					String postID = data.getStringExtra("postID");
					new getPostAfterRate().execute(postID);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

		if (requestCode == ACTIVITY_RESULT_COMMENT) {
			if (data != null) {

				try {
					String Post_ID = data.getStringExtra("Post_ID");
					Log.d("TAG", "Post_ID " + Post_ID);
					new getPostAfterRate().execute(Post_ID);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

		Log.d("TAG", "ON ACTIVITY RESULT");

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		uiHelper.onSaveInstanceState(outState);

	}

	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains(PERMISSIONS2);
		} else {
			return false;
		}

	}

	public void requestPermissions(Context context) {
		Session s = Session.getActiveSession();
		// final PostDetail activity = (PostDetail) context;
		final Activity activity = (Activity) context;
		if (s != null) {
			System.out.println("session here------" + s.isOpened());
			if (s.isOpened()) {
				System.out.println("true to proceed");
				s.requestNewPublishPermissions(new Session.NewPermissionsRequest(activity, PERMISSIONS2));

				proceed1(s);
			} else {
				Session.openActiveSession(activity, true, new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if (session.isOpened()) {
							System.out.println("now the session is opened");

							session.requestNewReadPermissions(new Session.NewPermissionsRequest(activity, PERMISSIONS));
							proceed();
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

		// System.out.println("Currentsessi==>"+objMyFeed.currentSession);

	}

	// Private class for gestures
	private class SwipeGestureDetector extends SimpleOnGestureListener {
		// Swipe properties, you can change it to make the swipe
		// longer or shorter and speed
		private static final int SWIPE_MIN_DISTANCE = 50;
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 50;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				float diffAbs = Math.abs(e1.getY() - e2.getY());
				float diff = e1.getX() - e2.getX();

				if (diffAbs > SWIPE_MAX_OFF_PATH)
					return false;

				// Left swipe
				if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// PostDetail.this.swipeLeft();
					swipeRight();

					// Right swipe
				} else if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// PostDetail.this.swipeRight();
					swipeLeft();
				}
			} catch (Exception e) {
				Log.e("YourActivity", "Error on gestures");
			}
			return false;
		}
	}

	class submitRate extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			imgSubmitRate.setClickable(false);
			showDialog();
		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
			String UserID = prefs.getString("UserID", "");
			postid = params[0];
			String url = cc.GetBaseUrl() + "api=Rating&User_ID=" + UserID + "&Post_ID=" + postid + "&Rating_Rate=" + rating_rate;
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			String response = parser.getStringFromUrlInTime(encoded_url.toString());

			try {
				JSONObject json = new JSONObject(response);
				Result1 = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return Result1;

		}

		@Override
		protected void onPostExecute(String status) {
			dismissDialog();
			imgSubmitRate.setClickable(true);
			if (status.equals("1")) {

				fun.SimpleAlert("CamRate", "Your rating has been submitted successfully");
				new getPostAfterRate().execute(postid);

			}
			if (status.equals("0")) {
				fun.SimpleAlert("CamRate", "Your rating has not been submitted. Please try again");
			}
			if (status.equals("2")) {
				fun.SimpleAlert("CamRate", "Your rating has not been submitted");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public class getPostAfterRate extends AsyncTask<String, Void, ArrayList<HashMap<String, Object>>> {
		@Override
		protected void onPreExecute() {
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			postid = params[0];
			String url = c.GetBaseUrl() + "api=PostDetail&Post_ID=" + postid + "&User_ID=" + UserID + "";
			// Log.d("Tag", "refresh--->" + url);
			myFeedPostDetailRefresh = new ArrayList<HashMap<String, Object>>();
			ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
			ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
			try {
				String refresh_PostData = parser.getStringFromUrlInTime(url);
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
				myFeedPostDetailRefresh.add(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return myFeedPostDetailRefresh;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);

			Log.d("TAG", "myFeedPostDetail " + myFeedPostDetail.size());
			mySelectedFeedDetail = result.get(0);
			UpdateUIWithMyFeedPostDetail(mySelectedFeedDetail);
			Log.d("TAG", myFeedPostDetail + "");
			exptAdpater.notifyDataSetChanged();
		}
	}

	// Can be triggered by a view event such as a button press
	public void onShareItem(String Post_Tag, String Post_Content, ImageView postImage, String avgRating, String CountRating, String PostID) {
		// Get access to bitmap image from view
		// Get access to the URI for the bitmap

		String dir = Environment.getExternalStorageDirectory().toString() + "/Share_CamRate_Images";
		String name = "shareImg11.png";
		File file = new File(dir, name);
		Uri bmpUri = getLocalBitmapUri(postImage, avgRating, CountRating);

		String mainUrl = "http://camrate.com";
		String FBBody = Post_Tag + "\n" + Post_Content + "\n\n" + mainUrl + "\n";

		String encodepostid = fun.encodePostID(PostID);
		String TwitterBodyLink = Post_Tag + "\n\n" + Post_Content + "\n\n" + "http://camrate.com/TestCamRate/Lovehate/postdetail/" + encodepostid;
		if (bmpUri != null) {
			// Construct a ShareIntent with link to image
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("image/jpeg");
			shareIntent.putExtra(Intent.EXTRA_TEXT, TwitterBodyLink);
			shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
			shareIntent.setType("image/jpeg");
			// Launch sharing dialog for image
			startActivity(Intent.createChooser(shareIntent, "Share via"));
		} else {
			// ...sharing failed, handle error
		}
	}

	// Returns the URI path to the Bitmap displayed in specified ImageView
	public Uri getLocalBitmapUri(ImageView imgView, String avgRating, String countRating) {

		// Store image to default external storage directory
		Drawable drawable = imgView.getDrawable();
		Bitmap bmp = null;
		if (drawable instanceof BitmapDrawable) {
			bmp = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
		} else {
			return null;
		}

		Bitmap topBar = BitmapFactory.decodeResource(context.getResources(), R.drawable.fbposttopbar);
		Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), R.drawable.fbpostoverlay);
		Bitmap updatedBitmap = fun.joinImages(bmp, topBar, overlay, avgRating, countRating);
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_Image_" + System.currentTimeMillis() + ".png");
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

	// @Override
	// public void onBackPressed() {
	// try {
	// Intent intent = new Intent();
	// intent.putExtra("Post_ID", mySelectedFeedDetail.get("Post_ID").toString());
	// intent.putExtra("postIndex", postIndex);
	// intent.putExtra("isForDelete", false);
	// setResult(ACTIVITY_RESULT_COMMENT, intent);
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// }

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

	class playingVideo extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			final String Video_Url = params[0];
			String path = "";
			try {
				path = getDataSource(Video_Url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return path;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setVisibility(View.GONE);
			playVideo(result);
		}

	}

	public void playVideo(String Video_Url) {
		String current = null;
		try {
			if (Video_Url == null || Video_Url.length() == 0) {
				Log.v("CamRate", "url is emprty");
			} else {
				// If the path has not changed, just start the media player
				if (Video_Url.equals(current) && videoview != null) {
					videoview.start();
					videoview.requestFocus();
					return;
				}
				current = Video_Url;
				videoview.setVideoPath(Video_Url);
				videoview.start();
				videoview.requestFocus();
				System.out.println("bufferfinish");

			}
		} catch (Exception e) {
			if (videoview != null) {
				videoview.stopPlayback();
			}
		}
		videoview.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated
				// method stub
				videoview.setVisibility(View.INVISIBLE);
				userisPostVideo.setVisibility(View.VISIBLE);
				imgpauseVideo.setVisibility(View.INVISIBLE);
				userPostImage.setVisibility(View.VISIBLE);
			}
		});
		videoview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (videoview.isPlaying()) {
					videoview.pause();
					imgpauseVideo.setVisibility(View.VISIBLE);
					videoposition = videoview.getCurrentPosition();
					return false;
				} else {
					imgpauseVideo.setVisibility(View.INVISIBLE);
					// videoview.seekTo(videoposition);
					videoview.start();
					return false;
				}

			}
		});

	}
}
