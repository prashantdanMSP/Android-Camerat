package com.camrate.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.pojo.PojoFacebookFriends;
import com.camrate.share.EntryItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FacebookFriendListAdapter extends BaseAdapter {
	ArrayList<PojoFacebookFriends> lst;
	private Context con;
	ImageLoader imgLoader;
	DisplayImageOptions options;

	public FacebookFriendListAdapter(Context context, ArrayList<PojoFacebookFriends> arrlstFri) {
		con = context;
		lst = arrlstFri;
		imgLoader.init(ImageLoaderConfiguration.createDefault(con));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		return lst.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = ((Activity) con).getLayoutInflater().inflate(R.layout.list_item_facebook_friends, null);
			holder.lblName = (TextView) convertView.findViewById(R.id.lblFacebookFriendsItemName);
			holder.imgPhoto = (ImageView) convertView.findViewById(R.id.imgFacebookFriendsItemPhoto);
			holder.chk = (CheckBox) convertView.findViewById(R.id.chkFacebookFriendsItemCheck);
			holder.lblId = (TextView) convertView.findViewById(R.id.lblFacebookFriendsItemId);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lblId.setText(lst.get(position).getId());
		holder.lblName.setText(lst.get(position).getName());
		holder.lblName.setTypeface(SplashScreen.ProxiNova_Regular);
		// holder.chk.setChecked(lst.get(position).isIsitemChecked());

		if (lst.get(position).isIsitemChecked()) {
			holder.chk.setChecked(true);
		} else {
			holder.chk.setChecked(false);
		}
		holder.chk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CheckBox ch = (CheckBox) v;
				PojoFacebookFriends entyItem = lst.get(position);

				if (ch.isChecked()) {
					entyItem.setIsitemChecked(true);
				} else {
					entyItem.setIsitemChecked(false);
				}

			}
		});
		imgLoader.displayImage(lst.get(position).getUrl(), holder.imgPhoto);

		return convertView;
	}

	class ViewHolder {
		TextView lblName, lblId;
		ImageView imgPhoto;
		CheckBox chk;
	}

}
