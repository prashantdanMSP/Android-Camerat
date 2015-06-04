package com.camrate.settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

public class CustomItemTitle extends Preference {

	public CustomItemTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public View getView(View convertView, ViewGroup parent) {
		View v = super.getView(convertView, parent);
		TextView txt=(TextView)v.findViewById(R.id.lblInviteFriTitle);
		txt.setText(getTitle());
		txt.setTypeface(SplashScreen.ProxiNova_Regular);
		return v;
	}

}
