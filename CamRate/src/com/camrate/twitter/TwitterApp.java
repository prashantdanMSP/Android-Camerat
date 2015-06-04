package com.camrate.twitter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.Window;


public class TwitterApp {
	private Twitter mTwitter;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private String mConsumerKey;
	private String mSecretKey;
	private ProgressDialog mProgressDlg;
	private TwDialogListener mListener;
	private Activity context;
	Boolean b = true;
	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	/*public static final String CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://"
			+ OAUTH_CALLBACK_HOST;*/
	
	
	//public static final String CALLBACK_URL =  OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	// public static final String CALLBACK_URL =
	// "http://abhinavasblog.blogspot.com/";
	
	static String CALLBACK_URL = "http://www.abc.com";
	//public static final String CALLBACK_URL = "oauth://t4jsample";
	//static String base_link_url = "http://thepubcrawlpages.com/";
	private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	private static final String TWITTER_AUTHORZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String MESSAGE = "Beweegwijzer App";
	
	public TwitterApp(Activity context, String consumerKey, String secretKey) {
		this.context = context;

		mTwitter = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context);
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey,
				mSecretKey);

		String request_url = TWITTER_REQUEST_URL;
		String access_token_url = TWITTER_ACCESS_TOKEN_URL;
		String authorize_url = TWITTER_AUTHORZE_URL;

		mHttpOauthprovider = new DefaultOAuthProvider(request_url,
				access_token_url, authorize_url);
		mAccessToken = mSession.getAccessToken();

		configureToken();
	}

	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	private void configureToken() {
		if (mAccessToken != null) {
			
				mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);
				mTwitter.setOAuthAccessToken(mAccessToken);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public void updateStatus(String msg) throws Exception {
		try {
			mTwitter.updateStatus(msg);
			String dir = "sdcard/Share_Camrate_Images/";
			String name = "shareImg.png";
			File file = new File(dir, name);
			Bitmap imageToTweet = BitmapFactory.decodeFile(name);
		    // use MSTwitter function to save image to file because startTweet() takes an image path
		    // this is done to avoid passing large image files between intents which is not android best practices 
		//    String tweetImagePath =  ((Object) mTwitter).putBitmapInDiskCache(this, imageToTweet);
			//StatusUpdate status = new StatusUpdate(msg);
	/*		String absolutePath = "/sdcard/file.PNG";
			try{
		        URL url = new URL(url1); //you can write here any link
		        File file = new File(absolutePath); //Something like ("/sdcard/file.mp3")

		            //Create parent directory if it doesn't exists
		            if(!new File(file.getParent()).exists())
		            {
		                System.out.println("Path is created " + new File(file.getParent()).mkdirs());
		            }
		            file = new File(absolutePath); //Something like ("/sdcard/file.mp3")
		            file.createNewFile();
		             Open a connection to that URL. 
		            URLConnection ucon = url.openConnection();
		            
		             * Define InputStreams to read from the URLConnection.
		             
		            InputStream is = ucon.getInputStream();
		            
		             * Read bytes to the Buffer until there is nothing more to read(-1).
		             
		            int bytesDownloaded = 0 ;
		            FileOutputStream fos = new FileOutputStream(file);
		            int size = 1024*1024;
		            byte[] buf = new byte[size];
		            int byteRead;
		            while (((byteRead = is.read(buf)) != -1)) {
		                fos.write(buf, 0, byteRead);
		                bytesDownloaded += byteRead;
		            }
		             Convert the Bytes read to a String. 

		            fos.close();

		    }
		        
		    catch(Exception e)
		    {   
		      
		        e.printStackTrace();
		    }
			status.setMedia(new File(absolutePath));
			mTwitter.updateStatus(status);
			
			File f = new File(absolutePath);
			f.delete();
			//File f = new File("/mnt/sdcard/Pictures/MyCameraApp/IMG.jpg");
			//mTwitter.updateProfileImage(f);
*/		} catch (TwitterException e) {
			throw e;
		}
	}

//	public void uploadPic(File file, String message)
//			throws Exception {
//		try {
//			StatusUpdate status = new StatusUpdate(message);
//			status.setMedia(file);
//			mTwitter.updateStatus(status);
//		} catch (TwitterException e) {
//			Log.d("TAG", "Pic Upload error" + e.getExceptionCode());
//			throw e;
//		}
//	}

	public void authorize() {
		mProgressDlg.setMessage("Initializing ...");
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler
						.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void processToken(String callbackUrl) {
		mProgressDlg.setMessage("Finalizing ...");
		mProgressDlg.show();

		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer,
							verifier);

					mAccessToken = new AccessToken(
							mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret());

					configureToken();

					User user = mTwitter.verifyCredentials();

					mSession.storeAccessToken(mAccessToken, user.getName());

					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {

			public void onComplete(String value) {
				processToken(value);
			}

			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		new TwitterDialog(context, url, listener).show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};

	public interface TwDialogListener {
		public void onComplete(String value);

		public void onError(String value);
	}
}
