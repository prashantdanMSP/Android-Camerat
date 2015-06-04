package com.camrate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;

public class Register extends Activity {

	EditText edt_email, edt_uname, edt_pass;
	TextView txt_uname_check, txt_ErrorShow, txtlbl_addImage, txtlbl_addImage_req, txtlbl_addImageText, txtlbl_email, txtlbl_email_req, txtlbl_username, txtlbl_username_req, txtlbl_pwd, txtlbl_pwd_req;
	String uname, email = "";
	RelativeLayout rel_Error;
	Button btnContinue;
	ImageView imgProfie;
	private final int SELECT_FILE = 1;
	private final static int REQUEST_CAMERA = 2;
	public static File mediaFile;;
	private static Uri fileUri; // file url to store image/video
	checkInternet chkNet;
	private static final String IMAGE_DIRECTORY_NAME = "CamRate_Images";
	private static String imageFilePath = "";
	File photos;
	byte[] imgbytearray;
	public static Bitmap b = null;
	String ImgName, ImageName;
	public static String picImage;
	boolean hasDrawable;
	String chkValidation = "";
	boolean isemailError = true, isunameError = true, ispassError = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Sign Up");
		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.VISIBLE);
		next.setText("Part 1 of 2");
		tv14.setTypeface(SplashScreen.ProxiNova_Bold);
		next.setTypeface(SplashScreen.ProxiNova_Regular, Typeface.BOLD);
		next.setTextColor(getResources().getColor(R.color.lbl_color_lefttitle));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Register.this, AfterSplashScreen.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
		});
		chkNet = new checkInternet(Register.this);
		edt_email = (EditText) findViewById(R.id.edt_email);
		edt_uname = (EditText) findViewById(R.id.edt_uname);
		edt_pass = (EditText) findViewById(R.id.edt_upass);
		txt_uname_check = (TextView) findViewById(R.id.txt_uname_available);
		rel_Error = (RelativeLayout) findViewById(R.id.relative1);
		txt_ErrorShow = (TextView) findViewById(R.id.txtErrorShow);
		txtlbl_addImage = (TextView) findViewById(R.id.tvRateName);
		txtlbl_addImage_req = (TextView) findViewById(R.id.tvRateName1);
		txtlbl_addImageText = (TextView) findViewById(R.id.tvRateName2);
		txtlbl_email = (TextView) findViewById(R.id.txtlbl_email);
		txtlbl_email_req = (TextView) findViewById(R.id.txtlbl_email_req);
		txtlbl_username = (TextView) findViewById(R.id.txtlbl_username);
		txtlbl_username_req = (TextView) findViewById(R.id.txtlbl_username_req);
		txtlbl_pwd = (TextView) findViewById(R.id.txtlbl_pwd);
		txtlbl_pwd_req = (TextView) findViewById(R.id.txtlbl_pwd_req);
		btnContinue = (Button) findViewById(R.id.btn_continue);
		imgProfie = (ImageView) findViewById(R.id.imgProfile_Pic);
		imgProfie.setTag(11);
		edt_email.setTypeface(SplashScreen.ProxiNova_Regular);
		edt_uname.setTypeface(SplashScreen.ProxiNova_Regular);
		edt_pass.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_addImage.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_addImage_req.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_addImageText.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_email.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_email_req.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_username.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_username_req.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlbl_pwd.setTypeface(SplashScreen.ProxiNova_Bold);
		txtlbl_pwd_req.setTypeface(SplashScreen.ProxiNova_Regular);
		btnContinue.setTypeface(SplashScreen.ProxiNova_Bold);
		edt_email.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

					if ((edt_email.getText().toString().trim().length() > 0) && (edt_email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+"))) {
						email = edt_email.getText().toString();
						if (chkNet.isNetworkConnected()) {
							new checkEmail().execute();
						}
						edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct_icon, 0);
						rel_Error.setVisibility(View.INVISIBLE);
						isemailError = false;
					} else {
						if (edt_email.getText().toString().trim().length() == 0) {
							edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
							rel_Error.setVisibility(View.VISIBLE);
							isemailError = true;

							txt_ErrorShow.setText("Please enter valid email");
						} else if ((!edt_email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+"))) {
							edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
							rel_Error.setVisibility(View.VISIBLE);
							isemailError = true;

							txt_ErrorShow.setText("Please enter valid email");
						}
					}
					// Must return true here to consume event
					return true;

				}
				return false;
			}
		});

		edt_email.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// code to execute when EditText loses focus
					if ((edt_email.getText().toString().trim().length() > 0) && (edt_email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+"))) {
						email = edt_email.getText().toString();
						if (chkNet.isNetworkConnected()) {
							new checkEmail().execute();
						}
						edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct_icon, 0);
						rel_Error.setVisibility(View.INVISIBLE);
						isemailError = false;
					} else {
						// edt_email.requestFocus();
						if (edt_email.getText().toString().trim().length() == 0) {
							edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
							rel_Error.setVisibility(View.INVISIBLE);
							isemailError = true;

							txt_ErrorShow.setText("Please enter valid email");
						} else if ((!edt_email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+"))) {
							edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
							rel_Error.setVisibility(View.VISIBLE);
							isemailError = true;

							txt_ErrorShow.setText("Please enter valid email");
						}
					}
				}
			}
		});
		edt_uname.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

					if (edt_uname.getText().toString().trim().length() > 0) {
						uname = edt_uname.getText().toString().trim();
						isunameError = false;
						if (chkNet.isNetworkConnected()) {
							new checkUserName().execute();
						}

					} else {
						edt_uname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
						rel_Error.setVisibility(View.VISIBLE);
						isunameError = true;
						chkValidation = "username";
						txt_ErrorShow.setText("Please enter valid username");

					}
					// Must return true here to consume event
					return true;

				}
				return false;
			}
		});

		edt_uname.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// code to execute when EditText loses focus
					if (edt_uname.getText().toString().trim().length() > 0) {
						uname = edt_uname.getText().toString().trim();
						isunameError = false;
						if (chkNet.isNetworkConnected()) {
							new checkUserName().execute();
						}

					} else {
						edt_uname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
						rel_Error.setVisibility(View.VISIBLE);
						isunameError = true;
						chkValidation = "username";
						txt_ErrorShow.setText("Please enter valid username");

					}
				}
			}
		});

		edt_pass.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

					if (edt_pass.getText().toString().trim().length() > 0) {
						edt_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct_icon, 0);
						rel_Error.setVisibility(View.INVISIBLE);
						ispassError = false;
					} else {
						if (edt_pass.getText().toString().trim().length() == 0) {
							edt_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
							rel_Error.setVisibility(View.VISIBLE);
							ispassError = true;
							chkValidation = "pass";
							txt_ErrorShow.setText("Please enter password");
							// edt_pass.setError("Please enter password", getResources().getDrawable(R.drawable.error_icon));
						}

					}
					// Must return true here to consume event
					return true;

				}
				return false;
			}
		});

		edt_pass.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					// code to execute when EditText loses focus
					if (edt_pass.getText().toString().trim().length() > 0) {
						edt_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct_icon, 0);
						rel_Error.setVisibility(View.INVISIBLE);
						ispassError = false;
					} else {
						if (edt_pass.getText().toString().trim().length() == 0) {
							edt_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
							rel_Error.setVisibility(View.VISIBLE);
							ispassError = true;
							chkValidation = "pass";
							txt_ErrorShow.setText("Please enter password");
							// edt_pass.setError("Please enter password", getResources().getDrawable(R.drawable.error_icon));
						}

					}
				}
			}
		});
		btnContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isemailError) {
					edt_email.requestFocus();
					rel_Error.setVisibility(View.VISIBLE);
					txt_ErrorShow.setText("Please enter valid email");
				} else if (isunameError) {
					edt_uname.requestFocus();
					rel_Error.setVisibility(View.VISIBLE);
					txt_ErrorShow.setText("Please enter valid username");
				} else if (ispassError) {
					edt_pass.requestFocus();
					rel_Error.setVisibility(View.VISIBLE);
					txt_ErrorShow.setText("Please enter password");
				}

				else {
					System.out.println("Visiblity===>" + rel_Error.isShown());
					Intent intent = new Intent(Register.this, Register1.class);
					intent.putExtra("imgProfilepic", imageFilePath);
					intent.putExtra("user_Email", edt_email.getText().toString().trim());
					intent.putExtra("user_Name", edt_uname.getText().toString().trim());
					intent.putExtra("user_Pass", edt_pass.getText().toString().trim());
					startActivity(intent);
					overridePendingTransition(0, 0);
				}
				// }
			}
		});

		imgProfie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// hasDrawable = (imgProfie.getDrawable() != null);
				if (imgProfie.getTag().equals(22)) {
					// imageView has image in it
					ChoosePic1();
				} else {
					// no image assigned to image view
					ChoosePic();
				}
			}
		});

	}

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	public void ChoosePic() {

		final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputMediaFileUri(REQUEST_CAMERA);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

					// start the image capture Intent
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}

	public void ChoosePic1() {

		final CharSequence[] items = { "Take Photo", "Choose from Library", "Remove Picture", "Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputMediaFileUri(REQUEST_CAMERA);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

					// start the image capture Intent
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, SELECT_FILE);
				} else if (items[item].equals("Remove Picture")) {
					imgProfie.setImageDrawable(getResources().getDrawable(R.drawable.addprofile));
					imgProfie.setTag(11);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				Log.d("FileUri", "val " + fileUri);

				imageFilePath = fileUri.getPath();
				Log.d("Tag cam path", " val " + imageFilePath);
				previewCapturedImage();

			} else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();

				String tempPath = getPath(selectedImageUri, Register.this);
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				imgProfie.setImageBitmap(bm);
				imgProfie.setTag(22);
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
		picImage = imageFileName;
		// System.out.println("ImageFileName : " + imageFileName);

		b = decodeFile(photos);

		// Toast.makeText(MoreActivityGroup.this, "Bit map : " + b, Toast.LENGTH_LONG).show();
		b = Bitmap.createScaledBitmap(b, 150, 150, true);
		Bitmap setBitmap = (b);
		imgProfie.setImageBitmap(b);
		String getImageName = (imageFileName);
		Log.d("tAg ", "val " + getImageName);
		// imageView.setImageBitmap(b);

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
				// Toast.makeText(MoreActivityGroup.this, "width : "+ width_tmp + " height : " + height_tmp, Toast.LENGTH_LONG).show();
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
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
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
			picImage = File.separator + "image_" + timeStamp + ".jpg";
			mediaFile = new File(mediaStorageDir.getPath() + picImage);
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

		// Toast.makeText(MoreActivityGroup.this, "Bit map : " + b, Toast.LENGTH_LONG).show();
		b = Bitmap.createScaledBitmap(b, 150, 150, true);
		Bitmap setBitmap = (b);
		imgProfie.setImageBitmap(b);
		imgProfie.setTag(22);
		String getImageName = (imageFileName);
		Log.d("tAg ", "val " + getImageName);
	}

	public class checkUserName extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			String Result = null;

			JSONParser jParser = new JSONParser();

			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=UserNameAvailability&User_Name=" + uname;
			// System.out.println(url);
			cc.GetUrl(url);

			JSONObject json = jParser.getJSONFromUrl(url.toString());
			// System.out.println(json);
			try {
				Result = json.getString("result");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return Result;
		}

		@Override
		protected void onPostExecute(String status) {

			if (status.equals("1")) {
				edt_uname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct_icon, 0);
				txt_uname_check.setVisibility(View.VISIBLE);
				txt_uname_check.setText("Available");
				rel_Error.setVisibility(View.INVISIBLE);
			}
			if (status.equals("0")) {
				edt_uname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
				rel_Error.setVisibility(View.VISIBLE);
				txt_uname_check.setVisibility(View.INVISIBLE);
				txt_ErrorShow.setText("Usename Un-Available");
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public class checkEmail extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			String Result = null;
			JSONParser jParser = new JSONParser();
			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + "api=UserEmailAvailability&Email_id=" + email;
			cc.GetUrl(url);
			JSONObject json = jParser.getJSONFromUrl(url.toString());
			try {
				Result = json.getString("result");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return Result;
		}

		@Override
		protected void onPostExecute(String status) {
			if (status.equals("1")) {
				edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct_icon, 0);
				rel_Error.setVisibility(View.INVISIBLE);
			}
			if (status.equals("0")) {
				edt_email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_icon, 0);
				rel_Error.setVisibility(View.VISIBLE);
				txt_ErrorShow.setText("Please enter another email");
			}

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
}
