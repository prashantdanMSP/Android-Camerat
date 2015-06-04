package com.camrate.global;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constant extends Application {
	private static Constant instance;
	String s;
	String encodedurl1;
	URI uri;
	public SharedPreferences TagmyPref;
	public static final String TAG_PREF = "UserProfile";
	public static final String TAG_KEY = "Act_Name";
	public static ArrayList<Activity> arrActivity = new ArrayList<Activity>();
	public static String KEY = "XbKluIf1MrNqmvdkHRaJ60e1m";
	public static String SECRET = "RMzCYQdVGRX6ZwREHz5v6Hdkkl88rd5QagaS3CtWH10FUqr8Sm";
	// public static final String MyFeed_Url = "http://camrate.com/TestCamRate/Lovehate/main/frmlovehateApi_v2.php?api=MyFeedtest";
	//
	public static final String Main_URL = "http://camrate.com/TestCamRate/Lovehate/main/frmlovehate_android_Api_v2.php";

	public static synchronized Constant getInstance() {
		if (instance == null) {
			instance = new Constant();
		}
		return instance;
	}

	public static Context getContext() {
		return instance;
	}

	public String GetBaseUrl() {

		// local
		// return "http://202.131.107.107/Lovehate/main/frmlovehateApi_v2.php?";
		return "http://camrate.com/TestCamRate/Lovehate/main/frmlovehateApi_v2.php?";
		/*
		 * live url return "http://camrate.it/main/frmlovehateApi.php?";
		 */
		// return "http://camrate.com/main/frmlovehateApi.php?";
	}

	public String GetBaseUrlRegister() {

		// return "http://192.168.0.16/Lovehate/main/frmlovehateApi.php?";
		// local
		return "http://camrate.com/TestCamRate/Lovehate/main/frmlovehateApi_v2.php?";
		/*
		 * live url return "http://camrate.it/main/frmlovehateApi.php?";
		 */
		// return "http://camrate.com/main/frmlovehateApi.php";
	}

	public String GetEncodeUrl(String encodeurl) {
		try {
			String url = encodeurl;
			encodedurl1 = URLEncoder.encode(url, "UTF-8");
			// Log.d("TEST", encodedurl1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedurl1;
	}

	public String GetUrl(String url1) {
		try {
			URL url = new URL(url1);

			try {
				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			url = uri.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url1;
	}

	public URI Geturl(String url) {
		String flag1 = url;
		URI uri = null;
		try {
			uri = new URI(flag1.replaceAll(" ", "%20"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(uri);
		return uri;
	}

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

}
