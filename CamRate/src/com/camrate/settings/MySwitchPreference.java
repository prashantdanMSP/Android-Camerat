package com.camrate.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.camrate.R;

public class MySwitchPreference extends SwitchPreference {
	public MySwitchPreference(Context context) {
		super(context);
	}

	public MySwitchPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MySwitchPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		if (view instanceof ViewGroup) {
			setLayout((ViewGroup) view);

		}
	}

	@SuppressLint("NewApi")
	private void setLayout(ViewGroup viewGroup) {
		int count = viewGroup.getChildCount();
		for (int n = 0; n < count; ++n) {
			View childView = viewGroup.getChildAt(n);
			if (childView instanceof Switch) {
				final Switch switchView = (Switch) childView;

				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
					switchView.setBackgroundResource(R.drawable.toggel_switch);
					switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							System.out.println("my Prefrences===>" + isChecked);
						}
					});
				}
				return;
			} else if (childView instanceof ViewGroup)
				setLayout((ViewGroup) childView);
		}
	}
}