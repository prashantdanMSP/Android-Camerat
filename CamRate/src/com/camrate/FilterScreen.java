package com.camrate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path.Direction;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class FilterScreen extends Activity {
	Button btnBack, btnNext;
	TextView txtlblMain;
	ImageView imgfinalImage;
	String image_video = "";
	int rate = 0;
	ImageButton imgRotateLeft, imgRotateRight, imgFlipVertical, imgFlipHorizontal;
	float angle = 0;
	boolean isvFlip = false;
	boolean ishFlip = false;
	public static Bitmap bmp_finalImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filterscreen);
		init();
	}

	public void init() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		btnBack = (Button) findViewById(R.id.btnRateBack);
		btnNext = (Button) findViewById(R.id.btnnext);
		txtlblMain = (TextView) findViewById(R.id.lblInviteFriTitle);
		imgRotateLeft = (ImageButton) findViewById(R.id.imgrotateleft);
		imgRotateRight = (ImageButton) findViewById(R.id.imgrotateright);
		imgFlipVertical = (ImageButton) findViewById(R.id.imgflipvertical);
		imgFlipHorizontal = (ImageButton) findViewById(R.id.imgfliphorizontal);
		imgfinalImage = (ImageView) findViewById(R.id.imagefinalfilter);
		txtlblMain.setText("Flip & Rotate");
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		setFinalImage();
		Intent intent = getIntent();
		rate = intent.getIntExtra("rate", 3);
		image_video = intent.getStringExtra("image_video");
		btnNext.setTypeface(SplashScreen.ProxiNova_Bold);
		imgRotateLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// imgfinalImage.setRotation(-90);
				// imViewAndroid.setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(), R.drawable.beautiful),270));
				// rotateAntiClockWise(90, "Left");

				imgfinalImage.setImageBitmap(rotateImage(RateItScreen.bmp, "Left"));

			}
		});
		imgRotateRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// imgfinalImage.setRotation(90);
				// rotateAntiClockWise(90, "Right");
				imgfinalImage.setImageBitmap(rotateImage(RateItScreen.bmp, "Right"));
			}
		});
		imgFlipHorizontal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgfinalImage.setImageBitmap(flipImage(RateItScreen.bmp, "Horizontal"));
			}
		});
		imgFlipVertical.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// flipImage("Vertical");
				imgfinalImage.setImageBitmap(flipImage(RateItScreen.bmp, "Vertical"));
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgfinalImage.setDrawingCacheEnabled(true);
				bmp_finalImage = Bitmap.createBitmap(imgfinalImage.getDrawingCache(true));
				Intent intent = new Intent(FilterScreen.this, ShareItScreen.class);
				intent.putExtra("image_video", image_video);
				intent.putExtra("rate", rate);
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
		});
	}

	public Bitmap flipImage(Bitmap src, String flipType) {

		Matrix matrix = new Matrix();
		// if vertical
		if (flipType.equalsIgnoreCase("Horizontal")) {
			// y = y * -1
			matrix.preScale(1.0f, -1.0f);
		}
		// if horizonal
		else if (flipType.equalsIgnoreCase("Vertical")) {
			// x = x * -1
			matrix.preScale(-1.0f, 1.0f);
			// unknown type
		} else {
			return null;
		}

		// return transformed image

		RateItScreen.bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

		return RateItScreen.bmp;
	}

	public void setFinalImage() {
		try {
			imgfinalImage.setImageBitmap(RateItScreen.bmp);
		} catch (Exception e) {
			e.printStackTrace();
			imgfinalImage.setImageBitmap(RateItScreen.bmp);
		}
	}

	public void rotateAntiClockWise(float a, String rotate) {
		if (rotate.equalsIgnoreCase("Right")) {
			angle = angle + a;
			Matrix matrix = new Matrix();
			imgfinalImage.setScaleType(ScaleType.MATRIX);
			matrix.postRotate(angle, imgfinalImage.getDrawable().getBounds().width() / 2, imgfinalImage.getDrawable().getBounds().height() / 2);
			imgfinalImage.setImageMatrix(matrix);
		} else {
			angle = angle - a;
			Matrix matrix = new Matrix();
			imgfinalImage.setScaleType(ScaleType.MATRIX);
			matrix.postRotate(angle, imgfinalImage.getDrawable().getBounds().width() / 2, imgfinalImage.getDrawable().getBounds().height() / 2);
			imgfinalImage.setImageMatrix(matrix);
		}
	}

	public Bitmap rotateImage(Bitmap src, String rotateType) {

		Matrix matrix = new Matrix();
		// if vertical
		if (rotateType.equalsIgnoreCase("Right")) {
			// y = y * -1
			matrix.postRotate(90);
		}
		// if horizonal
		else if (rotateType.equalsIgnoreCase("Left")) {
			// x = x * -1
			matrix.postRotate(-90);
			// unknown type
		} else {
			return null;
		}

		// return transformed image

		RateItScreen.bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

		return RateItScreen.bmp;
	}

}
