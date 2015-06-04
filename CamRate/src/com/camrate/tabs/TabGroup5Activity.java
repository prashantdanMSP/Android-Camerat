package com.camrate.tabs;

import com.camrate.SecondCameraTab;
import com.camrate.profile.EditProfile;
import com.camrate.profile.UserProfile;

import android.content.Intent;
import android.os.Bundle;

public class TabGroup5Activity extends TabGroupActivity {
	
	 int SELECT_FILE = 1;
	 int REQUEST_CAMERA = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startChildActivity("UserProfile", new Intent(this, UserProfile.class));
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			EditProfile activity1 = (EditProfile) getLocalActivityManager().getCurrentActivity();
			activity1.onActivityResult(requestCode, resultCode, data);
			break;
		case 2:
			EditProfile activity2 = (EditProfile) getLocalActivityManager().getCurrentActivity();
			activity2.onActivityResult(requestCode, resultCode, data);
			break;

		default:
			break;
		}

	}
}
