package com.camrate.tabs;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.camrate.R;

public class TabSample extends TabActivity {
	/** Called when the activity is first created. */
	private static final String TAB_1_TAG = "tab_1";
	private static final String TAB_2_TAG = "tab_3";
	private static final String TAB_3_TAG = "tab_4";
	private static final String TAB_4_TAG = "tab_5";
	private static final String TAB_5_TAG = "tab_6";
	TabHost tabHost;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs_layout);
		tabHost = getTabHost();

		View view0 = createTabView(this, TAB_1_TAG, 0);
		tabHost.addTab(tabHost.newTabSpec(TAB_1_TAG).setIndicator(view0).setContent(new Intent(this, TabGroup1Activity.class)));
		View view1 = createTabView(this, TAB_2_TAG, 1);
		tabHost.addTab(tabHost.newTabSpec(TAB_2_TAG).setIndicator(view1).setContent(new Intent(this, TabGroup2Activity.class)));
		View view2 = createTabView(this, TAB_3_TAG, 2);
		tabHost.addTab(tabHost.newTabSpec(TAB_3_TAG).setIndicator(view2).setContent(new Intent(this, TabGroup3Activity.class)));
		View view3 = createTabView(this, TAB_4_TAG, 3);
		tabHost.addTab(tabHost.newTabSpec(TAB_4_TAG).setIndicator(view3).setContent(new Intent(this, TabGroup4Activity.class)));
		View view4 = createTabView(this, TAB_5_TAG, 4);
		tabHost.addTab(tabHost.newTabSpec(TAB_5_TAG).setIndicator(view4).setContent(new Intent(this, TabGroup5Activity.class)));

		tabHost.getTabWidget().setDividerDrawable(null);

		try {

			if (getIntent().getStringExtra("isFor").equalsIgnoreCase("myfeed")) {
				tabHost.setCurrentTab(0);
			} else {
				tabHost.setCurrentTab(0);
				// tabHost.setCurrentTab(1);
			}
		} catch (Exception e) {

			tabHost.setCurrentTab(0);
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				if (tabId.equalsIgnoreCase(TAB_3_TAG)) {

					tabHost.getTabWidget().setVisibility(View.GONE);

				} else {
					tabHost.getTabWidget().setVisibility(View.VISIBLE);
				}

			}
		});

	}

	private static View createTabView(final Context context, final String text, int index) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_item1, null);

		if (index == 0) {
			ImageView tv = (ImageView) view.findViewById(R.id.tabsText);
			tv.setBackgroundResource(R.drawable.tab_bg_selector1);
		} else if (index == 1) {
			ImageView tv = (ImageView) view.findViewById(R.id.tabsText);
			tv.setBackgroundResource(R.drawable.tab_bg_selector2);
		}

		else if (index == 2) {
			ImageView tv = (ImageView) view.findViewById(R.id.tabsText);
			tv.setBackgroundResource(R.drawable.tab_bg_selector3);
		}

		else if (index == 3) {
			ImageView tv = (ImageView) view.findViewById(R.id.tabsText);
			tv.setBackgroundResource(R.drawable.tab_bg_selector4);
		}

		else if (index == 4) {
			ImageView tv = (ImageView) view.findViewById(R.id.tabsText);
			tv.setBackgroundResource(R.drawable.tab_bg_selector5);
		}

		return view;
	}

}