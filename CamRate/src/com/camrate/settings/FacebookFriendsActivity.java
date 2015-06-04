package com.camrate.settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.adapters.FacebookFriendListAdapter;
import com.camrate.global.JSONParser;
import com.camrate.pojo.PojoFacebookFriends;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookFriendsActivity extends Activity {
	private Facebook fb;
	ListView lstFriends;
	Button btnBack, btnInvite;
	private FacebookFriendListAdapter adapter;
	ArrayList<String> lstCheckedId;
	TextView txtlblMain;

	ArrayList<PojoFacebookFriends> arrlstFri = new ArrayList<PojoFacebookFriends>();;
	JSONParser parser = new JSONParser();
	Session session;
	private Session.StatusCallback statusCallback;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = new ArrayList<String>();
	private static final List<String> PERMISSIONS2 = Arrays.asList("publish_actions");
	boolean isFromDialog;
	String tokenFinal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_friends);
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		init();
		setListener();

	}

	private void setListener() {
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnInvite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// invite friends

				//
				String idsString = "";
				for (int i = 0; i < arrlstFri.size(); i++) {
					PojoFacebookFriends po = arrlstFri.get(i);
					if (po.isIsitemChecked()) {
						idsString = idsString + po.getId() + ",";
					}
				}
				if (idsString.length() > 1) {
					idsString = idsString.substring(0, idsString.length() - 1);
					Log.d("TAG", "Array IDs " + idsString);
				}

				//
				sendRequestDialog(idsString); //
				// for (int i = 0; i < lstCheckedId.size(); i++) {
				// String id = lstCheckedId.get(i);
				// sendRequestDialog(id);
				// }

			}
		});
		lstFriends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				PojoFacebookFriends ei = (PojoFacebookFriends) arrlstFri.get(position);

				if (ei.isIsitemChecked()) {
					ei.setIsitemChecked(false);
				} else {
					ei.setIsitemChecked(true);
				}
				adapter.notifyDataSetChanged();

			}
		});
	}

	private void init() {
		lstFriends = (ListView) findViewById(R.id.lstInviteFacebookFri);
		btnBack = (Button) findViewById(R.id.btnFacebookFriBack);
		btnInvite = (Button) findViewById(R.id.btnFacebookFriInvite);
		txtlblMain = (TextView) findViewById(R.id.lblInviteFriTitle);
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		// arrlstFri = SettingsActivity.getPojoList();
		//
		// if (arrlstFri != null) {
		// adapter = new FacebookFriendListAdapter(FacebookFriendsActivity.this, arrlstFri);
		// lstFriends.setAdapter(adapter);
		// lstFriends.setOnItemClickListener(this);
		// }
		connectFB();

		statusCallback = new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception);
				System.out.println("callback from dialog");
			}
		};

	}

	private void connectFB() {
		// TODO Auto-generated method stub
		List<String> permissions = new ArrayList<String>();
		permissions.add("email");
		permissions.add("public_profile");
		permissions.add("user_friends");
		permissions.add("publish_actions");

		session = new Session.Builder(this).build();
		session.addCallback(statusCallback);

		Session.OpenRequest openRequest = new Session.OpenRequest(FacebookFriendsActivity.this);
		openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
		openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
		openRequest.setPermissions(permissions);
		session.openForPublish(openRequest);
	}

	protected void onResume() {
		super.onResume();
		// uiHelper.onResume();
		if (Session.getActiveSession().isOpened()) {
			getFacebookFriendsCall(Session.getActiveSession().getAccessToken());
			System.out.println("Facebook Friends");
		} else {
			System.out.println("the session is not opened");
			isFromDialog = false;
			connectFB();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// uiHelper.onActivityResult(requestCode, resultCode, data);
		// Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (session != null) {
			session.onActivityResult(this, requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session != session) {
			return;
		}

		if (state.isOpened()) {
			// Log in just happened.
			if (arrlstFri.size() > 0) {
				sendRequestDialog("");
			} else {
				String accesstoken = session.getAccessToken();
				tokenFinal = accesstoken;
				getFacebookFriendsCall(accesstoken);
			}

		} else if (state.isClosed()) {
			// Log out just happened. Update the UI.
			// Toast.makeText(getApplicationContext(), "session closed", Toast.LENGTH_SHORT).show();
		}
	}

	void getFacebookFriendsCall(String token) {
		System.out.println("here is the token " + token);
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// HttpPost httppost = new HttpPost("http://202.131.107.108:8081/Lovehate/main/frmlovehateApi.php");
			String url = "https://graph.facebook.com/me/friends?access_token=" + token + "&fields=name,picture";
			System.out.println("this is the final url " + url);
			HttpGet httppost = new HttpGet(url);
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();
			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			String response1 = s.toString();
			// Log.e("test", "Response: " + s);
			saveJSONToList(response1);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void saveJSONToList(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			JSONArray arrJSON = jsonObj.getJSONArray("data");
			// arrlstFri = new ArrayList<PojoFacebookFriends>();
			if (arrlstFri != null) {
				arrlstFri.clear();
			}
			for (int i = 0; i < arrJSON.length(); i++) {
				JSONObject obj = arrJSON.getJSONObject(i);
				String id = obj.getString("id");
				String name = obj.getString("name");

				JSONObject objSecond = obj.getJSONObject("picture");
				JSONObject objThird = objSecond.getJSONObject("data");
				String url = objThird.getString("url");

				PojoFacebookFriends pojo = new PojoFacebookFriends();
				pojo.setId(id);
				pojo.setName(name);
				pojo.setUrl(url);
				arrlstFri.add(pojo);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (arrlstFri != null) {
			adapter = new FacebookFriendListAdapter(FacebookFriendsActivity.this, arrlstFri);
			lstFriends.setAdapter(adapter);
		}
	}

	private void sendRequestDialog(String id) {

		Session.getActiveSession().openActiveSessionFromCache(FacebookFriendsActivity.this);

		if (Session.getActiveSession().isOpened()) {
			System.out.println("this is opened now");
			System.out.println("the session is now opened");
			Bundle params = new Bundle();
			params.putString("title", "Send a Request");
			params.putString("message", "Hey! I have got an amazing social sharing app - CamRate. You can download and enjoy the app from the link: http://camrate.com");
			params.putString("to", id);
			// params.putString("data", "{\"badge_of_awesomeness\":\"1\"," + "\"social_karma\":\"5\"}"); // any additional data

			WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(FacebookFriendsActivity.this, Session.getActiveSession(), params)).setOnCompleteListener(new OnCompleteListener() {

				@Override
				public void onComplete(Bundle values, FacebookException error) {
					// do something, e.g. show toast message

					Toast.makeText(FacebookFriendsActivity.this, "Invitation send to Facebook Friends", Toast.LENGTH_SHORT).show();
					System.out.println("the requests are sent");
				}
			}).build();
			requestsDialog.show();

		} else {
			System.out.println("the session is still not opened");
		}
		// System.out.println("the session is now opened");
		// Bundle params = new Bundle();
		// params.putString("title", "Send a Request");
		// params.putString("message", "Learn how to make your Android apps social");
		// params.putString("to", "1387435468228690");
		// params.putString("data",
		// "{\"badge_of_awesomeness\":\"1\"," +
		// "\"social_karma\":\"5\"}"); // any additional data
		//
		// WebDialog requestsDialog = (
		// new WebDialog.RequestsDialogBuilder(FacebookFriendsActivity.this, Session.getActiveSession(), params))
		// .setOnCompleteListener(new OnCompleteListener() {
		//
		// @Override
		// public void onComplete(Bundle values, FacebookException error) {
		// // do something, e.g. show toast message
		//
		// System.out.println("the requests are sent");
		// }
		// })
		// .build();
		// requestsDialog.show();

	}
}
