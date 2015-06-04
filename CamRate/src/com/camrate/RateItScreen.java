package com.camrate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.os.Handler;
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
import android.view.WindowManager.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.camrate.global.Function;
import com.polites.android.GestureImageView;

public class RateItScreen extends Activity {

	Button btnBack, btnnext;
	TextView tvTitle;
	GestureImageView imgPostImage;
	ImageView imgisVideo, imgpauseVideo;
	VideoView videoView;
	private final static int REQUEST_CAMERA = 2;
	public static String IMAGE_DIRECTORY_NAME = "CamRate_Images";
	public static File mediaFile;;
	public static Uri fileuri;
	public static String ImageName;
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
		fun = new Function(RateItScreen.this);
		Display display = getWindowManager().getDefaultDisplay();
		Device_width = display.getWidth();
		Device_height = display.getHeight();
		paramimage = new RelativeLayout.LayoutParams(Device_width, Device_width);
		imgPostImage.setLayoutParams(paramimage);
		// paramimage.weight=1.7f;
		// relrate.setLayoutParams(paramimage);
		Intent intent = getIntent();
		isVideo = intent.getStringExtra("is_Video");
		is_web = intent.getStringExtra("is_web");
		btnnext.setTypeface(SplashScreen.ProxiNova_Bold);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		// scrollVideoview.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// return true;
		// }
		// });
		// scrollVideoview.scrollTo(0, 75);
		btnnext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String path = "file:///mnt/sdcard/CamRate_Images/" + ImageName;
				System.out.println();

				createBitmap(imgPostImage);
				// bmp = Bitmap.createBitmap(imgPostImage.getDrawingCache(true));
				if (isVideo.equalsIgnoreCase("0")) {

					Intent intent = new Intent(RateItScreen.this, FilterScreen.class);
					intent.putExtra("rate", rating);
					intent.putExtra("image_video", image_video);
					startActivity(intent);
					overridePendingTransition(0, 0);
				} else {
					Intent intent = new Intent(RateItScreen.this, ShareItScreen.class);
					intent.putExtra("image_video", image_video);
					intent.putExtra("rate", rating);
					startActivity(intent);
					overridePendingTransition(0, 0);
				}

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

		if (isVideo.equalsIgnoreCase("0")) {
			new loadingImage().execute(is_web);
			// setImage(is_web);
		} else {
			// String Videoname = SecondCameraTab.VideoName.replaceAll("/", "");
			Video_Url = "file:///mnt/sdcard/CamRate_Video/" + SecondCameraTab.VideoName;
			String url = "/sdcard/CamRate_Video/" + SecondCameraTab.VideoName;
			getwidthHeight(url);
			// setnPlayVideo();
		}

	}

	public class loadingImage extends AsyncTask<String, Void, Void> {

		String isweb = null;
		Bitmap resizedBitmap = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			isweb = params[0];

			try {
				image_video = "0";
				if (isweb.equalsIgnoreCase("0")) {
					int width = SecondCameraTab.bitmap.getWidth();
					int height = SecondCameraTab.bitmap.getHeight();
					int newWidth = width;
					int newHeight = height;

					// calculate the scale - in this case = 0.4f
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;

					// createa matrix for the manipulation
					Matrix matrix = new Matrix();
					// resize the bit map
					matrix.postScale(scaleWidth, scaleHeight);
					resizedBitmap = Bitmap.createBitmap(SecondCameraTab.bitmap, 0, 0, width, height, matrix, true);

				} else {
					int width = SearchImageFromWeb.bitmap.getWidth();
					int height = SearchImageFromWeb.bitmap.getHeight();
					int newWidth = width;
					int newHeight = height;

					// calculate the scale - in this case = 0.4f
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;

					// createa matrix for the manipulation
					Matrix matrix = new Matrix();
					// resize the bit map
					matrix.postScale(scaleWidth, scaleHeight);

					resizedBitmap = Bitmap.createBitmap(SearchImageFromWeb.bitmap, 0, 0, width, height, matrix, true);

				}

				postImage = fun.getCroppedBitmap1(resizedBitmap);
				fileuri = getOutputMediaFileUri(REQUEST_CAMERA, postImage);

			} catch (Exception e) {
				e.printStackTrace();

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissDialog();
			if (isweb.equalsIgnoreCase("0")) {
				if (SecondCameraTab.camID == 1) {
					resizedBitmap = rotateBitmap(resizedBitmap, 270);
					imgPostImage.setImageBitmap(resizedBitmap);
				} else if (SecondCameraTab.gallery == 1) {
					imgPostImage.setImageBitmap(resizedBitmap);
					SecondCameraTab.gallery = 0;
				} else if (SecondCameraTab.camID == 0) {
					resizedBitmap = rotateBitmap(resizedBitmap, 90);
					imgPostImage.setImageBitmap(resizedBitmap);
				} else {
					imgPostImage.setImageBitmap(resizedBitmap);
				}
			} else {
				imgPostImage.setImageBitmap(resizedBitmap);
			}
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

	public void setImage(String isweb) {

		Bitmap resizedBitmap = null;
		try {
			image_video = "0";

			// recreate the new Bitmap
			if (isweb.equalsIgnoreCase("0")) {

				int width = SecondCameraTab.bitmap.getWidth();
				int height = SecondCameraTab.bitmap.getHeight();
				int newWidth = width;
				int newHeight = height;

				// calculate the scale - in this case = 0.4f
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;

				// createa matrix for the manipulation
				Matrix matrix = new Matrix();
				// resize the bit map
				matrix.postScale(scaleWidth, scaleHeight);
				// rotate the Bitmap
				// matrix.postRotate(90);

				resizedBitmap = Bitmap.createBitmap(SecondCameraTab.bitmap, 0, 0, width, height, matrix, true);
				if (SecondCameraTab.camID == 1) {
					resizedBitmap = rotateBitmap(resizedBitmap, 270);
					imgPostImage.setImageBitmap(resizedBitmap);
				} else if (SecondCameraTab.gallery == 1) {
					imgPostImage.setImageBitmap(resizedBitmap);
					SecondCameraTab.gallery = 0;
				} else if (SecondCameraTab.camID == 0) {
					resizedBitmap = rotateBitmap(resizedBitmap, 90);
					imgPostImage.setImageBitmap(resizedBitmap);
				} else {
					imgPostImage.setImageBitmap(resizedBitmap);
				}

			} else {

				int width = SearchImageFromWeb.bitmap.getWidth();
				int height = SearchImageFromWeb.bitmap.getHeight();
				int newWidth = width;
				int newHeight = height;

				// calculate the scale - in this case = 0.4f
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;

				// createa matrix for the manipulation
				Matrix matrix = new Matrix();
				// resize the bit map
				matrix.postScale(scaleWidth, scaleHeight);
				// rotate the Bitmap
				// matrix.postRotate(90);

				resizedBitmap = Bitmap.createBitmap(SearchImageFromWeb.bitmap, 0, 0, width, height, matrix, true);

				imgPostImage.setImageBitmap(resizedBitmap);
			}
			// System.out.println("imagewidth==>" + imgPostImage.getWidth());
			// System.out.println("imageheight==>" + imgPostImage.getHeight());

			// storeImage(resizedBitmap, "SaveImage.jpg");

			postImage = fun.getCroppedBitmap1(resizedBitmap);

			fileuri = getOutputMediaFileUri(REQUEST_CAMERA, postImage);

		} catch (Exception e) {
			e.printStackTrace();
			// imgPostImage.setImageBitmap(SecondCameraTab.bitmap);
			// postImage = fun.getCroppedBitmap(SecondCameraTab.bitmap);
			// fileuri = getOutputMediaFileUri(REQUEST_CAMERA, postImage);

		}

	}

	private static File getOutputMediaFile(int type, Bitmap result) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

		if (type == REQUEST_CAMERA) {
			ImageName = File.separator + "image_" + timeStamp + ".jpg";
			System.out.println("sep===>" + File.separator + "image_" + timeStamp + ".jpg");
			System.out.println("sepwith===>" + "image_" + timeStamp + ".jpg");
			mediaFile = new File(mediaStorageDir.getPath() + ImageName);
			// Log.d("Tag file","val "+mediaFile);
			// imageFilePath=fileUri.getPath();
			System.out.println("AbsolutePAth===>" + mediaFile.getAbsolutePath());
			FileOutputStream fos = null;
			try {

				fos = new FileOutputStream(mediaFile);

				// Use the compress method on the BitMap object to write image
				// to the OutputStream
				result.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}

		return mediaFile;
	}

	/*** Get image */
	public Uri getOutputMediaFileUri(int type, Bitmap bmp) {
		return Uri.fromFile(getOutputMediaFile(type, bmp));
	}

	public void setnPlayVideo() {
		image_video = "1";

		imgisVideo.setVisibility(View.VISIBLE);

		imgisVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgPostImage.setVisibility(View.INVISIBLE);
				videoView.setVisibility(View.VISIBLE);
				try {
					// Start the MediaController
					MediaController mediacontroller = new MediaController(RateItScreen.this);
					mediacontroller.setAnchorView(videoView);
					videoView.setMediaController(null);
					Uri video = Uri.parse(Video_Url);
					System.out.println("videourl URi==>" + video.toString());
					videoView.setVideoURI(video);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				videoView.requestFocus();
				videoView.setOnPreparedListener(new OnPreparedListener() {
					// Close the progress bar and play the video
					public void onPrepared(MediaPlayer mp) {
						// progressBar.setVisibility(View.INVISIBLE);
						imgisVideo.setVisibility(View.INVISIBLE);
						System.out.println("Bufferfinish");
						videoView.start();

					}
				});
				videoView.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
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
							videoView.seekTo(videoposition);
							videoView.start();
							return false;
						}

					}
				});
			}
		});

	}

	public void getwidthHeight(String url) {
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(url);
		int currentPosition = videoView.getCurrentPosition(); // in millisecond
		Bitmap bmFrame = metaRetriever.getFrameAtTime(currentPosition * 1000); // unit in microsecond
		if (bmFrame != null) {
			imgPostImage.setImageBitmap(bmFrame);
		}
		String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
		String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
		System.out.println("Height of Video==>" + height);
		System.out.println("width of Video==>" + width);
		getVideoparams(Integer.parseInt(width), Integer.parseInt(height));
	}

	public void getVideoparams(int width, int height) {
		// scrollVideoview.setVisibility(View.VISIBLE);
		relVideoview.setVisibility(View.VISIBLE);
		int fixWidth = 300;
		int tempHeight = ((width * fixWidth) / height);
		final int y = (tempHeight - fixWidth) / 2;

		System.out.println("tempHeight==>" + tempHeight);
		System.out.println("y==>" + y);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float widthDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fixWidth, dm);
		float heightDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tempHeight, dm);

		System.out.println("widthDP==>" + widthDP);
		System.out.println("heightDP==>" + heightDP);
		int w = Math.round(widthDP);
		int h = Math.round(heightDP);

		// params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h + 300);
		params1 = new RelativeLayout.LayoutParams(Device_width, Device_width);

		final float yDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, dm);
		// params.setMargins(left, top, right, bottom)
		// params=new RelativeLayout.LayoutParams(500, 500);
		relVideoview.setLayoutParams(params1);
		// scrollVideoview.setLayoutParams(params1);
		System.out.println("params==>" + params1);
		// scrollVideoview.setScrollY(Math.round(yDP));
		// scrollVideoview.scrollTo(0, y);
		ypos = Math.round(yDP);
		System.out.println("Ydp===>" + ypos);
		// Handler handler = new Handler();
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// scrollVideoview.scrollTo(0, ypos);
		// }
		// }, 75);
		// scrollVideoview.setEnableScrolling(true);
		setnPlayVideo();
		// disable scrolling

	}

	private int getOrientation(String path) throws IOException {
		ExifInterface exif = new ExifInterface(path);
		int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	private Bitmap rotateBitmap(Bitmap srcBitmap, int angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		srcBitmap.recycle();
		return rotatedBitmap;
	}

	public Bitmap rotateBitmapFront(Bitmap bmp) {
		Matrix rotateRight = new Matrix();
		rotateRight.preRotate(90);

		float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
		rotateRight = new Matrix();
		Matrix matrixMirrorY = new Matrix();
		matrixMirrorY.setValues(mirrorY);

		rotateRight.postConcat(matrixMirrorY);

		rotateRight.preRotate(270);

		Bitmap rImg = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), rotateRight, true);
		return rImg;
	}

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(RateItScreen.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(RateItScreen.this, android.R.style.Theme_Light_NoTitleBar);
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
