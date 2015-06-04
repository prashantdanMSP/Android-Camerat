package com.camrate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.global.Constant;
import com.camrate.global.Function;

public class Register1 extends Activity {

	EditText edt_City;
	Spinner sp_Country, sp_Gender;
	// Spinner sp_Age;
	Button btnBirthDate;
	Button btnDone;
	ArrayList<HashMap<String, String>> lst_countries;
	String[] arrGender = { "Your Gender", "Male", "Female" };
	String[] arrAge = { "Select", "10-16 yrs", "16-20 yrs", "20-30 yrs", "30-40 yrs", "40+ yrs" };

	String UserName, UserEmail, UserPass, UserImgPic;
	String Response;
	int Gender_ID, Age_ID, Age, Country_ID;
	ImageView imgProfile;
	String tempAge;
	TextView txt_UserName, txt_UserEmail,txtlbl_country,txtlbl_country_opt,txtlbl_city,txtlbl_city_opt,txtlbl_gender,txtlbl_gender_opt,txtlbl_bday,txtlbl_bday_opt;
	Constant con = new Constant();
	private int mYear, maxYear, minYear;
	private int mMonth, maxMonth, minMonth;
	private int mDay, maxDay, minDay;
	Function fun;
	String Date="";
	ProgressBar pd;

	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register1);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();
		//setSpinnerHeight();
	}

	private void init() {
		// TODO Auto-generated method stub
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Sign Up");
		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.VISIBLE);
		next.setText("Part 2 of 2");
		next.setTextColor(getResources().getColor(R.color.lbl_color_lefttitle));
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		next.setTypeface(SplashScreen.ProxiNova_Regular,Typeface.BOLD);
		next.setTypeface(SplashScreen.ProxiNova_Regular);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		fun = new Function(Register1.this);
		sp_Country = (Spinner) findViewById(R.id.sp_country);
		edt_City = (EditText) findViewById(R.id.sp_city);
		sp_Gender = (Spinner) findViewById(R.id.sp_gender);
		// sp_Age=(Spinner)findViewById(R.id.sp_age);
		pd=(ProgressBar)findViewById(R.id.progressBar1);
		btnBirthDate = (Button) findViewById(R.id.btnbday);
		btnDone = (Button) findViewById(R.id.btn_Done);
		imgProfile = (ImageView) findViewById(R.id.imgProfilepic);
		txt_UserName = (TextView) findViewById(R.id.txt_username);
		txt_UserEmail = (TextView) findViewById(R.id.txt_useremail);
		txtlbl_country=(TextView)findViewById(R.id.txtlbl_country);
		txtlbl_country_opt=(TextView)findViewById(R.id.txtlbl_country_req);
		txtlbl_city=(TextView)findViewById(R.id.txtlbl_city);
		txtlbl_city_opt=(TextView)findViewById(R.id.txtlbl_city_req);
		txtlbl_gender=(TextView)findViewById(R.id.txtlbl_gender);
		txtlbl_gender_opt=(TextView)findViewById(R.id.txtlbl_gender_req);
		txtlbl_bday=(TextView)findViewById(R.id.txtlbl_bdate);
		txtlbl_bday_opt=(TextView)findViewById(R.id.txtlbl_bdate_req);
		System.out.println("ttt --->");
		txtlbl_country.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_country_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_city.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_city_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_gender.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_gender_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_bday.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_bday_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		btnDone.setTypeface(SplashScreen.ProxiNova_Bold);
		Intent intent = getIntent();

		UserImgPic = intent.getStringExtra("imgProfilepic");
		UserEmail = intent.getStringExtra("user_Email");
		UserPass = intent.getStringExtra("user_Pass");
		UserName = intent.getStringExtra("user_Name");
		System.out.println("UserImgPic--->" + UserImgPic);

		txt_UserEmail.setText(UserEmail);
		txt_UserName.setText(UserName);
		txt_UserName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		txt_UserEmail.setTypeface(SplashScreen.ProxiNova_Regular);
		edt_City.setTypeface(SplashScreen.ProxiNova_Regular);
		btnBirthDate.setTypeface(SplashScreen.ProxiNova_Regular);
		System.out.println("UserEmail--->" + UserEmail);
		System.out.println("UserName--->" + UserName);
		if (UserImgPic.length() > 0) {
			File imgFile = new File(UserImgPic);
			if (imgFile.exists()) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				imgProfile.setImageBitmap(myBitmap);

			}
		}

		/*** Getting Countries value from database */

		DataBaseAdapter mDbHelper = new DataBaseAdapter(getApplicationContext());
		try {
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		lst_countries = mDbHelper.getAllCountry();

		SimpleAdapter adpt = new SimpleAdapter(getApplicationContext(), lst_countries, R.layout.sp_item, new String[] { "Country_Name", "Country_ID" }, new int[] { R.id.text1, R.id.text2 });
		adpt.notifyDataSetChanged();
		//adpt.setDropDownViewResource(R.layout.sp_item);
		sp_Country.setAdapter(adpt);
		sp_Country.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Country_ID = (Integer.parseInt(lst_countries.get(position).get("Country_ID")));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		System.out.println("ttt --->");
		/*** Getting Gender values */
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.sp_item, R.id.text1, arrGender);
		adapter.notifyDataSetChanged();
		// adapter.setDropDownViewResource(R.layout.sp_item);
		sp_Gender.setAdapter(adapter);
		sp_Gender.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				Gender_ID = adapter.getPosition(parent.getItemAtPosition(position).toString());
				System.out.println("Gender_ID index--->" + Age_ID);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 9; i <= 100; i++) {
			if (i == 9) {
				list.add("Select");
			} else {
				list.add(String.valueOf(i));
			}

		}
		btnBirthDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
		setTodayDate();

		/*	*//*** Getting Age values */
		/*
		 * final ArrayAdapter<String> AgeAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.sp_item,R.id.text1,list); adapter.notifyDataSetChanged(); //adapter.setDropDownViewResource(R.layout.sp_item); sp_Age.setAdapter(AgeAdapter); sp_Age.setOnItemSelectedListener(new
		 * OnItemSelectedListener() {
		 * 
		 * @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // TODO Auto-generated method stub //Age_ID=parent.getItemAtPosition(position).toString(); Age_ID=AgeAdapter.getPosition(parent.getItemAtPosition(position).toString());
		 * System.out.println("Age_ID index--->" +Age_ID); tempAge=parent.getItemAtPosition(position).toString(); System.out.println("Age_ID index--->" +tempAge); if(!tempAge.equalsIgnoreCase("Select")) { for(int s=9;s<=100;s++) { if(Integer.parseInt(tempAge)>=10 && Integer.parseInt(tempAge)<=16) {
		 * Age_ID=1; } else if(Integer.parseInt(tempAge)>=16 && Integer.parseInt(tempAge)<=20) { Age_ID=2; } else if(Integer.parseInt(tempAge)>=20 && Integer.parseInt(tempAge)<=30) { Age_ID=3; } else if(Integer.parseInt(tempAge)>=30 && Integer.parseInt(tempAge)<=40) { Age_ID=4; } else { Age_ID=5; }
		 * } //Age_ID=AgeAdapter.getPosition(parent.getItemAtPosition(position).toString()); System.out.println("Age_ID index--->" +Age_ID); } }
		 * 
		 * @Override public void onNothingSelected(AdapterView<?> parent) { // TODO Auto-generated method stub
		 * 
		 * } });
		 */

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*if (isNetworkConnected()) {
					new RegisterUser().execute("");
				} else {
					Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}*/
				
				
				Intent intent=new Intent(Register1.this,TermsMain.class);
				intent.putExtra("User_Name", UserName);
				intent.putExtra("User_Password", UserPass);
				intent.putExtra("User_Email", UserEmail);
				intent.putExtra("countryID", String.valueOf(Country_ID));
				intent.putExtra("cityName", edt_City.getText().toString());
				intent.putExtra("gender", String.valueOf(Gender_ID));
				intent.putExtra("Birthdate", Date);
				intent.putExtra("UserImgPic", UserImgPic);
				startActivity(intent);
				overridePendingTransition(0, 0);

			}
		});
		
//setSpinnerHeight();
	}
	private void setSpinnerHeight() {
		  ViewTreeObserver observer = edt_City.getViewTreeObserver();
		  observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		   @Override
		   public void onGlobalLayout() {
		    try {

		     LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, edt_City.getHeight());
		     params.gravity=Gravity.CENTER_VERTICAL;
		     sp_Country.setLayoutParams(params);
		     sp_Gender.setLayoutParams(params);
		    } catch (Exception e) {

		    }
		   }
		  });

		 }
	public void setTodayDate() {
		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		maxYear = mYear - 10;
		maxMonth = mMonth;
		maxDay = mDay;

		minYear = mYear - 110;
		minMonth = mMonth;
		minDay = mDay;
		// display the current date (this method is below)
		// updateDisplay();
		   updateDisplay(maxYear, maxMonth, maxDay);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear-10, mMonth, mDay);
		}
		return null;
	}

	// updates the date we display in the TextView
	private void updateDisplay() {
		btnBirthDate.setText(new StringBuilder()
		// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
	}

	private void updateDisplay(int year, int month, int day) {
		// TODO Auto-generated method stub
		Date = new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ").toString();
		System.out.println("BirthDate==>" + Date);
		 btnBirthDate.setText(fun.getBirthDate(String.valueOf(month+1),String.valueOf(day),String.valueOf(year)));
		//btnBirthDate.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ").toString());
		// Date=new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ").toString();
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			/*
			 * mYear = year; mMonth = monthOfYear; mDay = dayOfMonth; updateDisplay();
			 */
			
			if (year > maxYear || monthOfYear > maxMonth && year == maxYear || dayOfMonth > maxDay && year == maxYear && monthOfYear == maxMonth) {

				view.updateDate(maxYear, maxMonth, maxDay);
				updateDisplay(maxYear, maxMonth, maxDay);

			} else if (year < minYear || monthOfYear < minMonth && year == minYear || dayOfMonth < minDay && year == minYear && monthOfYear == minMonth) {

				view.updateDate(minYear, minMonth, minDay);
				updateDisplay(minYear, minMonth, minDay);
			} else {

				view.updateDate(year, monthOfYear, dayOfMonth);
				updateDisplay(year, monthOfYear, dayOfMonth);
			}

		}
	};

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	class RegisterUser extends AsyncTask<String, Void, String> {
		

		@Override
		protected void onPreExecute() {
			// Toast.makeText(MainActivity.this,"Uploading Offer",Toast.LENGTH_SHORT).show();
			//pd = ProgressDialog.show(Register1.this, "", "Loading Wait..", true);
			pd.setVisibility(View.VISIBLE);
			// pd.setCancelable(true);
			// pd.setCanceledOnTouchOutside(true);
			// pd.setCanceledOnTouchOutside(true);
		}

		@Override
		protected String doInBackground(String... params) {
			String Result = null;
			String Result1 = null;
			// TODO Auto-generated method stub

			try {
				HttpClient httpclient = new DefaultHttpClient();

				// HttpPost httppost = new HttpPost("http://202.131.107.108:8081/Lovehate/main/frmlovehateApi.php");
				HttpPost httppost = new HttpPost(con.GetBaseUrl()+"api=Register");
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				// api=Register&User_Name=%@&User_Password=%@&User_Email=%@&countryID=%d&cityName=%@&gender=%d&age=%d
				File f = null;
				FileBody fo = null;

				f = new File(UserImgPic);
				fo = new FileBody(f);

				// code for send Text using post method
				//reqEntity.addPart("api", new StringBody("Register"));
				Log.i("Parameter", "tText Parameter added");
				System.out.println("User_Name" + UserName);
				System.out.println("User_Password ::> " + UserPass);
				System.out.println("User_Email ::> " + UserEmail);
				System.out.println("countryID ::> " + Country_ID);
				System.out.println("cityName ::> " + edt_City.getText().toString());
				System.out.println("gender ::> " + Gender_ID);
				System.out.println("BirthDate ::> " + Date);

				reqEntity.addPart("User_Name", new StringBody(UserName));
				reqEntity.addPart("User_Password", new StringBody(UserPass));
				reqEntity.addPart("User_Email", new StringBody(UserEmail));
				reqEntity.addPart("countryID", new StringBody(String.valueOf(Country_ID)));
				reqEntity.addPart("cityName", new StringBody(edt_City.getText().toString()));
				reqEntity.addPart("gender", new StringBody(String.valueOf(Gender_ID)));
				reqEntity.addPart("Birthdate", new StringBody(Date));
				// reqEntity.addPart("age_range",new StringBody(String.valueOf(Age_ID)));
				if (UserImgPic.length()>0) {
					reqEntity.addPart("User_Imagepath", fo);	
				}
				
				System.out.println("else fo" + fo);
				Log.i("Parameter", "image added Parameter added");
				// http://camrate.com/main/frmlovehateApi.php?api=Register&User_Name=harshidatest&User_Password=harshida&User_Email=harshidatest@gmail.com&countryID=105&cityName=ahmedabad&gender=2&Birthdate=05-06-1990
				httppost.setEntity(reqEntity);

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				Log.e("test", "SC:" + response.getStatusLine().getStatusCode());

				// HttpEntity resEntity = response.getEntity();

				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String sResponse;
				StringBuilder s = new StringBuilder();

				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}
				Response = s.toString();
				Log.e("test", "Response: " + s);
			} catch (ClientProtocolException ex) {
			} catch (IOException e) {
			}

			JSONObject jsonobject;
			try {
				jsonobject = new JSONObject(Response);
				Result = jsonobject.getString("result");
				if (Result.equalsIgnoreCase("1")) {
					JSONObject user = jsonobject.getJSONObject("user");
					String UserID = user.getString("User_ID");
					String User_Image = user.getString("User_Imagepath");
					String User_Email = user.getString("User_Email");
					/*
					 * SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext()); SharedPreferences.Editor edit1 = prefs.edit(); edit1.putString("UserID", UserID); edit1.putString("User_Image", User_Image); edit1.putString("User_Email", User_Email); edit1.commit();
					 */
				}
			} catch (JSONException e) {
				Log.d("the xceptions ", "Xcep in posting status messages are : " + e.getMessage());
			}

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {

			if (status.equals("1")) {
				pd.setVisibility(View.INVISIBLE);
				SimpleAlert("CamRate", "Register Successfully");

			}
			if (status.equals("0")) {
				// pd.dismiss();
				Toast.makeText(getApplicationContext(), "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				// pd.dismiss();
				Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}

	// show alert
	public void SimpleAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Register1.this, AfterSplashScreen.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
