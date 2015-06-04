package com.camrate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AlertFollowers extends Activity {

	ArrayList<Setter> items = new ArrayList<Setter>();
	String JsonString = null;
	JSONParser parser = new JSONParser();
	Constant c;
	CustomAdapter adapter;
	Context context;
	ListView listView;
	String User_ID, Post_ID;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	ProgressBar pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alertfollowers);
		c = new Constant();
		User_ID = getIntent().getStringExtra("User_ID");
		Post_ID = getIntent().getStringExtra("Post_ID");
		context = getParent();

		init();
	}

	public void init() {

		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Followers");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Button btnRefresh = (Button) findViewById(R.id.button2);
		Button btnDone = (Button) findViewById(R.id.button1);
		btnDone.setVisibility(View.VISIBLE);
		btnDone.setText("Submit");
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		imageLoader.init(ImageLoaderConfiguration.createDefault(AlertFollowers.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();

		adapter = new CustomAdapter(getParent());

		listView = (ListView) findViewById(android.R.id.list);
		listView.setItemsCanFocus(true);

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				JSONArray array = new JSONArray();

				for (int j = 0; j < items.size(); j++) {
					try {

						if (items.get(j).isChecked) {
							array.put(items.get(j).getUser_ID());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (array.length() > 0) {
					new SendFollowersList().execute(array + "");
				} else {
					finish();
				}
			}
		});

		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		new GetFollowersUser().execute();

	}

	public class GetFollowersUser extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {

			items.clear();
			String finalUrl = c.GetBaseUrl() + "api=Followerslist&User_ID=" + User_ID + "&Follow_Request=1";

			Log.d("TAG", "finalUrl " + finalUrl);
			try {
				JsonString = parser.getStringFromUrl(finalUrl);
				JSONArray json = new JSONArray(JsonString);

				if (json.length() > 0) {

					System.out.println("JsonArrayLength==>" + json.length());
					for (int i = 0; i < json.length(); i++) {
						JSONObject object = json.getJSONObject(i);
						String User_ID = object.getString("User_ID");
						String User_FirstName = object.getString("User_FirstName");
						String User_LastName = object.getString("User_LastName");
						String User_Name = object.getString("User_Name");
						String User_Imagepath = object.getString("User_Imagepath");

						Setter s = new Setter();
						s.setUser_ID(User_ID);
						s.setUser_FirstName(User_FirstName);
						s.setUser_LastName(User_LastName);
						s.setUser_Name(User_Name);
						s.setUser_Imagepath(User_Imagepath);
						items.add(s);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);
			listView.setAdapter(adapter);
		}
	}

	private class Setter {
		private boolean isChecked;
		String User_ID;
		String User_FirstName;
		String User_LastName;
		String User_Name;
		String User_Imagepath;

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public String getUser_ID() {
			return User_ID;
		}

		public void setUser_ID(String user_ID) {
			User_ID = user_ID;
		}

		public String getUser_FirstName() {
			return User_FirstName;
		}

		public void setUser_FirstName(String user_FirstName) {
			User_FirstName = user_FirstName;
		}

		public String getUser_LastName() {
			return User_LastName;
		}

		public void setUser_LastName(String user_LastName) {
			User_LastName = user_LastName;
		}

		public String getUser_Name() {
			return User_Name;
		}

		public void setUser_Name(String user_Name) {
			User_Name = user_Name;
		}

		public String getUser_Imagepath() {
			return User_Imagepath;
		}

		public void setUser_Imagepath(String user_Imagepath) {
			User_Imagepath = user_Imagepath;
		}

	}

	public class CustomAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;

		public CustomAdapter(Context context) {
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
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

			Setter s = items.get(position);

			holder.title.setText(s.getUser_Name());
			if (s.isChecked()) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
			}

			try {

				imageLoader.displayImage(s.getUser_Imagepath(), holder.imageView, options);

			} catch (Exception e) {
				// TODO: handle exception
			}

			holder.checkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					CheckBox ch = (CheckBox) v;

					Setter s = items.get(position);

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

	private class SendFollowersList extends android.os.AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			String responseStr = null, Result = null;

			String finalUrl = c.GetBaseUrl() + "api=AlertFollowers";
			Log.d("TAG", "finalUrl " + finalUrl);
			try {

				String User_Id = getIntent().getStringExtra("User_ID");
				String Post_Id = getIntent().getStringExtra("Post_ID");

				Log.d("TAG", "User_Id " + User_Id);
				Log.d("TAG", "Post_Id " + Post_Id);

				String emailArray = params[0].toString();
				Log.d("TAG", emailArray + "");
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(finalUrl);
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				reqEntity.addPart("User_ID", new StringBody(User_Id));
				reqEntity.addPart("Post_ID", new StringBody(Post_Id));
				reqEntity.addPart("Followers_ID", new StringBody(emailArray));

				httppost.setEntity(reqEntity);

				HttpResponse response = httpclient.execute(httppost);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String sResponse;
				StringBuilder s = new StringBuilder();

				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}
				responseStr = s.toString();

				JSONObject jsonobject;
				try {
					jsonobject = new JSONObject(responseStr);
					Result = jsonobject.getString("result");

				} catch (JSONException e) {
					Log.d("the xceptions ", "Xcep in posting status messages are : " + e.getMessage());
				}

				Log.d("TAG", "Response " + responseStr);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return Result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);

			if (result.equalsIgnoreCase("1")) {
				// Toast.makeText(AlertFollowers.this, "Email send successfully", Toast.LENGTH_LONG).show();
			}
			finish();
		}
	};

}
