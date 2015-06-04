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

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

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
import android.widget.ListView;
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
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.VideoPost;

public class TumblrReceiver extends BroadcastReceiver {

	Context context;
	String UserID = "";
	Context cntxt;
	checkInternet chkNet;
	ArrayList<HashMap<String, String>> postshare;
	Function fun;
	SharedPreferences prefs;
	private static final String REQUEST_TOKEN_URL = "https://www.tumblr.com/oauth/request_token";
	private static final String ACCESS_TOKEN_URL = "https://www.tumblr.com/oauth/access_token";
	private static final String AUTH_URL = "https://www.tumblr.com/oauth/authorize";

	// Get these from Tumblr app registration
	private static final String CONSUMER_KEY = "7hdNr0AwrsJEH9t5TMpyTEchoyMf5XVSqvKK5ehQmPY0MypXd5";
	private static final String CONSUMER_SECRET = "5Jg6I1IBSaAennLPi6Ht2uuYtVMvPdqds7rPypwCzSvXeFs0gv";

	// Set the callback url in the manifest. There are a few more things you must include in the manifest, so check it!
	private static final String CALLBACK_URL = "tumblrauth://tumblrok"; // for this example, it would be "example://ok"
	private static final String FILE_LOCATION = "/CamRate_Images";

	String authUrl;

	CommonsHttpOAuthConsumer consumer;
	CommonsHttpOAuthProvider provider;

	private String item;

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
		if (Video_Name.equalsIgnoreCase("1")) {
			postPicture(image, postTags, postDesc);
		} else {
			postVideo(Video_Name);
		}

	}

	public void postPicture(final String name, final String Post_Tags, final String Post_Desc) {

		final String theToken = prefs.getString("Tumblr_myToken", "");
		final String theSecret = prefs.getString("Tumblr_mySecret", "");
		final JumblrClient client = new JumblrClient(CONSUMER_KEY, CONSUMER_SECRET);
		client.setToken(theToken, theSecret);

		// Have the user pick one of their blogs to post to.
		// final String blog = preferences.getString("selectedBlog", "");

		final String blog = client.user().getBlogs().get(0).getName();

		Log.d("blog", "--->" + blog);
		Thread thread = new Thread() {
			@Override
			public void run() {

				String NewImageName = name.replaceAll("/", "");
				// String root_sd = Environment.getExternalStorageDirectory().toString();
				// File file = new File(root_sd + "/CamRate_Images/" + NewImageName);
				String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
				File file = new File(filepath, NewImageName);
				PhotoPost post;

				try {

					post = client.newPost(blog, PhotoPost.class);
					post.setData(file);
					post.setCaption(Post_Tags);
					post.save();

				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					Log.d("DrawLog", "null pointer wtf");
				}

			}
		};
		thread.start();
	}

	public void postVideo(final String name) {

		final String theToken = prefs.getString("Tumblr_myToken", "");
		final String theSecret = prefs.getString("Tumblr_mySecret", "");
		final JumblrClient client = new JumblrClient(CONSUMER_KEY, CONSUMER_SECRET);
		client.setToken(theToken, theSecret);

		// Have the user pick one of their blogs to post to.
		// final String blog = preferences.getString("selectedBlog", "");

		final String blog = client.user().getBlogs().get(0).getName();
		Log.e("blog", "--->" + blog);
		Thread thread = new Thread() {
			@Override
			public void run() {

				String root_sd = Environment.getExternalStorageDirectory().toString();
				File file = new File(root_sd + "/CamRate_Video/" + name);
				VideoPost post;
				try {

					// videopost=client.newPost(blog, VideoPost.class);
					// videopost.setData(file);
					post = client.newPost(blog, VideoPost.class);
					post.setData(file);
					post.save();

				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					Log.d("DrawLog", "null pointer wtf");
				}

			}
		};
		thread.start();
	}
}
