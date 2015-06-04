package com.camrate;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ResonForReport extends Activity implements OnClickListener {

	private Context context;
	ListView listview;
	listAdapter listAdapter;
	ProgressBar progressBar;
	Constant con;
	ArrayList<ReportReason> arrayReportReasons;
	String reasonType;
	String post_ID, user_ID;
	SharedPreferences prefs;
	int reasonReportIndex;
	String Comment_ID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reson_for_report);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		context = ResonForReport.this;

		init();

		new Loading().execute();

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				reasonReportIndex = position;

				new SendLoading().execute();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ActivityManage.isFromThankyou) {
			ActivityManage.isFromThankyou = false;
			finish();
		}
	}

	private void init() {

		reasonType = getIntent().getStringExtra("reasonType");

		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Reson For Report");

		Button btnBack = (Button) findViewById(R.id.button2);
		btnBack.setOnClickListener(this);
		Button btnNext = (Button) findViewById(R.id.button1);
		btnNext.setVisibility(View.INVISIBLE);

		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		listview = (ListView) findViewById(R.id.listview);
		listAdapter = new listAdapter();

		con = new Constant();
		arrayReportReasons = new ArrayList<ReportReason>();
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		post_ID = getIntent().getStringExtra("Post_ID");
		user_ID = prefs.getString("UserID", "");

		try {

			Comment_ID = getIntent().getStringExtra("Comment_ID");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setListAdapter() {
		listview.setAdapter(listAdapter);
	}

	public class Loading extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {

			String finalUrl = con.GetBaseUrl() + "api=GetReportReason";
			Log.d("TAG", finalUrl);
			JSONParser jsonParser = new JSONParser();

			String jsonString = jsonParser.getStringFromUrl(finalUrl);

			return jsonString;
		}

		@Override
		protected void onPostExecute(String jsoString) {
			super.onPostExecute(jsoString);
			progressBar.setVisibility(View.INVISIBLE);

			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(jsoString);
				String result = jsonObject.getString("result");

				if (result.equalsIgnoreCase("1")) {
					JSONArray jsonArray = jsonObject.getJSONArray("report");

					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject jsonObject2 = jsonArray.getJSONObject(i);

						String RR_ID = jsonObject2.getString("RR_ID");
						String RR_Reason = jsonObject2.getString("RR_Reason");
						String RR_For = jsonObject2.getString("RR_For");

						if (RR_For.equalsIgnoreCase(reasonType)) {
							ReportReason rr = new ReportReason();

							rr.setRR_ID(RR_ID);
							rr.setRR_Reason(RR_Reason);
							rr.setRR_For(RR_For);
							arrayReportReasons.add(rr);
						}

					}

				} else {
					Toast.makeText(context, "Please try later", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}

			setListAdapter();
		}

	}

	public class listAdapter extends BaseAdapter {

		LayoutInflater inflater;

		public listAdapter() {

			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrayReportReasons.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		class ViewHolder {
			public TextView textview;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View v = convertView;
			ViewHolder holder = null;

			if (v == null) {
				holder = new ViewHolder();
				v = inflater.inflate(R.layout.list_row, parent, false);
				holder.textview = (TextView) v.findViewById(R.id.textView1);
				v.setTag(holder);

			} else {
				holder = (ViewHolder) v.getTag();
			}

			ReportReason rr = arrayReportReasons.get(position);

			holder.textview.setText(rr.getRR_Reason().toString());
			holder.textview.setTypeface(SplashScreen.ProxiNova_Regular);

			return v;

		}

	}

	public class SendLoading extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {

			String finalUrl = null;

			if (reasonType.equalsIgnoreCase("0")) {

				finalUrl = con.GetBaseUrl() + "api=ReportInAppropriate&User_ID=" + user_ID + "&Report_IsPost=0&Report_UserPostID=" + post_ID + "&Report_CommentID=0&Report_Reason=" + reasonReportIndex;
			} else if (reasonType.equalsIgnoreCase("1")) {
				finalUrl = con.GetBaseUrl() + "api=ReportInAppropriate&User_ID=" + user_ID + "&Report_IsPost=1&Report_UserPostID=" + post_ID + "&Report_CommentID=" + Comment_ID + "&Report_Reason=" + reasonReportIndex;
			}

			Log.d("TAG", finalUrl);
			JSONParser jsonParser = new JSONParser();

			String jsonString = jsonParser.getStringFromUrl(finalUrl);

			return jsonString;
		}

		@Override
		protected void onPostExecute(String jsonString) {
			super.onPostExecute(jsonString);
			progressBar.setVisibility(View.INVISIBLE);
			Log.d("TAG", "Result " + jsonString);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(jsonString);
				String result = jsonObject.getString("result");

				if (result.equalsIgnoreCase("1")) {

					Intent intent = new Intent(getParent(), ThankYou.class);
					intent.putExtra("ThankYou_Type", "1");
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("ThankYou", intent);

				} else {
					Toast.makeText(context, "Please try later", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}

			setListAdapter();
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button2) {
			finish();
		}

	}

}
