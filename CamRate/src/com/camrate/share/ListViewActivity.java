package com.camrate.share;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;

public class ListViewActivity extends Activity {

	private TextView textView_addManually, textView_fromContacts;
	public static ArrayList<Setter> items = new ArrayList<Setter>();
	SharedPreferences prefs;
	CustomAdapter adapter;
	private Context context;
	Constant con = new Constant();
	ProgressBar pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shareviewemail);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		init();

	}

	public void init() {
		context = ListViewActivity.this;

		// for custome title
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Share via email");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Button btnRefresh = (Button) findViewById(R.id.button2);
		btnRefresh.setVisibility(View.INVISIBLE);
		Button btnDone = (Button) findViewById(R.id.button1);
		btnDone.setVisibility(View.VISIBLE);
		btnDone.setText("Done");
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		//

		textView_addManually = (TextView) findViewById(R.id.textView_addmanually);
		textView_fromContacts = (TextView) findViewById(R.id.textView2);

		final ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setItemsCanFocus(true);

		prefs = PreferenceManager.getDefaultSharedPreferences(ListViewActivity.this);

		getEmailList();

		adapter = new CustomAdapter(this);
		listView.setAdapter(adapter);

		textView_addManually.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListViewActivity.this, ManuallyAdd.class);
				startActivityForResult(intent, 0);
			}
		});
		textView_fromContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListViewActivity.this, ContactsList.class);
				startActivityForResult(intent, 1);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView1, View view, int pos, long id) {

				if (items.get(pos).isChecked()) {
					items.get(pos).setChecked(false);
				} else {
					items.get(pos).setChecked(true);
				}
				adapter.notifyDataSetChanged();
			}
		});

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				JSONArray array = new JSONArray();

				for (int j = 0; j < items.size(); j++) {
					try {

						if (items.get(j).isChecked) {
							array.put(items.get(j).getEmail());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				Log.d("TAG", "Email json " + array + "");

				if (array.length() > 0) {
					new SendEmailList().execute(array + "");
				} else {
					finish();
				}

			}
		});
	}

	public class CustomAdapter extends BaseAdapter {
		private final Context context;
		LayoutInflater layoutInflater;

		public CustomAdapter(Context context) {
			this.context = context;
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Holder holder;

			if (convertView == null) {
				holder = new Holder();
				v = layoutInflater.inflate(R.layout.item, null, false);
				holder.title = (TextView) v.findViewById(R.id.textView1);
				holder.checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
				v.setTag(holder);
			} else {
				holder = (Holder) v.getTag();
			}

			holder.checkBox.setId(position);

			Setter s = items.get(position);

			holder.title.setText(s.getEmail());
			if (s.isChecked()) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
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

	private class Setter {
		private boolean isChecked;
		private String email;
		private String name = "a";

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 0) {

			if (data != null) {

				if (!data.getStringExtra("email").toString().equalsIgnoreCase("")) {
					try {

						Setter s = new Setter();
						s.setEmail(data.getStringExtra("email").toString());
						s.setName(data.getStringExtra("name").toString());
						s.setChecked(true);
						items.add(s);
					} catch (Exception e) {
					}
				}
				Log.d("TAG", "email " + data.getStringExtra("email").toString());
				Log.d("TAG", "name " + data.getStringExtra("name").toString());

				saveEmailList();

				adapter.notifyDataSetChanged();

			}

		} else if (requestCode == 1) {

			if (data != null) {

				try {
					String result = data.getStringExtra("emaillist").toString();
					JSONObject json = new JSONObject(result);

					JSONArray array = json.getJSONArray("emaillist");
					for (int j = 0; j < array.length(); j++) {
						try {
							JSONObject jsonDetail = array.getJSONObject(j);
							String email = jsonDetail.getString("email");

							Setter s = new Setter();
							s.setEmail(email);
							s.setChecked(true);
							items.add(s);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} catch (JSONException e1) {

					e1.printStackTrace();
				}

				saveEmailList();

				adapter.notifyDataSetChanged();
			}

		}

	}

	private void getEmailList() {
		items.clear();
		try {
			String result = prefs.getString("saveEmails", "");

			JSONObject json = new JSONObject(result);

			JSONArray array = json.getJSONArray("emaillist");
			for (int j = 0; j < array.length(); j++) {
				try {
					JSONObject jsonDetail = array.getJSONObject(j);
					String email = jsonDetail.getString("email");
					String name = jsonDetail.getString("name");

					Setter s = new Setter();
					s.setEmail(email);
					s.setName(name);
					items.add(s);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e1) {

			e1.printStackTrace();
		}

	}

	private void saveEmailList() {

		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();

		for (int j = 0; j < items.size(); j++) {
			try {
				JSONObject jsonDetail = new JSONObject();
				jsonDetail.put("email", items.get(j).getEmail());
				jsonDetail.put("name", items.get(j).getName());
				array.put(jsonDetail);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		try {
			json.put("emaillist", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Editor editor = prefs.edit();
		editor.putString("saveEmails", json + "");
		Log.d("TAG", json + "");
		editor.commit();

	}

	private class SendEmailList extends android.os.AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			String responseStr = null, Result = null;

			try {

				String User_Id = getIntent().getStringExtra("User_ID");
				String Post_Id = getIntent().getStringExtra("Post_ID");
				String Post_Title = getIntent().getStringExtra("Post_Tags");
				String Rating_Rate = getIntent().getStringExtra("Average_Rating");
				String Post_isVideo = getIntent().getStringExtra("Post_IsVideo");

				Log.d("TAG", "User_Id " + User_Id);
				Log.d("TAG", "Post_Id " + Post_Id);
				Log.d("TAG", "Post_Title " + Post_Title);
				Log.d("TAG", "Rating_Rate " + Rating_Rate);
				Log.d("TAG", "Post_isVideo " + Post_isVideo);

				String emailArray = params[0].toString();
				Log.d("TAG", emailArray + "");
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(con.GetBaseUrl() + "api=EmailSharing");
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				reqEntity.addPart("User_ID", new StringBody(User_Id));
				reqEntity.addPart("Post_ID", new StringBody(Post_Id));
				reqEntity.addPart("Post_Title", new StringBody(Post_Title));
				reqEntity.addPart("Rating_Rate", new StringBody(Rating_Rate));
				reqEntity.addPart("Post_IsVideo", new StringBody(Post_isVideo));
				reqEntity.addPart("Email_List", new StringBody(emailArray));

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
				Toast.makeText(ListViewActivity.this, "Email send successfully", Toast.LENGTH_LONG).show();
			}
			finish();
		}
	};

}