package com.camrate.search;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.tabs.ActivityManage;
import com.camrate.tabs.TabGroupActivity;
import com.camrate.tools.VolleyJsonParser;
import com.camrate.tools.VolleyJsonParser.VolleyCallback;
import com.google.android.gms.internal.gt;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Search extends Activity {

	PullToRefreshListView mySearchListTag, mySearchListUser, mySearchListLoc;
	ListView ListTag, ListUser, ListLoc;
	Constant c = new Constant();
	ProgressBar pd;
	JSONParser parser = new JSONParser();
	JSONArray objmyFeedJson;
	String UserID;
	EditText edtsearch;
	String SearchKeywords;
	Button btncancel;
	ArrayList<HashMap<String, String>> mysearchTagData;
	ArrayList<HashMap<String, String>> mysearchUserData;
	ArrayList<HashMap<String, String>> mysearchLocData;
	TagAdapter objSearchTagAdapter;
	UserAdapter objSearchUserAdapter;
	LocAdapter objSearchLocAdapter;
	TextView txtlblSearch;
	Button btnSearchTag, btnSearchUser, btnSearchLocation;
	checkInternet chknet;
	int startIndex0, startIndex1, startIndex2;
	Function fun;
	private static final String TAG = Search.class.getSimpleName();
	String TAG_0 = "tag", TAG_1 = "user", TAG_2 = "location", CURRENT_TAG = "";
	LinearLayout linear_tag, linear_user, linear_loc;
	VolleyJsonParser volleyParser;
	Context context;
	HashMap<String, String> params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();

	}

	public void init() {

		CURRENT_TAG = TAG_0;
		Constant.arrActivity.add(Search.this);
		context = getParent();
		chknet = new checkInternet(context);
		fun = new Function(context);
		volleyParser = new VolleyJsonParser(context);
		TextView tv14lbl = (TextView) findViewById(R.id.textView1);
		Button btnnext = (Button) findViewById(R.id.button2);
		btnnext.setVisibility(View.INVISIBLE);
		Button btnBack = (Button) findViewById(R.id.button1);
		btnBack.setVisibility(View.INVISIBLE);
		btnSearchTag = (Button) findViewById(R.id.btnSearchTag);
		btnSearchUser = (Button) findViewById(R.id.btnSearchUser);
		btnSearchLocation = (Button) findViewById(R.id.btnSearchLocation);
		txtlblSearch = (TextView) findViewById(R.id.txtlblValueSearch);
		pd = (ProgressBar) findViewById(R.id.progressBar1);

		linear_tag = (LinearLayout) findViewById(R.id.linear_tag);
		linear_user = (LinearLayout) findViewById(R.id.linear_user);
		linear_loc = (LinearLayout) findViewById(R.id.linear_loc);

		mysearchTagData = new ArrayList<HashMap<String, String>>();
		mysearchUserData = new ArrayList<HashMap<String, String>>();
		mysearchLocData = new ArrayList<HashMap<String, String>>();

		mySearchListTag = (PullToRefreshListView) findViewById(R.id.listViewTag);
		mySearchListUser = (PullToRefreshListView) findViewById(R.id.listViewUser);
		mySearchListLoc = (PullToRefreshListView) findViewById(R.id.listViewLoc);

		ListTag = mySearchListTag.getRefreshableView();
		ListUser = mySearchListUser.getRefreshableView();
		ListLoc = mySearchListLoc.getRefreshableView();

		objSearchTagAdapter = new TagAdapter(Search.this, R.layout.searchtag, mysearchTagData);
		objSearchUserAdapter = new UserAdapter(Search.this, R.layout.searchuser, mysearchUserData);
		objSearchLocAdapter = new LocAdapter(Search.this, R.layout.searchloc, mysearchLocData);

		edtsearch = (EditText) findViewById(R.id.serchText1);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");

		System.out.println("UserID--->" + UserID);

		tv14lbl.setText("Search");
		tv14lbl.setTypeface(SplashScreen.ProxiNova_Bold);

		edtsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (edtsearch.getText().toString().trim().length() > 0) {
					SearchKeywords = edtsearch.getText().toString().trim();
				} else {
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		edtsearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					startIndex0 = 0;
					startIndex0 = 0;
					startIndex0 = 0;
					mysearchLocData.clear();
					mysearchTagData.clear();
					mysearchUserData.clear();

					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

					System.out.println("Go..");
					if (v.getText().toString().length() > 0) {
						if (chknet.isNetworkConnected()) {
							// new SearchTag().execute();
							if (chknet.isNetworkConnected()) {
								params = new HashMap<String, String>();
								params.put("api", "Searchlist2");
								params.put("SearchText", SearchKeywords);
								params.put("Type", "0");
								params.put("User_ID", UserID);
								params.put("start", String.valueOf(startIndex0));
								params.put("end", "15");
								volleyParser.makeStringReq(Constant.Main_URL, params, vTag);
							} else {
								Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
							}

						} else {
							Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
						}
					} else {
						fun.SimpleAlert("CamRate", "Please enter valid search term");
					}

				}
				return false;
			}
		});

		btnSearchTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CURRENT_TAG = TAG_0;
				manageTag();
				txtlblSearch.setVisibility(View.VISIBLE);
				txtlblSearch.setText("Results for " + edtsearch.getText().toString());
				btnSearchTag.setBackgroundResource(R.drawable.btn_themebg);
				btnSearchUser.setBackgroundResource(R.drawable.btn_themebg_unsel);
				btnSearchLocation.setBackgroundResource(R.drawable.btn_themebg_unsel);

			}
		});
		btnSearchUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CURRENT_TAG = TAG_1;
				manageTag();
				txtlblSearch.setVisibility(View.VISIBLE);
				btnSearchTag.setBackgroundResource(R.drawable.btn_themebg_unsel);
				btnSearchUser.setBackgroundResource(R.drawable.btn_themebg);
				btnSearchLocation.setBackgroundResource(R.drawable.btn_themebg_unsel);
				txtlblSearch.setText("Results for " + edtsearch.getText().toString());

			}
		});
		btnSearchLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CURRENT_TAG = TAG_2;
				manageTag();
				txtlblSearch.setVisibility(View.VISIBLE);
				btnSearchTag.setBackgroundResource(R.drawable.btn_themebg_unsel);
				btnSearchUser.setBackgroundResource(R.drawable.btn_themebg_unsel);
				btnSearchLocation.setBackgroundResource(R.drawable.btn_themebg);
				txtlblSearch.setText("Results for " + edtsearch.getText().toString());
			}
		});

		// onitemclick
		mySearchListTag.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position - 1;
				Log.d("TAG", "Position " + position);
				Intent intent = new Intent(getParent(), SearchTagsDetail.class);
				intent.putExtra("Key_Tag", mysearchTagData.get(position).get("tag"));
				intent.putExtra("Key_count", mysearchTagData.get(position).get("count"));
				intent.putExtra("Key_search", SearchKeywords);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("SearchTagsDetail", intent);

			}
		});

		mySearchListUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - 1;
				Log.d("TAG", "Position " + position);
				Intent intent = new Intent(getParent(), GeneralUserProfile.class);
				intent.putExtra("User_ID", mysearchUserData.get(position).get("User_ID"));
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("GeneralUserProfile", intent);

			}
		});

		mySearchListLoc.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				position = position - 1;
				Log.d("TAG", "Position " + position);
				Intent intent = new Intent(getParent(), SearchLocationDetail.class);
				intent.putExtra("Location", mysearchLocData.get(position).get("Location"));
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("SearchLocationDetail", intent);

			}
		});

		// pull to refresh
		mySearchListTag.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// new SearchTag().execute();
				if (chknet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "Searchlist2");
					params.put("SearchText", SearchKeywords);
					params.put("Type", "0");
					params.put("User_ID", UserID);
					params.put("start", String.valueOf(startIndex0));
					params.put("end", "15");
					volleyParser.makeStringReq(Constant.Main_URL, params, vTag);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}

			}
		});

		mySearchListUser.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (chknet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "Searchlist2");
					params.put("SearchText", SearchKeywords);
					params.put("Type", "1");
					params.put("User_ID", UserID);
					params.put("start", String.valueOf(startIndex1));
					params.put("end", "15");
					volleyParser.makeStringReq(Constant.Main_URL, params, vUser);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
			}
		});

		mySearchListLoc.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// new SearchLocation().execute();
				if (chknet.isNetworkConnected()) {
					params = new HashMap<String, String>();
					params.put("api", "Searchlist2");
					params.put("SearchText", SearchKeywords);
					params.put("Type", "2");
					params.put("User_ID", UserID);
					params.put("start", String.valueOf(startIndex2));
					params.put("end", "15");
					volleyParser.makeStringReq(Constant.Main_URL, params, vLocation);
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	VolleyCallback vTag = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			parse_setDataTag(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};
	VolleyCallback vUser = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			parse_setDataUser(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};
	VolleyCallback vLocation = new VolleyCallback() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			volleyParser.dismissDialog();
			parse_setDataLocation(result);
		}

		@Override
		public void onFailure(String jsonResponse) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		ActivityManage.lastSeletedTab = 3;
	}

	public void setListAdaper() {

		manageTag();

		try {

			txtlblSearch.setVisibility(View.VISIBLE);
			txtlblSearch.setText("Results for " + edtsearch.getText().toString());

			if (objSearchTagAdapter.getCount() <= 15) {
				ListTag.setAdapter(objSearchTagAdapter);
			} else {
				objSearchTagAdapter.notifyDataSetChanged();
			}

		} catch (Exception e) {
		}

		try {

			if (objSearchUserAdapter.getCount() <= 15) {
				ListUser.setAdapter(objSearchUserAdapter);
			} else {
				objSearchUserAdapter.notifyDataSetChanged();
			}

		} catch (Exception e) {
		}

		try {

			if (objSearchLocAdapter.getCount() <= 15) {
				ListLoc.setAdapter(objSearchLocAdapter);
			} else {
				objSearchLocAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {

		}

	}

	public void manageTag() {

		btnSearchTag.setBackgroundResource(R.color.btnsearchunsel);
		btnSearchUser.setBackgroundResource(R.color.btnsearchunsel);
		btnSearchLocation.setBackgroundResource(R.color.btnsearchunsel);

		linear_tag.setVisibility(View.INVISIBLE);
		linear_user.setVisibility(View.INVISIBLE);
		linear_loc.setVisibility(View.INVISIBLE);

		if (CURRENT_TAG.equalsIgnoreCase(TAG_0)) {
			btnSearchTag.setBackgroundResource(R.color.Theme_Color);
			linear_tag.setVisibility(View.VISIBLE);

		} else if (CURRENT_TAG.equalsIgnoreCase(TAG_1)) {

			btnSearchUser.setBackgroundResource(R.color.Theme_Color);
			linear_user.setVisibility(View.VISIBLE);

		} else if (CURRENT_TAG.equalsIgnoreCase(TAG_2)) {
			btnSearchLocation.setBackgroundResource(R.color.Theme_Color);
			linear_loc.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		c.TagmyPref = getSharedPreferences(c.TAG_PREF, Context.MODE_WORLD_WRITEABLE);
		Editor edt = c.TagmyPref.edit();
		edt.putString(c.TAG_KEY, TAG);
		edt.commit();
	}

	public void parse_setDataTag(String result) {
		try {

			JSONArray json = new JSONArray(result);

			if (json.length() > 0) {

				startIndex0 += 15;

				for (int s = 0; s < json.length(); s++) {

					JSONObject object = json.getJSONObject(s);

					String Tag = object.getString("tag");
					String count = object.getString("count");

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("tag", Tag);
					map.put("count", count);

					mysearchTagData.add(map);
				}
			}
			Log.d("TAG", "mysearchTagData " + mysearchTagData.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (chknet.isNetworkConnected()) {
			params = new HashMap<String, String>();
			params.put("api", "Searchlist2");
			params.put("SearchText", SearchKeywords);
			params.put("Type", "1");
			params.put("User_ID", UserID);
			params.put("start", String.valueOf(startIndex1));
			params.put("end", "15");
			volleyParser.makeStringReq(Constant.Main_URL, params, vUser);
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	public void parse_setDataUser(String result) {
		try {
			JSONArray json = new JSONArray(result);
			if (json.length() > 0) {
				startIndex1 += 15;
				for (int s = 0; s < json.length(); s++) {
					JSONObject object = json.getJSONObject(s);
					String User_ID = object.getString("User_ID");
					String User_FirstName = object.getString("User_FirstName");
					String User_LastName = object.getString("User_LastName");
					String User_Name = object.getString("User_Name");
					String User_Imagepath = object.getString("User_Imagepath");

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("User_ID", User_ID);
					map.put("User_FirstName", User_FirstName);
					map.put("User_LastName", User_LastName);
					map.put("User_Name", User_Name);
					map.put("User_Imagepath", User_Imagepath);

					mysearchUserData.add(map);
				}
			}
			Log.d("TAG", "mysearchUserData " + mysearchUserData.size());
		} catch (Exception e) {

		}
		if (chknet.isNetworkConnected()) {
			params = new HashMap<String, String>();
			params.put("api", "Searchlist2");
			params.put("SearchText", SearchKeywords);
			params.put("Type", "2");
			params.put("User_ID", UserID);
			params.put("start", String.valueOf(startIndex2));
			params.put("end", "15");
			volleyParser.makeStringReq(Constant.Main_URL, params, vLocation);
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	public void parse_setDataLocation(String result) {
		mySearchListTag.onRefreshComplete();
		mySearchListUser.onRefreshComplete();
		mySearchListLoc.onRefreshComplete();

		try {

			JSONArray json = new JSONArray(result);

			if (json.length() > 0) {

				startIndex2 += 15;

				for (int s = 0; s < json.length(); s++) {

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("Location", json.getString(s));
					mysearchLocData.add(map);
				}
			}
			Log.d("TAG", "mysearchLocData " + mysearchLocData);
		} catch (Exception e) {
		}

		setListAdaper();
	}
}
