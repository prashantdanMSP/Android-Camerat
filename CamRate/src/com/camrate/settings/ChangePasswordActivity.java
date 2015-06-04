package com.camrate.settings;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.camrate.MyFeed;
import com.camrate.R;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tools.Dialogs;

public class ChangePasswordActivity extends Activity {
	private EditText txtOldPass;
	private EditText txtConfirmPass;
	private EditText txtNewPass;
	private String strConfirmPass;
	private String strNewPass;
	private String strOldPass;
	private String userId;
	Constant con = new Constant();
	JSONParser jparser = new JSONParser();
	public String result;
	Function fun;
	checkInternet chkNet;
	ProgressBar pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pass);
		init();

		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.btnChangePassSubmit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getVals();
				if (strOldPass.equals("") && strOldPass != null) {
					fun.SimpleAlert("CamRate", "Please enter your Old password");
				} else if (strNewPass.equals("") && strNewPass != null) {
					fun.SimpleAlert("CamRate", "Please enter your New password");
				} else if (strConfirmPass.equals("") && strConfirmPass != null) {
					fun.SimpleAlert("CamRate", "Please enter your Confirm password");
				} else {
					if (!strNewPass.equals(strConfirmPass)) {
						fun.SimpleAlert("CamRate", "Your new password does not match. Please check");
					} else {
						getUserIdPreference();

						if (chkNet.isNetworkConnected()) {

							String url = con.GetBaseUrl() + "api=changepassword";
							String[] params = new String[] { url, userId, strOldPass, strConfirmPass };
							new changePassword().execute(params);
							// new Post().execute("api=changepassword&User_ID=" + userId + "&User_OldPassword=" + strOldPass + "&User_Password=" + strConfirmPass);
						} else {
							Toast.makeText(getParent(), "Check your internet connection", Toast.LENGTH_SHORT).show();
						}

					}

				}

			}
		});
	}

	protected void getUserIdPreference() {
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		userId = prefs.getString("UserID", "123");
	}

	protected void getVals() {
		strOldPass = txtOldPass.getText().toString();
		strNewPass = txtNewPass.getText().toString();
		strConfirmPass = txtConfirmPass.getText().toString();
	}

	private void init() {
		txtOldPass = (EditText) findViewById(R.id.txtOldPass);
		txtNewPass = (EditText) findViewById(R.id.txtNewPass);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		txtConfirmPass = (EditText) findViewById(R.id.txtConfirmPass);
		fun = new Function(getParent());
		chkNet = new checkInternet(getParent());

	}

	public void clearText() {
		txtOldPass.setText("");
		txtNewPass.setText("");
		txtConfirmPass.setText("");
	}

	private class changePassword extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			String json = "";
			String Result = null;
			String url = params[0];
			String User_ID = params[1];
			String User_OldPassword = params[2];
			String User_Password = params[3];
			try {
				InputStream is = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				entity.addPart("User_ID", new StringBody(User_ID));
				entity.addPart("User_OldPassword", new StringBody(User_OldPassword));
				entity.addPart("User_Password", new StringBody(User_Password));
				httpPost.setEntity(entity);

				// new
				HttpParams httpParameters = httpPost.getParams();
				// Set the timeout in milliseconds until a connection is
				// established.
				int timeoutConnection = 10000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT)
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 10000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				// new
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
			JSONObject objPost;
			try {
				objPost = new JSONObject(status);
				if (objPost.getString("result").equalsIgnoreCase("1")) {
					fun.SimpleAlert("CamRate", "Your password has been changed successfully");
					clearText();
				} else {
					fun.SimpleAlert("CamRate", "Your old password is not correct. Please check and try again");
				}
			} catch (JSONException e) {
			}

		}
	}
}
