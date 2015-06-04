package com.camrate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.camrate.settings.ShowContactsActivity;
import com.camrate.settings.ShowContactsMobile;
import com.camrate.share.TwitterFriendsActivity;
import com.camrate.tabs.TabGroupActivity;

public class InviteFriends extends Activity {

	TextView txtlblMain, txtlblEmail, txtlblTextMsg, txtlblTwitter;
	Button btnNext, btnBack;
	LinearLayout linearEmail, linearText, linearTwitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_friends);
		init();
	}

	public void init() {
		// TODO Auto-generated method stub
		txtlblMain = (TextView) findViewById(R.id.textView1);
		txtlblEmail = (TextView) findViewById(R.id.lblinvite_email);
		txtlblTextMsg = (TextView) findViewById(R.id.lblinvite_text);
		txtlblTwitter = (TextView) findViewById(R.id.lblinvite_twitter);
		btnNext = (Button) findViewById(R.id.button1);
		btnBack = (Button) findViewById(R.id.button2);
		btnBack.setVisibility(View.VISIBLE);
		btnNext.setVisibility(View.INVISIBLE);
		linearEmail = (LinearLayout) findViewById(R.id.LinearEmail);
		linearText = (LinearLayout) findViewById(R.id.LinearTextMsg);
		linearTwitter = (LinearLayout) findViewById(R.id.LinearTwitter);
		txtlblMain.setText("Invite Friends");
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlblEmail.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblTextMsg.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblTwitter.setTypeface(SplashScreen.ProxiNova_Regular);

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		linearEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), ShowContactsActivity.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("ShowContactsActivity", intent);
			}
		});
		linearText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), ShowContactsMobile.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("ShowContactsMobile", intent);
			}
		});
		linearTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getParent(), TwitterFriendsActivity.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("TwitterFriendsActivity", intent);
				overridePendingTransition(0, 0);
			}
		});
	}
}
