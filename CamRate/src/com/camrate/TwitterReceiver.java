package com.camrate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.SocialSharing.updateTwitterMedia;
import com.camrate.global.Function;
import com.camrate.global.checkInternet;
import com.camrate.tools.HttpClient;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

public class TwitterReceiver extends BroadcastReceiver {

	Context context;
	String UserID = "";
	Context cntxt;
	checkInternet chkNet;
	ArrayList<HashMap<String, String>> postshare;
	Function fun;
	SharedPreferences prefs;
	static String TWITTER_CONSUMER_KEY = "Yi91A6xPiHQfxjV5NzWBw";
	// place your consumer secret here
	static String TWITTER_CONSUMER_SECRET = "NJkHm22eWgJROQmdfaod8zVuKfkEnyYmfcmrJu5k";
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

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
		String TwitterBodyLink = postTags;
		new updateTwitterMedia().execute(TwitterBodyLink, image, Video_Name);

	}

	class updateTwitterMedia extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(SocialSharing.this); pDialog.setMessage("Updating to twitter..."); pDialog.setIndeterminate(false); pDialog.setCancelable(false); pDialog.show();
			 */
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			Log.d("Tweet Text", "> " + args[0]);
			String status = args[0];
			String ImageName = args[1];
			String VideoName = args[1];
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

			// Access Token
			String access_token = prefs.getString(PREF_KEY_OAUTH_TOKEN, "");
			// Access Token Secret
			String access_token_secret = prefs.getString(PREF_KEY_OAUTH_SECRET, "");

			AccessToken accessToken = new AccessToken(access_token, access_token_secret);
			Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
			String NewImageName = ImageName.replaceAll("/", "");
			System.out.println("FinalName===>" + ImageName);
			//String filepath = Environment.getExternalStorageDirectory() + "/CamRate_Images";
			String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
			File file = new File(filepath, NewImageName);
			try {
				twitter4j.UploadedMedia twits = twitter.uploadMedia(file);
				long mediaID = twits.getMediaId();
				long[] longarr = new long[1];
				longarr[0] = mediaID;
				StatusUpdate su = new StatusUpdate(status);
				su.setMediaIds(longarr);
				twitter.updateStatus(su);

				Log.d("Status", "> " + twits.getMediaId());
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {

		}

	}
}
