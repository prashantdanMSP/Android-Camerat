package com.camrate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.camrate.global.Function;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.profile.UserProfile;
import com.camrate.tabs.TabGroupActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	Context _context;
	private ArrayList<HashMap<String, String>> list;
	ArrayList<ArrayList<HashMap<String, String>>> parentList;
	int TotalRate, AvgRate;
	int LoveCount = 0, LikeCount = 0, DislikeCount = 0, WhateverCount = 0, HateCount = 0;
	int LovePer = 0, LikePer = 0, DislikePer = 0, WhateverPer = 0, HatePer = 0;
	LinearLayout linearlayout;
	HashMap<String, Object> objPostDetail;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	Function fun;
	ArrayList<HashMap<String, String>> tempList;
	String UserID;

	public ExpandableListAdapter(Context context, ArrayList<HashMap<String, String>> list, HashMap<String, Object> postDetail, ArrayList<ArrayList<HashMap<String, String>>> parentList) {
		this._context = context;
		this.list = list;
		objPostDetail = postDetail;
		this.parentList = parentList;

		RatePercentage(objPostDetail, list);
		fun = new Function(context);
		Log.e("tag", list.toString() + "\n\n" + objPostDetail.toString());
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		SharedPreferences prefs = context.getSharedPreferences("User_Info", Context.MODE_PRIVATE);
		UserID = prefs.getString("UserID", "");
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		System.out.println("Childcalled");
		Log.e("group postion", "" + groupPosition + "  " + childPosititon);

		return childPosititon;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		Log.e("group postion", "" + groupPosition + "  " + childPosition);
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		// final String childText = (String) getChild(groupPosition,
		// childPosition);

		LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.list_child, null);

		TextView txtListChild = (TextView) convertView.findViewById(R.id.txtValueUser);
		ImageView imgUser = (ImageView) convertView.findViewById(R.id.imgSearchUser);
		TextView txtLastSeen = (TextView) convertView.findViewById(R.id.tvlastseen);

		try {

			Log.e("tag", childPosition + "   size :" + tempList.size() + " group position :" + groupPosition);

			// if(groupPosition == 0)
			{
				txtListChild.setText(parentList.get(groupPosition).get(childPosition).get("User_Name"));
				txtListChild.setTypeface(SplashScreen.ProxiNova_Regular);
				imageLoader.displayImage(parentList.get(groupPosition).get(childPosition).get("User_Imagepath"), imgUser, options);
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date myDate = simpleDateFormat.parse((objPostDetail.get("Post_Date")).toString());
					String diffVal = fun.checkDate_Diffrences(myDate);
					txtLastSeen.setText(diffVal);
					txtLastSeen.setTypeface(SplashScreen.ProxiNova_Regular);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isAuthor(parentList.get(groupPosition).get(childPosition).get("User_ID"))) {
					Intent intent = new Intent(_context, UserProfileChild.class);
					intent.putExtra("User_ID", parentList.get(groupPosition).get(childPosition).get("User_ID"));
					TabGroupActivity parentActivity = (TabGroupActivity) _context;
					parentActivity.startChildActivity("UserProfileChild", intent);

				} else {
					Intent intent = new Intent(_context, GeneralUserProfile.class);
					intent.putExtra("User_ID", parentList.get(groupPosition).get(childPosition).get("User_ID"));
					TabGroupActivity parentActivity = (TabGroupActivity) _context;
					parentActivity.startChildActivity("GeneralUserProfile", intent);
				}

			}
		});
		return convertView;
	}

	private boolean isAuthor(String strPostAuthor2) {
		if (strPostAuthor2.equals(UserID)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int size = 0;
		if (groupPosition == 0) {
			tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("Rating_Rate").equals(5 + "")) {
					tempList.add(list.get(i));
					size = tempList.size();
				}
			}
		} else if (groupPosition == 1) {
			// ArrayList<HashMap<String, String>>
			tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("Rating_Rate").equals(4 + "")) {
					tempList.add(list.get(i));
					size = tempList.size();
				}
			}

		} else if (groupPosition == 2) {
			// ArrayList<HashMap<String, String>>
			tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("Rating_Rate").equals(3 + "")) {
					tempList.add(list.get(i));
					size = tempList.size();
				}
			}
		} else if (groupPosition == 3) {
			// ArrayList<HashMap<String, String>>
			tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("Rating_Rate").equals(2 + "")) {
					tempList.add(list.get(i));
					size = tempList.size();
				}
			}
		} else if (groupPosition == 4) {
			// ArrayList<HashMap<String, String>>
			tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("Rating_Rate").equals(1 + "")) {
					tempList.add(list.get(i));
					size = tempList.size();
				}
			}
		}
		Log.e("size", "" + size);
		return size;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getGroupCount() {
		// return this._listDataHeader.size();
		return 5;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.list_group1, parent, false);
		LinearLayout linearlayout = (LinearLayout) convertView.findViewById(R.id.linearlove);
		ImageView imgLove = (ImageView) convertView.findViewById(R.id.imglove);
		TextView txtCount = (TextView) convertView.findViewById(R.id.txtcountlove);
		ProgressBar prog = (ProgressBar) convertView.findViewById(R.id.progressbarcal);
		// TextView txtText = (TextView)
		// convertView.findViewById(R.id.txtlovelbl);
		TextView txtAverage = (TextView) convertView.findViewById(R.id.txtloveavg);
		// final ImageView imgArrow = (ImageView)
		// convertView.findViewById(R.id.imglovearrow);

		if (groupPosition == 0) {
			// int color=Color.parseColor("#F4F4F4");
			// linearlayout.setBackgroundColor(color);
			imgLove.setImageResource(R.drawable.filter_5_sel);
			String lvcount = String.valueOf(LoveCount);
			txtCount.setText("(" + lvcount + ")");
			txtCount.setTypeface(SplashScreen.ProxiNova_Regular);
			String love = String.valueOf(LovePer);
			// txtText.setText("Love");
			prog.setProgress(Integer.parseInt(love));
			txtAverage.setText(love + "%");
			txtAverage.setTypeface(SplashScreen.ProxiNova_Regular);
			// if (isExpanded) {
			// imgArrow.setImageResource(R.drawable.uparrow);
			// } else {
			// imgArrow.setImageResource(R.drawable.downarrow);
			// }
		} else if (groupPosition == 1) {

			// int color=Color.parseColor("#EAEAEA");
			// linearlayout.setBackgroundColor(color);
			imgLove.setImageResource(R.drawable.filter_4_sel);
			String lkcount = String.valueOf(LikeCount);
			txtCount.setText("(" + lkcount + ")");
			String like = String.valueOf(LikePer);
			// txtText.setText("Like");
			prog.setProgress(Integer.parseInt(like));
			txtAverage.setText(like + "%");
			txtCount.setTypeface(SplashScreen.ProxiNova_Regular);
			txtAverage.setTypeface(SplashScreen.ProxiNova_Regular);

			/*
			 * if (isExpanded) { imgArrow.setImageResource(R.drawable.uparrow); } else { imgArrow.setImageResource(R.drawable.downarrow); }
			 */
		} else if (groupPosition == 2) {

			// int color=Color.parseColor("#F4F4F4");
			// linearlayout.setBackgroundColor(color);
			imgLove.setImageResource(R.drawable.filter_3_sel);
			String whcount = String.valueOf(WhateverCount);
			txtCount.setText("(" + whcount + ")");
			String whatever = String.valueOf(WhateverPer);
			// txtText.setText("Whatever");
			prog.setProgress(Integer.parseInt(whatever));
			txtAverage.setText(whatever + "%");
			txtCount.setTypeface(SplashScreen.ProxiNova_Regular);
			txtAverage.setTypeface(SplashScreen.ProxiNova_Regular);
			// if (isExpanded) {
			// imgArrow.setImageResource(R.drawable.uparrow);
			// } else {
			// imgArrow.setImageResource(R.drawable.downarrow);
			// }
		} else if (groupPosition == 3) {
			// int color=Color.parseColor("#d3d3d3");
			// int color=Color.parseColor("#EAEAEA");
			// linearlayout.setBackgroundColor(color);
			imgLove.setImageResource(R.drawable.filter_2_sel);
			String dlcount = String.valueOf(DislikeCount);
			txtCount.setText("(" + dlcount + ")");
			String dislike = String.valueOf(DislikePer);
			// txtText.setText("Dislike");
			prog.setProgress(Integer.parseInt(dislike));
			txtAverage.setText(dislike + "%");
			txtCount.setTypeface(SplashScreen.ProxiNova_Regular);
			txtAverage.setTypeface(SplashScreen.ProxiNova_Regular);
			// if (isExpanded) {
			// imgArrow.setImageResource(R.drawable.uparrow);
			// } else {
			// imgArrow.setImageResource(R.drawable.downarrow);
			// }
		} else if (groupPosition == 4) {

			// int color=Color.parseColor("#F4F4F4");
			// linearlayout.setBackgroundColor(color);
			imgLove.setImageResource(R.drawable.filter_1_sel);
			String hcount = String.valueOf(HateCount);
			txtCount.setText("(" + hcount + ")");
			String hate = String.valueOf(HatePer);
			prog.setProgress(Integer.parseInt(hate));
			// txtText.setText("Hate");
			txtAverage.setText(hate + "%");
			txtCount.setTypeface(SplashScreen.ProxiNova_Regular);
			txtAverage.setTypeface(SplashScreen.ProxiNova_Regular);

			// if (isExpanded) {
			// imgArrow.setImageResource(R.drawable.uparrow);
			// } else {
			// imgArrow.setImageResource(R.drawable.downarrow);
			// }
		}

		ExpandableListView listv = (ExpandableListView) parent;

		listv.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				// TODO Auto-generated method stub
				setListViewHeight(parent, groupPosition);
				// imgArrow.setImageResource(R.drawable.uparrow);
				return false;

			}
		});

		return convertView;

	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void RatePercentage(HashMap<String, Object> mySelectedFeedDetail, ArrayList<HashMap<String, String>> RateDetail) {
		String RateCount = mySelectedFeedDetail.get("Count_Rating").toString();
		if (RateCount != null) {
			TotalRate = Integer.parseInt(RateCount);
			System.out.println("Count_Rating---> " + TotalRate);

		}

		if (RateDetail.size() > 0) {
			for (int i = 0; i < RateDetail.size(); i++) {
				String RatingCount = RateDetail.get(i).get("Rating_Rate").toString();
				if (RatingCount != null) {
					int count = Integer.valueOf(RatingCount);
					switch (count) {
					case 5:
						LoveCount++;
						break;
					case 4:
						LikeCount++;
						break;
					case 3:
						WhateverCount++;
						break;
					case 2:
						DislikeCount++;
						break;
					case 1:
						HateCount++;
						break;
					}

				}
				RateCalculation(LoveCount, LikeCount, WhateverCount, DislikeCount, HateCount, TotalRate);
			}

		}

	}

	public void setListViewHeight(ExpandableListView listView, int group) {
		ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getGroupCount(); i++) {
			View groupItem = listAdapter.getGroupView(i, false, null, listView);
			groupItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += groupItem.getMeasuredHeight();

			if (((listView.isGroupExpanded(i)) && (i != group)) || ((!listView.isGroupExpanded(i)) && (i == group))) {
				for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
					View listItem = listAdapter.getChildView(i, j, false, null, listView);
					listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
					totalHeight += listItem.getMeasuredHeight();
				}
			}
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
		if (height < 10)
			height = 200;
		params.height = height;
		listView.setLayoutParams(params);
		listView.requestLayout();

	}

	public void RateCalculation(int love, int like, int whatever, int dislike, int hate, int totalcount) {

		if (totalcount == 0) {
			totalcount = 1;
		}
		LovePer = (((love * 100) / totalcount));
		LikePer = (((like * 100) / totalcount));
		WhateverPer = (((whatever * 100) / totalcount));
		DislikePer = (((dislike * 100) / totalcount));
		HatePer = (((hate * 100) / totalcount));

	}
}
