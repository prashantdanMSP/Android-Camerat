package com.camrate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camrate.global.JSONParser;
import com.camrate.tools.ImageDownloaderOnly;
import com.camrate.tools.ImageDownloaderOnly.OnTaskCompleted;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SearchImageFromWeb extends GridViewListBaseActivity {

	String UserID;
	EditText edtsearch;
	Button btnclose;
	String SearchKeywords;
	ProgressBar pd;
	String mySearch;
	JSONParser parser = new JSONParser();
	ArrayList<HashMap<String, String>> mysearchImageData;
	ImageAdapter objImageAdapter;
	DisplayImageOptions options;
	int width, height;
	RelativeLayout.LayoutParams paramImage;
	ImageLoader imageLoader;
	public static Bitmap bitmap = null;
	public static String ImageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_images);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitlebar);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		init();
	}

	public void init() {
		TextView tv14 = (TextView) findViewById(R.id.textView1);
		tv14.setText("Images from Web");
		Button btnmyFriend = (Button) findViewById(R.id.button2);
		btnmyFriend.setVisibility(View.INVISIBLE);
		Button btncancel = (Button) findViewById(R.id.button1);
		btncancel.setVisibility(View.VISIBLE);
		edtsearch = (EditText) findViewById(R.id.serchText1);
		btnclose = (Button) findViewById(R.id.btnclose);
		btncancel.setText("Cancel");
		btncancel.setTypeface(SplashScreen.ProxiNova_Bold);
		Display display = getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		System.out.println("Width--->" + width);
		System.out.println("Height--->" + height);
		System.out.println("IMageview Height and width--->" + (width - 20) / 3);
		paramImage = new RelativeLayout.LayoutParams((width - 20) / 3, (width - 20) / 3);
		int round = (width - 20) / 3;
		pd = (ProgressBar) findViewById(R.id.progressBar1);
		listView = (GridView) findViewById(R.id.gridview);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.imageloadingnew).showImageForEmptyUri(R.drawable.imageloadingnew).showImageOnFail(R.drawable.imageloadingnew).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(SearchImageFromWeb.this));
		btncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, 0);

			}
		});
		btnclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edtsearch.setText("");
			}
		});
		tv14.setTypeface(MyFeed.Kbrayant_med);
		((EditText) findViewById(R.id.serchText1)).setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub

				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					// onSearchAction(v);
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					new GetSearch().execute("");
					return true;
				}
				return false;
			}
		});
		edtsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (edtsearch.getText().toString().trim().length() > 0) {
					btnclose.setVisibility(View.VISIBLE);
					SearchKeywords = edtsearch.getText().toString().trim();
					System.out.println("serached key-->" + SearchKeywords);
				} else {
					btnclose.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	public class GetSearch extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setVisibility(View.VISIBLE);

		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			// TODO Auto-generated method stub

			try {
				String strreplacespace = SearchKeywords.replace(" ", "%20");
				String webUrl = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=" + "%27" + strreplacespace + "%27" + "&ImageFilters='Size:Medium'&$format=json";
				// webUrl.replace(" ", "%20");
				URL url = new URL(webUrl);
				System.out.println("Url-->" + url);

				String credentials = "KNRSzFBelK7vLNbfZYTYCVcI2yBikIIkl5+DYyhCXzI" + ":" + "KNRSzFBelK7vLNbfZYTYCVcI2yBikIIkl5+DYyhCXzI";
				String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
				InputStream content = (InputStream) connection.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(content));
				String output;
				StringBuilder sb = new StringBuilder();
				char[] buffer = new char[4096];
				while ((output = br.readLine()) != null) {
					// System.out.println(line);
					sb.append(output);
				}
				mySearch = sb.toString();
				connection.disconnect();

				JSONObject json = new JSONObject(mySearch).getJSONObject("d");
				// System.out.println("json-->" + json);
				JSONArray objectSearchItem = json.getJSONArray("results");
				mysearchImageData = new ArrayList<HashMap<String, String>>();
				if (objectSearchItem.length() > 0) {
					for (int s = 0; s < objectSearchItem.length(); s++) {
						JSONObject objJson = objectSearchItem.getJSONObject(s);
						String MediaUrl = objJson.getString("MediaUrl");

						JSONObject objThumbnil = objJson.getJSONObject("Thumbnail");

						String Thumb_MediaUrl = objThumbnil.getString("MediaUrl");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("MediaUrl", MediaUrl);
						map.put("Thumb_MediaUrl", Thumb_MediaUrl);
						mysearchImageData.add(map);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mysearchImageData;
		}

		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.setVisibility(View.INVISIBLE);
			if (result.size() == 0) {
				Toast.makeText(getApplicationContext(), "No More Post To Load", Toast.LENGTH_SHORT).show();

			} else {
				// System.out.println("SearhData-->" + result);
				objImageAdapter = new ImageAdapter(result);

				((GridView) listView).setAdapter(objImageAdapter);
				// System.out.println("Adapter First===");

				objImageAdapter.notifyDataSetChanged();
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
						// TODO Auto-generated method stub

						String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
						ImageName = File.separator + "image_" + timeStamp + ".jpg";
						String imagepath = result.get(position).get("MediaUrl");
						String path = "file:///mnt/sdcard/CamRate_Images/" + ImageName;
						final Uri uriFromPath = Uri.fromFile(new File(path));

						new ImageDownloaderOnly(SearchImageFromWeb.this, ImageName, new OnTaskCompleted() {

							@Override
							public void onTaskCompleted() {

								try {

									File f = new File("/mnt/sdcard/CamRate_Images/" + ImageName);
									bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

									// bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								// TODO Auto-generated method stub
								Intent intent = new Intent(SearchImageFromWeb.this, RateItScreen.class);
								intent.putExtra("is_Video", "0");
								intent.putExtra("is_web", "1");
								startActivity(intent);
							}
						}).execute(imagepath);
					}
				});

			}
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	static class ViewHolder {
		ImageView imgaeFromWeb;
		ProgressBar progressBar;

	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> items;

		ImageAdapter(ArrayList<HashMap<String, String>> items) {
			this.items = items;
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.search_listimage, parent, false);
				holder = new ViewHolder();
				assert view != null;

				holder.imgaeFromWeb = (ImageView) view.findViewById(R.id.imguserpost);
				holder.imgaeFromWeb.setLayoutParams(paramImage);

				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (holder.imgaeFromWeb != null) {
				imageLoader.displayImage(items.get(position).get("Thumb_MediaUrl"), holder.imgaeFromWeb, options, new SimpleImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.progressBar.setProgress(0);
						holder.progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						holder.progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						holder.progressBar.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total) {
						holder.progressBar.setProgress(Math.round(100.0f * current / total));
					}
				});
			}

			return view;
		}
	}
}
