package com.camrate.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

public class ContactsList extends Activity {
	private Context context;
	ContactListAdapter simpleAdapter;
	ListView listLawyers;
	public static ArrayList<Item> items = new ArrayList<Item>();
	private List<Object[]> alphabet = new ArrayList<Object[]>();
	private HashMap<String, Integer> sections = new HashMap<String, Integer>();
	ArrayList<String> emails;
	TextView textViewSelectAll;
	boolean isSelectAll;
	ProgressBar pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.contectslist);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		context = ContactsList.this;

		// for custome title
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Address book");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		Button btnRefresh = (Button) findViewById(R.id.button2);
		btnRefresh.setVisibility(View.INVISIBLE);
		Button btnDone = (Button) findViewById(R.id.button1);
		btnDone.setVisibility(View.VISIBLE);
		btnDone.setText("Done");
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		//

		textViewSelectAll = (TextView) findViewById(R.id.textView_selectAll);
		listLawyers = (ListView) findViewById(R.id.list);

		new GetContactsLoading().execute();

		listLawyers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				EntryItem ei = (EntryItem) items.get(position);

				if (ei.lawyer.isChecked()) {
					ei.lawyer.setChecked(false);
					ei.lawyer.setChecked(false);
				} else {
					ei.lawyer.setChecked(true);
					ei.lawyer.setChecked(true);
				}
				simpleAdapter.notifyDataSetChanged();

			}
		});

		textViewSelectAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isSelectAll) {
					isSelectAll = false;

					for (int i = 0; i < items.size(); i++) {

						if (!items.get(i).isSection()) {
							EntryItem ei = (EntryItem) items.get(i);
							ei.lawyer.setChecked(false);
						}
					}

				} else {
					isSelectAll = true;

					for (int i = 0; i < items.size(); i++) {
						if (!items.get(i).isSection()) {
							EntryItem ei = (EntryItem) items.get(i);
							ei.lawyer.setChecked(true);
						}
					}

				}

				simpleAdapter.notifyDataSetChanged();
			}
		});

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();
				for (int j = 0; j < items.size(); j++) {
					try {
						JSONObject jsonDetail = new JSONObject();

						if (!items.get(j).isSection()) {
							EntryItem ei = (EntryItem) items.get(j);
							if (ei.lawyer.isChecked()) {
								jsonDetail.put("email", ei.lawyer.getEmail());
								array.put(jsonDetail);
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				try {
					json.put("emaillist", array);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				// sed data
				Intent intent = new Intent();
				intent.putExtra("emaillist", json + "");
				setResult(1, intent);
				finish();

			}

		});
	}

	public class FishNameComparator implements Comparator<EmailAll> {
		public int compare(EmailAll left, EmailAll right) {
			return left.getEmail().toLowerCase().compareTo(right.getEmail().toLowerCase());
		}
	}

	public void makeSection(ArrayList<EmailAll> arrayTemp) {

		items.clear();
		alphabet.clear();
		int start = 0;
		int end = 0;
		String previousLetter = null;
		Object[] tmpIndexItem = null;
		Pattern numberPattern = Pattern.compile("[0-9]");

		for (EmailAll country : arrayTemp) {
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
				items.add(new SectionItem(firstLetter.toUpperCase(Locale.UK)));
				sections.put(firstLetter, start);
			}

			// Add the country to the list
			items.add(new EntryItem(country));
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

	public ArrayList<EmailAll> getLawyersFromJSON(String json) {
		ArrayList<EmailAll> tmpRtn = new ArrayList<EmailAll>();

		try {

			for (int i = 0; i < emails.size(); i++) {
				EmailAll tmpLawyer = new EmailAll();

				tmpLawyer.setEmail(emails.get(i));
				tmpLawyer.setPosition(i + "");

				tmpRtn.add(tmpLawyer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tmpRtn;
	}

	public ArrayList<String> getNameEmailDetails() {
		ArrayList<String> names = new ArrayList<String>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
				while (cur1.moveToNext()) {
					String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					// Log.e("Email", email);
					if (email != null) {
						names.add(email);
					}
				}
				cur1.close();
			}
		}
		return names;
	}

	class GetContactsLoading extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			emails = getNameEmailDetails();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);

			ArrayList<EmailAll> tmpRtn = getLawyersFromJSON("");
			Collections.sort(tmpRtn, new FishNameComparator());
			makeSection(tmpRtn);

			simpleAdapter = new ContactListAdapter(context, android.R.layout.simple_list_item_1, android.R.id.text1, items);
			listLawyers.setAdapter(simpleAdapter);

		}
	};

}
