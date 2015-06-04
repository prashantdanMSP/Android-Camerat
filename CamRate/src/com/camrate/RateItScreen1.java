package com.camrate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.camrate.global.Function;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.polites.android.GestureImageView;

public class RateItScreen1 extends Activity {

	Button btnBack, btnnext;
	TextView tvTitle;
	GestureImageView imgPostImage;
	ImageView imgisVideo, imgpauseVideo;
	VideoView videoView;
	private final static int REQUEST_CAMERA = 2;
	public static String IMAGE_DIRECTORY_NAME = "CamRate_Images";
	public static File mediaFile;;
	public static Uri fileuri;
	// public static String ImageName;
	Bitmap postImage;
	Function fun;
	String image_video = "", is_web = "";
	String Video_Url;
	// ScrollView scrollVideoview;
	RelativeLayout relVideoview, relrate;
	RelativeLayout.LayoutParams params;
	RelativeLayout.LayoutParams params1;
	int ypos = 0;
	RelativeLayout.LayoutParams paramimage;
	int Device_width = 0, Device_height = 0;
	RelativeLayout.LayoutParams paramSeekBar;
	LinearLayout linearRating;
	int[] a = new int[] { 0, 25, 50, 75, 100 };
	int rating = 3;
	ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
	ImageView imgBubble1, imgSubmitRate, imgisRated;
	SeekBar seekBar;
	Button btnRate, btnComment, btnStat, btnShare, btnTapToShare;
	RelativeLayout relSlider;
	int prog = 0;
	float x = 0;
	float y = 0;
	float center_point = 0;
	public static Bitmap bmp = null;
	String isVideo = "";
	ProgressBar pd;
	Dialog dialog;
	int videoposition = 1;
	SharedPreferences prefs;
	static HashMap<String, Object> mySelectedFeedDetail;
	DisplayImageOptions options, options1;
	ImageLoader imageLoader = ImageLoader.getInstance();
	int width, height;
	RelativeLayout.LayoutParams paramImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rateit);
		Log.d("Tag", "Rate IT Screen");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		initProcessDialog();
		btnBack = (Button) findViewById(R.id.btnRateBack);
		btnnext = (Button) findViewById(R.id.btnnext);
		imgPostImage = (GestureImageView) findViewById(R.id.imgPostImage);
		videoView = (VideoView) findViewById(R.id.VideoViewPost);
		imgisVideo = (ImageView) findViewById(R.id.imguserpostvideo);
		imgpauseVideo = (ImageView) findViewById(R.id.imguserpausevideo);
		tvTitle = (TextView) findViewById(R.id.lblInviteFriTitle);
		relrate = (RelativeLayout) findViewById(R.id.relativeRate);
		// scrollVideoview = (ScrollView) findViewById(R.id.scrollVideoView);
		relVideoview = (RelativeLayout) findViewById(R.id.relativeVideoView);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		imgStar1 = (ImageView) findViewById(R.id.imgStar1);
		imgStar2 = (ImageView) findViewById(R.id.imgStar2);
		imgStar3 = (ImageView) findViewById(R.id.imgStar3);
		imgStar4 = (ImageView) findViewById(R.id.imgStar4);
		imgStar5 = (ImageView) findViewById(R.id.imgStar5);
		imgBubble1 = (ImageView) findViewById(R.id.imgbubble1);
		imgSubmitRate = (ImageView) findViewById(R.id.imgSubmitRated);
		linearRating = (LinearLayout) findViewById(R.id.ratingStar);
		imgStar1.setImageResource(R.drawable.star);
		imgStar2.setImageResource(R.drawable.star);
		imgStar3.setImageResource(R.drawable.star);
		imgStar4.setImageResource(R.drawable.star_unsel);
		imgStar5.setImageResource(R.drawable.star_unsel);
		relSlider = (RelativeLayout) findViewById(R.id.relative_slider);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		imgSubmitRate.setVisibility(View.GONE);
		imgStar1.setId(0);
		imgStar2.setId(1);
		imgStar3.setId(2);
		imgStar4.setId(3);
		imgStar5.setId(4);
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		// x=206;
		Display display = getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		paramImage = new RelativeLayout.LayoutParams((width - 10), (width - 10));
		imageLoader.init(ImageLoaderConfiguration.createDefault(RateItScreen1.this));
		options1 = new DisplayImageOptions.Builder().showImageOnLoading(null).showImageForEmptyUri(null).showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		ViewTreeObserver vto = imgStar3.getViewTreeObserver();
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

		setBubblleAndSeekBar(imgStar3.getId());
		imgBubble1.setImageResource(R.drawable.bubble_3);
		tvTitle.setTypeface(SplashScreen.ProxiNova_Bold);
		fun = new Function(RateItScreen1.this);
		Device_width = display.getWidth();
		Device_height = display.getHeight();
		mySelectedFeedDetail = new HashMap<String, Object>();
		paramimage = new RelativeLayout.LayoutParams(Device_width, Device_width);
		imgPostImage.setLayoutParams(paramImage);
		videoView.setLayoutParams(paramImage);
		// paramimage.weight=1.7f;
		// relrate.setLayoutParams(paramimage);
		Intent intent = getIntent();
		is_web = intent.getStringExtra("is_web");
		mySelectedFeedDetail = (HashMap<String, Object>) intent.getSerializableExtra("SelectedPostData");
		btnnext.setTypeface(SplashScreen.ProxiNova_Bold);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btnnext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// String path = "file:///mnt/sdcard/CamRate_Images/" + ImageName;
				System.out.println();

				createBitmap(imgPostImage);

				Intent intent = new Intent(RateItScreen1.this, ShareItScreen1.class);
				intent.putExtra("image_video", isVideo);
				intent.putExtra("rate", rating);
				intent.putExtra("PostData", mySelectedFeedDetail);
				startActivity(intent);
				overridePendingTransition(0, 0);

			}
		});
		// setting image

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
					imgBubble1.setImageResource(R.drawable.bubble_1);
					imgStar1.setImageResource(R.drawable.star);
					// seekBar.setProgress(2);
					imgStar2.setImageResource(R.drawable.star_unsel);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating = 1;
					x = imgStar1.getLeft();
					setImageXY(x, imgStar1.getWidth());
				} else if (arg1 >= 13 && arg1 <= 37) {
					// seekBar.setProgress(18);
					imgBubble1.setImageResource(R.drawable.bubble_2);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating = 2;
					x = imgStar2.getLeft();
					setImageXY(x, imgStar2.getWidth());
				} else if (arg1 >= 38 && arg1 <= 63) {
					// seekBar.setProgress(35);
					imgBubble1.setImageResource(R.drawable.bubble_3);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating = 3;
					x = imgStar3.getLeft();
					setImageXY(x, imgStar3.getWidth());
				} else if (arg1 >= 64 && arg1 <= 89) {
					// seekBar.setProgress(50);
					imgBubble1.setImageResource(R.drawable.bubble_4);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating = 4;
					x = imgStar4.getLeft();
					setImageXY(x, imgStar4.getWidth());
				} else if (arg1 >= 90) {
					// seekBar.setProgress(65);
					imgBubble1.setImageResource(R.drawable.bubble_5);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star);
					rating = 5;
					x = imgStar5.getLeft();
					setImageXY(x, imgStar5.getWidth());
				}
				// textview.setText(arg1 + "");

			}
		});

		imgStar1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star_unsel);
				imgStar3.setImageResource(R.drawable.star_unsel);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar1.getId());
				rating = 1;
				x = imgStar1.getLeft();
				setImageXY(x, imgStar1.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble_1);

			}
		});
		imgStar2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star_unsel);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar2.getId());
				rating = 2;
				x = imgStar2.getLeft();
				setImageXY(x, imgStar2.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble_2);

			}
		});
		imgStar3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar3.getId());
				rating = 3;
				x = imgStar3.getLeft();
				setImageXY(x, imgStar3.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble_3);

			}
		});
		imgStar4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar4.getId());
				rating = 4;
				x = imgStar4.getLeft();
				setImageXY(x, imgStar4.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble_4);

			}
		});
		imgStar5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star);
				imgStar5.setImageResource(R.drawable.star);
				setBubblleAndSeekBar(imgStar5.getId());
				rating = 5;
				x = imgStar5.getLeft();
				setImageXY(x, imgStar5.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble_5);

			}
		});
		isVideo = mySelectedFeedDetail.get("Post_IsVideo").toString();
		String img1 = (mySelectedFeedDetail.get("Post_ImageSquare").toString());
		imageLoader.displayImage(img1, imgPostImage, options1, new SimpleImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
			}
		});
		if (isVideo.equalsIgnoreCase("0")) {
			// setImage(is_web);
			imgisVideo.setVisibility(View.INVISIBLE);

		} else {
			// String Videoname = SecondCameraTab.VideoName.replaceAll("/", "");
			Video_Url = "file:///mnt/sdcard/CamRate_Video/" + SecondCameraTab.VideoName;
			String url = mySelectedFeedDetail.get("Post_VideoURL").toString();
			imgisVideo.setVisibility(View.VISIBLE);
			// setnPlayVideo();
			relVideoview.setVisibility(View.VISIBLE);
			relVideoview.setLayoutParams(paramImage);

			imgisVideo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					imgisVideo.setVisibility(View.INVISIBLE);
					imgPostImage.setVisibility(View.INVISIBLE);
					videoView.setVisibility(View.VISIBLE);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							String Video_Url = mySelectedFeedDetail.get("Post_VideoURL").toString();
							String current = null;
							try {
								if (Video_Url == null || Video_Url.length() == 0) {
									Log.v("CamRate", "url is emprty");
								} else {
									// If the path has not changed, just start the media player
									if (Video_Url.equals(current) && videoView != null) {
										videoView.start();
										videoView.requestFocus();
										return;
									}
									current = Video_Url;
									videoView.setVideoPath(getDataSource(Video_Url));
									videoView.start();
									videoView.requestFocus();
									System.out.println("bufferfinish");

								}
							} catch (Exception e) {
								if (videoView != null) {
									videoView.stopPlayback();
								}
							}
							videoView.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									// TODO Auto-generated
									// method stub
									videoView.setVisibility(View.INVISIBLE);
									imgisVideo.setVisibility(View.VISIBLE);
									imgPostImage.setVisibility(View.VISIBLE);
								}
							});
							videoView.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View v, MotionEvent event) {
									// TODO Auto-generated method stub

									if (videoView.isPlaying()) {
										videoView.pause();
										imgpauseVideo.setVisibility(View.VISIBLE);
										videoposition = videoView.getCurrentPosition();
										return false;
									} else {
										imgpauseVideo.setVisibility(View.INVISIBLE);
										// videoview.seekTo(videoposition);
										videoView.start();
										return false;
									}

								}
							});
						}
					});
				}
			});
		}

	}

	public void setImageXY(float X, int width) {

		// Log.d("X", ":>" + X);
		int tempwidth = (width);
		// Log.d("Width", ":>" + tempwidth);
		center_point = X;
		center_point = center_point - (imgBubble1.getWidth() / 2) + (tempwidth / 2);
		imgBubble1.setX(center_point);
		imgBubble1.invalidate();
	}

	public void createBitmap(ImageView imageview) {
		imgPostImage.setDrawingCacheEnabled(true);
		if (bmp != null) {
			bmp.recycle();
			bmp = null;
		}
		bmp = Bitmap.createBitmap(imageview.getDrawingCache());
		imageview.setDrawingCacheEnabled(false);
		// Your Code of bitmap Follows here
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

	private Bitmap rotateBitmap(Bitmap srcBitmap, int angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		srcBitmap.recycle();
		return rotatedBitmap;
	}

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(RateItScreen1.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(RateItScreen1.this, android.R.style.Theme_Light_NoTitleBar);
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
