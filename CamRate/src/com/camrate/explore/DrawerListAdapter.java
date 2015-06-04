package com.camrate.explore;

import java.util.ArrayList;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

public class DrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DrawerItem> navDrawerItems;
	int prog = 0;
	SharedPreferences prefs;
	int radius = 0;

	public DrawerListAdapter(Context context, ArrayList<DrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (position == 3) {
			if (convertView == null) {
				// LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				// convertView = mInflater.inflate(R.layout.explore_item, null);
				LayoutInflater mInflater = LayoutInflater.from(this.context);
				convertView = mInflater.inflate(R.layout.explore_item, null);

			}
			final SeekBar seekbar = (SeekBar) convertView.findViewById(R.id.seekBar1);
			final TextView txtSeekValue = (TextView) convertView.findViewById(R.id.txtvaluemiles);
			final TextView txttitle=(TextView)convertView.findViewById(R.id.titleNearby);
			txtSeekValue.setTypeface(SplashScreen.ProxiNova_Regular);
			txttitle.setTypeface(SplashScreen.ProxiNova_Regular);
			radius = prefs.getInt("radius", 5);
			seekbar.setProgress(radius);
			txtSeekValue.setText(String.valueOf(radius) + " mi");
			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					// TODO Auto-generated method stub
					prog = progress;
					txtSeekValue.setText(String.valueOf(seekbar.getProgress()) + " mi");
					prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
					SharedPreferences.Editor edit1 = prefs.edit();
					edit1.putInt("radius", prog);
					edit1.commit();
					Fragment fragment = new Newest();
					Bundle bundle = new Bundle();
					SlidingDrawerActivity.TAG = 3;
					bundle.putInt("tag", SlidingDrawerActivity.TAG);
					bundle.putBoolean("gridon", SlidingDrawerActivity.isGridOn);
					fragment.setArguments(bundle);
					FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, String.valueOf(SlidingDrawerActivity.TAG)).commit();
					SlidingDrawerActivity.txtlabel_Title.setText("Near By");
					SlidingDrawerActivity.txtlabel_Title.setTypeface(SplashScreen.ProxiNova_Bold);
				}
			});

			// txtSeekValue.setText(String.valueOf(seekbar.getProgress()) + " mil");
			seekbar.setOnTouchListener(new ListView.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN:
						// Disallow Drawer to intercept touch events.
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;

					case MotionEvent.ACTION_UP:
						// Allow Drawer to intercept touch events.
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
					}

					// Handle seekbar touch events.
					v.onTouchEvent(event);
					return true;
				}
			});
		} else {
			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			}

			ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
			TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
			TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

			imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
			txtTitle.setText(navDrawerItems.get(position).getTitle());
			txtTitle.setTypeface(SplashScreen.ProxiNova_Regular);

			// displaying count
			// check whether it set visible or not
			if (navDrawerItems.get(position).getCounterVisibility()) {
				txtCount.setText(navDrawerItems.get(position).getCount());
			} else {
				// hide the counter view
				txtCount.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

}
