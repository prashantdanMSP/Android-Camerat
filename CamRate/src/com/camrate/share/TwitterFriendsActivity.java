package com.camrate.share;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.google.android.gms.internal.ei;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

interface OnAccessTokenListener {
	public void onAccessToken(AccessToken accessToken);
}

public class TwitterFriendsActivity extends Activity implements OnAccessTokenListener {

	SharedPreferences prefs;
	WebView webView = null;
	ArrayList<Setter> friendList;
	CustomAdapter adapter;
	ListView listView;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	ProgressBar pd;
	Twitter twitter = null;
	RequestToken requestToken = null;
	String message = "Hey! I am using an amazing new rating app – CamRate. Download it today from iTunes or Google Play.";

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_friends);

		init();

		try {

			twitter = TwitterFactory.getSingleton();
		} catch (Exception e) {
		}

		try {

			AccessToken a = null;

			try {
				a = twitter.getOAuthAccessToken();
			} catch (Exception e) {
			}

			if (a == null) {
				// webView.setVisibility(View.INVISIBLE);
				twitter.setOAuthConsumer(Constant.KEY, Constant.SECRET);

				try {
					requestToken = twitter.getOAuthRequestToken();
					final String authtorizationURL = requestToken.getAuthorizationURL();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							TwitterWebViewClient client = new TwitterWebViewClient(TwitterFriendsActivity.this, requestToken, getParent());
							webView.setWebViewClient(client);
							webView.addJavascriptInterface(client, "GETHTML");
							TwitterFriendsActivity.this.webView.loadUrl(authtorizationURL);
						}
					});
				} catch (TwitterException e) {
					e.printStackTrace();
				}

			} else {

				friendList.clear();
				try {
					twitter.setOAuthAccessToken(a);

					List<User> users = twitter.getFollowersList(a.getUserId(), -1);
					Log.i("", "users count = " + users.size());

					new GetUsers(users).execute();

				} catch (TwitterException e) {
					e.printStackTrace();
					Log.i("", "error");
				}

			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println();

	}

	public void init() {
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Twitter Friends");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Button btnBack = (Button) findViewById(R.id.button2);
		Button btnDone = (Button) findViewById(R.id.button1);
		btnDone.setVisibility(View.VISIBLE);
		btnDone.setText("Invite");
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		prefs = PreferenceManager.getDefaultSharedPreferences(TwitterFriendsActivity.this);

		friendList = new ArrayList<Setter>();
		imageLoader.init(ImageLoaderConfiguration.createDefault(TwitterFriendsActivity.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();

		adapter = new CustomAdapter(getParent());

		listView = (ListView) findViewById(R.id.listView1);

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new SendMessagetoFriends().execute();
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

	}

	@Override
	public void onAccessToken(AccessToken accessToken) {

		String AccessToken = accessToken.toString();
		Editor editor = prefs.edit();
		editor.putString("ttoken", AccessToken);
		editor.commit();

		friendList.clear();
		try {
			twitter.setOAuthAccessToken(accessToken);

			List<User> users = twitter.getFollowersList(accessToken.getUserId(), -1);
			Log.i("", "users count = " + users.size());

			new GetUsers(users).execute();

		} catch (TwitterException e) {
			e.printStackTrace();
			Log.i("", "error");
		}

	}

	public class GetUsers extends AsyncTask<Void, Void, Void> {

		List<User> users;

		public GetUsers(List<User> users) {
			this.users = users;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			for (User user : users) {

				Log.i("", "" + user.getId());
				Log.i("", user.getName());
				Log.i("", user.getProfileImageURL());

				long userId = user.getId();
				String UserName = user.getName();
				String imageProfilePath = user.getProfileImageURL();

				Setter s = new Setter();
				s.setFriendUserId(userId);
				s.setUserName(UserName);
				s.setImagePath(imageProfilePath);
				friendList.add(s);

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			webView.setVisibility(View.GONE);
			listView.setAdapter(adapter);
		}

	}

	public class CustomAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;

		public CustomAdapter(Context context) {
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return friendList.size();
		}

		@Override
		public Object getItem(int position) {
			return friendList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).hashCode();
		}

		public class Holder {

			private TextView title;
			private CheckBox checkBox;
			private ImageView imageView;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Holder holder;

			if (convertView == null) {
				holder = new Holder();
				v = layoutInflater.inflate(R.layout.item_alertfollowers, null, false);
				holder.title = (TextView) v.findViewById(R.id.textView1);
				holder.checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
				holder.imageView = (ImageView) v.findViewById(R.id.imgFollowingUser);
				v.setTag(holder);
			} else {
				holder = (Holder) v.getTag();
			}

			holder.checkBox.setId(position);

			Setter s = friendList.get(position);

			holder.title.setText(s.getUserName());
			if (s.isChecked()) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
			}

			try {

				imageLoader.displayImage(s.getImagePath(), holder.imageView, options);

			} catch (Exception e) {
				// TODO: handle exception
			}

			holder.checkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					CheckBox ch = (CheckBox) v;

					Setter s = friendList.get(position);

					if (ch.isChecked()) {
						s.setChecked(true);
					} else {
						s.setChecked(false);
					}

				}
			});

			return v;
		}
	}

	private class Setter {
		private boolean isChecked;
		long friendUserId;
		String userName;
		String imagePath;

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public long getFriendUserId() {
			return friendUserId;
		}

		public void setFriendUserId(long friendUserId) {
			this.friendUserId = friendUserId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

	}

	private class SendMessagetoFriends extends android.os.AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				Log.d("TAG", "list size " + friendList.size());
				for (int i = 0; i < friendList.size(); i++) {

					Setter s = friendList.get(i);
					if (s.isChecked) {
						DirectMessage m = twitter.sendDirectMessage(s.getFriendUserId(), message);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			finish();
		}
	};

	interface MyJavaScriptInterface {
		@JavascriptInterface
		public void processHTML(String html);
	}

	public class TwitterWebViewClient extends WebViewClient implements MyJavaScriptInterface {

		String LOG_TAG = "TwitterWebViewClient";
		protected OnAccessTokenListener _listener = null;
		protected RequestToken _requestToken = null;
		Context context;
		protected final static String twitterAuthtorizeUrl = "https://api.twitter.com/oauth/authorize";
		

		public TwitterWebViewClient(OnAccessTokenListener listener, RequestToken requestToken, Context context) {
			setListener(listener);
			setRequestToken(requestToken);
			this.context = context;
			
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			pd.setVisibility(View.VISIBLE);

		}

		@SuppressLint("JavascriptInterface")
		@Override
		public void onPageFinished(WebView view, final String url) {
			super.onLoadResource(view, url);
			pd.setVisibility(View.INVISIBLE);
			Log.i("", url);
			if (url.equals(twitterAuthtorizeUrl)) {
				view.loadUrl("javascript:window.GETHTML.processHTML(document.getElementsByTagName('code')[0].innerHTML);");
			}

		}

		public OnAccessTokenListener getListener() {
			return _listener;
		}

		public void setListener(OnAccessTokenListener listener) {
			this._listener = listener;
		}

		public RequestToken getRequestToken() {
			return _requestToken;
		}

		public void setRequestToken(RequestToken requestToken) {
			this._requestToken = requestToken;
		}

		@JavascriptInterface
		public void processHTML(final String pinCode) {
			// TODO Auto-generated method stub

			try {
				Twitter twitter = TwitterFactory.getSingleton();
				AccessToken accessToken = twitter.getOAuthAccessToken(getRequestToken(), pinCode);
				Log.d(LOG_TAG, "success = " + accessToken.toString());
				Log.d(LOG_TAG, "token = " + accessToken.getToken());
				Log.d(LOG_TAG, "tokenSecret = " + accessToken.getTokenSecret());
				getListener().onAccessToken(accessToken);
			} catch (TwitterException e) {
				e.printStackTrace();
			}

		}
	}
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		webView.clearHistory();
		webView.clearFormData();
	}

}
