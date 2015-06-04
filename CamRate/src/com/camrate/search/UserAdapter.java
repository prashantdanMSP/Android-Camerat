package com.camrate.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class UserAdapter extends ArrayAdapter<HashMap<String, String>> {
	DisplayImageOptions options;
	String query;
	Context c;
	Date date1;
	// Constant c=new Constant();

	ImageLoader imageLoader = ImageLoader.getInstance();

	private ArrayList<HashMap<String, String>> items;

	public UserAdapter(Context context, int resource, ArrayList<HashMap<String, String>> items) {

		super(context, resource, items);
		this.c = context;
		this.items = items;
		imageLoader.init(ImageLoaderConfiguration.createDefault(c));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();

	}

	public int getCount() {
		return items.size();
	}

	public HashMap<String, String> getItem(int position) {
		return items.get(position);
	}

	public static class ViewHolder {
		public ImageView image;

	}

	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		// ViewHolder holder = null;

		if (v == null) {

			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());

			v = vi.inflate(R.layout.searchuser, null);
			// v.setTag(holder);

		}
		/*
		 * else { holder=(ViewHolder)v.getTag(); }
		 */
		if (items != null) {

			ImageView imgSearchUSer = (ImageView) v.findViewById(R.id.imgSearchUser);
			TextView tvSearchUser = (TextView) v.findViewById(R.id.txtValueUser);

			if (imgSearchUSer != null) {

				String img = (items.get(position).get("User_Imagepath"));
				imageLoader.displayImage(img, imgSearchUSer, options);
			}
			if (tvSearchUser != null) {
				tvSearchUser.setText(items.get(position).get("User_Name"));

				tvSearchUser.setTypeface(SplashScreen.ProxiNova_Regular);
			}

		}

		return v;

	}

}