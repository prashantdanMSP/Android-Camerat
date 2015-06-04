package com.camrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.camrate.tabs.TabGroup3Activity;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

@SuppressLint("SdCardPath")
public class SecondCameraTab extends Activity implements Callback, OnSeekBarChangeListener, OnTouchListener {
	private static final int SELECT_IMAGE = 1234;
	private static final int SELECT_VIDEO = 12345;
	Camera camera = null;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	private boolean isLighOn = false;
	private boolean isGridDisplay = true;
	private boolean chkCameraMode = true;
	private static final String IMAGE_DIRECTORY_NAME = "CamRate_Images";
	LinearLayout linearVideoInfo;
	FrameLayout frameLayout;
	ImageButton btnTakeShot, btnWeb, btnTakeVideo, btnSelectVideo, btnDeleteVideo, btnButtonGrid, btnImportOptions, btnchkCameraMode, btnswitchmode;
	ImageView btnchkFlashMode;
	Button btnNext;
	int currentCameraId = 0;
	public static int camID = 0;
	public static int CamVideo = 0;
	public static int gallery = 0;
	SeekBar skBarVideo;
	private Handler mHandler;
	ImageView imgGridView;
	protected int counter = 1;
	List<Integer> lstRecordedTimes;
	byte[] bindata;
	public static Bitmap bitmap = null;
	private GestureDetector gestureScanner;
	public static String VideoName;
	public static String picImage;
	RelativeLayout relBottom;
	TextView chronometer;
	Camera.Parameters paramsCamera;
	private int countTime = 0;
	private MediaRecorder mrec;
	private int fileCounter = 1;
	ProgressDialog pDialog;
	FrameLayout framePreview;
	float mDist = 0;
	ScheduledExecutorService myScheduledExecutorService;
	SharedPreferences prefs;
	private boolean chkFocusMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.secondcamera);

		mHandler = new Handler();
		lstRecordedTimes = new ArrayList<Integer>();
		lstRecordedTimes.add(1);
		getWindow().setFormat(PixelFormat.UNKNOWN);

		surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceView.setVisibility(View.VISIBLE);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.secondcustom1, null);
		LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(viewControl, layoutParamsControl);

		// btnchkFlashMode = (ImageButton) findViewById(R.id.chkFlashLight);
		btnchkFlashMode = (ImageView) findViewById(R.id.chkFlashLight);
		btnchkCameraMode = (ImageButton) findViewById(R.id.btnswitchToVideo);
		btnswitchmode = (ImageButton) findViewById(R.id.btnswitchToCamera);
		// chkFlashMode.setOnCheckedChangeListener(flashModeListener);
		// chkCameraMode.setOnCheckedChangeListener(cameraModeListener);
		btnButtonGrid = (ImageButton) findViewById(R.id.imageButtonGrid);
		imgGridView = (ImageView) findViewById(R.id.imgGridDisplay);
		btnImportOptions = (ImageButton) findViewById(R.id.imgCameraImportWeb);
		btnTakeShot = (ImageButton) findViewById(R.id.btnCameraTakePicture);
		btnWeb = (ImageButton) findViewById(R.id.imgCameraWeb);
		btnNext = (Button) findViewById(R.id.btnCameraNext);
		TextView lbltextTitle = (TextView) findViewById(R.id.lblInviteFriTitle);
		lbltextTitle.setTypeface(SplashScreen.ProxiNova_Bold);
		btnTakeVideo = (ImageButton) findViewById(R.id.btnCameraTakeVideo);
		// btnTakeVideo.setOnLongClickListener(this);
		btnTakeVideo.setOnTouchListener(this);
		chronometer = (TextView) findViewById(R.id.chronometer);
		relBottom = (RelativeLayout) findViewById(R.id.relvideoBottom);
		relBottom.setBackgroundResource(R.color.Transperant);
		btnSelectVideo = (ImageButton) findViewById(R.id.btnSelectVideo);
		btnDeleteVideo = (ImageButton) findViewById(R.id.btnDeleteVideo);
		btnDeleteVideo.setVisibility(View.INVISIBLE);

		skBarVideo = (SeekBar) findViewById(R.id.skBarVideoRecording);
		skBarVideo.setOnSeekBarChangeListener(this);

		linearVideoInfo = (LinearLayout) findViewById(R.id.linearMinimumVideoInfo);
		pDialog = new ProgressDialog(getParent());
		pDialog.setMessage("Loading...");
		pDialog.setCancelable(false);

		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		myScheduledExecutorService = Executors.newScheduledThreadPool(1);

		gestureScanner = new GestureDetector(new DoubleTapGestureDetector());

		surfaceView.setOnTouchListener(this);

		myScheduledExecutorService.schedule(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				camera.autoFocus(myAutoFocusCallback);
			}
		}, 50, TimeUnit.MILLISECONDS);

		btnchkFlashMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setFlashMode();
			}
		});
		btnButtonGrid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isGridDisplay) {
					imgGridView.setImageResource(R.drawable.grid_large);
					isGridDisplay = false;
				} else {
					imgGridView.setImageResource(0);
					isGridDisplay = true;
				}
			}
		});
		btnImportOptions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (chkCameraMode) {
					chooseImportOptions();
				} else {
					chooseImportOptionsVideo();
				}
			}
		});
		btnchkCameraMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					camera.reconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// deleteFinalVideo();
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {

						deleteAllRecordedFile();
						if (lstRecordedTimes != null || lstRecordedTimes.size() != 0)
							lstRecordedTimes.clear();

						if (chkCameraMode) {
							if (mrec != null)
								releaseMediaRecorder();
							initMediaRecorder();
							counter = 1;
							CamVideo = 1;
							skBarVideo.setProgress(counter);
							skBarVideo.invalidate();
							btnTakeVideo.setVisibility(View.VISIBLE);
							chronometer.setVisibility(View.VISIBLE);
							btnTakeShot.setVisibility(View.INVISIBLE);
							btnNext.setVisibility(View.VISIBLE);
							skBarVideo.setVisibility(View.VISIBLE);
							btnchkCameraMode.setVisibility(View.INVISIBLE);
							btnswitchmode.setVisibility(View.VISIBLE);
							relBottom.setBackgroundResource(R.color.Black);
							chkCameraMode = false;
							chkFocusMode = false;

						} else {
							if (mrec != null) {
								releaseMediaRecorder();
							}
							chkCameraMode = true;
							chkFocusMode = true;

							// relBottom.setBackgroundResource(Color.TRANSPARENT);
							// relBottom.invalidate();
							CamVideo = 0;
							btnTakeVideo.setVisibility(View.INVISIBLE);
							btnswitchmode.setVisibility(View.INVISIBLE);
							chronometer.setVisibility(View.INVISIBLE);
							btnTakeShot.setVisibility(View.VISIBLE);
							btnNext.setVisibility(View.INVISIBLE);
							skBarVideo.setVisibility(View.INVISIBLE);
							// btnSelectVideo.setVisibility(View.INVISIBLE);
							// btnSelectVideo.invalidate();
							btnchkCameraMode.setVisibility(View.VISIBLE);
						}
					}
				}, 1500);

				System.out.println("chkmode===============");
				if (isLighOn) {

					if (CamVideo == 0) {
						paramsCamera.setFlashMode(Parameters.FLASH_MODE_ON);
					} else {
						paramsCamera.setFlashMode(Parameters.FLASH_MODE_TORCH);
					}

					camera.setParameters(paramsCamera);
					btnchkFlashMode.setImageResource(R.drawable.btnflash);
					btnchkFlashMode.invalidate();
				} else {
					paramsCamera.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(paramsCamera);
					btnchkFlashMode.setImageResource(R.drawable.btnflashoff);
					btnchkFlashMode.invalidate();
				}

			}
		});
		btnswitchmode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					camera.reconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						deleteAllRecordedFile();
						// deleteFinalVideo();
						counter = 1;
						skBarVideo.setProgress(counter);
						skBarVideo.invalidate();

						if (lstRecordedTimes != null || lstRecordedTimes.size() != 0)
							lstRecordedTimes.clear();

						if (chkCameraMode) {
							if (mrec != null)
								releaseMediaRecorder();

							initMediaRecorder();
							btnTakeVideo.setVisibility(View.VISIBLE);
							chronometer.setVisibility(View.VISIBLE);
							btnTakeShot.setVisibility(View.INVISIBLE);
							btnNext.setVisibility(View.VISIBLE);
							skBarVideo.setVisibility(View.VISIBLE);
							btnchkCameraMode.setVisibility(View.INVISIBLE);
							btnswitchmode.setVisibility(View.VISIBLE);
							btnWeb.setEnabled(false);
							// btnWeb.setAlpha((float) 0.3);
							chkCameraMode = false;
							chkFocusMode = false;

						} else {
							if (mrec != null) {
								releaseMediaRecorder();
							}
							CamVideo = 0;
							relBottom.setBackgroundColor(Color.TRANSPARENT);
							chkCameraMode = true;
							chkFocusMode = true;
							btnSelectVideo.setVisibility(View.INVISIBLE);
							btnDeleteVideo.setVisibility(View.INVISIBLE);
							chronometer.setVisibility(View.INVISIBLE);
							btnTakeVideo.setVisibility(View.INVISIBLE);
							btnTakeShot.setVisibility(View.VISIBLE);
							btnNext.setVisibility(View.INVISIBLE);
							skBarVideo.setVisibility(View.INVISIBLE);
							btnchkCameraMode.setVisibility(View.VISIBLE);
							btnswitchmode.setVisibility(View.INVISIBLE);
							btnWeb.setEnabled(true);
							// btnWeb.setAlpha((float) 1.0);

						}
					}
				}, 1500);
				CamVideo = 0;
				System.out.println("switchmode===============" + CamVideo);
				if (isLighOn) {

					if (CamVideo == 0) {
						paramsCamera.setFlashMode(Parameters.FLASH_MODE_ON);
					} else {
						paramsCamera.setFlashMode(Parameters.FLASH_MODE_TORCH);
					}

					camera.setParameters(paramsCamera);

					btnchkFlashMode.setImageResource(R.drawable.btnflash);
					btnchkFlashMode.invalidate();
				} else {
					paramsCamera.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(paramsCamera);
					btnchkFlashMode.setImageResource(R.drawable.btnflashoff);
					btnchkFlashMode.invalidate();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		try {
			surfaceView.setVisibility(View.VISIBLE);
		} catch (Exception e) {

		}
		if (camera != null && !previewing) {
			camera.startPreview();
			previewing = true;
		}
		super.onResume();

		try {
			if (mrec != null) {
				releaseMediaRecorder();

			}
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.removeCallbacks(mRecordVideoTask);
			counter = 1;
			skBarVideo.setProgress(counter);
			skBarVideo.invalidate();
			deleteLastRecordedFile();
			btnSelectVideo.setVisibility(View.INVISIBLE);
			btnDeleteVideo.setVisibility(View.INVISIBLE);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void setFlashMode() {
		camera.lock();
		SharedPreferences.Editor edit = prefs.edit();

		if (isLighOn) {

			paramsCamera.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(paramsCamera);
			isLighOn = false;
			edit.putBoolean("camera_flash", isLighOn);
			edit.commit();
			btnchkFlashMode.setImageResource(R.drawable.btnflashoff);
			btnchkFlashMode.invalidate();
		} else {
			if (CamVideo == 0) {
				paramsCamera.setFlashMode(Parameters.FLASH_MODE_ON);
			} else {
				paramsCamera.setFlashMode(Parameters.FLASH_MODE_TORCH);
			}

			camera.setParameters(paramsCamera);
			// camera.startPreview();
			isLighOn = true;
			edit.putBoolean("camera_flash", isLighOn);
			edit.commit();
			btnchkFlashMode.setImageResource(R.drawable.btnflash);
			btnchkFlashMode.invalidate();
		}
		camera.unlock();
	}

	private boolean isFlashSupported(PackageManager packageManager) {
		// if device support camera flash?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (camera != null) {
			releaseCamera();

		}
		try {
			Log.e("Camera Id", "-->" + currentCameraId);
			camera = Camera.open(currentCameraId);
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		try {
			if (previewing) {
				camera.stopPreview();
				previewing = false;
				camera = null;

			}

			paramsCamera = camera.getParameters();
			System.out.println("CurrentCameraId in Surfacechanged==>" + currentCameraId);
			camera.setParameters(paramsCamera);

			camera.setDisplayOrientation(90);
			camera.startPreview();
			previewing = true;
			System.out.println("width==>" + width);
			System.out.println("height==>" + height);
			isLighOn = prefs.getBoolean("camera_flash", false);
			if (isLighOn) {

				if (CamVideo == 0) {
					paramsCamera.setFlashMode(Parameters.FLASH_MODE_ON);
				} else {
					paramsCamera.setFlashMode(Parameters.FLASH_MODE_TORCH);
				}

				camera.setParameters(paramsCamera);
				btnchkFlashMode.setImageResource(R.drawable.btnflash);
				btnchkFlashMode.invalidate();
			} else {
				paramsCamera.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(paramsCamera);
				btnchkFlashMode.setImageResource(R.drawable.btnflashoff);
				btnchkFlashMode.invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// setFlashMode();

	}

	@Override
	protected void onPause() {
		if (camera != null && previewing) {
			camera.stopPreview();
			previewing = false;
		}
		super.onPause();
		// releaseMediaRecorder();
		// releaseCamera();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
			previewing = false;
		}
	}

	public void onTakePicture(View v) {
		if (mrec != null) {
			releaseMediaRecorder();
		}
		Log.e("CaptureID", "-->" + capturedIt);
		Log.e("Camera Id", "-->" + currentCameraId);

		if (capturedIt == null) {
			Log.e("CaptureID", "null-->");
		} else {
			Log.e("CaptureID", "-->");
		}
		try {
			camera.reconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera.takePicture(null, null, capturedIt);

	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			skBarVideo.setProgress(counter);
			skBarVideo.invalidate();
			counter += 1;
			// mHandler.postDelayed(this, 93);
			mHandler.postDelayed(this, 100);
		}
	};

	private Runnable mRecordVideoTask = new Runnable() {
		public void run() {
			if (mrec == null) {
				initMediaRecorder();
			}
			try {
				// mrec.prepare();
				Thread.sleep(1000);
				mrec.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (chkFocusMode) {
				btnSelectVideo.setVisibility(View.VISIBLE);
				btnDeleteVideo.setVisibility(View.INVISIBLE);
			} else {
				btnSelectVideo.setVisibility(View.INVISIBLE);
				btnDeleteVideo.setVisibility(View.INVISIBLE);

			}
			chkCameraMode = false;
			chkFocusMode = false;
		}
	};

	public PictureCallback capturedIt = new PictureCallback() {

		@Override
		public void onPictureTaken(final byte[] data, Camera camera) {
			System.out.println("hi");

			if (!pDialog.isShowing()) {
				pDialog.show();
			}
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// Do something after 5s = 5000ms

					if (data == null) {
						System.out.println("nulldata");
					} else {
						bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					}

					camID = currentCameraId;
					Log.d("Tag", "Camera Screen");

					if (pDialog.isShowing()) {
						pDialog.dismiss();
					}
					Intent intent = new Intent(SecondCameraTab.this, RateItScreen.class);
					intent.putExtra("is_Video", "0");
					intent.putExtra("is_web", "0");
					startActivity(intent);

				}
			}, 1000);

		}

	};

	// protected void onResume() {
	//
	// if (ActivityManage.isFromCreate) {
	// recreate();
	// } else {
	//
	// }
	// ActivityManage.isFromCreate = true;
	// };

	public void onBack(View v) {
		try {
			surfaceView.setVisibility(View.INVISIBLE);
		} catch (Exception e) {

		}
		((TabGroup3Activity) getParent()).changeTab();

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		try {
			surfaceView.setVisibility(View.INVISIBLE);
		} catch (Exception e) {

		}

		((TabGroup3Activity) getParent()).changeTab();
	}

	public void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release(); // release the camera for other applications
			camera = null;
		}
	}

	// open gallery for choosing picture
	public void onSDCardClick() {

		if (!btnNext.isShown()) {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			SecondCameraTab.this.getParent().startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
		} else {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("video/*");
			SecondCameraTab.this.getParent().startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_VIDEO);
		}
	}

	public void chooseImportOptions() {

		final CharSequence[] items = { "Import from gallery", "Search from web", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Import from gallery")) {
					onSDCardClick();
				} else if (items[item].equals("Search from web")) {
					onWebClick();
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	public void chooseImportOptionsVideo() {

		final CharSequence[] items = { "Import from gallery", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Import from gallery")) {
					onSDCardClick();
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	// picture chosen by user
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_IMAGE:
			if (resultCode == RESULT_OK) {

				if (!pDialog.isShowing()) {
					pDialog.show();
				}
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Do something after 5s = 5000ms
						Uri selectedImageUri = data.getData();
						getBitmapFromUri(selectedImageUri);
						Intent intent = new Intent(SecondCameraTab.this, RateItScreen.class);
						intent.putExtra("is_Video", "0");
						intent.putExtra("is_web", "0");
						startActivity(intent);
						if (pDialog.isShowing()) {
							pDialog.dismiss();
						}
					}
				}, 500);
			}
			break;

		case SELECT_VIDEO:
			if (resultCode == RESULT_OK) {
				Uri selectedVideoUri = data.getData();

				Intent intent = new Intent(SecondCameraTab.this, VideoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("url", selectedVideoUri.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		}
	}

	public void getBitmapFromUri(Uri imageUri) {
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
			gallery = 1;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onWebClick() {
		Intent intent = new Intent(SecondCameraTab.this, SearchImageFromWeb.class);
		startActivity(intent);
	}

	public void onChangeCameraClick(View v) {

		try {

			int noCamera = Camera.getNumberOfCameras();
			System.out.println("Camera==>" + noCamera);
			System.out.println("Camera==>" + CameraInfo.CAMERA_FACING_FRONT);
			System.out.println("Camera==>" + CameraInfo.CAMERA_FACING_BACK);
			if (noCamera == 2) {
				if (currentCameraId == CameraInfo.CAMERA_FACING_BACK) {
					currentCameraId = CameraInfo.CAMERA_FACING_FRONT;
				} else {
					currentCameraId = CameraInfo.CAMERA_FACING_BACK;
				}
			} else {
				currentCameraId = CameraInfo.CAMERA_FACING_BACK;
			}

			if (chkCameraMode) {
				surfaceDestroyed(surfaceHolder);
				surfaceCreated(surfaceHolder);
				surfaceChanged(surfaceHolder, 0, 0, 0);

			} else {
				// in video recording
				camera.lock();
				// camera = Camera.open(currentCameraId);
				paramsCamera.set("cam-mode", currentCameraId);
				paramsCamera.set("camera-id", currentCameraId);
				camera.setParameters(paramsCamera);
				// camera.setDisplayOrientation(90);
				camera.unlock();
				System.out.println("this is the current camera - " + currentCameraId);

				try {
					// releaseCamera();
					camera.reconnect();
					// camera.lock();
					// camera.unlock();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				surfaceDestroyed(surfaceHolder);
				surfaceCreated(surfaceHolder);
				surfaceChanged(surfaceHolder, 0, 0, 0);
				if (mrec != null)
					releaseMediaRecorder();
				initMediaRecorder();
				counter = 1;
				CamVideo = 1;
				skBarVideo.setProgress(counter);
				skBarVideo.invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// for next button in video mode
	public void onNext(View v) {
		// check the the video is minimum 5 second recorded or not
		if (skBarVideo.getProgress() <= 50) {
			if (linearVideoInfo.isShown()) {
				linearVideoInfo.setVisibility(View.INVISIBLE);
			} else {
				linearVideoInfo.setVisibility(View.VISIBLE);
			}
		} else {
			// merge the video
			new MergeVideos().execute();
		}
	}

	protected void releaseMediaRecorder() {
		if (mrec != null) {
			mrec.release();
			mrec = null;
		}
	}

	@SuppressLint("SdCardPath")
	protected void initMediaRecorder() {

		try {

			CamVideo = 1;

			mrec = new MediaRecorder();
			camera.lock();
			// camera = Camera.open(currentCameraId);

			isLighOn = prefs.getBoolean("camera_flash", false);
			if (isLighOn) {

				if (CamVideo == 0) {
					paramsCamera.setFlashMode(Parameters.FLASH_MODE_ON);
				} else {
					paramsCamera.setFlashMode(Parameters.FLASH_MODE_TORCH);
				}

				camera.setParameters(paramsCamera);
				btnchkFlashMode.setImageResource(R.drawable.btnflash);
				btnchkFlashMode.invalidate();
			} else {
				paramsCamera.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(paramsCamera);
				btnchkFlashMode.setImageResource(R.drawable.btnflashoff);
				btnchkFlashMode.invalidate();
			}
			paramsCamera.set("cam-mode", currentCameraId);
			paramsCamera.set("camera-id", currentCameraId);
			camera.setParameters(paramsCamera);

			camera.unlock();
			// camera.lock();
			mrec.setCamera(camera);

			mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mrec.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
			mrec.setPreviewDisplay(surfaceHolder.getSurface());
			if (currentCameraId == 0) {
				mrec.setOrientationHint(90);
			} else {
				mrec.setOrientationHint(270);
			}

			File file = new File(Environment.getExternalStorageDirectory().toString() + "/sunny" + fileCounter + ".mp4");
			if (file.exists()) {
				fileCounter += 1;
			}
			File videoFile = new File(Environment.getExternalStorageDirectory().toString() + "/sunny" + fileCounter + ".mp4");
			mrec.setOutputFile(videoFile.getAbsolutePath());

			mrec.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteAllRecordedFile() {
		File file = new File(Environment.getExternalStorageDirectory().toString());
		File[] lists = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".mp4") && filename.startsWith("sunny"))
					return true;
				return false;
			}
		});
		if (lists != null) {
			if (lists.length > 0) {
				for (int i = 0; i < lists.length; i++) {
					File f = lists[i];
					f.delete();
				}
			}

		}
	}

	public void deleteLastRecordedFile() {
		File file = new File(Environment.getExternalStorageDirectory().toString());
		File[] lists = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".mp4") && filename.startsWith("sunny"))
					return true;
				return false;
			}
		});
		if (lists != null) {
			if (lists.length > 0) {
				for (int i = 0; i < lists.length; i++) {
					File f = lists[i];
					f.delete();
				}
			}

		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (progress == 10) {
			mHandler.removeCallbacks(mUpdateTimeTask);
		}
		if (progress == 1) {
			// frameLayout.setVisibility(View.INVISIBLE);
			btnSelectVideo.setVisibility(View.VISIBLE);
			btnDeleteVideo.setVisibility(View.INVISIBLE);
			// chkCameraMode.setVisibility(View.VISIBLE);
			chkCameraMode = true;

			deleteFinalVideo();
		}

		if (linearVideoInfo.isShown()) {
			if (progress >= 50) {
				linearVideoInfo.setVisibility(View.INVISIBLE);
			}
		}

		int devideNO = progress / 10;
		try {
			int prog = progress / 10;
			chronometer.setText("0.0" + (devideNO) + "");
			chronometer.invalidate();
			if (prog == 10) {
				chronometer.setText("0." + (devideNO) + "");
				chronometer.invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteFinalVideo() {
		File file = new File("/sdcard/finalVideo.mp4");
		if (file.exists())
			file.delete();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	// to delete captured video
	public void onDeleteVideoSegment(View v) {

		skBarVideo.setProgress(counter);
		skBarVideo.invalidate();
		// counter = maxRange;
		if (skBarVideo.getProgress() % 10 == 0) {
			try {
				int prog = skBarVideo.getProgress() / 10;
				chronometer.setText("0.0" + (prog) + "");
				chronometer.invalidate();
				if (prog == 10) {
					chronometer.setText("0." + (prog) + "");
					chronometer.invalidate();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		deleteLastRecordedFile();
		btnSelectVideo.setVisibility(View.VISIBLE);
		v.setVisibility(View.INVISIBLE);

	}

	// to select captured video for delete
	public void onSelectVideoSegment(View v) {
		btnDeleteVideo.setVisibility(View.VISIBLE);
		v.setVisibility(View.INVISIBLE);

		int maxRange = 1;
		if (lstRecordedTimes.size() > 0 && lstRecordedTimes != null) {
			lstRecordedTimes.remove(lstRecordedTimes.size() - 1);
			for (int i = 0; i < lstRecordedTimes.size(); i++) {
				maxRange = lstRecordedTimes.get(i);
			}
		}
		skBarVideo.setSecondaryProgress(maxRange);
		skBarVideo.invalidate();
		counter = maxRange;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (CamVideo == 0) {
			try {
				int action = event.getAction();

				if (event.getPointerCount() > 1) {
					// handle multi-touch events
					if (action == MotionEvent.ACTION_POINTER_DOWN) {
						mDist = getFingerSpacing(event);
					} else if (action == MotionEvent.ACTION_MOVE && paramsCamera.isZoomSupported()) {

						handleZoom(event, paramsCamera);
					}
				} else {
					// handle single touch events
					if (action == MotionEvent.ACTION_UP) {
						handleFocus(event, paramsCamera);
					}
				}
			} catch (Exception e) {

			}

		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mHandler.postDelayed(mRecordVideoTask, 200);
			mHandler.postDelayed(mUpdateTimeTask, 1500);
			// CamVideo = 1;
			break;

		case MotionEvent.ACTION_UP:
			if (mrec != null) {
				releaseMediaRecorder();
				lstRecordedTimes.add(skBarVideo.getProgress());
			}
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.removeCallbacks(mRecordVideoTask);
			break;

		default:
			break;
		}

		return true;
	}

	private class MergeVideos extends AsyncTask<Void, Void, Void> {
		File f1, f2, f3, f4, f5;
		ProgressDialog pd;
		String path = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(getParent(), "", "");
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			deleteAllRecordedFile();
			if (pd.isShowing())
				pd.dismiss();

			// now we can find the final video file from sdcard
			// start activity - rate it
			Intent intent = new Intent(SecondCameraTab.this, RateItScreen.class);
			intent.putExtra("is_Video", "1");
			intent.putExtra("is_web", "0");
			startActivity(intent);

		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			try {

				String dir = Environment.getExternalStorageDirectory().toString() + "/CamRate_Video";
				File renameFile1 = new File(dir);
				if (!renameFile1.exists()) {
					renameFile1.mkdirs();
				}
				getOutputMediaFile(1);
				System.out.println("Videoname==>" + VideoName);
				path = dir + "/" + VideoName;
				File newFileName = new File(dir, VideoName);
				final FileOutputStream fos = new FileOutputStream(newFileName);

				File file = new File(Environment.getExternalStorageDirectory().toString());
				File[] lists = file.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".mp4") && filename.startsWith("sunny"))
							return true;
						return false;
					}
				});

				if (lists.length == 1) {
					// only one video recorded
					File renameFile2 = new File(Environment.getExternalStorageDirectory().toString() + "/sunny1.mp4");
					String dir1 = Environment.getExternalStorageDirectory().toString() + "/CamRate_Video";

					File renameFile3 = new File(dir1);
					if (!renameFile3.exists()) {
						renameFile3.mkdirs();
					}
					getOutputMediaFile(1);
					System.out.println("Videoname==>" + VideoName);
					File newFileName1 = new File(dir1, VideoName);
					if (renameFile2.exists()) {
						renameFile2.renameTo(newFileName1);
					}
				}

				if (lists.length == 2) {
					// Editor edit = prefs.edit();
					// edit.putBoolean("is_videoRotate", true);
					// edit.commit();
					f1 = lists[0];
					f2 = lists[1];
					// final FileInputStream fisOne = new FileInputStream(f1);
					// final FileInputStream fisTwo = new FileInputStream(f2);
					append(f1, f2);
				}

				if (lists.length == 3) {

					f1 = lists[0];
					f2 = lists[1];
					f3 = lists[2];
					final FileInputStream fisOne = new FileInputStream(f1);
					final FileInputStream fisTwo = new FileInputStream(f2);
					final FileInputStream fisThree = new FileInputStream(f3);
					append(f1, f2, f3);
				}

				if (lists.length == 4) {

					f1 = lists[0];
					f2 = lists[1];
					f3 = lists[2];
					f4 = lists[3];
					final FileInputStream fisOne = new FileInputStream(f1);
					final FileInputStream fisTwo = new FileInputStream(f2);
					final FileInputStream fisThree = new FileInputStream(f3);
					final FileInputStream fisFour = new FileInputStream(f4);
					append(f1, f2, f3, f4);
				}

				if (lists.length == 5) {
					f1 = lists[0];
					f2 = lists[1];
					f3 = lists[2];
					f4 = lists[3];
					f5 = lists[4];

					final FileInputStream fisOne = new FileInputStream(f1);
					final FileInputStream fisTwo = new FileInputStream(f2);
					final FileInputStream fisThree = new FileInputStream(f3);
					final FileInputStream fisFour = new FileInputStream(f4);
					final FileInputStream fisFive = new FileInputStream(f5);
					append(f1, f2, f3, f4, f5);

				}

			} catch (Exception e) {
			}
			return null;

		}

		private void append(File f1, File f2, File f3, File f4, File f5) throws IOException {

			// Vishal Code

			Movie[] inMovies = new Movie[] { MovieCreator.build(f1.getAbsolutePath()), MovieCreator.build(f2.getAbsolutePath()), MovieCreator.build(f3.getAbsolutePath()), MovieCreator.build(f4.getAbsolutePath()), MovieCreator.build(f5.getAbsolutePath()) };
			List<Track> videoTracks = new LinkedList<Track>();
			List<Track> audioTracks = new LinkedList<Track>();
			for (Movie m : inMovies) {
				for (Track t : m.getTracks()) {
					if (t.getHandler().equals("soun")) {
						audioTracks.add(t);
					}
					if (t.getHandler().equals("vide")) {
						videoTracks.add(t);
					}
				}
			}

			Movie result = new Movie();

			if (audioTracks.size() > 0) {
				result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0) {
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
			}

			Container out = new DefaultMp4Builder().build(result);
			String outputPath = Environment.getExternalStorageDirectory() + "/CamRate_Video" + "/" + VideoName;
			Log.e("outputPath", "-->" + outputPath);
			FileChannel fc = new RandomAccessFile(String.format(outputPath), "rw").getChannel();
			out.writeContainer(fc);
			fc.close();

		}

		private void append(File f1, File f2, File f3, File f4) throws IOException {

			// Vishal Code

			Movie[] inMovies = new Movie[] { MovieCreator.build(f1.getAbsolutePath()), MovieCreator.build(f2.getAbsolutePath()), MovieCreator.build(f3.getAbsolutePath()), MovieCreator.build(f4.getAbsolutePath()) };
			List<Track> videoTracks = new LinkedList<Track>();
			List<Track> audioTracks = new LinkedList<Track>();
			for (Movie m : inMovies) {
				for (Track t : m.getTracks()) {
					if (t.getHandler().equals("soun")) {
						audioTracks.add(t);
					}
					if (t.getHandler().equals("vide")) {
						videoTracks.add(t);
					}
				}
			}

			Movie result = new Movie();

			if (audioTracks.size() > 0) {
				result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0) {
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
			}

			Container out = new DefaultMp4Builder().build(result);
			String outputPath = Environment.getExternalStorageDirectory() + "/CamRate_Video" + "/" + VideoName;
			Log.e("outputPath", "-->" + outputPath);
			FileChannel fc = new RandomAccessFile(String.format(outputPath), "rw").getChannel();
			out.writeContainer(fc);
			fc.close();
		}

		private void append(File f1, File f2, File f3) throws IOException {
			// Vishal Code

			Movie[] inMovies = new Movie[] { MovieCreator.build(f1.getAbsolutePath()), MovieCreator.build(f2.getAbsolutePath()), MovieCreator.build(f3.getAbsolutePath()) };
			List<Track> videoTracks = new LinkedList<Track>();
			List<Track> audioTracks = new LinkedList<Track>();
			for (Movie m : inMovies) {
				for (Track t : m.getTracks()) {
					if (t.getHandler().equals("soun")) {
						audioTracks.add(t);
					}
					if (t.getHandler().equals("vide")) {
						videoTracks.add(t);
					}
				}
			}

			Movie result = new Movie();

			if (audioTracks.size() > 0) {
				result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0) {
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
			}

			Container out = new DefaultMp4Builder().build(result);

			String outputPath = Environment.getExternalStorageDirectory() + "/CamRate_Video" + "/" + VideoName;
			Log.e("outputPath", "-->" + outputPath);

			FileChannel fc = new RandomAccessFile(String.format(outputPath), "rw").getChannel();
			out.writeContainer(fc);
			fc.close();
		}

		public void append(File f1, File f2) {

			Movie[] inMovies;
			Log.e("filepath", "--->" + f1.getAbsolutePath());
			Log.e("filepath", "--->" + f2.getAbsolutePath());
			// String filecheckpath = SecondCameraTab.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			// Log.e("filecheckpath", "-->" + filecheckpath);

			try {
				inMovies = new Movie[] { MovieCreator.build(f1.getAbsolutePath()), MovieCreator.build(f2.getAbsolutePath()) };

				// final List<Track> movieOneTracks = movieOne.getTracks();
				// final List<Track> movieTwoTracks = movieTwo.getTracks();
				//
				List<Track> videoTracks = new LinkedList<Track>();
				List<Track> audioTracks = new LinkedList<Track>();

				for (Movie m : inMovies) {
					for (Track t : m.getTracks()) {
						if (t.getHandler().equals("soun")) {
							audioTracks.add(t);
						}
						if (t.getHandler().equals("vide")) {
							videoTracks.add(t);
						}
					}
				}

				Movie result = new Movie();

				if (audioTracks.size() > 0) {
					result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
				}
				if (videoTracks.size() > 0) {
					result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
				}
				Container out = new DefaultMp4Builder().build(result);
				String outputPath = Environment.getExternalStorageDirectory() + "/CamRate_Video" + "/" + VideoName;
				Log.e("outputPath", "-->" + outputPath);
				FileChannel fc = new RandomAccessFile(String.format(outputPath), "rw").getChannel();
				out.writeContainer(fc);
				fc.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class DoubleTapGestureDetector extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d("TAG", "Double Tap Detected ...");
			try {
				if (mrec != null) {
					releaseMediaRecorder();
				}
				camera.takePicture(null, null, capturedIt);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return true;
		}

	}

	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "CamRate_Video");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("CamRate_Video", "Oops! Failed create " + "CamRate_Video" + " directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile;
		VideoName = "VID_" + timeStamp + ".mp4";
		if (type == 1) {
			mediaFile = new File(mediaStorageDir.getPath() + VideoName);
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	protected void onDestroy() {

		Log.d("TAG", "oncamera destroy");
		if (camera != null) {
			camera.release();
		}
		try {
			surfaceView.setVisibility(View.INVISIBLE);
		} catch (Exception e) {

		}
		super.onDestroy();
	}

	private void handleZoom(MotionEvent event, Camera.Parameters params) {
		int maxZoom = params.getMaxZoom();
		int zoom = params.getZoom();
		float newDist = getFingerSpacing(event);
		if (newDist > mDist) {
			// zoom in
			if (zoom < maxZoom)
				zoom++;
		} else if (newDist < mDist) {
			// zoom out
			if (zoom > 0)
				zoom--;
		}
		mDist = newDist;
		params.setZoom(zoom);
		camera.setParameters(params);
	}

	public void handleFocus(MotionEvent event, Camera.Parameters params) {
		try {

			int pointerId = event.getPointerId(0);
			int pointerIndex = event.findPointerIndex(pointerId);
			// Get the pointer's current position
			float x = event.getX(pointerIndex);
			float y = event.getY(pointerIndex);

			List<String> supportedFocusModes = params.getSupportedFocusModes();
			if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				camera.autoFocus(myAutoFocusCallback);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private float getFingerSpacing(MotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			if (arg0) {
				camera.cancelAutoFocus();
			}

		}
	};

}
