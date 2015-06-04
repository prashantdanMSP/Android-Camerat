package com.camrate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.global.Function;
import com.camrate.global.checkInternet;

public class FBReceiver extends BroadcastReceiver {

	Context context;
	String UserID = "";
	Context cntxt;
	checkInternet chkNet;
	ArrayList<HashMap<String, String>> postshare;
	Function fun;
	SharedPreferences prefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getExtras() != null) {

			cntxt = context;
			prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
			UserID = prefs.getString("id", "");
			chkNet = new checkInternet(context);
			fun = new Function(context);
			DataBaseAdapter mDbHelper = new DataBaseAdapter(context);
			try {
				Log.d("TAG", "create db");
				mDbHelper.createDatabase();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			postshare = mDbHelper.getAllPost();
			if (chkNet.isNetworkConnected()) {

				for (int i = 0; i < postshare.size(); i++) {
					postData(i, postshare);
				}

			} else {

			}

		}
	}

	public void postData(int pos, ArrayList<HashMap<String, String>> items) {

		String User_ID = String.valueOf(items.get(pos).get("Post_userid"));// int
		String postTitle = items.get(pos).get("Post_title");
		String postDesc = items.get(pos).get("Post_description");
		String postLoc = items.get(pos).get("Post_location");
		String postlat = items.get(pos).get("Post_latitude");
		String postlong = items.get(pos).get("Post_longitude");
		String postTags = items.get(pos).get("Post_title");
		String ratingrate = items.get(pos).get("Post_ratingRate");
		String catid = String.valueOf(items.get(pos).get("Post_categoryid"));// int
		String image = items.get(pos).get("Post_imagename");
		String Video_Name = items.get(pos).get("Post_videoname");
		String emailList = items.get(pos).get("Post_emailList");
		// /FbShare
		if (Video_Name.equalsIgnoreCase("1")) {
			publishStory(image, postTags, postDesc);
		} else {
			publishVideo(Video_Name, postTags, postDesc);
		}

	}

	public void publishStory(String ImageName, String PostTag, String PostContent) {
		// String dir = "sdcard/Share_Camrate_Images/";

		String NewImageName = ImageName.replaceAll("/", "");
		System.out.println("FinalName===>" + ImageName);
	//	String filepath = Environment.getExternalStorageDirectory() + "/CamRate_Images";
		String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
		File file = new File(filepath, NewImageName);

		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			Bitmap bi = BitmapFactory.decodeStream(fis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("onCreate", "debug error  e = " + e.toString());
		}

		String FBBody = PostTag + "\n" + PostContent;
		Bundle params = new Bundle();

		params.putString("message", FBBody);
		params.putByteArray("picture", data);

		new submitPost(0, data, FBBody).execute("");

	}

	public void publishVideo(String VideoName, String PostTag, String PostContent) {
		byte[] data = null;
		String filepath = Environment.getExternalStorageDirectory() + "/CamRate_Video/" + VideoName;
		String dataMsg = "Your video description here.";
		Bundle param;
		InputStream is = null;
		try {
			is = new FileInputStream(filepath);
			data = readBytes(is);
			param = new Bundle();
			param.putString("message", dataMsg);
			param.putByteArray("video", data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String FBBody = PostTag + "\n" + PostContent;
		new submitPostVideo(0, data, FBBody).execute("");
	}

	private class submitPost extends AsyncTask<String, Void, String> {
		int position;
		byte[] data;
		String msg;

		public submitPost(int pos, byte[] dt, String mess) {
			position = pos;
			data = dt;
			msg = mess;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d("TAG", "SUBMITING");
			String json = null;
			try {

				InputStream is = null;

				String Token = prefs.getString("Facebook_Access_Tocken", "");

				String url = "https://graph.facebook.com/me/photos?access_token=" + Token;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("message", new StringBody(msg));
				entity.addPart("picture", new ByteArrayBody(data, "facebookPost.png"));
				httpPost.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					json = sb.toString();
					// Log.d("TAG", "IN BACKGROUND" + json);
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}

			} catch (Throwable t) {
				t.printStackTrace();
			}

			return json;

		}

		@Override
		protected void onPostExecute(String status) {

		}
	}

	private class submitPostVideo extends AsyncTask<String, Void, String> {
		int position;
		byte[] data;
		String msg;

		public submitPostVideo(int pos, byte[] dt, String mess) {
			position = pos;
			data = dt;
			msg = mess;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d("TAG", "SUBMITING Video");
			String json = null;
			try {

				InputStream is = null;

				String Token = prefs.getString("Facebook_Access_Tocken", "");

				String url = "https://graph.facebook.com/me/videos?access_token=" + Token;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("message", new StringBody(msg));
				entity.addPart("filename", new StringBody("facebookvideo.mp4"));
				entity.addPart("video", new ByteArrayBody(data, "facebookvideo.mp4"));
				httpPost.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					json = sb.toString();
					// Log.d("TAG", "IN BACKGROUND" + json);
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}

			} catch (Throwable t) {
				t.printStackTrace();
			}

			return json;

		}

		@Override
		protected void onPostExecute(String status) {

		}
	}

	public byte[] readBytes(InputStream inputStream) throws IOException {
		// This dynamically extends to take the bytes you read.
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// This is storage overwritten on each iteration with bytes.
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		// We need to know how may bytes were read to write them to the byteBuffer.
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}

		// And then we can return your byte array.
		return byteBuffer.toByteArray();
	}
}
