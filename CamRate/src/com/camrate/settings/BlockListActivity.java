package com.camrate.settings;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BlockListActivity extends Activity {

	checkInternet chkNet;
	Function fun;
	JSONParser parser = new JSONParser();
	ProgressBar pd;
	ArrayList<HashMap<String, String>> arrBlockUser;
	Constant con = new Constant();
	String User_ID, UnBlockUser_ID;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ListView lstBlockUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blocklist);
		init();
	}

	public void init() {
		imageLoader.init(ImageLoaderConfiguration.createDefault(BlockListActivity.this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		TextView lbltxtMain = (TextView) findViewById(R.id.lblInviteFriTitle);
		Button btnBack = (Button) findViewById(R.id.button2);
		lstBlockUser = (ListView) findViewById(R.id.listBlock);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		lbltxtMain.setTypeface(SplashScreen.ProxiNova_Bold);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "");
		chkNet = new checkInternet(BlockListActivity.this);
		fun = new Function(BlockListActivity.this);
		if (chkNet.isNetworkConnected()) {
			new getBlockList().execute("");
		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	public class getBlockList extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pd.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			arrBlockUser = new ArrayList<HashMap<String, String>>();
			String url = con.GetBaseUrl() + "api=Blocklist&User_ID=" + User_ID + "";
			System.out.println("url-->" + url);
			// start += 15;
			try {
				String BlockUser = parser.getStringFromUrlInTime(url);
				// JSONArray json = new JSONArray(BlockUser);
				JSONObject objJson = new JSONObject(BlockUser);
				String Result = objJson.getString("result");
				if (Result.equalsIgnoreCase("1")) {
					JSONArray json = objJson.getJSONArray("User");
					for (int s = 0; s < json.length(); s++) {
						JSONObject object = json.getJSONObject(s);
						String User_ID = object.getString("User_ID");
						String User_Name = object.getString("User_Name");
						String User_Imagepath = object.getString("User_Imagepath");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("User_ID", User_ID);
						map.put("User_Name", User_Name);
						map.put("User_Imagepath", User_Imagepath);
						arrBlockUser.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return arrBlockUser;

		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);
			setBlockListUser objBlocklistuser = new setBlockListUser(result);
			lstBlockUser.setAdapter(objBlocklistuser);
		}
	}

	static class ViewHolder {
		ImageView imgBlockUser;
		TextView tvBlockListUserName;
		TextView txtUnBlock;

	}

	/** Adapter */
	public class setBlockListUser extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> items;
		String DecodedComment;

		setBlockListUser(ArrayList<HashMap<String, String>> items) {
			this.items = items;
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.blocklist_item, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imgBlockUser = (ImageView) view.findViewById(R.id.imgBlockUser);
				holder.tvBlockListUserName = (TextView) view.findViewById(R.id.txtValueBlockUserName);
				holder.txtUnBlock = (TextView) view.findViewById(R.id.btnUnblock);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (holder.imgBlockUser != null) {

				String img = (items.get(position).get("User_Imagepath"));

				imageLoader.displayImage(img, holder.imgBlockUser, options);
			}
			if (holder.tvBlockListUserName != null) {
				holder.tvBlockListUserName.setText(items.get(position).get("User_Name"));
				holder.tvBlockListUserName.setTypeface(SplashScreen.ProxiNova_Regular);
			}

			holder.txtUnBlock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					UnBlockUser_ID = items.get(position).get("User_ID");
					if (chkNet.isNetworkConnected()) {
						new UnBlockUser().execute("");

					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				}
			});

			return view;
		}
	}

	class UnBlockUser extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			// pd = ProgressDialog.show(Followers.this, "", "");
			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=UnblockUser&User_ID=" + User_ID + "&BlockedUserID=" + UnBlockUser_ID;
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = parser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result1 = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result1);

			return Result1;

		}

		@Override
		protected void onPostExecute(String status) {
			// pd.dismiss();
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				SimpleAlert("CamRate", "User has been unblocked successfully");

			}
			if (status.equals("0")) {
				fun.SimpleAlert("CamRate", "User has not been unblocked. Please try again");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void SimpleAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(BlockListActivity.this);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new getBlockList().execute("");
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
