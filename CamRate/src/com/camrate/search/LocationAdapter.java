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

public class LocationAdapter extends ArrayAdapter<HashMap<String, String>> {
	DisplayImageOptions options;
	String query; 
	Context c;
	Date date1;
	//Constant c=new Constant();
	

	ImageLoader imageLoader = ImageLoader.getInstance();

	private ArrayList<HashMap<String, String>> items;

	public LocationAdapter(Context context, int resource,
			ArrayList<HashMap<String, String>> items) {

		super(context, resource, items);
		this.c = context;
		this.items = items;
	
		

	}

	public int getCount() {
		return items.size();
	}

	public HashMap<String, String> getItem(int position) {
		return items.get(position);
	}
	public static class ViewHolder{
	     public ImageView image;
	   
	}
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		 //ViewHolder holder = null;  

		if (v == null) {

			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());

			v = vi.inflate(R.layout.searchloc, null);
			//v.setTag(holder);
			
		} 
		 /*else
		    {          
		        holder=(ViewHolder)v.getTag();
		    }*/
		if (items != null) {

			TextView tvSearchLocation = (TextView) v.findViewById(R.id.txtValueLocation);
	
			
			if(tvSearchLocation !=null)
			{
				tvSearchLocation.setText(items.get(position).get("Location"));
				tvSearchLocation.setTypeface(SplashScreen.ProxiNova_Regular);
			}
		
		
			}

		

		return v;

	}


}