package com.camrate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.VideoView;

public class VideoActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnCompletionListener {
	private static final int SELECT_VIDEO = 0;
	private Uri url;
	VideoView video;
	Button btnCancel, btnChoose;
	CheckBox chkPlay;
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		video = (VideoView) findViewById(R.id.seeVideo);
		btnCancel = (Button) findViewById(R.id.btnVideoCancel);
		btnChoose = (Button) findViewById(R.id.btnVideoChoose);
		chkPlay = (CheckBox) findViewById(R.id.chkVideoPlayPause);

		Bundle bundle = getIntent().getExtras();
		String strUrl = bundle.getString("url");
		url = Uri.parse(strUrl);

		video.setVideoURI(url);
		video.seekTo(1000);
		video.setOnCompletionListener(this);

		chkPlay.setOnCheckedChangeListener(this);
		btnCancel.setOnClickListener(this);
		btnChoose.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btnVideoCancel) {
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					SELECT_VIDEO);
		} else {

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_VIDEO:
			if (resultCode == RESULT_OK) {
				url = data.getData();
				video.setVideoURI(url);
				chkPlay.setChecked(true);
				video.seekTo(1000);
			}
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		if (!isChecked) {
			video.start();
		} else {
			video.pause();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		chkPlay.setChecked(true);
		video.seekTo(1000);
	}

}
