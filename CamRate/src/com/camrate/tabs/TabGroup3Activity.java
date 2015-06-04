package com.camrate.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.camrate.SecondCameraTab;

public class TabGroup3Activity extends TabGroupActivity {

	int SELECT_IMAGE = 1234;
	int SELECT_VIDEO = 12345;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startChildActivity("Tab3Activity", new Intent(this, SecondCameraTab.class));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1234:
			SecondCameraTab activity1 = (SecondCameraTab) getLocalActivityManager().getCurrentActivity();
			activity1.onActivityResult(requestCode, resultCode, data);
			break;
		case 12345:
			SecondCameraTab activity2 = (SecondCameraTab) getLocalActivityManager().getCurrentActivity();
			activity2.onActivityResult(requestCode, resultCode, data);
			break;

		default:
			break;
		}

	}

	public void changeTab() {
		Log.d("TAG", "Last Selected Tab " + ActivityManage.lastSeletedTab);
		((TabSample) getParent()).getTabHost().setCurrentTab(ActivityManage.lastSeletedTab);
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		Log.d("TAG", "onbackLast Selected Tab " + ActivityManage.lastSeletedTab);
		((TabSample) getParent()).getTabHost().setCurrentTab(ActivityManage.lastSeletedTab);
	}

}
