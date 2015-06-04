package com.camrate.categories;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.explore.DrawerItem;
import com.camrate.explore.DrawerListAdapter;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;

public class Category extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String mDrawerTitle;
	private String mTitle;
	DrawerListAdapter adapter;
	TextView txtlabel_Title;
	ArrayList<DrawerItem> dataList;
	SimpleAdapter adpt;
	Constant con=new Constant();
	JSONParser parser = new JSONParser();
	Button Back;
	ArrayList<HashMap<String, String>> PostCategoryData;
	String ParseCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.sliding_menu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.customtitlebar);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// Initializing

		init();
		if (savedInstanceState == null) {
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	public void init() {
		
		txtlabel_Title = (TextView) findViewById(R.id.tvRateName);
		txtlabel_Title.setText("Category");
		Back = (Button) findViewById(R.id.button2);
		Back.setVisibility(View.VISIBLE);
		Button next = (Button) findViewById(R.id.button1);
		next.setVisibility(View.INVISIBLE);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		next.setText("");
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		new GetCategoryList().execute("");

		// mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_Gridon).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void SelectItem(int possition) {

		Fragment fragment = null;
		Bundle args = new Bundle();
		/*switch (possition) {
		case 0:
			fragment = new Animals_Pets();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 1:
			fragment = new Art_Design();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 2:
			fragment = new Companies();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 3:
			fragment = new Culture_Education();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 4:
			fragment = new Entertainment();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 5:
			fragment = new EverydayLife();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 6:
			fragment = new Family_Children();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 7:
			fragment = new Fashion_Style();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 8:
			fragment = new Film_TV();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 9:
			fragment = new Food_Drink();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 10:
			fragment = new Health_Fitness();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 11:
			fragment = new Hobbies();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 12:
			fragment = new Home();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 13:
			fragment = new Miscellaneous_Other();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 14:
			fragment = new Music();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 15:
			fragment = new Nature();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 16:
			fragment = new News_CurrentEvents();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 17:
			fragment = new Politics_Government();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 18:
			fragment = new Products();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 19:
			fragment = new Relationships();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 20:
			fragment = new Religion_Spirituality();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 21:
			fragment = new Science_Technology();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 22:
			fragment = new Services();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 23:
			fragment = new Social();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 24:
			fragment = new Sports();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 25:
			fragment = new TownCity_Country();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 26:
			fragment = new Travel_Hotels();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		case 27:
			fragment = new Vehicles_Transport();
			args.putString("Category_ID",
					PostCategoryData.get(possition).get("Category_ID"));
			args.putString("Category_Name", PostCategoryData.get(possition)
					.get("Category_Name"));
			break;
		default:
			break;
		}*/

		// error in creating fragment
		Log.e("MainActivity", "Error in creating fragment");

	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mTitle = PostCategoryData.get(position).get("Category_Name");				
			String Cat_ID = PostCategoryData.get(position).get("Category_ID");
			System.out.println("ID-->"+Cat_ID);
			System.out.println("Name-->"+mTitle);
			
			
			// Creating a fragment object
			Animals_Pets rFragment = new Animals_Pets();
			
			// Creating a Bundle object
			Bundle data = new Bundle();
			
			// Setting the index of the currently selected item of mDrawerList
			data.putString("Category_ID", Cat_ID);
			data.putString("Category_Name", mTitle);
			
			// Setting the position to the fragment
			rFragment.setArguments(data);
			
			// Getting reference to the FragmentManager
			FragmentManager fragmentManager  = getFragmentManager();
			
			// Creating a fragment transaction
			FragmentTransaction ft = fragmentManager.beginTransaction();
			
			// Adding a fragment to the fragment transaction
			ft.replace(R.id.frame_container, rFragment);
			
			// Committing the transaction
			ft.commit();
			
			// Closing the drawer
			mDrawerLayout.closeDrawer(mDrawerList);	

		}
	}

	/** Getting List Of Catgory */

	public class GetCategoryList extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

		/*
		 * @Override protected void onPreExecute() { //isLoading=true;
		 * if(count==0){ pd = ProgressDialog.show(CommentActivity.this, "","");
		 * } }
		 */
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			// TODO Auto-generated method stub
			PostCategoryData = new ArrayList<HashMap<String, String>>();
			String url = con.GetBaseUrl()+"api=GetCategory";

			// Log.d("Tag","url "+url);
			try {
				ParseCategory = parser.getStringFromUrl(url);
				JSONArray jsonMyFeed = new JSONArray(ParseCategory);
				for (int s = 0; s < jsonMyFeed.length(); s++) {
					JSONObject objComment = jsonMyFeed.getJSONObject(s);
					HashMap<String, String> map1 = new HashMap<String, String>();
					map1.put("Category_ID", objComment.getString("Category_ID"));
					map1.put("Category_Name",
							objComment.getString("Category_Name"));

					PostCategoryData.add(map1);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// System.out.println("PostCommentData -->"+PostCommentData);
	
			return PostCategoryData;
		}

		@Override
		protected void onPostExecute(
				final ArrayList<HashMap<String, String>> menuItems) {

			if (menuItems.size() == 0) {

			} else {
				adpt = new SimpleAdapter(Category.this, menuItems,
						R.layout.sp_item, new String[] { "Category_Name",
								"Category_ID" }, new int[] { R.id.text1,
								R.id.text2 });
				mDrawerList.setAdapter(adpt);
				
				mDrawerList
						.setOnItemClickListener(new DrawerItemClickListener());
			}
		}
	}

}
