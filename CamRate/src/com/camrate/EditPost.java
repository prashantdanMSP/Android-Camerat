package com.camrate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tabs.ActivityManage;

public class EditPost extends Activity {

	Constant con = new Constant();
	JSONParser parser = new JSONParser();
	checkInternet chkNet;
	Function fun;
	ProgressBar pd;
	String User_ID;
	EditText edtTag, edtDesc;
	Spinner spCategory;
	ArrayList<HashMap<String, String>> PostCategoryData;
	String ParseCategory;
	private SimpleAdapter categoryAdapter;
	Context context;
	String catID = "";
	String postTag = "", postDesc = "", postID = "", postCategory_ID = "";
	private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_post);
		init();
	}

	public void init() {
		ActivityManage.isFromEditPost = true;
		context = getParent();
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Edit Title & Description");
		Button btnBack = (Button) findViewById(R.id.button2);
		btnBack.setVisibility(View.VISIBLE);
		Button btnSubmit = (Button) findViewById(R.id.button1);
		btnSubmit.setVisibility(View.VISIBLE);
		btnSubmit.setText("Submit");
		btnSubmit.setTypeface(SplashScreen.ProxiNova_Bold);
		edtTag = (EditText) findViewById(R.id.edtTag);
		edtDesc = (EditText) findViewById(R.id.edtDesc);
		spCategory = (Spinner) findViewById(R.id.spCategory);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		chkNet = new checkInternet(context);
		fun = new Function(context);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if (chkNet.isNetworkConnected()) {
			downloadAndSetCategoryItem();
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}

		Intent intent = getIntent();
		postTag = intent.getStringExtra("editTag");
		postDesc = intent.getStringExtra("editDesc");
		postID = intent.getStringExtra("editPost_ID");
		catID = intent.getStringExtra("editCategory");

		edtTag.setText(postTag);
		edtDesc.setText(postDesc);
		edtTag.setTypeface(SplashScreen.ProxiNova_Regular);
		edtDesc.setTypeface(SplashScreen.ProxiNova_Regular);
		for (int i = 0; i < PostCategoryData.size(); i++) {
			if (catID.equals(PostCategoryData.get(i).get("Category_ID"))) {
				spCategory.setSelection(i);
			}
		}
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (edtTag.getText().toString().trim().length() == 0) {
					fun.SimpleAlert("CamRate", "Please enter title for your post");
				} else if (edtDesc.getText().toString().trim().length() == 0) {
					fun.SimpleAlert("CamRate", "Please enter description for your post");
				} else {
					if (chkNet.isNetworkConnected()) {

						String url = con.GetBaseUrl() + "api=EditPost";
						String Tag = edtTag.getText().toString().trim();
						String desc = edtDesc.getText().toString().trim();
						String[] params = new String[] { url, Tag, desc, postID, catID };
						new editPostDetail().execute(params);
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

	}

	private void downloadAndSetCategoryItem() {
		PostCategoryData = new ArrayList<HashMap<String, String>>();
		String url = con.GetBaseUrl() + "api=GetCategory";

		// Log.d("Tag","url "+url);
		try {
			ParseCategory = parser.getStringFromUrl(url);
			JSONArray jsonMyFeed = new JSONArray(ParseCategory);
			for (int s = 0; s < jsonMyFeed.length(); s++) {
				JSONObject objComment = jsonMyFeed.getJSONObject(s);
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("Category_ID", objComment.getString("Category_ID"));
				map1.put("Category_Name", objComment.getString("Category_Name"));

				PostCategoryData.add(map1);

			}

			categoryAdapter = new SimpleAdapter(EditPost.this, PostCategoryData, R.layout.sp_item, new String[] { "Category_Name", "Category_ID" }, new int[] { R.id.text1, R.id.text2 });
			spCategory.setAdapter(categoryAdapter);
			spCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					catID = PostCategoryData.get(position).get("Category_ID");
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// show alert
	public void SimpleAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				Intent intent = new Intent();
				intent.putExtra("postID", postID);
				setResult(2, intent);
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManage.Post_ID = postID;
	}

	private class editPostDetail extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {

			String json = "";
			String Result = null;
			String url = params[0];
			String editedTag = params[1];
			String editedDesc = params[2];
			String PostID = params[3];
			String CatID = params[4];
			try {
				InputStream is = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("Post_ID", new StringBody(postID));
				entity.addPart("Post_Description", new StringBody(editedDesc));
				entity.addPart("Post_Tag", new StringBody(editedTag));
				entity.addPart("Category_ID", new StringBody(CatID));
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
			pd.setVisibility(View.INVISIBLE);
			JSONObject objPost;
			try {
				objPost = new JSONObject(status);
				if (objPost.getString("result").equalsIgnoreCase("1")) {
					SimpleAlert("CamRate", "Post has been updated successfully");
				}
			} catch (JSONException e) {
			}

		}
	}

	public void clearText() {
		edtTag.setText("");
		edtDesc.setText("");
	}
}
