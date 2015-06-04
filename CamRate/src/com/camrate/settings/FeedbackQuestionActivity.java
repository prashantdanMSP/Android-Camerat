package com.camrate.settings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.MyFeed;
import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.adapters.FeedbackQuestionAdapter;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;

public class FeedbackQuestionActivity extends Activity {
	TextView btnSubmit;
	JSONParser jparser = new JSONParser();
	List<HashMap<String, String>> lstMap = new ArrayList<HashMap<String, String>>();
	public FeedbackQuestionAdapter adapter;
	private String value;
	public static final String TAG_QUE_ID = "Question_ID";
	public static final String TAG_QUE_DESC = "Question_Description";
	public static final String TAG_QUE_TYPE = "Question_Type";
	public static final String TAG_QUE_STATUS = "Question_Status";
	public static final String TAG_QUE_DATE = "Question_Date";
	TextView txtlblMain, txtlblque1, txtlblslider_hint, txtlblque2, txtlblCommenthint;
	EditText edtComment;
	RelativeLayout.LayoutParams paramimage;
	RelativeLayout.LayoutParams paramSeekBar;
	LinearLayout linearRating;
	int[] a = new int[] { 0, 25, 50, 75, 100 };
	int rating_rate = 3;
	String rating_Tag = "This is OK";
	ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
	ImageView imgBubble1, imgSubmitRate, imgisRated;
	SeekBar seekBar;
	RelativeLayout relSlider;
	float x = 0;
	float y = 0;
	float center_point = 0;
	String Answer, User_ID;
	ProgressBar pd;
	Function fun;
	checkInternet chkNet;
	Context context;
	Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_question);
		init();
		new GetQuestion().execute();
	}

	private void init() {
		txtlblMain = (TextView) findViewById(R.id.lblInviteFriTitle);
		txtlblque1 = (TextView) findViewById(R.id.lbltxtfeedback_que1);
		txtlblque2 = (TextView) findViewById(R.id.lbltxtfeedback_que2);
		txtlblslider_hint = (TextView) findViewById(R.id.lbltxtfeedback_slider);
		txtlblCommenthint = (TextView) findViewById(R.id.lbltxtfeedback_comment);
		edtComment = (EditText) findViewById(R.id.edt_comments_suggestions);
		btnSubmit = (TextView) findViewById(R.id.btnSubmit_Feedback);
		btnBack = (Button) findViewById(R.id.btnBack);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		imgStar1 = (ImageView) findViewById(R.id.imgStar1);
		imgStar2 = (ImageView) findViewById(R.id.imgStar2);
		imgStar3 = (ImageView) findViewById(R.id.imgStar3);
		imgStar4 = (ImageView) findViewById(R.id.imgStar4);
		imgStar5 = (ImageView) findViewById(R.id.imgStar5);
		imgBubble1 = (ImageView) findViewById(R.id.imgbubble1);
		imgSubmitRate = (ImageView) findViewById(R.id.imgSubmitRated);
		linearRating = (LinearLayout) findViewById(R.id.ratingStar);
		relSlider = (RelativeLayout) findViewById(R.id.relative_slider);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		imgSubmitRate.setVisibility(View.INVISIBLE);
		imgStar1.setId(0);
		imgStar2.setId(1);
		imgStar3.setId(2);
		imgStar4.setId(3);
		imgStar5.setId(4);
		ViewTreeObserver vto = null;
		vto = imgStar3.getViewTreeObserver();
		setBubblleAndSeekBar(imgStar3.getId());
		imgBubble1.setImageResource(R.drawable.bubble3);
		imgStar1.setImageResource(R.drawable.star);
		imgStar2.setImageResource(R.drawable.star);
		imgStar3.setImageResource(R.drawable.star);
		imgStar4.setImageResource(R.drawable.star_unsel);
		imgStar5.setImageResource(R.drawable.star_unsel);

		setImageXY(imgStar3.getLeft(), imgStar3.getMeasuredWidth());
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlblque1.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlblslider_hint.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblque2.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlblCommenthint.setTypeface(SplashScreen.ProxiNova_Regular);
		edtComment.setTypeface(SplashScreen.ProxiNova_Regular);
		btnSubmit.setTypeface(SplashScreen.ProxiNova_Bold);
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "");
		chkNet = new checkInternet(FeedbackQuestionActivity.this);
		fun = new Function(FeedbackQuestionActivity.this);
		context = getParent();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {

				paramSeekBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				paramSeekBar.addRule(RelativeLayout.BELOW, linearRating.getId());
				paramSeekBar.addRule(RelativeLayout.CENTER_HORIZONTAL);
				paramSeekBar.setMargins(0, 10, 0, 0);
				seekBar.setLayoutParams(paramSeekBar);
				// x = imgStar3.getLeft();
				// setImageXY(x, imgStar3.getWidth());
				switch (seekBar.getProgress()) {
				case 0:
					setImageXY(imgStar1.getLeft(), imgStar1.getMeasuredWidth());
					break;
				case 25:
					setImageXY(imgStar2.getLeft(), imgStar2.getMeasuredWidth());
					break;
				case 50:
					setImageXY(imgStar3.getLeft(), imgStar3.getMeasuredWidth());
					break;
				case 75:
					setImageXY(imgStar4.getLeft(), imgStar4.getMeasuredWidth());
					break;
				case 100:
					setImageXY(imgStar5.getLeft(), imgStar5.getMeasuredWidth());
					break;
				default:
					break;
				}
				return true;
			}
		});

		imgStar1.setOnClickListener(mclickListner);
		imgStar2.setOnClickListener(mclickListner);
		imgStar3.setOnClickListener(mclickListner);
		imgStar4.setOnClickListener(mclickListner);
		imgStar5.setOnClickListener(mclickListner);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				setSliderPosition();
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

				seekBar.setProgress(arg1);

				if (arg1 >= 0 && arg1 <= 12) {
					imgBubble1.setImageResource(R.drawable.bubble1);
					imgStar1.setImageResource(R.drawable.star);
					// seekBar.setProgress(2);
					imgStar2.setImageResource(R.drawable.star_unsel);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 1;
					rating_Tag = "No Way";
					x = imgStar1.getLeft();
					setImageXY(x, imgStar1.getWidth());
				} else if (arg1 >= 13 && arg1 <= 37) {
					// seekBar.setProgress(18);
					imgBubble1.setImageResource(R.drawable.bubble2);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star_unsel);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 2;
					rating_Tag = "Not for me";
					x = imgStar2.getLeft();
					setImageXY(x, imgStar2.getWidth());
				} else if (arg1 >= 38 && arg1 <= 63) {
					// seekBar.setProgress(35);
					imgBubble1.setImageResource(R.drawable.bubble3);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star_unsel);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 3;
					rating_Tag = "This is OK";
					x = imgStar3.getLeft();
					setImageXY(x, imgStar3.getWidth());
				} else if (arg1 >= 64 && arg1 <= 89) {
					// seekBar.setProgress(50);
					imgBubble1.setImageResource(R.drawable.bubble4);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star_unsel);
					rating_rate = 4;
					rating_Tag = "Like it!";
					x = imgStar4.getLeft();
					setImageXY(x, imgStar4.getWidth());
				} else if (arg1 >= 90) {
					// seekBar.setProgress(65);
					imgBubble1.setImageResource(R.drawable.bubble5);
					imgStar1.setImageResource(R.drawable.star);
					imgStar2.setImageResource(R.drawable.star);
					imgStar3.setImageResource(R.drawable.star);
					imgStar4.setImageResource(R.drawable.star);
					imgStar5.setImageResource(R.drawable.star);
					rating_rate = 5;
					rating_Tag = "Love it!";
					x = imgStar5.getLeft();
					setImageXY(x, imgStar5.getWidth());
				}
				// textview.setText(arg1 + "");

			}
		});

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (edtComment.getText().toString().equalsIgnoreCase("")) {
					fun.SimpleAlert("CamRate", "Please enter message");
				} else {
					String comm = edtComment.getText().toString();

					HashMap<String, String> mapRate = new HashMap<String, String>();
					mapRate.put("Answer", rating_Tag);
					mapRate.put("Question_ID", lstMap.get(0).get(TAG_QUE_ID));
					mapRate.put("Reason", String.valueOf(rating_rate));
					mapRate.put("Type", lstMap.get(0).get(TAG_QUE_TYPE));
					HashMap<String, String> mapComment = new HashMap<String, String>();
					mapComment.put("Answer", edtComment.getText().toString());
					mapComment.put("Question_ID", lstMap.get(1).get(TAG_QUE_ID));
					mapComment.put("Reason", "");
					mapComment.put("Type", lstMap.get(1).get(TAG_QUE_TYPE));

					JSONObject objFirst = new JSONObject(mapRate);
					JSONObject objSecond = new JSONObject(mapComment);
					Answer = objFirst + "," + objSecond;
					if (chkNet.isNetworkConnected()) {
						new submitFeedBack().execute("");
					} else {
						Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				}

			}
		});
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private class GetQuestion extends AsyncTask<Void, Void, List<HashMap<String, String>>> {
		@Override
		protected List<HashMap<String, String>> doInBackground(Void... params) {
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=FeedbackQuestion";
			URI encoded_url = cc.Geturl(url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());

			try {
				JSONArray json1 = json.getJSONArray("questions");
				for (int s = 0; s < json1.length(); s++) {
					JSONObject pubdetail = json1.getJSONObject(s);

					HashMap<String, String> map = new HashMap<String, String>();
					map.put(TAG_QUE_ID, pubdetail.getString(TAG_QUE_ID));
					map.put(TAG_QUE_DESC, pubdetail.getString(TAG_QUE_DESC));
					map.put(TAG_QUE_TYPE, pubdetail.getString(TAG_QUE_TYPE));
					map.put(TAG_QUE_STATUS, pubdetail.getString(TAG_QUE_STATUS));
					map.put(TAG_QUE_DATE, pubdetail.getString(TAG_QUE_DATE));

					lstMap.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return lstMap;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {
			super.onPostExecute(result);
			System.out.println("FeedBck===>" + result);
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setImageXY(float X, int width) {

		// Log.d("X", ":>" + X);
		int tempwidth = (width);
		// Log.d("Width", ":>" + tempwidth);
		center_point = X;
		center_point = center_point - (imgBubble1.getWidth() / 2) + (tempwidth / 2);
		imgBubble1.setX(center_point);

		imgBubble1.invalidate();
	}

	public void setSliderPosition() {

		int diff = 0;
		int finalValue = 0;
		int valueSeek = seekBar.getProgress();
		int storage = 100;
		for (int i = 0; i < a.length; i++) {
			int arrVal = a[i];
			diff = arrVal - valueSeek;
			if (diff < 0) {
				diff = -diff;
			}
			if (diff < storage) {
				storage = diff;
				finalValue = arrVal;
			}

		}

		seekBar.setProgress(finalValue);
	}

	public void setBubblleAndSeekBar(int id) {
		int tag = id * 25;
		seekBar.setProgress(tag);

	}

	public OnClickListener mclickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case 0:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star_unsel);
				imgStar3.setImageResource(R.drawable.star_unsel);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar1.getId());
				rating_rate = 1;
				rating_Tag = "No Way";
				x = imgStar1.getLeft();
				setImageXY(x, imgStar1.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble1);
				break;
			case 1:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star_unsel);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar2.getId());
				rating_rate = 2;
				rating_Tag = "Not for me";
				x = imgStar2.getLeft();
				setImageXY(x, imgStar2.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble2);
				break;
			case 2:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star_unsel);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar3.getId());
				rating_rate = 3;
				rating_Tag = "This is OK";
				x = imgStar3.getLeft();
				setImageXY(x, imgStar3.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble3);

				break;
			case 3:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star);
				imgStar5.setImageResource(R.drawable.star_unsel);
				setBubblleAndSeekBar(imgStar4.getId());
				rating_rate = 4;
				rating_Tag = "Like it!";
				x = imgStar4.getLeft();
				setImageXY(x, imgStar4.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble4);
				break;

			case 4:
				imgStar1.setImageResource(R.drawable.star);
				imgStar2.setImageResource(R.drawable.star);
				imgStar3.setImageResource(R.drawable.star);
				imgStar4.setImageResource(R.drawable.star);
				imgStar5.setImageResource(R.drawable.star);
				setBubblleAndSeekBar(imgStar5.getId());
				rating_rate = 5;
				rating_Tag = "Love it!";
				x = imgStar5.getLeft();
				setImageXY(x, imgStar5.getWidth());
				imgBubble1.setImageResource(R.drawable.bubble5);
				break;

			}
		}
	};

	class submitFeedBack extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			// pd = ProgressDialog.show(Login.this, "","Validating User",true);
			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String json = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=FeedbackAnswerQue";
			System.out.println(url);
			try {
				InputStream is = null;
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("Answer_Data", new StringBody(Answer));
				entity.addPart("Answer_UserID", new StringBody(User_ID));
				httpPost.setEntity(entity);

				// new
				HttpParams httpParameters = httpPost.getParams();
				// Set the timeout in milliseconds until a connection is
				// established.
				int timeoutConnection = 10000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT)
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 10000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				// new
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					json = sb.toString();
					// Log.d("TAG", "IN BACKGROUND" + json);
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}

			} catch (Throwable t) {
				t.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			JSONObject objPost;
			try {
				objPost = new JSONObject(status);

				if (objPost.getString("result").equalsIgnoreCase("1")) {
					SimpleAlert1("CamRate", "Feedback sent successfully.\nThank You!");

				} else {
					Toast.makeText(FeedbackQuestionActivity.this, "Please try again.", Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void SimpleAlert1(String t, String b) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
