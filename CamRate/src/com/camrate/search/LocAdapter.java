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
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class LocAdapter extends ArrayAdapter<HashMap<String, String>> {
	DisplayImageOptions options;
	String query; 
	Context c;
	Date date1;
	//Constant c=new Constant();

	private ArrayList<HashMap<String, String>> items;

	public LocAdapter(Context context, int resource,
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

			TextView tvSearchUser = (TextView) v.findViewById(R.id.txtValueLocation);
	
		
			if(tvSearchUser !=null)
			{
				tvSearchUser.setText(items.get(position).get("Location"));
				System.out.println("lc"+tvSearchUser.getText().toString());
			}
		
		
			}

		

		return v;

	}


}