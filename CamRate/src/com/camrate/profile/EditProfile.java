package com.camrate.profile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.camrate.R;
import com.camrate.SecondCameraTab;
import com.camrate.SplashScreen;
import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.global.Constant;
import com.camrate.global.Function;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EditProfile extends Activity {

	ArrayList<HashMap<String, String>> UserProfileData = new ArrayList<HashMap<String, String>>();
	ProgressBar pd;
	Button btnProfileInfo, btnAboutInfo;
	Fragment fragment = null;
	JSONParser parser = new JSONParser();
	JSONArray objUSerDetail;
	String UserParse;
	Constant c = new Constant();
	String UserID;
	checkInternet chknet;

	ToggleButton btnPrivacy;
	TextView txtvalueprivacy, txtUserName;
	EditText edtname, edtsurname, edtdesc;
	TextView edtemail;
	ImageView imgProfilePic;
	String User_Name, User_LastName, User_Desc, User_Gender, User_Age, Country_Name, User_ImagePath;
	String User_ID, FirtName, PrivateUser, User_IsPrivate;
	String Result = null;
	Button btneditPhoto;
	Constant cc = new Constant();
	JSONParser jparser = new JSONParser();
	private final int SELECT_FILE = 1;
	private final static int REQUEST_CAMERA = 2;
	public static File mediaFile;;
	private static Uri fileUri; // file url to store image/video
	private static final String IMAGE_DIRECTORY_NAME = "CamRate";
	private static String imageFilePath = "";
	File photos;
	byte[] imgbytearray;
	public static Bitmap b = null;
	String ImgName, ImageName;
	boolean hasDrawable;
	Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	Constant con = new Constant();
	JSONObject profile;
	String fbID, Name, Email;
	String facebook_Image;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	Context context;
	EditText edt_City;
	Spinner sp_Country, sp_Age, sp_Gender;
	ImageView imgUserProfPic;
	ArrayList<HashMap<String, String>> lst_countries;
	String[] arrGender = { "Your Gender", "Male", "Female" };
	/*
	 * String[] arrAge = { "Select", "10-16 yrs", "16-20 yrs", "20-30 yrs", "30-40 yrs", "40+ yrs" };
	 */
	String[] arrAge = new String[100];
	int Gender_ID, Age_ID, Country_ID;
	String tempAge;
	String User_City, User_Country, User_Email, User_BirthDate;
	SimpleAdapter adpt;
	ArrayAdapter<String> adapter;
	ArrayAdapter<String> AgeAdapter;
	private int mYear, maxYear, minYear;
	private int mMonth, maxMonth, minMonth;
	private int mDay, maxDay, minDay;
	Function fun;
	String Date = "";
	static final int DATE_DIALOG_ID = 0;
	Button btnBDay;
	DatePickerDialog datepicker;
	String Response = "";
	TextView txtlbl_country, txtlbl_country_opt, txtlbl_city, txtlbl_city_opt, txtlbl_gender, txtlbl_gender_opt, txtlbl_bday, txtlbl_bday_opt;
	ScrollView sc_Profile, sc_About;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editprofilemain);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = getParent();

		btnProfileInfo = (Button) findViewById(R.id.chkProfState);
		btnAboutInfo = (Button) findViewById(R.id.chkAboutState);
		chknet = new checkInternet(context);
		fun = new Function(getParent());
		init();

	}

	public void init() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Edit Profile");
		Button Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.VISIBLE);
		Button btnSave = (Button) findViewById(R.id.button1);
		btnSave.setText("Save");
		btnSave.setVisibility(View.VISIBLE);
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		txtUserName = (TextView) findViewById(R.id.txteditusername);
		edtname = (EditText) findViewById(R.id.editprof_edt_uname);
		edtsurname = (EditText) findViewById(R.id.editprof_edt_surname);
		edtdesc = (EditText) findViewById(R.id.editprof_edt_desc);
		btnPrivacy = (ToggleButton) findViewById(R.id.btnprivacyonoff);
		txtvalueprivacy = (TextView) findViewById(R.id.txtvalueprivacy);
		imgProfilePic = (ImageView) findViewById(R.id.imgeditprofpic);
		btneditPhoto = (Button) findViewById(R.id.btneditphoto);
		edtemail = (TextView) findViewById(R.id.txtedituseremail);
		imgUserProfPic = (ImageView) findViewById(R.id.imgeditprofpic);
		txtUserName = (TextView) findViewById(R.id.txteditusername);
		sp_Country = (Spinner) findViewById(R.id.sp_country);
		edt_City = (EditText) findViewById(R.id.sp_city);
		sp_Gender = (Spinner) findViewById(R.id.sp_gender);
		// sp_Age = (Spinner) view.findViewById(R.id.sp_age);
		txtlbl_country = (TextView) findViewById(R.id.txtlbl_country);
		txtlbl_country_opt = (TextView) findViewById(R.id.txtlbl_country_req);
		txtlbl_city = (TextView) findViewById(R.id.txtlbl_city);
		txtlbl_city_opt = (TextView) findViewById(R.id.txtlbl_city_req);
		txtlbl_gender = (TextView) findViewById(R.id.txtlbl_gender);
		txtlbl_gender_opt = (TextView) findViewById(R.id.txtlbl_gender_req);
		txtlbl_bday = (TextView) findViewById(R.id.txtlbl_bdate);
		txtlbl_bday_opt = (TextView) findViewById(R.id.txtlbl_bdate_req);
		btnBDay = (Button) findViewById(R.id.btnbday);
		txtlbl_country.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_country_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_city.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_city_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_gender.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_gender_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_bday.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_bday_opt.setTypeface(SplashScreen.ProxiNova_Regular);
		sc_Profile = (ScrollView) findViewById(R.id.scrollView1Profile);
		sc_About = (ScrollView) findViewById(R.id.scrollView1About);
		prefs = getSharedPreferences("User_Info", MODE_PRIVATE);
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		btnSave.setTypeface(SplashScreen.ProxiNova_Bold);
		User_ID = prefs.getString("UserID", "");
		FirtName = prefs.getString("User_FirstName", "");
		User_IsPrivate = prefs.getString("User_IsPrivate", "");
		User_Name = prefs.getString("User_Name", "");
		User_LastName = prefs.getString("User_LastName", "");
		User_Desc = prefs.getString("User_Desc", "");
		User_Email = prefs.getString("User_Email", "");
		User_Gender = prefs.getString("User_Gender", "");
		User_Age = prefs.getString("User_Age", "");
		User_ImagePath = prefs.getString("User_Imagepath", "");
		User_BirthDate = prefs.getString("User_Birthdate", "1-1-2015");
		User_City = prefs.getString("User_City", "");
		User_Country = prefs.getString("User_CountryID", "");
		System.out.println("UserID--->" + User_ID);
		facebook = new Facebook("480839781954688");

		txtUserName.setText(User_Name);
		edtname.setText(FirtName);
		edtsurname.setText(User_LastName);
		edtdesc.setText(User_Desc);
		edtemail.setText(User_Email);
		edtname.setTypeface(SplashScreen.ProxiNova_Regular);
		edtsurname.setTypeface(SplashScreen.ProxiNova_Regular);
		edtdesc.setTypeface(SplashScreen.ProxiNova_Regular);
		edtemail.setTypeface(SplashScreen.ProxiNova_Regular);
		btnProfileInfo.setTypeface(SplashScreen.ProxiNova_Bold);
		btnAboutInfo.setTypeface(SplashScreen.ProxiNova_Bold);
		imageLoader.displayImage(User_ImagePath, imgProfilePic, options);

		if (prefs.getString("User_IsPrivate", "0").equalsIgnoreCase("1")) {
			btnPrivacy.setChecked(true);
		} else {
			btnPrivacy.setChecked(false);
		}
		btnPrivacy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					txtvalueprivacy.setText("Privacy On");
					PrivateUser = "1";
					// txtvalueprivacy.setTextColor(Color.parseColor("#00CD66"));
					txtvalueprivacy.setTextColor(R.color.Theme_Color);
				} else {
					PrivateUser = "0";
					txtvalueprivacy.setText("Privacy Off");
					txtvalueprivacy.setTextColor(R.color.lbl_color_email);
				}
			}
		});
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();

		btnProfileInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * fragment = new ProfileInfo(); // btnProfileInfo.setBackgroundResource(R.drawable.profileinfobuthover); btnProfileInfo.setBackgroundColor(getResources().getColor( R.color.Theme_Color)); btnAboutInfo.setBackgroundColor(getResources().getColor( R.color.lbl_btn_register_bg));
				 * btnProfileInfo.setTypeface(SplashScreen.ProxiNova_Bold); btnAboutInfo.setTypeface(SplashScreen.ProxiNova_Bold); // btnAboutInfo.setBackgroundResource(R.drawable.aboutyoubut); FragmentManager fragmentManager = getFragmentManager(); fragmentManager.beginTransaction()
				 * .replace(R.id.frame_container, fragment).commit();
				 */
				btnProfileInfo.setBackgroundResource(R.drawable.btn_themebg);
				btnAboutInfo.setBackgroundResource(R.drawable.btn_themebg_unsel);
				sc_Profile.setVisibility(View.VISIBLE);
				sc_About.setVisibility(View.GONE);
			}
		});
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				finish();

			}
		});

		btnAboutInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * fragment = new Aboutyou(); // btnProfileInfo.setBackgroundResource(R.drawable.profileinfobut); // btnAboutInfo.setBackgroundResource(R.drawable.aboutyoubuthover); btnProfileInfo.setBackgroundColor(getResources().getColor( R.color.lbl_btn_register_bg));
				 * btnAboutInfo.setBackgroundColor(getResources().getColor( R.color.Theme_Color)); btnProfileInfo.setTypeface(SplashScreen.ProxiNova_Bold); btnAboutInfo.setTypeface(SplashScreen.ProxiNova_Bold); FragmentManager fragmentManager = getFragmentManager();
				 * fragmentManager.beginTransaction() .replace(R.id.frame_container, fragment).commit();
				 */

				btnProfileInfo.setBackgroundResource(R.drawable.btn_themebg_unsel);
				btnAboutInfo.setBackgroundResource(R.drawable.btn_themebg);
				sc_Profile.setVisibility(View.GONE);
				sc_About.setVisibility(View.VISIBLE);
			}
		});

		if (btnPrivacy.isChecked()) {
			txtvalueprivacy.setText("Privacy On");
			// txtvalueprivacy.setTextColor(Color.parseColor("#00CD66"));
			txtvalueprivacy.setTextColor(R.color.Theme_Color);
		} else {
			txtvalueprivacy.setText("Privacy Off");
			txtvalueprivacy.setTextColor(R.color.lbl_color_email);
		}
		imgProfilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChoosePic();
			}
		});

		// About us
		DataBaseAdapter mDbHelper1 = new DataBaseAdapter(context);
		try {
			mDbHelper1.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		Country_Name = mDbHelper1.getCountryName(Country_ID);

		txtUserName.setText(User_Name);
		sp_Country.setSelection(Integer.parseInt(User_Country));
		edt_City.setText(User_City);

		// set the default according to value

		sp_Gender.setSelection(Integer.parseInt(User_Gender));
		if (User_BirthDate.length() > 0) {
			String User_BirthDateNew = fun.getDateFromStringVal(User_BirthDate);
			btnBDay.setText(User_BirthDateNew);
		} else {
			setTodayDate();
		}

		imageLoader.displayImage(User_ImagePath, imgUserProfPic, options);

		/*** Getting Countries value from database */

		DataBaseAdapter mDbHelper = new DataBaseAdapter(context);
		try {
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		lst_countries = mDbHelper.getAllCountry();

		adpt = new SimpleAdapter(context, lst_countries, R.layout.sp_item, new String[] { "Country_Name", "Country_ID" }, new int[] { R.id.text1, R.id.text2 });
		adpt.notifyDataSetChanged();
		adpt.setDropDownViewResource(R.layout.sp_item);
		sp_Country.setAdapter(adpt);

		for (int i = 0; i < lst_countries.size(); i++) {
			if (User_Country.equals(lst_countries.get(i).get("Country_ID"))) {
				sp_Country.setSelection(i);
			}
		}

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
		adapter = new ArrayAdapter<String>(context, R.layout.sp_item, R.id.text1, arrGender);

		// adapter.setDropDownViewResource(R.layout.sp_item);
		sp_Gender.setAdapter(adapter);
		if (adapter != null) {
			int pos;
			if (User_Gender.equalsIgnoreCase("1")) {
				pos = adapter.getPosition("Male");
				sp_Gender.setSelection(pos);
			} else if (User_Gender.equalsIgnoreCase("2")) {
				pos = adapter.getPosition("FeMale");
				sp_Gender.setSelection(pos);
			}

		}
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

		btnBDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Birthdateclick");
				// getActivity().showDialog(DATE_DIALOG_ID);
				datepicker = new DatePickerDialog(context, mDateSetListener, mYear - 10, mMonth, mDay);
				datepicker.show();
			}
		});
		// if (btnBDay.getText().toString().length() == 0) {
		// setTodayDate();
		// }

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (chknet.isNetworkConnected()) {
					new UpdateEditProfile().execute("");
				} else {
					Toast.makeText(context, "Check your Internet connection", Toast.LENGTH_SHORT).show();
				}
				System.out.println("hi....btn save click...");
			}
		});
	}

	public void ChoosePic() {

		final CharSequence[] items = { "Remove Current Photo", "Choose from Gallery", "Take Pic", "Import From Facebook", "Import From Twitter", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Pic")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputMediaFileUri(REQUEST_CAMERA);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

					// start the image capture Intent
					EditProfile.this.getParent().startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					EditProfile.this.getParent().startActivityForResult(intent, SELECT_FILE);
				} else if (items[item].equals("Remove Current Photo")) {
					// imgProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.noimageuser));
					if (chknet.isNetworkConnected()) {
						new RemoveProfilePic().execute("");
					} else {
						Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}

				} else if (items[item].equals("Import From Facebook")) {
					if (facebook.isSessionValid()) {
						if (con.isNetworkConnected()) {
							getProfileInformation();
							// new checkLogin().execute("");

						} else {
							Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
						}

						// return;
					} else {
						facebook.authorize(EditProfile.this, new String[] { "email", "read_stream", "offline_access" }, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {
							public void onComplete(Bundle values) {
								// postFacebookMessage(pos);
								getProfileInformation();
							}

							public void onFacebookError(FacebookError e) {
								// TODO Auto-generated method stub

							}

							public void onError(DialogError e) {
								// TODO Auto-generated method stub

							}

							public void onCancel() {
								// TODO Auto-generated method stub

							}
						});
					}
				} else if (items[item].equals("Import From Twitter")) {

				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				Log.d("FileUri", "val " + fileUri);

				imageFilePath = fileUri.getPath();
				Log.d("Tag cam path", " val " + imageFilePath);
				previewCapturedImage();

			} else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, EditProfile.this);
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				imgProfilePic.setImageBitmap(bm);
				getImageData(data);
			}
		}

	}

	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void getImageData(Intent data) {

		// User had pick an image.
		Uri _uri = data.getData();
		Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
		cursor.moveToFirst();

		// Link to the image
		imageFilePath = cursor.getString(0);
		Log.v("imageFilePath", imageFilePath);

		photos = new File(imageFilePath);

		/************** GETTING IMAGE SIZE **********/
		final long imageFileSize = photos.getAbsoluteFile().length();
		// System.out.println("ImageFileSize : " + imageFileSize);
		String setImage = (convertToByte(imageFilePath));
		final String imageFileName = photos.getName();
		ImageName = imageFileName;
		String setImageFileName = (imageFileName);
		// System.out.println("ImageFileName : " + imageFileName);

		b = decodeFile(photos);

		// Toast.makeText(MoreActivityGroup.this, "Bit map : " + b,
		// Toast.LENGTH_LONG).show();
		b = Bitmap.createScaledBitmap(b, 150, 150, true);
		Bitmap setBitmap = (b);
		imgProfilePic.setImageBitmap(b);
		String getImageName = (imageFileName);
		Log.d("tAg ", "val " + getImageName);
		// imageView.setImageBitmap(b);
		if (chknet.isNetworkConnected()) {
			new UploadProfilePic().execute("");
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	public String convertToByte(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);

		Bitmap photo = bm;// this is your image.
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		imgbytearray = byteArray;
		// System.out.println("Image : " + imgbytearray);

		/************* BASE64 CONVERSION **********************/

		String encodedImage = com.camrate.global.Base64.encodeBytes(byteArray);
		// System.out.println("encodeimgae"+encodedImage);
		ImgName = encodedImage;

		return encodedImage;
	}

	/******************* Making the thumbnail of image to show on app *******************/
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				// Toast.makeText(MoreActivityGroup.this, "width : "+ width_tmp
				// + " height : " + height_tmp, Toast.LENGTH_LONG).show();
				scale++;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;

	}

	/*** Get image */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Get image path */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

		if (type == REQUEST_CAMERA) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image_" + timeStamp + ".jpg");
			Log.d("Tag file", "val " + mediaFile);
			// imageFilePath=fileUri.getPath();

		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		photos = new File(imageFilePath);

		/************** GETTING IMAGE SIZE **********/
		final long imageFileSize = photos.getAbsoluteFile().length();
		System.out.println("ImageFileSize : " + imageFileSize);
		String setImage = (convertToByte(imageFilePath));
		final String imageFileName = photos.getName();
		ImageName = imageFileName;
		String setImageFileName = (imageFileName);
		System.out.println("ImageFileName : " + imageFileName);

		b = decodeFile(photos);

		// Toast.makeText(MoreActivityGroup.this, "Bit map : " + b,
		// Toast.LENGTH_LONG).show();
		b = Bitmap.createScaledBitmap(b, 150, 150, true);
		Bitmap setBitmap = (b);
		imgProfilePic.setImageBitmap(b);
		String getImageName = (imageFileName);
		Log.d("tAg ", "val " + getImageName);
		if (chknet.isNetworkConnected()) {
			new UploadProfilePic().execute("");
		} else {
			Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
		}
	}

	// Get Facebook profile information who is loggedIn

	public void getProfileInformation() {
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				Log.d("Profile", response);

				String json = response;

				try {
					// Facebook Profile JSON data
					profile = new JSONObject(json);

					// getting id of the user
					fbID = profile.getString("id");

					// getting name of the user
					Name = profile.getString("first_name");

					// getting LastName of the user
					Email = profile.getString("email");
					Log.d("Tag ", "Email " + Email);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (con.isNetworkConnected()) {

								new UserProfilePic().execute("");
								// Intent intent=new
								// Intent(BeforeLogin.this,EpForumHome.class);
								// startActivity(intent);

							} else {
								Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
							}

						}

					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}

		});

	}

	class UserProfilePic extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=ImageUpload&User_ID=" + User_ID + "&postparameter=" + facebook_Image;
			System.out.println(url);

			JSONObject json = jparser.getJSONFromUrl(url);
			System.out.println(json);
			try {
				Result = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				imageLoader.displayImage(facebook_Image, imgProfilePic, options);
			}
			if (status.equals("0")) {
				Toast.makeText(context, "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/** Update Profile */
	class UpdateEditProfile extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub

			String url = cc.GetBaseUrl() + "api=UpdateProfile&User_ID=" + User_ID + "&User_Name=" + User_Name + "&User_FirstName=" + edtname.getText().toString().trim() + "&User_LastName=" + edtsurname.getText().toString().trim() + "&User_Desc=" + edtdesc.getText().toString().trim()
					+ "&User_IsPrivate=" + PrivateUser + "&countryID=" + String.valueOf(Country_ID) + "&cityName=" + edt_City.getText().toString() + "&gender=" + String.valueOf(Gender_ID) + "&Birthdate=" + Date + "";
			System.out.println(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");
				if (Result.equalsIgnoreCase("1")) {
					if (Result.equalsIgnoreCase("1")) {
						JSONObject user = json.getJSONObject("user");
						String UserID = user.getString("User_ID");
						String User_Image = user.getString("User_Imagepath");
						String User_Email = user.getString("User_Email");
						String User_Name = user.getString("User_Name");
						SharedPreferences prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
						SharedPreferences.Editor edit1 = prefs.edit();
						edit1.putString("UserID", UserID);
						edit1.putBoolean("KeepLoggedIn", Boolean.TRUE);
						edit1.putString("User_Imagepath", User_Image);
						edit1.putString("User_Email", User_Email);
						edit1.putString("User_Name", User_Name);
						edit1.putString("User_FirstName", user.getString("User_FirstName"));
						edit1.putString("User_LastName", user.getString("User_LastName"));
						edit1.putString("User_CountryID", user.getString("User_CountryID"));
						edit1.putString("User_City", user.getString("User_City"));
						edit1.putString("User_Gender", user.getString("User_Gender"));
						edit1.putString("User_Birthdate", user.getString("User_Birthdate"));
						edit1.putString("User_Age", user.getString("User_Age"));
						edit1.putString("User_Desc", user.getString("User_Desc"));
						edit1.putString("User_PushNotification", user.getString("User_PushNotification"));
						edit1.putString("User_Location", user.getString("User_Location"));
						edit1.putString("User_Badge", user.getString("User_Badge"));
						edit1.putString("User_Status", user.getString("User_Status"));
						edit1.putString("User_IsPrivate", user.getString("User_IsPrivate"));
						edit1.putString("User_IsActive", user.getString("User_IsActive"));
						edit1.putString("User_LastSession", user.getString("User_LastSession"));
						edit1.putString("User_Date", user.getString("User_Date"));
						edit1.commit();
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				/*
				 * imageLoader.displayImage(facebook_Image, imgProfilePic,options); SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); SharedPreferences.Editor edit1 = prefs.edit(); edit1.putString("User_Name", edtname.getText().toString());
				 * edit1.putString("User_FirstName", FirtName); edit1.putString("User_LastName", edtsurname.getText().toString()); edit1.putString("User_IsPrivate", PrivateUser); edit1.commit();
				 */
				SimpleAlert("CamRate", "You have successfully updated your profile");
			}
			if (status.equals("0")) {
				Toast.makeText(context, "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	/** Image Remove Profile */
	class RemoveProfilePic extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			// TODO Auto-generated method stub

			String url = cc.GetBaseUrl() + "api=ImageUnlink&User_ID=" + User_ID;
			System.out.println(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				Result = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			System.out.println("Return Result -->" + Result);

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				imgProfilePic.setImageDrawable(getResources().getDrawable(R.drawable.noimageuser));

				SimpleAlert1("CamRate", "You have successfully updated your profile");

			}
			if (status.equals("0")) {
				Toast.makeText(context, "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	class UploadProfilePic extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {
			String Result = null;
			String Result1 = null;
			// TODO Auto-generated method stub

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(con.GetBaseUrl());
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				File f = null;
				FileBody fo = null;

				f = new File(imageFilePath);
				fo = new FileBody(f);

				// code for send Text using post method
				reqEntity.addPart("api", new StringBody("ImageUpload"));
				Log.i("Parameter", "tText Parameter added");
				reqEntity.addPart("User_ID", new StringBody(User_ID));
				reqEntity.addPart(" User_Imagepath", fo);
				System.out.println("else fo" + fo);
				Log.i("Parameter", "image added Parameter added");

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
			} catch (JSONException e) {
				Log.d("the xceptions ", "Xcep in posting status messages are : " + e.getMessage());
			}

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				Toast.makeText(context, "Profile Pic Uploaded Successfully  ", Toast.LENGTH_SHORT).show();

			}
			if (status.equals("0")) {
				Toast.makeText(context, "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void SimpleAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void SimpleAlert1(String t, String b) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(t);
		builder.setMessage(b);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (chknet.isNetworkConnected()) {
					new GetUserDetail().execute("");
				} else {
					Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public class GetUserDetail extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url = con.GetBaseUrl() + "api=ProfileDetail&User_ID=" + UserID + "&start=0&end=12&totalrec=9";
			Log.d("Tag", "url " + url);
			try {
				UserParse = parser.getStringFromUrl(url);
				// System.out.println("myFeedParseJson-->"+myFeedParse);
				// System.out.println("myFeedParseJson length-->"+myFeedParse.length());
				JSONObject jsonUser = new JSONObject(UserParse);
				for (int s = 0; s < jsonUser.length(); s++) {
					JSONObject objJsonUser = jsonUser.getJSONObject("User");
					String User_ID = objJsonUser.getString("User_ID").toString();
					String User_FirstName = objJsonUser.getString("User_FirstName").toString();
					String User_LastName = objJsonUser.getString("User_LastName").toString();
					String User_Name = objJsonUser.getString("User_Name").toString();
					String User_Password = objJsonUser.getString("User_Password").toString();
					String User_Imagepath = objJsonUser.getString("User_Imagepath").toString();
					String User_Email = objJsonUser.getString("User_Email").toString();
					String User_CountryID = objJsonUser.getString("User_CountryID").toString();
					String User_City = objJsonUser.getString("User_City").toString();
					String User_Birthdate = objJsonUser.getString("User_Birthdate").toString();
					String User_Gender = objJsonUser.getString("User_Gender").toString();
					String User_Age = objJsonUser.getString("User_Age").toString();
					String User_Desc = objJsonUser.getString("User_Desc").toString();
					String User_PushNotification = objJsonUser.getString("User_PushNotification").toString();
					String User_Location = objJsonUser.getString("User_Location").toString();
					String User_Status = objJsonUser.getString("User_Status").toString();
					String User_IsPrivate = objJsonUser.getString("User_IsPrivate").toString();
					String User_Date = objJsonUser.getString("User_Date").toString();

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("User_ID", User_ID);
					map.put("User_FirstName", User_FirstName);
					map.put("User_LastName", User_LastName);
					map.put("User_Name", User_Name);
					map.put("User_Password", User_Password);
					map.put("User_Imagepath", User_Imagepath);
					map.put("User_Email", User_Email);
					map.put("User_CountryID", User_CountryID);
					map.put("User_City", User_City);
					map.put("User_Gender", User_Gender);
					map.put("User_Birthdate", User_Birthdate);
					map.put("User_Imagepath", User_Imagepath);
					map.put("User_Age", User_Age);
					map.put("User_Desc", User_Desc);
					map.put("User_PushNotification", User_PushNotification);
					map.put("User_Location", User_Location);
					map.put("User_Status", User_Status);
					map.put("User_IsPrivate", User_IsPrivate);
					map.put("User_Date", User_Date);
					UserProfileData.add(map);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return UserProfileData;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);
			if (result.size() == 0) {
				Toast.makeText(context, "No More Post To Load", Toast.LENGTH_SHORT).show();
			} else {
				SharedPreferences prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
				SharedPreferences.Editor edit1 = prefs.edit();
				edit1.putString("UserID", result.get(0).get("User_ID"));
				edit1.putString("User_Imagepath", result.get(0).get("User_Imagepath"));
				edit1.putString("User_Email", result.get(0).get("User_Email"));
				edit1.putString("User_Name", result.get(0).get("User_Name"));
				edit1.putString("User_FirstName", result.get(0).get("User_FirstName"));
				edit1.putString("User_LastName", result.get(0).get("User_LastName"));
				edit1.putString("User_CountryID", result.get(0).get("User_CountryID"));
				edit1.putString("User_City", result.get(0).get("User_City"));
				edit1.putString("User_Gender", result.get(0).get("User_Gender"));
				edit1.putString("User_Birthdate", result.get(0).get("User_Birthdate"));
				edit1.putString("User_Age", result.get(0).get("User_Age"));
				edit1.putString("User_Desc", result.get(0).get("User_Desc"));
				edit1.putString("User_PushNotification", result.get(0).get("User_PushNotification"));
				edit1.putString("User_Location", result.get(0).get("User_Location"));
				edit1.putString("User_Badge", result.get(0).get("User_Badge"));
				edit1.putString("User_Status", result.get(0).get("User_Status"));
				edit1.putString("User_IsPrivate", result.get(0).get("User_IsPrivate"));
				edit1.putString("User_IsActive", result.get(0).get("User_IsActive"));
				edit1.putString("User_LastSession", result.get(0).get("User_LastSession"));
				edit1.putString("User_Date", result.get(0).get("User_Date"));
				edit1.commit();

			}
		}

	}

	// About us function
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
		// updateDisplay(minYear, minMonth, minDay);
	}

	public Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// return new DatePickerDialog(this, mDateSetListener, mYear-10,
			// mMonth, mDay);
			// return new DatePickerDialog(getActivity(), mDateSetListener,
			// mYear-10, mMonth, mDay);
			datepicker = new DatePickerDialog(context, mDateSetListener, mYear - 10, mMonth, mDay);
			// datepicker = new DatePickerDialog(context, mDateSetListener, mYear - 110, minMonth, minDayDay);
			return datepicker;
		}
		return null;
	}

	// updates the date we display in the TextView
	private void updateDisplay() {
		btnBDay.setText(new StringBuilder()
		// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
	}

	private void updateDisplay(int year, int month, int day) {
		// TODO Auto-generated method stub
		Date = new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ").toString();
		System.out.println("BirthDate==>" + Date);
		btnBDay.setText(fun.getBirthDate(String.valueOf(month + 1), String.valueOf(day), String.valueOf(year)));
		// btnBirthDate.setText(new StringBuilder().append(month +
		// 1).append("-").append(day).append("-").append(year).append(" ").toString());
		Date = new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" ").toString();
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			/*
			 * mYear = year; mMonth = monthOfYear; mDay = dayOfMonth; updateDisplay();
			 */
			setTodayDate();
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

	class UpdateAboutProfile extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {

			String Result1 = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();

				// HttpPost httppost = new
				// HttpPost("http://202.131.107.108:8081/Lovehate/main/frmlovehateApi.php");
				HttpPost httppost = new HttpPost(cc.GetBaseUrl() + "api=UpdateAboutProfile");
				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				reqEntity.addPart("User_ID", new StringBody(User_ID));
				reqEntity.addPart("countryID", new StringBody(String.valueOf(Country_ID)));
				reqEntity.addPart("cityName", new StringBody(edt_City.getText().toString()));
				reqEntity.addPart("gender", new StringBody(String.valueOf(Gender_ID)));
				reqEntity.addPart("Birthdate", new StringBody(Date));

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
					if (Result.equalsIgnoreCase("1")) {
						JSONObject user = jsonobject.getJSONObject("user");
						String UserID = user.getString("User_ID");
						String User_Image = user.getString("User_Imagepath");
						String User_Email = user.getString("User_Email");
						String User_Name = user.getString("User_Name");
						SharedPreferences prefs = context.getSharedPreferences("User_Info", context.MODE_PRIVATE);
						SharedPreferences.Editor edit1 = prefs.edit();
						edit1.putString("UserID", UserID);
						edit1.putBoolean("KeepLoggedIn", Boolean.TRUE);
						edit1.putString("User_Imagepath", User_Image);
						edit1.putString("User_Email", User_Email);
						edit1.putString("User_Name", User_Name);
						edit1.putString("User_FirstName", user.getString("User_FirstName"));
						edit1.putString("User_LastName", user.getString("User_LastName"));
						edit1.putString("User_CountryID", user.getString("User_CountryID"));
						edit1.putString("User_City", user.getString("User_City"));
						edit1.putString("User_Birthdate", user.getString("User_Birthdate"));
						edit1.putString("User_Gender", user.getString("User_Gender"));
						edit1.putString("User_Age", user.getString("User_Age"));
						edit1.putString("User_Desc", user.getString("User_Desc"));
						edit1.putString("User_PushNotification", user.getString("User_PushNotification"));
						edit1.putString("User_Location", user.getString("User_Location"));
						edit1.putString("User_Badge", user.getString("User_Badge"));
						edit1.putString("User_Status", user.getString("User_Status"));
						edit1.putString("User_IsPrivate", user.getString("User_IsPrivate"));
						edit1.putString("User_IsActive", user.getString("User_IsActive"));
						edit1.putString("User_LastSession", user.getString("User_LastSession"));
						edit1.putString("User_Date", user.getString("User_Date"));
						edit1.commit();
					}

				}
			} catch (JSONException e) {
				Log.d("the xceptions ", "Xcep in posting status messages are : " + e.getMessage());
			}

			return Result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.setVisibility(View.INVISIBLE);
			if (status.equals("1")) {
				SimpleAlert("CamRate", "You have successfully updated your profile");
			}
			if (status.equals("0")) {
				Toast.makeText(context, "Sorry  Failed ", Toast.LENGTH_SHORT).show();
			}
			if (status.equals("2")) {
				Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

}
