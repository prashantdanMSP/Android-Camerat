package com.camrate.explore;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.MyFeed;
import com.camrate.MyFeedAdapter;
import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.categories.Animals_Pets;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.tools.VolleyJsonParser;
import com.camrate.tools.VolleyJsonParser.VolleyCallback;

public class SlidingDrawerActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String mDrawerTitle;
	private String mTitle;
	DrawerListAdapter mainAdapter;
	static TextView txtlabel_Title;
	ArrayList<DrawerItem> dataList;
	Button DrawerButton, btnGrid;
	MyFeedAdapter objMyFeed;
	boolean drawerOpen;
	static boolean isGridOn = true;
	Fragment fragment = null;
	static int TAG = 0;
	ArrayList<HashMap<String, String>> PostCategoryData;
	String ParseCategory;
	Constant con = new Constant();
	JSONParser parser = new JSONParser();
	private SimpleAdapter categoryAdapter;
	Constant c = new Constant();
	private static final String TAG1 = SlidingDrawerActivity.class.getSimpleName();
	public Context context;
	Dialog dialog;
	VolleyJsonParser volleyParser;
	checkInternet chkNet;
	HashMap<String, String> params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sliding_menu);
		Constant.arrActivity.add(SlidingDrawerActivity.this);
		context = getParent();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// Initializing

		init();
		DrawerButton.setText("More");

		if (savedInstanceState == null) {
			mDrawerLayout.closeDrawer(mDrawerList);
			SelectItem(1);
		}

		// new Loading().execute();
		params = new HashMap<String, String>();
		params.put("api", "GetCategory");
		if (chkNet.isNetworkConnected()) {
			volleyParser.makeStringReq(Constant.Main_URL, params, v);
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	public class Loading extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			showDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// downloadAndSetCategoryItem();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				dismissDialog();
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void downloadAndSetCategoryItem(String ParseCategory) {
		PostCategoryData = new ArrayList<HashMap<String, String>>();
		try {
			JSONArray jsonMyFeed = new JSONArray(ParseCategory);
			for (int s = 0; s < jsonMyFeed.length(); s++) {
				JSONObject objComment = jsonMyFeed.getJSONObject(s);
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("Category_ID", objComment.getString("Category_ID"));
				map1.put("Category_Name", objComment.getString("Category_Name"));

				PostCategoryData.add(map1);

			}

			categoryAdapter = new SimpleAdapter(getParent(), PostCategoryData, R.layout.sp_item, new String[] { "Category_Name", "Category_ID" }, new int[] { R.id.text1, R.id.text2 });
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	VolleyCallback v = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			downloadAndSetCategoryItem(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};

	public void init() {
		initProcessDialog();
		chkNet = new checkInternet(context);
		volleyParser = new VolleyJsonParser(context);
		txtlabel_Title = (TextView) findViewById(R.id.tvRateName);
		txtlabel_Title.setText("");
		DrawerButton = (Button) findViewById(R.id.button2);
		DrawerButton.setVisibility(View.VISIBLE);
		btnGrid = (Button) findViewById(R.id.button1);
		btnGrid.setVisibility(View.VISIBLE);

		DrawerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ListAdapter adapter = mDrawerList.getAdapter();
				if (adapter.equals(mainAdapter)) {
					if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
						mDrawerLayout.closeDrawer(mDrawerList);
					} else {
						setToggleIcon();
					}
				} else {
					if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
						// mDrawerLayout.closeDrawer(mDrawerList);
						mDrawerList.setAdapter(mainAdapter);
						setToggleIcon();
					} else {
						mDrawerLayout.openDrawer(mDrawerList);
						setBackIcon();
					}
					// mDrawerList.setAdapter(mainAdapter);
					// mDrawerList.invalidate();
				}
			}
		});
		dataList = new ArrayList<DrawerItem>();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		dataList.add(new DrawerItem("Trending", R.drawable.list_arrow));
		dataList.add(new DrawerItem("Newest", R.drawable.list_arrow));
		dataList.add(new DrawerItem("Most re-rated", R.drawable.list_arrow));
		dataList.add(new DrawerItem("Near By", R.drawable.list_arrow));
		dataList.add(new DrawerItem("Category", R.drawable.list_arrow));
		dataList.add(new DrawerItem("Favourites", R.drawable.list_arrow));

		mainAdapter = new DrawerListAdapter(this, dataList);

		mDrawerList.setAdapter(mainAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		btnGrid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Fragment frag = getFragmentManager().findFragmentByTag(String.valueOf(TAG));
				String mTag = frag.getTag().toString();

				if (isGridOn) {
					btnGrid.setText("Grid Off");
					btnGrid.setTextColor(Color.rgb(128, 128, 128));
					if (mTag.equals("10")) {

						Animals_Pets frPets = (Animals_Pets) getFragmentManager().findFragmentByTag(String.valueOf(TAG));
						frPets.setGridOn(false);

					} else {

						Newest frNewest = (Newest) getFragmentManager().findFragmentByTag(String.valueOf(TAG));
						frNewest.setGridOn(false);

					}
					btnGrid.invalidate();
					isGridOn = false;

				} else {

					btnGrid.setText("Grid On");
					btnGrid.setTextColor(Color.rgb(38, 189, 191));
					if (mTag.equals("10")) {

						Animals_Pets frPets = (Animals_Pets) getFragmentManager().findFragmentByTag(String.valueOf(TAG));
						frPets.setGridOn(true);

					} else {

						Newest frNewest = (Newest) getFragmentManager().findFragmentByTag(String.valueOf(TAG));
						frNewest.setGridOn(true);

					}
					btnGrid.invalidate();
					isGridOn = true;
				}

			}
		});
		// mDrawerLayout.setDrawerListener(mDrawerToggle);
		drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	}

	protected void setBackIcon() {

		DrawerButton.setText("");
		DrawerButton.setTextSize(15);
		Drawable drBack = getResources().getDrawable(R.drawable.btnback);
		DrawerButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drBack, null, null, null);

	}

	protected void setToggleIcon() {
		mDrawerLayout.openDrawer(mDrawerList);
		// Drawable drToggle = getResources().getDrawable(R.drawable.explore_icon);
		DrawerButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
		DrawerButton.setText("More");
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_Gridon).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void SelectItem(int possition) {
		fragment = new Newest();
		Bundle bundle = new Bundle();
		switch (possition) {
		case 0:
			TAG = 0;
			break;
		case 1:
			TAG = 1;
			break;
		case 2:
			TAG = 2;
			/*
			 * args.putString(Trending.Category_ID, dataList.get(possition).getCategory_ID()); args.putString(Trending.Category_Name, dataList.get(possition).getTitle());
			 */
			break;
		case 3:
			TAG = 3;
			System.out.println("hi tag 3");
			break;
		case 4:
			break;
		case 5:
			TAG = 5;
			break;
		default:
			break;
		}

		if (fragment != null) {
			bundle.putInt("tag", TAG);
			bundle.putBoolean("gridon", isGridOn);
			fragment.setArguments(bundle);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, String.valueOf(TAG)).commit();
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(possition, true);
			mDrawerList.setSelection(possition);
			txtlabel_Title.setText(dataList.get(TAG).getTitle());
			txtlabel_Title.setTypeface(SplashScreen.ProxiNova_Bold);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListAdapter adapter = mDrawerList.getAdapter();
			if (adapter.equals(mainAdapter)) {
				if (position == 4) {
					setBackIcon();

					txtlabel_Title.setText("Category");
					txtlabel_Title.setTypeface(SplashScreen.ProxiNova_Bold);
					mDrawerList.setAdapter(categoryAdapter);
					mDrawerList.invalidate();
				} else {
					SelectItem(position);
				}
			} else {
				setToggleIcon();

				// Closing the drawer
				mDrawerLayout.closeDrawer(mDrawerList);
				mDrawerList.setAdapter(categoryAdapter);

				mTitle = PostCategoryData.get(position).get("Category_Name");
				String Cat_ID = PostCategoryData.get(position).get("Category_ID");

				txtlabel_Title.setText(mTitle);
				txtlabel_Title.setTypeface(SplashScreen.ProxiNova_Bold);
				Animals_Pets rFragment = new Animals_Pets();
				Bundle data = new Bundle();
				data.putString("Category_ID", Cat_ID);
				data.putString("Category_Name", mTitle);
				data.putBoolean("gridon", isGridOn);

				TAG = 10;

				// Setting the position to the fragment
				rFragment.setArguments(data);
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction ft = fragmentManager.beginTransaction();
				// ft.replace(R.id.frame_container, rFragment, TAG);?
				ft.replace(R.id.frame_container, rFragment, String.valueOf(TAG));
				ft.commit();
			}

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		c.TagmyPref = getSharedPreferences(c.TAG_PREF, Context.MODE_WORLD_WRITEABLE);
		Editor edt = c.TagmyPref.edit();
		edt.putString(c.TAG_KEY, TAG1);
		edt.commit();
		// uiHelper.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Fragment frNewest = (Newest) getFragmentManager().findFragmentByTag(String.valueOf(TAG));

		if (frNewest != null) {
			frNewest.onActivityResult(requestCode, resultCode, data);
		}

	}

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(getParent(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(getParent(), android.R.style.Theme_Light_NoTitleBar);
		}

		dialog = new Dialog(themedContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		dialog.setCancelable(false);

	}

	public void showDialog() {
		try {
			if (dialog != null && (!dialog.isShowing())) {
				Log.d("TAG", "SHOW DIALOG");
				dialog.show();
			}
		} catch (Exception e) {

		}
	}

	public void dismissDialog() {
		try {
			if (dialog != null && dialog.isShowing()) {
				Log.d("TAG", "DISMISS DIALOG");
				dialog.dismiss();
			}
		} catch (Exception e) {
		}
	}

}
