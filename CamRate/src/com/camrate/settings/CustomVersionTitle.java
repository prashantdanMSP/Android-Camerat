package com.camrate.settings;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

public class CustomVersionTitle extends Preference {
	Context context;

	public CustomVersionTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	public View getView(View convertView, ViewGroup parent) {
		View v = super.getView(convertView, parent);
		TextView txt = (TextView) v.findViewById(R.id.textview);
		txt.setText(getTitle());
		txt.setTypeface(SplashScreen.ProxiNova_Regular);

		TextView txt1 = (TextView) v.findViewById(R.id.lblInviteFriTitle);

		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			String app_version = info.versionName;
			txt1.setText(app_version);
			txt1.setTypeface(SplashScreen.ProxiNova_Regular);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return v;
	}
}
