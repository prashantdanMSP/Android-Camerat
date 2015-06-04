package com.camrate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.camrate.explore.SlidingDrawerActivity;
import com.camrate.global.Constant;
import com.camrate.profile.GeneralUserProfile;
import com.camrate.profile.UserProfile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PostLocation_Detail extends Activity {

	DisplayImageOptions options1;
	ImageLoader imageLoader = ImageLoader.getInstance();
	int width, height;
	RelativeLayout.LayoutParams paramImage, paramImage1;
	TextView tv14;
	Constant c = new Constant();
	private static final String TAG = PostLocation_Detail.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapdetail);
		tv14 = (TextView) findViewById(R.id.textView1);

		// tv14.setTextAlignment(Gravity.CENTER);+

		Button back = (Button) findViewById(R.id.button2);
		back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.INVISIBLE);
		Display display = getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		// System.out.println("Width--->" + width);
		// System.out.println("Height--->" + height);
		// System.out.println("IMageview Height and width--->" + (width - 20) / 3);
		paramImage = new RelativeLayout.LayoutParams((width - 10) / 3, (width - 10) / 3);
		paramImage1 = new RelativeLayout.LayoutParams((width - 10) / 3, (width - 10) / 3);
		int round = (width - 20) / 2;
		imageLoader.init(ImageLoaderConfiguration.createDefault(PostLocation_Detail.this));
		options1 = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.imageloading).showImageForEmptyUri(R.drawable.imageloading).showImageOnFail(R.drawable.imageloading).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888).build();
		// Receiving latitude from MainActivity screen
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backClickMange();
			}
		});
		String Post_Tag = getIntent().getStringExtra("Post_Tags");
		tv14.setText(Post_Tag);
		final String Post_Date = getIntent().getStringExtra("Post_Date");
		final String Post_Location = getIntent().getStringExtra("Post_Location");
		// System.out.println("loc==>"+Post_Location);
		final String User_Name = getIntent().getStringExtra("User_Name");
		final String Post_ImageSquare = getIntent().getStringExtra("Post_ImageSquare");
		final String Average_Rating = getIntent().getStringExtra("Average_Rating");

		double latitude = getIntent().getDoubleExtra("Post_Latitude", 0);

		// Receiving longitude from MainActivity screen
		double longitude = getIntent().getDoubleExtra("Post_Longitude", 0);

		LatLng position = new LatLng(latitude, longitude);

		// Instantiating MarkerOptions class
		final MarkerOptions options = new MarkerOptions();

		// Setting position for the MarkerOptions
		options.position(position);

		// Setting title for the MarkerOptions
		// options.title(BusinessName);
		// options.title(Business_Address);
		// options.snippet(Business_Address);

		// Setting snippet for the MarkerOptions
		// options.snippet("Latitude:"+latitude+",Longitude:"+longitude);
		options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
		// Getting Reference to SupportMapFragment of activity_map.xml
		MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

		// Getting reference to google map
		final GoogleMap googleMap = fm.getMap();

		// Custom Info windows map
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			// Use default InfoWindow frame
			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			// Defines the contents of the InfoWindow
			@Override
			public View getInfoContents(Marker arg0) {

				// Getting view from the layout file info_window_layout
				View v = getLayoutInflater().inflate(R.layout.mapinfo_window_layout, null);

				// Getting the position from the marker
				LatLng latLng = arg0.getPosition();

				// Getting reference to the TextView to set latitude
				TextView txtlocation = (TextView) v.findViewById(R.id.txtlocationvalue);
				ImageView userpostimage = (ImageView) v.findViewById(R.id.imguserpost);
				ImageView userRate = (ImageView) v.findViewById(R.id.imguserrate);
				RelativeLayout relGradient = (RelativeLayout) v.findViewById(R.id.rel_Gradient);
				TextView tvuser = (TextView) v.findViewById(R.id.txtpostedbyvalue);
				userpostimage.setLayoutParams(paramImage);
				paramImage1.setMargins(0, 10, 0, 0);
				// paramImage1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				relGradient.setLayoutParams(paramImage);
				// Getting reference to the TextView to set longitude
				// TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
				if (userRate != null) {
					int ratcount = Integer.valueOf(Average_Rating);
					switch (ratcount) {
					case 1:
						userRate.setImageResource(R.drawable.one_star_grid);
						break;
					case 2:
						userRate.setImageResource(R.drawable.two_star_grid);
						break;
					case 3:
						userRate.setImageResource(R.drawable.three_star_grid);
						break;
					case 4:
						userRate.setImageResource(R.drawable.four_star_grid);
						break;
					case 5:
						userRate.setImageResource(R.drawable.five_star_grid);
						break;

					default:
						break;
					}
				}

				System.out.println("loc==>" + Post_Location);
				// Setting the latitude
				txtlocation.setText(Post_Location);
				tvuser.setText(User_Name);
				txtlocation.setTypeface(SplashScreen.ProxiNova_Regular);
				tvuser.setTypeface(SplashScreen.ProxiNova_Bold, Typeface.BOLD);
				tv14.setTypeface(SplashScreen.ProxiNova_Bold);
				imageLoader.displayImage(Post_ImageSquare, userpostimage, options1);
				// Setting the longitude
				// tvLng.setText("Longitude:"+ latLng.longitude);

				// Returning the view containing InfoWindow contents
				return v;

			}
		});

		// Adding Marker on the Google Map
		googleMap.addMarker(options).showInfoWindow();

		// Creating CameraUpdate object for position
		// CameraUpdate updatePosition =
		// CameraUpdateFactory.newLatLng(position);

		// Creating CameraUpdate object for zoom
		// CameraUpdate updateZoom = CameraUpdateFactory.zoomBy(14);

		// Updating the camera position to the user input latitude and longitude
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));

		// googleMap.moveCamera(center);
		// Applying zoom to the marker position
		// googleMap.animateCamera(updateZoom);
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

	}

	private void backClickMange() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		c.TagmyPref = getSharedPreferences(c.TAG_PREF, Context.MODE_WORLD_WRITEABLE);
		Editor edt = c.TagmyPref.edit();
		edt.putString(c.TAG_KEY, TAG);
		edt.commit();
	}
}