package com.camrate.settings;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.checkInternet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterFriends extends Activity {

	SharedPreferences prefs;
	checkInternet chkNet;
	Context context;
	TextView txtlblMain;
	Button btnNext, btnBack;
	// Twitter share
	// place your cosumer key here
	// static String TWITTER_CONSUMER_KEY = "Yi91A6xPiHQfxjV5NzWBw";
	static String TWITTER_CONSUMER_KEY = "oOXzUgbHHdgRlddyaMH0ywE6Q";
	// place your consumer secret here

	// static String TWITTER_CONSUMER_SECRET = "NJkHm22eWgJROQmdfaod8zVuKfkEnyYmfcmrJu5k";
	static String TWITTER_CONSUMER_SECRET = "K8JNKee38f9fCt497k8J9AOZ4lSbDOy6HPN0BjE0a0HNzU81NI";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String TWITTER_CALLBACK_URL = "friendsoauth://friendsample";

	// Twitter oauth urls
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;
	AccessToken accessToken = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_friends);
		init();
	}

	public void init() {
		context = getParent();
		chkNet = new checkInternet(TwitterFriends.this);
		txtlblMain = (TextView) findViewById(R.id.textView1);
		btnNext = (Button) findViewById(R.id.button1);
		btnBack = (Button) findViewById(R.id.button2);
		btnNext.setVisibility(View.INVISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		txtlblMain.setText("Twitter Friends");
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		if (chkNet.isNetworkConnected()) {
			loginToTwitter();
		} else {
			Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
		}

		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
				try {
					// Get the access token
					accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
					// Shared Preferences
					Editor e = prefs.edit();
					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);

					Log.d("Twitter OAuth Token", "> " + accessToken.getToken());
					long userID = accessToken.getUserId();
					e.putLong("TwitterUser_ID", userID);
					User user = twitter.showUser(userID);
					String username = user.getName();
					e.commit();
					// getFollowersList(String.valueOf(userID));
				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			// Toast.makeText(getApplicationContext(),
			// "Already Logged into twitter", Toast.LENGTH_LONG).show();
			System.out.println("twitter user log in alredy");
			long userID = prefs.getLong("TwitterUser_ID", 0);
			System.out.println("LoginID===>" + userID);
			try {
				System.out.println("LoginID===>" + twitter.getId());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// getFollowersList(String.valueOf(userID));

		}
	}

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return prefs.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	public void getFollowersList(String User_ID) {
		try {
			long lCursor = -1;
			IDs friendsIDs = twitter.getFriendsIDs(User_ID, lCursor);
			System.out.println(twitter.showUser(User_ID).getName());
			System.out.println("==========================");
			do {
				for (long i : friendsIDs.getIDs()) {
					System.out.println("follower ID #" + i);
					System.out.println(twitter.showUser(i).getName());
				}
			} while (friendsIDs.hasNext());

		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
