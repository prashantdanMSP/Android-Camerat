package com.camrate.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	HttpResponse httpResponse;
	static Context context;
	static int internetStatus = 1;
	static String internetType = null;
	private final static String jString = "{" + "                \"code\": \"402\"," + "                \"msg\": \"No data found\"," + "                \"status\" : \"failed\"," + "}";
	// constructor
	int timeoutConnection = 10000;
	// Set the default socket timeout (SO_TIMEOUT)
	// in milliseconds which is the timeout for waiting for data.
	int timeoutSocket = 20000;

	public JSONParser() {

	}

	public String getStringFromUrl(String url) {
		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpResponse = httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			StatusLine statusLine = httpResponse.getStatusLine();
			// Log.d("Status","statusLine"+statusLine);

			int statusCode = statusLine.getStatusCode();
			// Log.d("Status","statusCode"+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();

				json = sb.toString();
				// Log.e("Parser",json.toString());

			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				Toast.makeText(context, "No Data Found", Toast.LENGTH_LONG).show();
			} else if (statusCode == 500) {
				Toast.makeText(context, "Our Server is not responding.Please try again later.", Toast.LENGTH_LONG).show();
			} else {
				httpResponse.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {

			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		return json;

	}

	public JSONObject getJSONFromUrl(String url) {

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpResponse = httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			StatusLine statusLine = httpResponse.getStatusLine();
			// Log.d("Status","statusLine"+statusLine);

			int statusCode = statusLine.getStatusCode();
			// Log.d("Status","statusCode"+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();

				json = sb.toString();
				// Log.e("Parser",json.toString());

			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				Toast.makeText(context, "No Data Found", Toast.LENGTH_LONG).show();
			} else if (statusCode == 500) {
				Toast.makeText(context, "Our Server is not responding.Please try again later.", Toast.LENGTH_LONG).show();
			} else {
				httpResponse.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {

			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			/*
			 * if(!json.equalsIgnoreCase("{")){ jObj=new JSONObject(jString); } else{
			 */
			jObj = new JSONObject(json);
			// }

		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

	public JSONObject getJSONobjectFromUrl(String url) {

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpget);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			StatusLine statusLine = httpResponse.getStatusLine();
			// Log.d("Status","statusLine"+statusLine);

			int statusCode = statusLine.getStatusCode();
			// Log.d("Status","statusCode"+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				Toast.makeText(context, "No Data Found", Toast.LENGTH_LONG).show();
			} else if (statusCode == 500) {
				Toast.makeText(context, "Our Server is not responding.Please try again later.", Toast.LENGTH_LONG).show();
			} else {
				httpResponse.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {

			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

	public boolean isJSONValid(String test) {
		boolean valid = false;
		try {
			new JSONObject(test);
			valid = true;
		} catch (JSONException ex) {
			valid = false;
		}
		return valid;
	}

	public String getStringFromUrlInTime(String url) {
		// Making HTTP request
		try {
			// defaultHttpClient

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpPost httpPost = new HttpPost(url);

			httpResponse = httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			StatusLine statusLine = httpResponse.getStatusLine();
			// Log.d("Status","statusLine"+statusLine);

			int statusCode = statusLine.getStatusCode();
			// Log.d("Status","statusCode"+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();

				json = sb.toString();
				// Log.e("Parser",json.toString());

			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				Toast.makeText(context, "No Data Found", Toast.LENGTH_LONG).show();
			} else if (statusCode == 500) {
				Toast.makeText(context, "Our Server is not responding.Please try again later.", Toast.LENGTH_LONG).show();
			} else {
				httpResponse.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {

			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		return json;

	}

}
