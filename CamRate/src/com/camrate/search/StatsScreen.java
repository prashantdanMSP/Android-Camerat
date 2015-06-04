package com.camrate.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;

public class StatsScreen extends Activity {
	JSONParser parser = new JSONParser();
	checkInternet chkNet;
	Function fun;
	Constant con = new Constant();
	String strTag = "", strSearchTerms = "";
	ArrayList<HashMap<String, String>> arrStats;
	ProgressBar pd;
	String User_ID;
	ProgressBar progLove, progLike, progDis, progHate, progWhatever;
	TextView txtLoveCount, txtLikeCount, txtDisCount, txtHateCount, txtWhateverCount, txtAverage_Rate, txtLoveAvg, txtLikeAvg, txtDisAvg, txtHateAvg, txtWhateverAvg;
	ImageView imgLove, imgLike, imgWhatever, imgDis, imgHate, imgAverageRate;
	int LovePer = 0, LikePer = 0, DislikePer = 0, WhateverPer = 0, HatePer = 0;
	TextView btnOverView, btnTimeLine, btnDemographics, btnRelated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		init();
	}

	public void init() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		Button Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.VISIBLE);
		Button btnStats = (Button) findViewById(R.id.button1);
		btnStats.setVisibility(View.INVISIBLE);
		imgAverageRate = (ImageView) findViewById(R.id.imguserrate);
		txtAverage_Rate = (TextView) findViewById(R.id.tvRateName);
		imgLove = (ImageView) findViewById(R.id.imglove);
		imgLike = (ImageView) findViewById(R.id.imglike);
		imgWhatever = (ImageView) findViewById(R.id.imgwhatever);
		imgDis = (ImageView) findViewById(R.id.imgdislike);
		imgHate = (ImageView) findViewById(R.id.imghate);
		txtLoveCount = (TextView) findViewById(R.id.txtcountlove);
		txtLikeCount = (TextView) findViewById(R.id.txtcountlike);
		txtWhateverCount = (TextView) findViewById(R.id.txtcountwhatever);
		txtHateCount = (TextView) findViewById(R.id.txtcounthate);
		txtDisCount = (TextView) findViewById(R.id.txtcountdislike);
		txtLoveAvg = (TextView) findViewById(R.id.txtloveavg);
		txtLikeAvg = (TextView) findViewById(R.id.txtlikeavg);
		txtWhateverAvg = (TextView) findViewById(R.id.txtwhateveravg);
		txtDisAvg = (TextView) findViewById(R.id.txtdislikeavg);
		txtHateAvg = (TextView) findViewById(R.id.txthateavg);
		progLove = (ProgressBar) findViewById(R.id.progressbarcal);
		progLike = (ProgressBar) findViewById(R.id.progressbarcallike);
		progWhatever = (ProgressBar) findViewById(R.id.progressbarcalwhatever);
		progDis = (ProgressBar) findViewById(R.id.progressbarcaldislike);
		progHate = (ProgressBar) findViewById(R.id.progressbarcalhate);

		pd = (ProgressBar) findViewById(R.id.progressBar1);
		btnOverView = (TextView) findViewById(R.id.btnoverview);
		btnTimeLine = (TextView) findViewById(R.id.btnTimeline);
		btnDemographics = (TextView) findViewById(R.id.btnDemograohics);
		btnRelated = (TextView) findViewById(R.id.btnRelated);
		chkNet = new checkInternet(StatsScreen.this);
		fun = new Function(getParent());
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		SharedPreferences prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		User_ID = prefs.getString("UserID", "");
		Intent intent = getIntent();
		strTag = intent.getStringExtra("Key_Tag");
		strSearchTerms = intent.getStringExtra("Key_search");

		if (chkNet.isNetworkConnected()) {
			new getStatsData().execute();
		} else {
			Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
		tv14.setText(strTag);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		btnDemographics.setTypeface(SplashScreen.ProxiNova_Bold);
		btnOverView.setTypeface(SplashScreen.ProxiNova_Bold);
		btnRelated.setTypeface(SplashScreen.ProxiNova_Bold);
		btnTimeLine.setTypeface(SplashScreen.ProxiNova_Bold);
		btnTimeLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fun.SimpleAlert("CamRate", "Coming Soon");
			}
		});
		btnRelated.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fun.SimpleAlert("CamRate", "Coming Soon");
			}
		});
		btnDemographics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fun.SimpleAlert("CamRate", "Coming Soon");
			}
		});
	}

	public class getStatsData extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = con.GetBaseUrl() + "api=SearchStats&SearchTerm=" + strSearchTerms + "&Tags=" + strTag + "&User_ID=" + User_ID;
			Log.d("Tag", "url " + url);
			URI url1 = con.Geturl(url);
			String EnodeURl = con.GetUrl(url1.toString());

			System.out.println("Encode==>" + EnodeURl);
			arrStats = new ArrayList<HashMap<String, String>>();
			try {
				String statsstring = parser.getStringFromUrl(EnodeURl.toString());
				JSONObject jsonstats = new JSONObject(statsstring);

				String Tag = jsonstats.getString("Tag");
				String TotalRateCount = jsonstats.getString("TotalRateCount");
				String AverageRateCount = jsonstats.getString("AverageRateCount");
				String love_count = jsonstats.getString("love_count");
				String like_count = jsonstats.getString("like_count");
				String whatever_count = jsonstats.getString("whatever_count");
				String dislike_count = jsonstats.getString("dislike_count");
				String hate_count = jsonstats.getString("hate_count");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Tag", Tag);
				map.put("TotalRateCount", TotalRateCount);
				map.put("AverageRateCount", AverageRateCount);
				map.put("love_count", love_count);
				map.put("like_count", like_count);
				map.put("whatever_count", whatever_count);
				map.put("dislike_count", dislike_count);
				map.put("hate_count", hate_count);
				arrStats.add(map);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return arrStats;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);
			setAvgStarRate(result.get(0).get("AverageRateCount"));
			String TotalRate = result.get(0).get("TotalRateCount");
			if (TotalRate.equalsIgnoreCase("1")) {
				txtAverage_Rate.setText(TotalRate + " Rate");
			} else {
				txtAverage_Rate.setText(TotalRate + " Rates");
			}

			txtAverage_Rate.setTypeface(SplashScreen.ProxiNova_Regular);
			RateCalculation(Integer.parseInt(result.get(0).get("love_count")), Integer.parseInt(result.get(0).get("like_count")), Integer.parseInt(result.get(0).get("whatever_count")), Integer.parseInt(result.get(0).get("dislike_count")), Integer.parseInt(result.get(0).get("hate_count")),
					Integer.parseInt(result.get(0).get("TotalRateCount")));

			setProgandRateCount(result.get(0).get("love_count"), result.get(0).get("like_count"), result.get(0).get("whatever_count"), result.get(0).get("dislike_count"), result.get(0).get("hate_count"));
		}
	}

	public void setAvgStarRate(String string) {
		// TODO Auto-generated method stub
		int ratcount = Integer.valueOf(string);
		switch (ratcount) {
		case 1:
			imgAverageRate.setImageResource(R.drawable.one_star);
			break;
		case 2:
			imgAverageRate.setImageResource(R.drawable.two_star);
			break;
		case 3:
			imgAverageRate.setImageResource(R.drawable.three_star);
			break;
		case 4:
			imgAverageRate.setImageResource(R.drawable.four_star);
			break;
		case 5:
			imgAverageRate.setImageResource(R.drawable.five_star);
			break;

		default:
			break;
		}
	}

	public void RateCalculation(int love, int like, int whatever, int dislike, int hate, int totalcount) {

		if (totalcount == 0) {
			totalcount = 1;
		}
		LovePer = (((love * 100) / totalcount));
		LikePer = (((like * 100) / totalcount));
		WhateverPer = (((whatever * 100) / totalcount));
		DislikePer = (((dislike * 100) / totalcount));
		HatePer = (((hate * 100) / totalcount));

	}

	public void setProgandRateCount(String love_count, String like_count, String Whatever_count, String Dislike_count, String Hate_count) {
		// Love Count
		txtLoveCount.setText("(" + love_count + ")");
		txtLoveCount.setTypeface(SplashScreen.ProxiNova_Regular);
		String love = String.valueOf(LovePer);
		progLove.setProgress(Integer.parseInt(love));
		txtLoveAvg.setText(love + "%");
		txtLoveAvg.setTypeface(SplashScreen.ProxiNova_Regular);

		// Like Count
		txtLikeCount.setText("(" + like_count + ")");
		txtLikeCount.setTypeface(SplashScreen.ProxiNova_Regular);
		String like = String.valueOf(LikePer);
		progLike.setProgress(Integer.parseInt(like));
		txtLikeAvg.setText(like + "%");
		txtLikeAvg.setTypeface(SplashScreen.ProxiNova_Regular);

		// Whatever Count
		txtWhateverCount.setText("(" + Whatever_count + ")");
		txtWhateverCount.setTypeface(SplashScreen.ProxiNova_Regular);
		String Whatever = String.valueOf(WhateverPer);
		progWhatever.setProgress(Integer.parseInt(Whatever));
		txtWhateverAvg.setText(Whatever + "%");
		txtWhateverAvg.setTypeface(SplashScreen.ProxiNova_Regular);

		// DisLike Count
		txtDisCount.setText("(" + Dislike_count + ")");
		txtDisCount.setTypeface(SplashScreen.ProxiNova_Regular);
		String DisLike = String.valueOf(DislikePer);
		progDis.setProgress(Integer.parseInt(DisLike));
		txtDisAvg.setText(DisLike + "%");
		txtDisAvg.setTypeface(SplashScreen.ProxiNova_Regular);

		// Hate Count
		txtHateCount.setText("(" + Hate_count + ")");
		txtHateCount.setTypeface(SplashScreen.ProxiNova_Regular);
		String Hate = String.valueOf(HatePer);
		progHate.setProgress(Integer.parseInt(Hate));
		txtHateAvg.setText(Hate + "%");
		txtHateAvg.setTypeface(SplashScreen.ProxiNova_Regular);

	}
}
