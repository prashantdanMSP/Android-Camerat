package com.camrate.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.share.TwitterFriendsActivity;
import com.camrate.tabs.TabGroupActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InviteFriendsActivity extends Activity implements OnItemClickListener {
	Context context;
	TextView lblTitle;
	ListView lstInviteFri;
	List<HashMap<String, Object>> mapData = new ArrayList<HashMap<String, Object>>();
	String[] from = { "Invite Friends via Email", "Invite Friends via Text Message", "Invite Friends via Twitter" };
	int[] to = { R.drawable.contact_icon, R.drawable.contact_icon, R.drawable.tw_icon };
	private ArrayList<String> arrlstContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_invite_friend_list);
		context = InviteFriendsActivity.this;

		init();

		lstInviteFri.setOnItemClickListener(this);

		findViewById(R.id.btnContactsBack).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init() {
		lblTitle = (TextView) findViewById(R.id.lblInviteFriTitle);
		lstInviteFri = (ListView) findViewById(R.id.lstInviteFriends);

		lblTitle.setTypeface(SplashScreen.ProxiNova_Bold);
		for (int i = 0; i < from.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("sharing", from[i]);
			map.put("sharing_to", to[i]);
			mapData.add(map);
		}

		// SimpleAdapter adapter = new SimpleAdapter(this, mapData, R.layout.custom_pref1, new String[] { "sharing", "sharing_to" }, new int[] { R.id.lblInviteFriItem, R.id.imageView1 });
		// lstInviteFri.setAdapter(adapter);
		// adapter.notifyDataSetChanged();
		inviteAdapter adapter = new inviteAdapter(InviteFriendsActivity.this, mapData, to);
		lstInviteFri.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:

			Intent intent = new Intent(getParent(), ShowContactsActivity.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("ShowContactsActivity", intent);
			break;

		case 1:
			Intent intent1 = new Intent(getParent(), ShowContactsMobile.class);
			TabGroupActivity parentActivity1 = (TabGroupActivity) getParent();
			parentActivity1.startChildActivity("ShowContactsMobile", intent1);

			break;

		case 2:
			Intent intent2 = new Intent(InviteFriendsActivity.this, TwitterFriendsActivity.class);
			TabGroupActivity parentActivity2 = (TabGroupActivity) getParent();
			parentActivity2.startChildActivity("TwitterFriendsActivity", intent2);
			break;

		}
	}

	public class inviteAdapter extends BaseAdapter {

		Context context;
		List<HashMap<String, Object>> mapData;
		private LayoutInflater mLayoutInflater = null;
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options;
		int[] imageid;

		public inviteAdapter(Context context, List<HashMap<String, Object>> mapData, int[] prgmImages) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.mapData = mapData;
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageid = prgmImages;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mapData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mapData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			CompleteListViewHolder viewHolder;
			if (convertView == null) {
				LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.custom_pref1, null);
				viewHolder = new CompleteListViewHolder(v);
				v.setTag(viewHolder);
			} else {
				viewHolder = (CompleteListViewHolder) v.getTag();
			}
			viewHolder.mTVItem.setText(mapData.get(position).get("sharing").toString());
			viewHolder.imgView.setImageResource(imageid[position]);
			return v;
		}
	}

	class CompleteListViewHolder {
		public TextView mTVItem;
		ImageView imgView;

		public CompleteListViewHolder(View base) {
			imgView = (ImageView) base.findViewById(R.id.imageView1);
			mTVItem = (TextView) base.findViewById(R.id.lblInviteFriItem);
			mTVItem.setTypeface(SplashScreen.ProxiNova_Regular);
		}
	}
}
