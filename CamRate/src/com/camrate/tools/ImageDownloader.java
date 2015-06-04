package com.camrate.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter.BigDecimalLayoutForm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Display;

import com.camrate.R;
import com.camrate.SplashScreen;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
	ProgressDialog dialog;
	Context con;
	String countRating, avgRating;
	private OnTaskCompleted listener;
	int width, height;
	int bwh;

	public ImageDownloader(OnTaskCompleted listener) {
		this.listener = listener;
	}

	public ImageDownloader(Context c, String countRating, String avgRating, OnTaskCompleted listener) {
		this.con = c;
		this.countRating = countRating;
		this.avgRating = avgRating;
		this.listener = listener;
		Display display = ((Activity) c).getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		bwh = width - 10;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(con, "", "");
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = null;
		try {
			// Download Image from URL
			InputStream input = new java.net.URL(params[0]).openStream();
			// Decode Bitmap
			bitmap = BitmapFactory.decodeStream(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);

		if (result != null) {

			Bitmap topBar = BitmapFactory.decodeResource(con.getResources(), R.drawable.fbposttopbar);
			Bitmap overlay = BitmapFactory.decodeResource(con.getResources(), R.drawable.fbpostoverlay);
			Bitmap updatedBitmap = joinImages(result, topBar, overlay, avgRating, countRating);// updateBitmap(result, countRating, avgRating,bwh,bwh);

			if (updatedBitmap != null) {
				saveToInternalSorage(updatedBitmap, "shareImg11.png");
			}
		}
		dialog.dismiss();
		listener.onTaskCompleted();
	}

	private synchronized String getStrRate(String ratingStr) {
		String finalRate;
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

	public Bitmap getCroppedBitmap(Bitmap bitmap) {
		bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		final Path path = new Path();
		// spath.addCircle((float) (width / 2), (float) (height / 2),(float) Math.min(width, (height / 2)), Path.Direction.CCW);

		final Canvas canvas = new Canvas(outputBitmap);
		canvas.clipPath(path);
		canvas.drawBitmap(bitmap, 0, 0, null);
		saveToInternalSorage(outputBitmap, "shareImg1.jpg");
		return outputBitmap;
	}

	private Bitmap getStarImage(int average) {
		switch (average) {
		case 1:
			return BitmapFactory.decodeResource(con.getResources(), R.drawable.one_star_fb);
		case 2:
			return BitmapFactory.decodeResource(con.getResources(), R.drawable.two_star_fb);
		case 3:
			return BitmapFactory.decodeResource(con.getResources(), R.drawable.three_star_fb);
		case 4:
			return BitmapFactory.decodeResource(con.getResources(), R.drawable.four_star_fb);
		case 5:
			return BitmapFactory.decodeResource(con.getResources(), R.drawable.five_star_fb);

		default:
			return null;
		}
	}

	private void saveToInternalSorage(Bitmap result, String string) {

		String dir = Environment.getExternalStorageDirectory().toString() + "/Share_CamRate_Images";
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}

		File filenew = new File(dir, string);
		try {
			if (filenew.createNewFile()) {
				try {
					filenew.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(filenew);

			// Use the compress method on the BitMap object to write image
			// to the OutputStream
			result.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface OnTaskCompleted {
		void onTaskCompleted();
	}

	// new images fb post combine code

	private Bitmap joinImages(Bitmap mainImage, Bitmap topBar, Bitmap overlay, String starRating, String CountRating) {
		if (mainImage == null || topBar == null || overlay == null)
			return mainImage;

		Bitmap bmpStar = getStarImage(Integer.parseInt(starRating));

		Bitmap resizedmain = Bitmap.createScaledBitmap(mainImage, 580, 580, true);
		Bitmap resizedbar = Bitmap.createScaledBitmap(topBar, 580, 54, true);
		Bitmap resizedstar = Bitmap.createScaledBitmap(bmpStar, 173, 31, true);
		Bitmap resizedoverlay = Bitmap.createScaledBitmap(overlay, 580, 580, true);

		Bitmap finalImage = Bitmap.createBitmap(580, 635, Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		paint.setTypeface(SplashScreen.ProxiNova_Regular);
		String strRate = "";
		if (countRating.equalsIgnoreCase("1")) {
			strRate = countRating + " Rate";
		} else {
			strRate = countRating + " Rates";
		}

		Path path = new Path();
		// path.addCircle(199, 18, 0, null);
		Canvas canvas = new Canvas(finalImage);
		canvas.drawBitmap(resizedbar, 0, 0, null);
		canvas.drawBitmap(resizedstar, 19, 11, null);
		canvas.drawText(strRate, 210, 35, paint);
		canvas.drawBitmap(resizedmain, 0, 53, null);
		canvas.drawBitmap(resizedoverlay, 0, 54, null);
		return finalImage;
	}
}
