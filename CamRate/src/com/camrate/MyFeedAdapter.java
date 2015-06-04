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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.camrate.global.CircleDisplay;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.tabs.TabGroupActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyFeedAdapter extends ArrayAdapter<HashMap<String, Object>> {

	DisplayImageOptions options, options1;
	String query;
	Context myFeedActivity;
	Date date1;

	private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

	ImageLoader imageLoader = ImageLoader.getInstance();

	private ArrayList<HashMap<String, Object>> items;
	private ArrayList<HashMap<String, Object>> itemsRate;

	String Rate;
	int width, height;
	RelativeLayout.LayoutParams paramImage;
	String pos;
	private String finalRate;
	private String count_rate;
	private String userName;
	private String strPostTag;
	private String strPostContent;
	protected String strMessage;
	JSONParser parser = new JSONParser();
	double lat = 0;
	double lng = 0;
	String imgPath, countRating, AvgRating, strPostId, strUserId, post_isvideo;
	int prog = 0;
	float x = 0;
	float y = 0;
	float center_point = 0;
	RelativeLayout.LayoutParams paramSeekBar, paramToplayout;
	LinearLayout linearRating;
	Function fun;
	int[] a = new int[] { 0, 25, 50, 75, 100 };
	int rating = 0;
	String postid = "";
	Constant con = new Constant();
	String UserID;
	int ACTIVITY_RESULT_COMMENT = 0;
	int videoposition = 1;
	Dialog dialog;

	public MyFeedAdapter(Context context, int resource, ArrayList<HashMap<String, Object>> items) {
		super(context, resource, items);

		Log.d("tag", "MY FEED ADAPTER");

		this.myFeedActivity = context;
		this.items = items;

		SharedPreferences prefs = myFeedActivity.getSharedPreferences("User_Info", myFeedActivity.MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");

		imageLoader.init(ImageLoaderConfiguration.createDefault(myFeedActivity));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888).considerExifParams(true).build();

		// d = new DrawableBackgroundDownloader();
		Display display = ((Activity) myFeedActivity).getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		System.out.println("Width--->" + width);
		System.out.println("Height--->" + height);
		System.out.println("IMageview Height and width--->" + (width - 10));
		paramImage = new RelativeLayout.LayoutParams((width - 10), (width - 10));
		int round = (width - 10) / 2;
		options1 = new DisplayImageOptions.Builder().cacheOnDisk(true).considerExifParams(true).showImageOnLoading(null).showImageForEmptyUri(null).showImageOnFail(null).cacheInMemory(true).bitmapConfig(Bitmap.Config.ARGB_8888).resetViewBeforeLoading(true).build();
		fun = new Function(myFeedActivity);
		initProcessDialog();
	}

	public int getCount() {
		return items.size();
	}

	public HashMap<String, Object> getItem(int position) {
		System.out.println("getItem-->" + position);
		return items.get(position);
	}

	public static class ViewHolder {
		public ImageView image;
		TextView tvRateName;
		public boolean flag = false;
		TextView tvUserName, tvUserLoc, tvPostName, tvPostDesc, tvCommentCount, tvRating, tvlastseen, tvisRated;
		ProgressBar progressBar;
		ImageView userRate, userImage, userPostImage, userPostVideo, userLevel;
		ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
		ImageView imgBubble1, imgSubmitRate, imgisRated;
		SeekBar seekBar;
		Button btnRate, btnComment, btnStat, btnShare, btnTapToShare, btnmore;
		VideoView videoview;
		RelativeLayout relSlider, relTopLayout;
		private CircleDisplay mCircleDisplay;
		View tapview;
		ImageView imgpauseVideo;
	}

	public long getItemId(int position) {
		System.out.println("getItemID-->" + position);
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		// System.out.println("adapterpos--->"+position);
		final ViewHolder holder;
		final int positionTemp = position;
		if (v == null) {
			holder = new ViewHolder();
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.myfeed_list, null);
			holder.tvUserName = (TextView) v.findViewById(R.id.tvusername);
			holder.tvUserLoc = (TextView) v.findViewById(R.id.tvuserlocation);
			holder.tvPostName = (TextView) v.findViewById(R.id.tvpostname);
			holder.tvPostDesc = (TextView) v.findViewById(R.id.tvpostdesc);
			holder.tvCommentCount = (TextView) v.findViewById(R.id.tvTotalCommentsFeed);
			holder.tvRating = (TextView) v.findViewById(R.id.tvTotalRatingFeed);
			holder.tvlastseen = (TextView) v.findViewById(R.id.tvlastseen);

			holder.imgpauseVideo = (ImageView) v.findViewById(R.id.imguserpausevideo);

			holder.userRate = (ImageView) v.findViewById(R.id.imguserrate);
			holder.userImage = (ImageView) v.findViewById(R.id.userimg);
			holder.userPostImage = (ImageView) v.findViewById(R.id.imguserpost);
			holder.userPostVideo = (ImageView) v.findViewById(R.id.imguserpostvideo);
			holder.videoview = (VideoView) v.findViewById(R.id.VideoView);
			// holder.videoview.setLayoutParams(paramImage);
			holder.userPostImage.setLayoutParams(paramImage);
			holder.videoview.setLayoutParams(paramImage);
			holder.progressBar = (ProgressBar) v.findViewById(R.id.progress);
			holder.userImage.setTag(new Integer(position));
			holder.userPostImage.setTag(new Integer(position));
			holder.tvUserName.setTag(new Integer(position));
			holder.userLevel = (ImageView) v.findViewById(R.id.img_levelTag);
			holder.btnRate = (Button) v.findViewById(R.id.bntRateFeed);
			holder.btnComment = (Button) v.findViewById(R.id.btnCommentFeed);
			holder.btnStat = (Button) v.findViewById(R.id.btnStatsFeed);
			holder.btnShare = (Button) v.findViewById(R.id.btnShareFeed);
			holder.btnmore = (Button) v.findViewById(R.id.btnmorefeed);
			holder.btnStat.setTag(new Integer(position));
			holder.mCircleDisplay = (CircleDisplay) v.findViewById(R.id.circleDisplay);
			holder.tvRateName = (TextView) v.findViewById(R.id.tvRateName);
			holder.btnmore.setTag(new Integer(position));
			holder.btnShare.setTypeface(SplashScreen.ProxiNova_Bold);
			holder.btnStat.setTypeface(SplashScreen.ProxiNova_Bold);
			holder.seekBar = (SeekBar) v.findViewById(R.id.seekBar1);
			holder.imgStar1 = (ImageView) v.findViewById(R.id.imgStar1);
			holder.imgStar2 = (ImageView) v.findViewById(R.id.imgStar2);
			holder.imgStar3 = (ImageView) v.findViewById(R.id.imgStar3);
			holder.imgStar4 = (ImageView) v.findViewById(R.id.imgStar4);
			holder.imgStar5 = (ImageView) v.findViewById(R.id.imgStar5);
			holder.imgBubble1 = (ImageView) v.findViewById(R.id.imgbubble1);
			holder.imgSubmitRate = (ImageView) v.findViewById(R.id.imgSubmitRated);
			linearRating = (LinearLayout) v.findViewById(R.id.ratingStar);
			holder.imgStar1.setImageResource(R.drawable.star);
			holder.imgStar2.setImageResource(R.drawable.star_unsel);
			holder.imgStar3.setImageResource(R.drawable.star_unsel);
			holder.imgStar4.setImageResource(R.drawable.star_unsel);
			holder.imgStar5.setImageResource(R.drawable.star_unsel);
			holder.relSlider = (RelativeLayout) v.findViewById(R.id.relative_slider);
			holder.btnTapToShare = (Button) v.findViewById(R.id.btnTap_toShare);
			holder.tapview = (View) v.findViewById(R.id.viewrate);
			holder.tvisRated = (TextView) v.findViewById(R.id.txtlblisRated);
			holder.imgisRated = (ImageView) v.findViewById(R.id.imgisRated);
			holder.relTopLayout = (RelativeLayout) v.findViewById(R.id.relative1);
			holder.imgStar1.setId(0);
			holder.imgStar2.setId(1);
			holder.imgStar3.setId(2);
			holder.imgStar4.setId(3);
			holder.imgStar5.setId(4);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
			items.get((Integer) holder.videoview.getTag()).put("isVideoPlaying", false);
			// check

		}

		holder.btnComment.setTag("" + position);
		holder.videoview.setTag(new Integer(position));
		if (position == 0) {
			paramToplayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			paramToplayout.setMargins(0, 0, 0, 0);
			holder.relTopLayout.setLayoutParams(paramToplayout);
		} else {
			paramToplayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			int top = fun.pxToDp(15);

			paramToplayout.setMargins(0, top, 0, 0);
			holder.relTopLayout.setLayoutParams(paramToplayout);
		}
		if (items != null) {

			if ((Boolean) items.get(position).get("flag")) {

				holder.imgBubble1.setVisibility(View.VISIBLE);
				holder.relSlider.setVisibility(View.VISIBLE);
				holder.btnTapToShare.setVisibility(View.GONE);
				holder.tapview.setVisibility(View.GONE);
			} else {

				holder.imgBubble1.setVisibility(View.GONE);
				holder.relSlider.setVisibility(View.GONE);
				holder.btnTapToShare.setVisibility(View.VISIBLE);
				holder.tapview.setVisibility(View.VISIBLE);

			}
			String user_rate = items.get(position).get("User_Rated").toString();
			// System.out.println("userrated===>" + user_rate);
			ViewTreeObserver vto = null;
			if (user_rate.equalsIgnoreCase("1")) {
				vto = holder.imgStar1.getViewTreeObserver();
				setBubblleAndSeekBar(holder.imgStar1.getId(), holder);
				holder.imgBubble1.setImageResource(R.drawable.bubble1);
			} else if (user_rate.equalsIgnoreCase("2")) {
				vto = holder.imgStar2.getViewTreeObserver();
				setBubblleAndSeekBar(holder.imgStar2.getId(), holder);
				holder.imgBubble1.setImageResource(R.drawable.bubble2);
			} else if (user_rate.equalsIgnoreCase("3")) {
				vto = holder.imgStar3.getViewTreeObserver();
				setBubblleAndSeekBar(holder.imgStar3.getId(), holder);
				holder.imgBubble1.setImageResource(R.drawable.bubble3);
			} else if (user_rate.equalsIgnoreCase("4")) {
				vto = holder.imgStar4.getViewTreeObserver();
				setBubblleAndSeekBar(holder.imgStar4.getId(), holder);
				holder.imgBubble1.setImageResource(R.drawable.bubble4);
			} else if (user_rate.equalsIgnoreCase("5")) {
				vto = holder.imgStar5.getViewTreeObserver();
				setBubblleAndSeekBar(holder.imgStar5.getId(), holder);
				holder.imgBubble1.setImageResource(R.drawable.bubble5);
			} else {
				vto = holder.imgStar3.getViewTreeObserver();
				setBubblleAndSeekBar(holder.imgStar3.getId(), holder);
				holder.imgBubble1.setImageResource(R.drawable.bubble3);

			}
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {

					paramSeekBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					paramSeekBar.addRule(RelativeLayout.BELOW, linearRating.getId());
					paramSeekBar.addRule(RelativeLayout.CENTER_HORIZONTAL);
					paramSeekBar.setMargins(0, 10, 0, 0);
					holder.seekBar.setLayoutParams(paramSeekBar);
					// x = imgStar3.getLeft();
					// setImageXY(x, imgStar3.getWidth());
					switch (holder.seekBar.getProgress()) {
					case 0:
						holder.imgStar1.setImageResource(R.drawable.star);
						holder.imgStar2.setImageResource(R.drawable.star_unsel);
						holder.imgStar3.setImageResource(R.drawable.star_unsel);
						holder.imgStar4.setImageResource(R.drawable.star_unsel);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						setImageXY(holder, holder.imgStar1.getLeft(), holder.imgStar1.getMeasuredWidth());
						break;
					case 25:
						holder.imgStar1.setImageResource(R.drawable.star);
						// seekBar.setProgress(2);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star_unsel);
						holder.imgStar4.setImageResource(R.drawable.star_unsel);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						setImageXY(holder, holder.imgStar2.getLeft(), holder.imgStar2.getMeasuredWidth());
						break;
					case 50:
						holder.imgStar1.setImageResource(R.drawable.star);
						// seekBar.setProgress(2);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star);
						holder.imgStar4.setImageResource(R.drawable.star_unsel);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						setImageXY(holder, holder.imgStar3.getLeft(), holder.imgStar3.getMeasuredWidth());
						break;
					case 75:
						holder.imgStar1.setImageResource(R.drawable.star);
						// seekBar.setProgress(2);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star);
						holder.imgStar4.setImageResource(R.drawable.star);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						setImageXY(holder, holder.imgStar4.getLeft(), holder.imgStar4.getMeasuredWidth());
						break;
					case 100:
						holder.imgStar1.setImageResource(R.drawable.star);
						// seekBar.setProgress(2);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star);
						holder.imgStar4.setImageResource(R.drawable.star);
						holder.imgStar5.setImageResource(R.drawable.star);
						setImageXY(holder, holder.imgStar5.getLeft(), holder.imgStar5.getMeasuredWidth());
						break;
					default:
						break;
					}
					return true;
				}
			});
			if (holder.tvUserName != null) {
				userName = items.get(position).get("User_Name").toString();
				holder.tvUserName.setText(userName);
				holder.tvUserName.setTypeface(SplashScreen.ProxiNova_SemiBold);
			}
			if (holder.tvUserLoc != null) {
				holder.tvUserLoc.setText(items.get(position).get("Post_Location").toString());
				holder.tvUserLoc.setTypeface(SplashScreen.ProxiNova_Regular);
			}
			if (holder.tvPostName != null) {
				String strPostTag1 = items.get(position).get("Post_Title").toString();
				holder.tvPostName.setText(strPostTag1);
				holder.tvPostName.setTypeface(SplashScreen.ProxiNova_SemiBold);
			}
			if (holder.tvPostDesc != null) {
				String strPostContent1 = items.get(position).get("Post_Content").toString();

				holder.tvPostDesc.setTypeface(SplashScreen.ProxiNova_Regular);
				// SpannableStringBuilder desc = fun.getclickableText(strPostContent1);
				holder.tvPostDesc.setText(strPostContent1);
			}
			if (holder.tvCommentCount != null) {
				// holder.tvCommentCount.setText(items.get(position).get("CommentsCount").toString());
				String comment_count = items.get(position).get("CommentsCount").toString();
				holder.btnComment.setText(" Comments " + "(" + comment_count + ")" + " ");
				// holder.btnComment.setText(" Comments ");
				holder.btnComment.setTypeface(SplashScreen.ProxiNova_Bold);
			}
			if (holder.tvRating != null) {
				holder.tvRating.setText(items.get(position).get("Count_Rating").toString());
			}
			if (holder.tvRateName != null) {
				count_rate = "";
				count_rate = items.get(position).get("Count_Rating").toString();
				String rating = "";
				if (count_rate.equalsIgnoreCase("1")) {
					rating = count_rate + " Rate";
				} else {
					rating = count_rate + " Rates";
				}
				holder.tvRateName.setText(rating);
				holder.tvRateName.setTypeface(SplashScreen.ProxiNova_Regular);
			}
			if (holder.userRate != null) {
				int ratcount = Integer.valueOf(items.get(position).get("Average_Rating").toString());
				switch (ratcount) {
				case 1:
					holder.userRate.setImageResource(R.drawable.one_star);
					break;
				case 2:
					holder.userRate.setImageResource(R.drawable.two_star);
					break;
				case 3:
					holder.userRate.setImageResource(R.drawable.three_star);
					break;
				case 4:
					holder.userRate.setImageResource(R.drawable.four_star);
					break;
				case 5:
					holder.userRate.setImageResource(R.drawable.five_star);
					break;

				default:
					break;
				}

			}

			if (holder.tvisRated != null) {
				String isRated = (items.get(position).get("User_Rated").toString());
				if (isRated.equalsIgnoreCase("0")) {
					holder.tvisRated.setVisibility(View.INVISIBLE);
					// holder.tvisRated.setTypeface(SplashScreen.ProxiNova_Regular);
					holder.imgisRated.setVisibility(View.INVISIBLE);
				} else {
					holder.tvisRated.setVisibility(View.VISIBLE);
					holder.tvisRated.setTypeface(SplashScreen.ProxiNova_Regular);
					holder.imgisRated.setVisibility(View.VISIBLE);
				}

			}
			if (holder.userImage != null) {

				String img = (items.get(position).get("User_Imagepath").toString());

				imageLoader.displayImage(img, holder.userImage, options, animateFirstListener);
			}
			if (holder.userPostImage != null) {
				String img = (items.get(position).get("Post_ImageSquare").toString());
				imageLoader.displayImage(img, holder.userPostImage, options1, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.progressBar.setMax(100);
						holder.progressBar.incrementProgressBy(1);
						holder.mCircleDisplay.setVisibility(View.VISIBLE);
						holder.mCircleDisplay.setAnimDuration(0);
						holder.mCircleDisplay.setFormatDigits(1);
						holder.mCircleDisplay.setDimAlpha(0);
						holder.mCircleDisplay.setTouchEnabled(false);
						holder.mCircleDisplay.showValue(0, 100, true);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						holder.mCircleDisplay.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						holder.mCircleDisplay.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						holder.mCircleDisplay.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total) {
						prog = Math.round(100.0f * current / total);

						holder.mCircleDisplay.showValue(Math.round(100.0f * current / total), 100, true);

					}
				});

			}

			if (holder.userPostVideo != null) {

				String video = (items.get(position).get("Post_IsVideo").toString());

				if (video.equalsIgnoreCase("1")) {

					holder.userPostVideo.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							items.get(position).put("isVideoPlaying", true);
							// check
							holder.userPostVideo.setVisibility(View.INVISIBLE);
							holder.userPostImage.setVisibility(View.INVISIBLE);
							holder.videoview.setVisibility(View.VISIBLE);

							final String Video_Url = (items.get(position).get("Post_VideoURL").toString());
							new playingVideo(holder, holder.progressBar, holder.videoview).execute(Video_Url);

						}
					});

					if ((Boolean) items.get(position).get("isVideoPlaying") == true) {

						holder.userPostVideo.setVisibility(View.INVISIBLE);
						holder.userPostImage.setVisibility(View.INVISIBLE);
						holder.videoview.setVisibility(View.VISIBLE);

					} else {
						holder.userPostVideo.setVisibility(View.VISIBLE);
						holder.userPostImage.setVisibility(View.VISIBLE);
						holder.videoview.setVisibility(View.INVISIBLE);
						holder.imgpauseVideo.setVisibility(View.INVISIBLE);
					}

				} else {
					holder.userPostVideo.setVisibility(View.INVISIBLE);
					holder.userPostImage.setVisibility(View.VISIBLE);
					holder.videoview.setVisibility(View.INVISIBLE);
					holder.imgpauseVideo.setVisibility(View.INVISIBLE);
				}
			}
			if (holder.userLevel != null) {
				String userLevelCount = (items.get(position).get("PostCount").toString());
				int PostLevelCount = 0;
				// System.out.println("UserPostcount--->"+PostLevelCount);
				if (userLevelCount.length() > 0) {
					PostLevelCount = Integer.parseInt(userLevelCount);
					// PostLevelCount=200;
					// System.out.println("IF UserPostcount--->"+PostLevelCount);

				}
			}
			/*
			 * Investor Badge Code if(userInvestorBadge!=null) { String User_BagdeValue=items.get(position).get("User_Badge").toString(); if(!User_BagdeValue.equals("NA")) {
			 * 
			 * userInvestorBadge.setVisibility(View.VISIBLE); if(User_BagdeValue.equalsIgnoreCase("SILVER")) { userInvestorBadge.setImageResource(R.drawable.investor_silver); } else if(User_BagdeValue.equalsIgnoreCase("BLACK")) { userInvestorBadge.setImageResource(R.drawable.investor_black); } else
			 * if(User_BagdeValue.equalsIgnoreCase("BRONZE")) { userInvestorBadge.setImageResource(R.drawable.investor_bronze); } else if(User_BagdeValue.equalsIgnoreCase("GOLD")) { userInvestorBadge.setImageResource(R.drawable.investor_gold); }
			 * 
			 * 
			 * } else { userInvestorBadge.setVisibility(View.INVISIBLE); } }
			 */
			if (holder.tvlastseen != null) {
				/*
				 * System.out.println("Locale Language---->"+Locale.getDefault(). getDisplayLanguage()); System.out.println("Locale dispCountry---->" +Locale.getDefault().getDisplayCountry()); System.out.println( "Locale country---->"+Locale.getDefault().getCountry());
				 * System.out.println("Locale name---->"+Locale.getDefault(). getDisplayName()); System.out.println("Locale confg country---->" +c.getResources( ).getConfiguration().locale.getDisplayCountry()); System.out.println ("Locale confg name---->"+c.getResources().getConfiguration
				 * ().locale.getDisplayName()); System.out.println("Locale confg variant---->" +c.getResources( ).getConfiguration().locale.getDisplayVariant());
				 */

				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date postDate = simpleDateFormat.parse((items.get(position).get("Post_Date")).toString());
					String diffVal = fun.checkDate_Diffrences(postDate);
					Log.d("Diff", "val==>" + diffVal);
					holder.tvlastseen.setText(diffVal);
					holder.tvlastseen.setTypeface(SplashScreen.ProxiNova_Regular);
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}

			holder.btnComment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(myFeedActivity, CommentActivity.class);
					intent.putExtra("Post_ID", items.get(position).get("Post_ID").toString());
					intent.putExtra("postindex", v.getTag() + "");
					TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
					parentActivity.startChildActivity("CommentActivity", intent);
				}
			});

			holder.tvUserLoc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String Post_lat = items.get(position).get("Post_Latitude").toString();
					String Post_long = items.get(position).get("Post_Longitude").toString();
					String Post_date = items.get(position).get("Post_Date").toString();
					String Post_tag = items.get(position).get("Post_Tags").toString();
					String Post_location = items.get(position).get("Post_Location").toString();
					String Average_Rating = items.get(position).get("Average_Rating").toString();
					String Post_ImageSquare = items.get(position).get("Post_ImageSquare").toString();
					String User_Name = items.get(position).get("User_Name").toString();

					if (!(Post_lat.equals("") && Post_long.equals(""))) {
						lat = Double.parseDouble(Post_lat);

						lng = Double.parseDouble(Post_long);

						Intent intent = new Intent(myFeedActivity, PostLocation_Detail.class);
						intent.putExtra("Post_Latitude", lat);
						intent.putExtra("Post_Longitude", lng);
						intent.putExtra("Post_Date", Post_date);
						intent.putExtra("Post_Tags", Post_tag);
						intent.putExtra("Post_Location", Post_location);
						intent.putExtra("User_Name", User_Name);
						intent.putExtra("Post_ImageSquare", Post_ImageSquare);
						intent.putExtra("Average_Rating", Average_Rating);
						TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
						parentActivity.startChildActivity("PostLocation_Detail", intent);
					}

					else {
						Toast.makeText(myFeedActivity, "Please enter latitude and longitude", Toast.LENGTH_SHORT).show();
					}
				}

			});
			holder.btnShare.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					imgPath = items.get(position).get("Post_ImageSquare").toString();
					countRating = items.get(position).get("Count_Rating").toString();
					AvgRating = items.get(position).get("Average_Rating").toString();
					strPostId = items.get(position).get("Post_ID").toString();
					strUserId = items.get(position).get("User_ID").toString();
					strPostContent = items.get(position).get("Post_Content").toString();
					post_isvideo = items.get(position).get("Post_IsVideo").toString();
					strPostTag = items.get(position).get("Post_Tags").toString();

					onShareItem(strPostTag, strPostContent, holder.userPostImage, AvgRating, countRating, strPostId);
					// new ImageDownloader(myFeedActivity, countRating, AvgRating, new OnTaskCompleted() {
					//
					// @Override
					// public void onTaskCompleted() {
					// // TODO Auto-generated method stub
					// // share.ShareDialog(strUserId, strPostId,
					// // userName, strPostTag,strPostContent);
					// Intent intent = new Intent(myFeedActivity, SocialSharing.class);
					// intent.putExtra("User_ID", strUserId);
					// intent.putExtra("Post_ID", strPostId);
					// intent.putExtra("User_Name", userName);
					// intent.putExtra("Post_Tags", strPostTag);
					// intent.putExtra("Post_Content", strPostContent);
					//
					// intent.putExtra("Average_Rating", AvgRating);
					// intent.putExtra("Post_IsVideo", post_isvideo);
					//
					// myFeedActivity.startActivity(intent);
					// ((Activity) myFeedActivity).overridePendingTransition(0, 0);
					//
					// }
					// }).execute(imgPath);

				}
			});

			holder.btnmore.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// // TODO Auto-generated method stub
					final String postid = items.get(position).get("Post_ID").toString();
					final String userId = items.get(position).get("User_ID").toString();

					final Dialog dialog = new Dialog(myFeedActivity);
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
							SimpleAlert("CamRate", position);
						}
					});

					linear_reportinapp.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();

							// 0 for post items
							Intent intent = new Intent(myFeedActivity, ResonForReport.class);
							intent.putExtra("reasonType", "0");
							intent.putExtra("Post_ID", items.get(position).get("Post_ID").toString());
							TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
							parentActivity.startChildActivity("ResonForReport", intent);

						}
					});
					linear_addtofav.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							String postid = items.get(position).get("Post_ID").toString();
							String userId = items.get(position).get("User_ID").toString();

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

							String userId = items.get(position).get("User_ID").toString();
							String postid = items.get(position).get("Post_ID").toString();

							Intent intent = new Intent(myFeedActivity, AlertFollowers.class);
							intent.putExtra("User_ID", userId);
							intent.putExtra("Post_ID", postid);
							TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
							parentActivity.startActivity(intent);

						}
					});
					linear_reShare.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();

							Intent intent = new Intent(myFeedActivity, RateItScreen1.class);
							intent.putExtra("is_web", "0");
							intent.putExtra("PostData", items);
							intent.putExtra("CheckedIndex", pos);
							intent.putExtra("SelectedPostData", items.get(position));
							intent.putExtra("TotalCheckedIndex", items.size());
							intent.putExtra("progress", prog);
							TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
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

			holder.btnStat.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos;
					pos = (Integer) v.getTag();
					System.out.println("checkedIndex===>" + pos);

					Intent intent = new Intent(myFeedActivity, PostDetail.class);
					intent.putExtra("PostData", items);
					intent.putExtra("CheckedIndex", pos);
					intent.putExtra("SelectedPostData", items.get(position));
					intent.putExtra("TotalCheckedIndex", items.size());
					intent.putExtra("progress", prog);
					TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
					parentActivity.startChildActivity("PostDetail", intent);

				}
			});
			holder.tvUserName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int pos;
					pos = (Integer) v.getTag();
					if (isAuthor(items.get(position).get("User_ID").toString())) {
						Intent intent = new Intent(myFeedActivity, UserProfileChild.class);
						TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
						parentActivity.startChildActivity("UserProfileChild", intent);

					} else {
						Intent intent = new Intent(myFeedActivity, GeneralUserProfile.class);
						intent.putExtra("User_ID", items.get(position).get("User_ID").toString());
						TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
						parentActivity.startChildActivity("GeneralUserProfile", intent);
					}
				}
			});
			holder.userImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int pos;
					pos = (Integer) v.getTag();
					int p = position;
					System.out.println("position==>" + p);
					if (isAuthor(items.get(position).get("User_ID").toString())) {
						Intent intent = new Intent(myFeedActivity, UserProfileChild.class);
						TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
						parentActivity.startChildActivity("UserProfileChild", intent);

					} else {
						Intent intent = new Intent(myFeedActivity, GeneralUserProfile.class);
						intent.putExtra("User_ID", items.get(position).get("User_ID").toString());
						TabGroupActivity parentActivity = (TabGroupActivity) myFeedActivity;
						parentActivity.startChildActivity("GeneralUserProfile", intent);
					}
				}
			});
			holder.btnTapToShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// holder.flag = true;
					items.get(position).put("flag", true);
					// holder.btnTapToShare.setVisibility(View.GONE);
					// holder.imgBubble1.setVisibility(View.VISIBLE);
					// holder.relSlider.setVisibility(View.VISIBLE);
					notifyDataSetChanged();
				}
			});
			holder.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					setSliderPosition(holder);
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

					Log.e("Progress:", "::>" + arg1);
					holder.seekBar.setProgress(arg1);
					Log.d("Center_Point", ":>" + center_point);

					if (arg1 == 0) {
						holder.imgBubble1.setImageResource(R.drawable.bubble1);
						holder.imgStar1.setImageResource(R.drawable.star);
						// seekBar.setProgress(2);
						holder.imgStar2.setImageResource(R.drawable.star_unsel);
						holder.imgStar3.setImageResource(R.drawable.star_unsel);
						holder.imgStar4.setImageResource(R.drawable.star_unsel);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						rating = 1;
						x = holder.imgStar1.getLeft();
						setImageXY(holder, x, holder.imgStar1.getWidth());
					} else if (arg1 == 25) {
						// seekBar.setProgress(18);
						holder.imgBubble1.setImageResource(R.drawable.bubble2);
						holder.imgStar1.setImageResource(R.drawable.star);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star_unsel);
						holder.imgStar4.setImageResource(R.drawable.star_unsel);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						rating = 2;
						x = holder.imgStar2.getLeft();
						setImageXY(holder, x, holder.imgStar2.getWidth());
					} else if (arg1 == 50) {
						// seekBar.setProgress(35);
						holder.imgBubble1.setImageResource(R.drawable.bubble3);
						holder.imgStar1.setImageResource(R.drawable.star);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star);
						holder.imgStar4.setImageResource(R.drawable.star_unsel);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						rating = 3;
						x = holder.imgStar3.getLeft();
						setImageXY(holder, x, holder.imgStar3.getWidth());
					} else if (arg1 == 75) {
						// seekBar.setProgress(50);
						holder.imgBubble1.setImageResource(R.drawable.bubble4);
						holder.imgStar1.setImageResource(R.drawable.star);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star);
						holder.imgStar4.setImageResource(R.drawable.star);
						holder.imgStar5.setImageResource(R.drawable.star_unsel);
						rating = 4;
						x = holder.imgStar4.getLeft();
						setImageXY(holder, x, holder.imgStar4.getWidth());
					} else if (arg1 == 100) {
						// seekBar.setProgress(65);
						holder.imgBubble1.setImageResource(R.drawable.bubble5);
						holder.imgStar1.setImageResource(R.drawable.star);
						holder.imgStar2.setImageResource(R.drawable.star);
						holder.imgStar3.setImageResource(R.drawable.star);
						holder.imgStar4.setImageResource(R.drawable.star);
						holder.imgStar5.setImageResource(R.drawable.star);
						rating = 5;
						x = holder.imgStar5.getLeft();
						setImageXY(holder, x, holder.imgStar5.getWidth());
					}
					// textview.setText(arg1 + "");

				}
			});
			holder.imgSubmitRate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("Rating", "::>>" + rating);
					String post_ID = items.get(position).get("Post_ID").toString();
					int pos = position;
					// String[] params = new String[] { post_ID, pos}
					new submitRate(holder.relSlider, holder.btnTapToShare, pos).execute(post_ID);
					// holder.flag = false;
					// notifyDataSetChanged();
				}

			});
			holder.imgStar1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					holder.imgStar1.setImageResource(R.drawable.star);
					holder.imgStar2.setImageResource(R.drawable.star_unsel);
					holder.imgStar3.setImageResource(R.drawable.star_unsel);
					holder.imgStar4.setImageResource(R.drawable.star_unsel);
					holder.imgStar5.setImageResource(R.drawable.star_unsel);
					setBubblleAndSeekBar(holder.imgStar1.getId(), holder);
					rating = 1;
					x = holder.imgStar1.getLeft();
					setImageXY(holder, x, holder.imgStar1.getWidth());
					holder.imgBubble1.setImageResource(R.drawable.bubble1);

				}
			});
			holder.imgStar2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					holder.imgStar1.setImageResource(R.drawable.star);
					holder.imgStar2.setImageResource(R.drawable.star);
					holder.imgStar3.setImageResource(R.drawable.star_unsel);
					holder.imgStar4.setImageResource(R.drawable.star_unsel);
					holder.imgStar5.setImageResource(R.drawable.star_unsel);
					setBubblleAndSeekBar(holder.imgStar2.getId(), holder);
					rating = 2;
					x = holder.imgStar2.getLeft();
					setImageXY(holder, x, holder.imgStar2.getWidth());
					holder.imgBubble1.setImageResource(R.drawable.bubble2);

				}
			});
			holder.imgStar3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					holder.imgStar1.setImageResource(R.drawable.star);
					holder.imgStar2.setImageResource(R.drawable.star);
					holder.imgStar3.setImageResource(R.drawable.star);
					holder.imgStar4.setImageResource(R.drawable.star_unsel);
					holder.imgStar5.setImageResource(R.drawable.star_unsel);
					setBubblleAndSeekBar(holder.imgStar3.getId(), holder);
					rating = 3;
					x = holder.imgStar3.getLeft();
					setImageXY(holder, x, holder.imgStar3.getWidth());
					holder.imgBubble1.setImageResource(R.drawable.bubble3);

				}
			});
			holder.imgStar4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					holder.imgStar1.setImageResource(R.drawable.star);
					holder.imgStar2.setImageResource(R.drawable.star);
					holder.imgStar3.setImageResource(R.drawable.star);
					holder.imgStar4.setImageResource(R.drawable.star);
					holder.imgStar5.setImageResource(R.drawable.star_unsel);
					setBubblleAndSeekBar(holder.imgStar4.getId(), holder);
					rating = 4;
					x = holder.imgStar4.getLeft();
					setImageXY(holder, x, holder.imgStar4.getWidth());
					holder.imgBubble1.setImageResource(R.drawable.bubble4);

				}
			});
			holder.imgStar5.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					holder.imgStar1.setImageResource(R.drawable.star);
					holder.imgStar2.setImageResource(R.drawable.star);
					holder.imgStar3.setImageResource(R.drawable.star);
					holder.imgStar4.setImageResource(R.drawable.star);
					holder.imgStar5.setImageResource(R.drawable.star);
					setBubblleAndSeekBar(holder.imgStar5.getId(), holder);
					rating = 5;
					x = holder.imgStar5.getLeft();
					setImageXY(holder, x, holder.imgStar5.getWidth());
					holder.imgBubble1.setImageResource(R.drawable.bubble5);

				}
			});
		}
		((ViewGroup) v).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

		return v;

	}

	public void checkDate(long miliseconds) {
		// long ts = System.currentTimeMillis();

		Date localTime = new Date(miliseconds);
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		// Convert Local Time to UTC (Works Fine)
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gmtTime = new Date(sdf.format(localTime));
		System.out.println("Local:" + localTime.toString() + "," + localTime.getTime() + " --> UTC time:" + gmtTime.toString() + "," + gmtTime.getTime());

		// ** YOUR CODE ** END **

		// Convert UTC to Local Time
		Date fromGmt = new Date(gmtTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
		System.out.println("UTC time:" + gmtTime.toString() + "," + gmtTime.getTime() + " --> Local:" + fromGmt.toString() + "-" + fromGmt.getTime());
	}

	public void setImageXY(ViewHolder holder, float X, int width) {

		// Log.d("X", ":>" + X);
		int tempwidth = (width);
		// Log.d("Width", ":>" + tempwidth);
		center_point = X;
		center_point = center_point - (holder.imgBubble1.getWidth() / 2) + (tempwidth / 2);
		holder.imgBubble1.setX(center_point);

		holder.imgBubble1.invalidate();
	}

	public void setSliderPosition(ViewHolder holder) {

		int diff = 0;
		int finalValue = 0;
		int valueSeek = holder.seekBar.getProgress();
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

		holder.seekBar.setProgress(finalValue);
	}

	public void setBubblleAndSeekBar(int id, ViewHolder holder) {
		int tag = id * 25;
		holder.seekBar.setProgress(tag);

	}

	private synchronized String getStrRate(String ratingStr) {

		int totalAcceptableLength = 24;
		int lengthStr = ratingStr.length();
		int outstandingSpace = totalAcceptableLength - lengthStr;
		int leftSpace = outstandingSpace / 2;

		finalRate = "";
		for (int i = 0; i < outstandingSpace + leftSpace; i++) {
			finalRate = finalRate + " ";
		}

		finalRate = finalRate + ratingStr;

		for (int i = 0; i < outstandingSpace + leftSpace; i++) {
			finalRate = finalRate + " ";
		}

		return finalRate;
	}

	public String printDifference(Date date, Date curDate) {
		// difference in milliseconds
		long different = curDate.getTime() - date.getTime();

		String diffrences = "";

		if ((different / 1000) < 60) {
			// print seconds
			return (different / 1000) + " sec ago";

		} else if ((different / (1000 * 60)) < 60) {
			// minutes
			return (different / (1000 * 60)) + " min ago";
		} else if ((different / (1000 * 60 * 60)) < 24) {
			// hours
			return (different / (1000 * 60 * 60)) + " hrs ago";
		} else if ((different / (1000 * 60 * 60 * 24)) < 30) {
			// days
			return (different / (1000 * 60 * 60 * 24)) + " day ago";
		} else if (((different / (1000 * 60 * 60 * 24 * 7)) < 4)) {
			return (different / (1000 * 60 * 60 * 24 * 7)) + " week ago";
		} else if ((different / (1000 * 60 * 60 * 24 * 30)) < 12) {
			// months
			return (different / (1000 * 60 * 60 * 24 * 30)) + " month ago";
		} else if ((different / (1000 * 60 * 60 * 24 * 30 * 12)) < 365) {
			return (different / (1000 * 60 * 60 * 24 * 30 * 12)) + " yrs ago";
		} else {
			return "";
		}

	}

	public Date ConvertToDate(String dateString) {
		Date convertedDate = new Date();
		try {
			convertedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;

	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	static private class RateView extends View {
		// private static final String QUOTE = "1-Rate";
		// ABCDEFGHIJKLMNOPQRSTUVWX

		private static final String QUOTE = "";
		private Path circle;
		private Paint cPaint;
		private Paint tPaint;
		String finalstr;

		public RateView(Context main, String finalRate) {
			// TODO Auto-generated constructor stub
			super(main);
			finalstr = finalRate;

			circle = new Path();
			circle.addCircle(350, 420, 150, Direction.CCW);
			// circle.addCircle(10, 10, 150, Direction.CCW);

			cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			cPaint.setStyle(Paint.Style.STROKE);
			cPaint.setColor(Color.LTGRAY);
			cPaint.setStrokeWidth(3);

			// setBackgroundResource(R.drawable.ic_launcher);

			tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			tPaint.setColor(Color.WHITE);
			tPaint.setTextSize(30);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawTextOnPath(finalstr, circle, 485, 20, tPaint);
		}
	}

	class submitRate extends AsyncTask<String, Void, String> {

		RelativeLayout rl;
		Button b1;
		int position;

		public submitRate(RelativeLayout rl, Button b1, int pos) {
			// TODO Auto-generated constructor stub

			this.rl = rl;
			this.b1 = b1;
			position = pos;
		}

		@Override
		protected void onPreExecute() {
			showDialog();
		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			postid = params[0];
			String url = con.GetBaseUrl() + "api=Rating&User_ID=" + UserID + "&Post_ID=" + postid + "&Rating_Rate=" + rating;
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = con.Geturl(url);
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
			if (status.equals("1")) {
				SimpleAlert("CamRate", "Your rating has been submitted successfully", position);
				// rl.setVisibility(View.GONE);
				// b1.setVisibility(View.VISIBLE);

				// new getRefreshData(position).execute();

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

	public void SimpleAlert(String t, String b, final int pos) {
		Builder builder = new AlertDialog.Builder(myFeedActivity);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				new getRefreshData(pos).execute();
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	class getRefreshData extends AsyncTask<String, Void, String> {

		int position;

		public getRefreshData(int pos) {
			// TODO Auto-generated constructor stub
			position = pos;
		}

		// Dialog rateDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// rateDialog = new Dialog(c);
			// rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// rateDialog.setContentView(R.layout.rateposted);
			// rateDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			SharedPreferences prefs = myFeedActivity.getSharedPreferences("User_Info", myFeedActivity.MODE_PRIVATE);
			String UserID = prefs.getString("UserID", "");
			String url = cc.GetBaseUrl() + "api=PostDetail&Post_ID=" + postid + "&User_ID=" + UserID + "";
			System.out.println(url);
			JSONObject json = parser.getJSONFromUrl(url);
			return json.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			JSONObject objPost;
			try {
				objPost = new JSONObject(result);
				// System.out.println("jsonFeed==>" + objPost);
				setParsePost(position, objPost);
				// rateDialog.dismiss();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setParsePost(int pos, JSONObject myFeedData) throws JSONException {
		ArrayList<HashMap<String, String>> myFeedCommentsData1 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> myFeedRateData1 = new ArrayList<HashMap<String, String>>();
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
		items.set(pos, map);
		notifyDataSetChanged();

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

	public void SimpleAlert(String t, final int postIndex) {

		Log.d("TAG", "delete post index " + postIndex);

		Builder builder = new AlertDialog.Builder(myFeedActivity);
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
		String postid = items.get(postindex).get("Post_ID").toString();
		String url = con.GetBaseUrl() + "api=PostDelete&Post_ID=" + postid;
		System.out.println(url);
		try {
			String myFeedParse = parser.getStringFromUrl(url);
			JSONObject jsonObj = new JSONObject(myFeedParse);
			String res = jsonObj.getString("result");
			if (res.equals("0")) {

				Builder builder = new AlertDialog.Builder(myFeedActivity);
				builder.setTitle("CamRate");
				builder.setMessage("Post has not been deleted. Please try again");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			} else {

				Builder builder = new AlertDialog.Builder(myFeedActivity);
				builder.setTitle("CamRate");
				builder.setMessage("Post has been deleted successfully");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						items.remove(postindex);
						notifyDataSetChanged();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
			shareIntent.setAction(android.content.Intent.ACTION_SEND);
			shareIntent.setType("image/*");
			shareIntent.putExtra(Intent.EXTRA_TEXT, TwitterBodyLink);
			shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
			// Launch sharing dialog for image
			myFeedActivity.startActivity(Intent.createChooser(shareIntent, "Share via"));
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

		Bitmap topBar = BitmapFactory.decodeResource(myFeedActivity.getResources(), R.drawable.fbposttopbar);
		Bitmap overlay = BitmapFactory.decodeResource(myFeedActivity.getResources(), R.drawable.fbpostoverlay);
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

	class playingVideo extends AsyncTask<String, Void, String> {
		ViewHolder holder;
		ProgressBar pd;
		VideoView videoview;

		public playingVideo(ViewHolder holder, ProgressBar pd, VideoView videoview) {
			this.holder = holder;
			this.pd = pd;
			this.videoview = videoview;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
			pd.invalidate();
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
			playVideo(holder, result);
		}

	}

	public void playVideo(final ViewHolder holder, String Video_Url) {
		String current = null;
		try {
			if (Video_Url == null || Video_Url.length() == 0) {
				Log.v("CamRate", "url is emprty");
			} else {
				// If the path has not changed, just start the media player
				if (Video_Url.equals(current) && holder.videoview != null) {
					holder.videoview.start();
					holder.videoview.requestFocus();
					return;
				}
				current = Video_Url;
				holder.videoview.setVideoPath(Video_Url);
				holder.videoview.start();
				holder.videoview.requestFocus();
				System.out.println("bufferfinish");

			}
		} catch (Exception e) {
			if (holder.videoview != null) {
				holder.videoview.stopPlayback();
			}
		}
		// holder.videoview.setOnPreparedListener(new OnPreparedListener() {
		//
		// @Override
		// public void onPrepared(MediaPlayer mp) {
		// // TODO Auto-generated method stub
		// holder.progressBar.setVisibility(View.GONE);
		// }
		// });

		holder.videoview.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated
				// method stub
				Log.d("TAG", "position in adapter " + (Integer) holder.videoview.getTag());
				items.get((Integer) holder.videoview.getTag()).put("isVideoPlaying", false);
				holder.videoview.setVisibility(View.INVISIBLE);
				holder.imgpauseVideo.setVisibility(View.INVISIBLE);
				holder.userPostVideo.setVisibility(View.VISIBLE);
				holder.userPostImage.setVisibility(View.VISIBLE);
				// check
				notifyDataSetChanged();
			}
		});
		holder.videoview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (holder.videoview.isPlaying()) {
					holder.videoview.pause();
					holder.userPostVideo.setVisibility(View.INVISIBLE);
					holder.imgpauseVideo.setVisibility(View.VISIBLE);
					videoposition = holder.videoview.getCurrentPosition();
					return false;
				} else {
					holder.imgpauseVideo.setVisibility(View.INVISIBLE);
					// holder.videoview.seekTo(videoposition);
					holder.videoview.start();
					return false;
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

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(myFeedActivity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(myFeedActivity, android.R.style.Theme_Light_NoTitleBar);
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

}