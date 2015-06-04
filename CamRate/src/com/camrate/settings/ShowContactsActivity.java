package com.camrate.settings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.camrate.share.EmailAllInvite;
import com.camrate.share.InviteEntryItem;
import com.camrate.share.Item;
import com.camrate.share.PinnedSectionListView.PinnedSectionListAdapter;
import com.camrate.share.SectionItem;

public class ShowContactsActivity extends Activity {
	JSONParser parser = new JSONParser();
	Constant constant = new Constant();
	ProgressBar pd;
	String User_ID = "";
	ArrayList<EmailAllInvite> inviteContectList;

	private Context context;
	InviteListAdapter simpleAdapter;
	ListView listInvited;
	public static ArrayList<Item> items = new ArrayList<Item>();
	private List<Object[]> alphabet = new ArrayList<Object[]>();
	private HashMap<String, Integer> sections = new HashMap<String, Integer>();
	ArrayList<String> emails;
	TextView textViewSelectAll;
	boolean isSelectAll;
	Button btnback;
	TextView txtMainlbl;
	ArrayList<EmailAllInvite> tempArray;
	Dialog dialog;
	EditText edtSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_contacts);

		context = getParent();

		pd = (ProgressBar) findViewById(R.id.progressBar1);
		listInvited = (ListView) findViewById(R.id.list);
		btnback = (Button) findViewById(R.id.btnContactsBack);
		edtSearch = (EditText) findViewById(R.id.search);
		txtMainlbl = (TextView) findViewById(R.id.lblInviteFriTitle);
		txtMainlbl.setTypeface(SplashScreen.ProxiNova_Bold);

		inviteContectList = new ArrayList<EmailAllInvite>();
		initProcessDialog();
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "0");
		edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					performSearch(edtSearch.getText().toString().trim());
					return true;
				}
				return false;
			}
		});
		new LoadContacts().execute();

	}

	public void performSearch(String text) {
		// String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());

		inviteContectList.clear();

		for (int i = 0; i < tempArray.size(); i++) {
			EmailAllInvite item = tempArray.get(i);
			if (item.getName().toLowerCase(Locale.getDefault()).contains(text)) {
				inviteContectList.add(item);
			}

		}

		//
		Collections.sort(inviteContectList, new FishNameComparator());
		makeSection(inviteContectList);
		simpleAdapter.notifyDataSetChanged();

	}

	private JSONObject createJSON(String text, String mEmail) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("email", mEmail);
			obj.put("name", text);
			obj.put("status", 0);
			obj.put("userid", 0);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public ArrayList<EmailAllInvite> getNameEmailDetails() {
		ArrayList<EmailAllInvite> emailList = new ArrayList<EmailAllInvite>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
				while (cur1.moveToNext()) {
					// to get the contact names
					String name = cur1.getString(cur1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					if (name != null) {

						EmailAllInvite emailinvite = new EmailAllInvite();
						emailinvite.setEmail(email);
						emailinvite.setName(name);
						emailList.add(emailinvite);
					}
				}
				cur1.close();
			}
		}

		return emailList;
	}

	public void onContactBack(View v) {
		finish();
	}

	public class LoadContacts extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			tempArray = getNameEmailDetails();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);

			JSONArray array = new JSONArray();

			for (int i = 0; i < tempArray.size(); i++) {

				EmailAllInvite emailInvite = tempArray.get(i);
				JSONObject jsonObject = new JSONObject();

				try {
					jsonObject.put("status", "0");
					jsonObject.put("name", emailInvite.getName());
					jsonObject.put("userid", "0");
					jsonObject.put("email", emailInvite.getEmail());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				array.put(jsonObject);
			}

			new SendEmailList().execute(array + "");
		}
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

				String emailArray = params[0].toString();
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(constant.GetBaseUrl() + "api=CheckInvitationStatus");
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				reqEntity.addPart("User_ID", new StringBody(User_ID));
				reqEntity.addPart("EmailList", new StringBody(emailArray));

				httppost.setEntity(reqEntity);

				HttpResponse response = httpclient.execute(httppost);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String sResponse;
				StringBuilder s = new StringBuilder();

				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}
				responseStr = s.toString();

				Log.d("TAG", "Response " + responseStr);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return responseStr;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);

			Log.d("TAG", result + "");

			JSONArray array = null;
			try {
				array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonObject;
					try {
						jsonObject = array.getJSONObject(i);
						EmailAllInvite emailInvite = new EmailAllInvite();
						emailInvite.setStatus(jsonObject.getString("status"));
						emailInvite.setEmail(jsonObject.getString("email"));
						emailInvite.setUserid(jsonObject.getString("userid"));
						emailInvite.setName(jsonObject.getString("name"));
						inviteContectList.add(emailInvite);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			// /
			//
			tempArray = (ArrayList<EmailAllInvite>) inviteContectList.clone();

			Collections.sort(inviteContectList, new FishNameComparator());
			makeSection(inviteContectList);
			simpleAdapter = new InviteListAdapter(context, items);
			listInvited.setAdapter(simpleAdapter);
		}

	};

	public class FishNameComparator implements Comparator<EmailAllInvite> {
		public int compare(EmailAllInvite left, EmailAllInvite right) {
			return left.getEmail().toLowerCase().compareTo(right.getEmail().toLowerCase());
		}
	}

	public void makeSection(ArrayList<EmailAllInvite> arrayTemp) {

		items.clear();
		alphabet.clear();
		int start = 0;
		int end = 0;
		String previousLetter = null;
		Object[] tmpIndexItem = null;
		Pattern numberPattern = Pattern.compile("[0-9]");

		for (EmailAllInvite country : arrayTemp) {
			String firstLetter = country.getEmail().substring(0, 1);

			// Group numbers together in the scroller
			if (numberPattern.matcher(firstLetter).matches()) {
				firstLetter = "#";
			}

			// alphabet scroller
			if (previousLetter != null && !firstLetter.equalsIgnoreCase(previousLetter)) {
				end = items.size() - 1;
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
				tmpIndexItem[1] = start;
				tmpIndexItem[2] = end;
				alphabet.add(tmpIndexItem);

				start = end + 1;
			}

			// Check if we need to add a header row
			if (!firstLetter.equalsIgnoreCase(previousLetter)) {
				items.add(new SectionItem(firstLetter));
				sections.put(firstLetter, start);
			}

			// Add the country to the list
			items.add(new InviteEntryItem(country));
			previousLetter = firstLetter;
		}

		if (previousLetter != null) {
			tmpIndexItem = new Object[3];
			tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
			tmpIndexItem[1] = start;
			tmpIndexItem[2] = items.size() - 1;
			alphabet.add(tmpIndexItem);
		}
	}

	public class ViewHolder {
		TextView header;
		int previousTop = 0;
	}

	public class InviteListAdapter extends BaseAdapter implements PinnedSectionListAdapter {

		ArrayList<Item> items;
		private LayoutInflater inflater;
		Function fun;
		checkInternet chkNet;
		Context ctx;
		HolderChild holderChield;
		JSONParser parser = new JSONParser();
		String UserID = "", UnFollow_User_ID = "";

		public InviteListAdapter(Context context, ArrayList<Item> items) {
			this.items = items;
			this.ctx = context;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			fun = new Function(ctx);
			chkNet = new checkInternet(ctx);
			SharedPreferences prefs = ctx.getSharedPreferences("User_Info", ctx.MODE_PRIVATE);
			UserID = prefs.getString("UserID", "");
		}

		public void updateListView(ArrayList<Item> items) {
			this.items = items;
			notifyDataSetChanged();

		}

		protected void prepareSections(int sectionsNumber) {
		}

		protected void onSectionAdded(Item section, int sectionPosition) {
		}

		public class Holder {

			private TextView title;
		}

		public class HolderChild {

			private TextView title, email;
			Button status;
			ProgressBar pd;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Holder holder;

			final Item item = items.get(position);
			if (item != null) {
				if (item.isSection()) {

					if (convertView == null) {
						holder = new Holder();
						v = inflater.inflate(R.layout.list_item_section, null, false);
						holder.title = (TextView) v.findViewById(R.id.title);
						v.setTag(holder);
					} else {
						holder = (Holder) v.getTag();
					}

					SectionItem si = (SectionItem) item;

					v.setOnClickListener(null);
					v.setOnLongClickListener(null);
					v.setLongClickable(false);

					holder.title.setText(si.getTitle());

				} else {

					if (convertView == null) {
						holderChield = new HolderChild();
						v = inflater.inflate(R.layout.list_item_child, null, false);
						holderChield.title = (TextView) v.findViewById(R.id.title);
						holderChield.email = (TextView) v.findViewById(R.id.txtemails);
						holderChield.status = (Button) v.findViewById(R.id.textView_status);
						holderChield.pd = (ProgressBar) v.findViewById(R.id.progressBar1);
						holderChield.email.setVisibility(View.GONE);
						v.setTag(holderChield);
					} else {
						holderChield = (HolderChild) v.getTag();
					}

					final InviteEntryItem ei = (InviteEntryItem) item;

					holderChield.title.setText(ei.lawyer.getEmail());
					holderChield.title.setTypeface(SplashScreen.ProxiNova_Regular);

					// 0 :- invite
					// 1 :- invited
					// 2 :- Follow
					// 3:- Following

					if (ei.lawyer.getStatus().equalsIgnoreCase("0")) {
						// holderChield.status.setText("invite");
						holderChield.status.setBackgroundResource(R.drawable.btn_invite);
						holderChield.status.setText("");
					} else if (ei.lawyer.getStatus().equalsIgnoreCase("1")) {
						// holderChield.status.setText("invited");
						holderChield.status.setBackgroundResource(R.drawable.btn_reinvited);
						holderChield.status.setText("");
					} else if (ei.lawyer.getStatus().equalsIgnoreCase("2")) {
						holderChield.status.setBackgroundResource(R.drawable.btnbg);
						holderChield.status.setText("Follow");
						holderChield.status.setTextColor(R.color.Theme_Color);
						holderChield.status.setTypeface(SplashScreen.ProxiNova_Bold);
					} else if (ei.lawyer.getStatus().equalsIgnoreCase("3")) {
						holderChield.status.setBackgroundResource(R.color.btnFollowcolor);
						holderChield.status.setText("Following");
						holderChield.status.setTextColor(Color.parseColor("#ffffff"));
						holderChield.status.setTypeface(SplashScreen.ProxiNova_Bold);
					}
					holderChield.status.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							System.out.println("status" + ei.lawyer.getStatus());
							new UnFollowUser(ei).execute();
						}
					});

				}

			}
			return v;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {

			if (items.get(position).isSection()) {
				return 1;
			}

			return 0;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {

			if (viewType == 0) {
				return false;
			} else {
				return true;
			}

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		/** UnFollow Api */

		class UnFollowUser extends AsyncTask<String, Void, String> {
			InviteEntryItem ei;

			public UnFollowUser(InviteEntryItem ei) {
				// TODO Auto-generated constructor stub
				this.ei = ei;
			}

			@Override
			protected void onPreExecute() {

				// pd = ProgressDialog.show(Following.this, "", "");
				showDialog();
			}

			@Override
			protected String doInBackground(String... params) {

				String Result1 = null;
				// TODO Auto-generated method stub
				Constant cc = new Constant();
				String url = "";
				UnFollow_User_ID = ei.lawyer.getUserid();
				String email = ei.lawyer.getEmail();
				if (ei.lawyer.getStatus().equalsIgnoreCase("0")) {
					url = cc.GetBaseUrl() + "api=InvitationSend&User_ID=" + UserID + "&Email=" + email;
				} else if (ei.lawyer.getStatus().equalsIgnoreCase("1")) {
					url = cc.GetBaseUrl() + "api=InvitationSend&User_ID=" + UserID + "&Email=" + email;
				} else if (ei.lawyer.getStatus().equalsIgnoreCase("2")) {
					url = cc.GetBaseUrl() + "api=Following&User_ID=" + UserID + "&Follow_Following=" + UnFollow_User_ID;
				} else if (ei.lawyer.getStatus().equalsIgnoreCase("3")) {
					url = cc.GetBaseUrl() + "api=Unfollow&User_ID=" + UserID + "&Follow_Following=" + UnFollow_User_ID;
				}

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
				dismissDialog();
				holderChield.pd.setVisibility(View.INVISIBLE);
				if (status.equals("1")) {
					// new GetFollowingUser().execute();
					if (ei.lawyer.getStatus().equalsIgnoreCase("0")) {
						fun.SimpleAlert("CamRate", "Invitation has been sent successfully");
						ei.lawyer.setStatus("1");
						notifyDataSetChanged();

					} else if (ei.lawyer.getStatus().equalsIgnoreCase("1")) {
						fun.SimpleAlert("CamRate", "Invitation has been sent successfully");
						ei.lawyer.setStatus("1");
						notifyDataSetChanged();
					} else if (ei.lawyer.getStatus().equalsIgnoreCase("2")) {
						ei.lawyer.setStatus("3");
						notifyDataSetChanged();
					} else if (ei.lawyer.getStatus().equalsIgnoreCase("3")) {
						ei.lawyer.setStatus("2");
						notifyDataSetChanged();
					}
				}
				if (status.equals("0")) {
					fun.SimpleAlert("CamRate", "Server Error Please try again");
				}
			}

			@Override
			protected void onProgressUpdate(Void... values) {
			}
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
