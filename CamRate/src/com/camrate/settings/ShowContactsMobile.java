package com.camrate.settings;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.camrate.share.EmailAllInvite;
import com.camrate.share.InviteEntryItem;
import com.camrate.share.Item;
import com.camrate.share.PinnedSectionListView.PinnedSectionListAdapter;
import com.camrate.share.SectionItem;
import com.camrate.tabs.TabGroupActivity;

public class ShowContactsMobile extends Activity {
	JSONParser parser = new JSONParser();
	Constant constant = new Constant();
	ProgressBar pd;
	String User_ID = "";
	ArrayList<EmailAllInvite> inviteContectList;

	private Context context;
	InviteListAdapterMobile simpleAdapter;
	ListView listInvited;
	ArrayList<EmailAllInvite> tempArray = new ArrayList<EmailAllInvite>();
	public static ArrayList<Item> items = new ArrayList<Item>();
	private List<Object[]> alphabet = new ArrayList<Object[]>();
	private HashMap<String, Integer> sections = new HashMap<String, Integer>();
	ArrayList<String> emails;
	TextView textViewSelectAll;
	boolean isSelectAll;
	Button btnback;
	TextView txtMainlbl;
	EditText edtSearch;
	public static String InviteMsg = "";
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_contacts);

		context = getParent();

		pd = (ProgressBar) findViewById(R.id.progressBar1);
		initProcessDialog();
		listInvited = (ListView) findViewById(R.id.list);
		btnback = (Button) findViewById(R.id.btnContactsBack);
		txtMainlbl = (TextView) findViewById(R.id.lblInviteFriTitle);
		txtMainlbl.setTypeface(SplashScreen.ProxiNova_Bold);
		edtSearch = (EditText) findViewById(R.id.search);
		inviteContectList = new ArrayList<EmailAllInvite>();

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

		new getContactDetails().execute();

	}

	public void performSearch(String text) {
		// String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());

		tempArray.clear();

		for (int i = 0; i < inviteContectList.size(); i++) {
			EmailAllInvite item = inviteContectList.get(i);
			if (item.getName().toLowerCase(Locale.getDefault()).contains(text)) {
				tempArray.add(item);
			}
		}
		Collections.sort(tempArray, new FishNameComparator());
		makeSection(tempArray);
		simpleAdapter.notifyDataSetChanged();

	}

	public class getContactDetails extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			ArrayList<EmailAllInvite> tempArray = getNameEmailDetails();

			inviteContectList = (ArrayList<EmailAllInvite>) tempArray.clone();
			Collections.sort(tempArray, new FishNameComparator());
			makeSection(tempArray);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissDialog();
			simpleAdapter = new InviteListAdapterMobile(context, items);
			listInvited.setAdapter(simpleAdapter);
			new getInvitemessage().execute("");
		}
	}

	public ArrayList<EmailAllInvite> getNameEmailDetails() {
		ArrayList<EmailAllInvite> emailList = new ArrayList<EmailAllInvite>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
				while (cur1.moveToNext()) {
					// to get the contact names
					String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

					String number = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if (name != null) {

						EmailAllInvite emailinvite = new EmailAllInvite();
						emailinvite.setNumber(number);
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

	public class FishNameComparator implements Comparator<EmailAllInvite> {
		public int compare(EmailAllInvite left, EmailAllInvite right) {
			return left.getName().toLowerCase().compareTo(right.getName().toLowerCase());
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
			String firstLetter = country.getName().substring(0, 1);

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

	public String stripHtml(String html) {
		return Html.fromHtml(html).toString();
	}

	class getInvitemessage extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			String Result = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=CMS&CMS_Type=invite text message";
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = parser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");
				if (Result.equalsIgnoreCase("1")) {
					JSONObject user = json.getJSONObject("type");
					String CMS_Description = user.getString("CMS_Description");
					InviteMsg = stripHtml(CMS_Description);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return Result;
		}

		@Override
		protected void onPostExecute(String status) {
			if (status.equals("1")) {

			}
			if (status.equals("0")) {
				Toast.makeText(getApplicationContext(), "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
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

	public class InviteListAdapterMobile extends BaseAdapter implements PinnedSectionListAdapter {

		ArrayList<Item> items;
		private LayoutInflater inflater;
		Function fun;
		checkInternet chkNet;
		Context ctx;
		HolderChild holderChield;
		JSONParser parser = new JSONParser();
		String UserID = "", UnFollow_User_ID = "";
		private ArrayList<Item> arraylist;

		public InviteListAdapterMobile(Context context, ArrayList<Item> items) {
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
						v.setTag(holderChield);
					} else {
						holderChield = (HolderChild) v.getTag();
					}

					final InviteEntryItem ei = (InviteEntryItem) item;

					holderChield.title.setText(ei.lawyer.getName());
					holderChield.email.setText(ei.lawyer.getNumber());
					holderChield.title.setTypeface(SplashScreen.ProxiNova_Regular);
					holderChield.email.setTypeface(SplashScreen.ProxiNova_Regular);

					// 0 :- invite
					// 1 :- reinvited
					// 2 :- Follow
					// 3:- Following

					holderChield.status.setBackgroundResource(R.drawable.btn_invite);
					holderChield.status.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							sendSMS(ei);
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

		protected void sendSMS(InviteEntryItem ei) {
			try {
				String number = ei.lawyer.getNumber();
				Uri uri = Uri.parse("smsto:" + number + "");
				Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
				smsIntent.putExtra("address", new String(number));
				smsIntent.putExtra("sms_body", ShowContactsMobile.InviteMsg);
				//smsIntent.setType("vnd.android-dir/mms-sms");
				//
				// TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				// parentActivity.startActivity(smsIntent);
				getParent().startActivity(smsIntent);
				// finish();
			} catch (android.content.ActivityNotFoundException ex) {
				ex.printStackTrace();
			}
		}
	}
}
