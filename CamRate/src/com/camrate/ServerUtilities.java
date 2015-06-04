package com.camrate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	static String TAG = "tag";

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	static void register(final Context context, String regId, String deviceID, String deviceName, String App_name, String clientid, String app_version) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		String serverUrl = CommonUtilities.SERVER_URL;

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				post(serverUrl, regId, deviceID, deviceName, App_name, clientid, app_version);
				GCMRegistrar.setRegisteredOnServer(context, true);
				return;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, String regId, String deviceID, String deviceName, String App_name, String clientid, String app_version) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");

		try {
			postUnregister(regId, deviceID);
			GCMRegistrar.setRegisteredOnServer(context, false);
		} catch (Exception e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static void post(String endpoint, String regId, String deviceID, String deviceName, String App_name, String clientid, String app_version) throws IOException {

		Log.d("TAG", "POST DATA");

		Log.d("TAG", "=====FOR GCM ======");
		Log.d("TAG", "regId " + regId);
		Log.d("TAG", "deviceID " + deviceID);
		Log.d("TAG", "deviceName " + deviceName);
		Log.d("TAG", "App_name " + App_name);
		Log.d("TAG", "clientid " + clientid);
		Log.d("TAG", "app_version " + app_version);

		try {

			String finalUrl = endpoint + "&appname=" + App_name + "&appversion=" + app_version + "&deviceuid=" + deviceID + "&devicetoken=" + regId + "&devicename=" + deviceName + "&clientid=" + clientid;

			finalUrl = finalUrl.replace(" ", "%20");

			Log.d("TAG", "GCM Registered api " + finalUrl);

			HttpGet httpGet = new HttpGet(finalUrl);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(httpGet);
			String responseStr = EntityUtils.toString(response.getEntity());
			Log.d("TAG", "Response " + responseStr);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

	}

	private static void postUnregister(String regId, String deviceID) throws IOException {

		Log.d("TAG", "POST DATA UNREG");

		Log.d("TAG", "=====FOR GCM ======");
		Log.d("TAG", "regId " + regId);
		Log.d("TAG", "deviceID " + deviceID);

		try {
			String finalUrl = "http://camrate.com/TestCamRate/Lovehate/main/frmlovehateApi_v2.php?api=Unlinkdevicegcm&deviceuid=" + SplashScreen.deviceID + "&clientid=" + SplashScreen.clientid;

			HttpGet httpGet = new HttpGet(finalUrl);
			HttpParams httpParameters = new BasicHttpParams(); // Set the timeout in milliseconds until a connection is established. // The default value is zero, that means the timeout is not used.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); // Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response;

			response = httpClient.execute(httpGet);
			String responseStr = EntityUtils.toString(response.getEntity());
			Log.d("TAG", "Response unregister" + responseStr);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
