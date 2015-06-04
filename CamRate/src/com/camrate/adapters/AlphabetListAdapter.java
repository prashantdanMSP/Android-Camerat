package com.camrate.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.settings.ShowContactsActivity;

public class AlphabetListAdapter extends BaseAdapter {
	Context context;
	ArrayList<String> arrlstName, arrlstResult;

	public AlphabetListAdapter(ShowContactsActivity showContactsActivity,
			ArrayList<ArrayList<String>> sendData) {
		context = showContactsActivity;
		arrlstName = sendData.get(0);
		arrlstResult = sendData.get(1);
	}

	@Override
	public int getCount() {
		return arrlstName.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.contact_item, null);

			holder.lblName = (TextView) convertView
					.findViewById(R.id.lblFacebookFriendsItemName);
			holder.btnFollow = (Button) convertView
					.findViewById(R.id.btnContactFollow);
			holder.btnFollowing = (Button) convertView
					.findViewById(R.id.btnContactFollowing);
			holder.btnInvite = (Button) convertView
					.findViewById(R.id.btnContactInvite);
			holder.btnInvited = (Button) convertView
					.findViewById(R.id.btnContactInvited);

			holder.btnFollow.setVisibility(View.GONE);
			holder.btnFollowing.setVisibility(View.GONE);
			holder.btnInvite.setVisibility(View.GONE);
			holder.btnInvited.setVisibility(View.GONE);

			String strStatus = arrlstResult.get(position);
			if (strStatus.equals("0")) {
				holder.btnInvite.setVisibility(View.VISIBLE);
			} else if (strStatus.equals("1")) {
				holder.btnInvited.setVisibility(View.VISIBLE);
			} else if (strStatus.equals("2")) {
				holder.btnFollow.setVisibility(View.VISIBLE);
			} else if (strStatus.equals("3")) {
				holder.btnFollowing.setVisibility(View.VISIBLE);
			}

			holder.btnFollow.invalidate();
			holder.btnFollowing.invalidate();
			holder.btnInvite.invalidate();
			holder.btnInvited.invalidate();

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lblName.setText(arrlstName.get(position).toString());

		return convertView;
	}

	class ViewHolder {
		TextView lblName;
		Button btnFollow, btnFollowing, btnInvite, btnInvited;
	}

}
