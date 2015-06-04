package com.camrate.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ImageDownloaderOnly extends AsyncTask<String, Void, Bitmap> {
	ProgressDialog dialog;
	Context con;
	static String ImageName;
	private OnTaskCompleted listener;
	public final static int REQUEST_CAMERA = 2;
	public static String IMAGE_DIRECTORY_NAME = "CamRate_Images";
	public static File mediaFile;;
	public static Uri fileuri;

	public ImageDownloaderOnly(OnTaskCompleted listener) {
		this.listener = listener;
	}

	public ImageDownloaderOnly(Context c, String imagename, OnTaskCompleted listener) {
		this.con = c;
		this.ImageName = imagename;
		this.listener = listener;

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
			/*
			 * Bitmap updatedBitmap = updateBitmap(result); if (updatedBitmap != null) {
			 * 
			 * }
			 */
			fileuri = getOutputMediaFileUri(REQUEST_CAMERA, result);
		}
		dialog.dismiss();
		listener.onTaskCompleted();
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
		if (type == REQUEST_CAMERA) {

			mediaFile = new File(mediaStorageDir.getPath() + ImageName);
			// Log.d("Tag file","val "+mediaFile);
			// imageFilePath=fileUri.getPath();
			System.out.println("AbsolutePAth===>" + mediaFile.getAbsolutePath());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(mediaFile);
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

	public interface OnTaskCompleted {
		void onTaskCompleted();
	}

}
