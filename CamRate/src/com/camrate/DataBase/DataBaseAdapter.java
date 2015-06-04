package com.camrate.DataBase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.camrate.RateItScreen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseAdapter {
	protected static final String TAG = "TAG";

	private SQLiteDatabase mDb;
	DataBaseHelper mDbHelper;
	Context c;

	/*
	 * private static String ACCOUNT_TABLE = "account"; public static String ACCOUNT_EXTRADATA = "extraData"; public static String ACCOUNT_ID = "ID"; public static String ACCOUNT_ADDITIONALDATA = "additionalData"; public static String ACCOUNT_DATA = "data";
	 */

	public DataBaseAdapter(Context context) {
		mDbHelper = new DataBaseHelper(context);
		this.c = context;
	}

	public DataBaseAdapter createDatabase() throws SQLException {
		try {
			mDbHelper.createDataBase();
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public DataBaseAdapter open() throws SQLException {
		try {
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLException mSQLException) {
			Log.e(TAG, mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public ArrayList<HashMap<String, String>> getAllCountry() {
		ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();

		// Select All Query
		String selectQuery = "Select * from countries order by countryName";

		Cursor cursor = mDb.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		HashMap<String, String> tempmap = new HashMap<String, String>();
		tempmap.put("Country_ID", "0");
		tempmap.put("Country_Name", "Select");
		tempmap.put("Country_Code", "0");
		menuItems.add(tempmap);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Country_ID", cursor.getString(cursor.getColumnIndex("idCountry")));
				map.put("Country_Name", cursor.getString(cursor.getColumnIndex("countryName")));
				map.put("Country_Code", cursor.getString(cursor.getColumnIndex("countryCode")));
				menuItems.add(map);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		mDb.close();

		// returning lables
		return menuItems;
	}

	public String getCountryName(int i) {
		String name = null;

		// Select All Query
		String selectQuery = "SELECT  * FROM countries where idCountry=" + i;

		Cursor cursor = mDb.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {

				name = cursor.getString(cursor.getColumnIndex("countryName"));

			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		mDb.close();

		// returning lables
		return name;
	}

	public long insertValuesInDatabase(HashMap<String, Object> map, String tblName) {

		ContentValues initialValues = new ContentValues();
		for (String key : map.keySet()) {
			initialValues.put(key, map.get(key).toString());

		}
		Long l = mDb.insert(tblName, null, initialValues);
		mDb.close();
		return l;
	}

	public void deleteAllValueFromTable(String tableName) {

		String strSQL = "Delete FROM " + tableName;
		// System.out.println("query - "+strSQL);
		mDb.execSQL(strSQL);
		mDb.execSQL("VACUUM");
		// mDb.rawQuery(strSQL, null);
	}

	public ArrayList<HashMap<String, String>> getAllPost() {
		ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();

		// Select All Query
		String selectQuery = "SELECT  * FROM tblPost ";

		Cursor cursor = mDb.rawQuery(selectQuery, null);

		// looping through all rows and adding to list

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Post_twitter", cursor.getString(cursor.getColumnIndex("Post_twitter")));
				map.put("Post_tumblr", cursor.getString(cursor.getColumnIndex("Post_tumblr")));
				map.put("Post_facebook", cursor.getString(cursor.getColumnIndex("Post_facebook")));
				map.put("Post_videoname", cursor.getString(cursor.getColumnIndex("Post_videoname")));
				map.put("Post_location", cursor.getString(cursor.getColumnIndex("Post_location")));
				map.put("Post_userid", cursor.getString(cursor.getColumnIndex("Post_userid")));
				map.put("Post_emailList", cursor.getString(cursor.getColumnIndex("Post_emailList")));
				map.put("Post_latitude", cursor.getString(cursor.getColumnIndex("Post_latitude")));
				map.put("Post_longitude", cursor.getString(cursor.getColumnIndex("Post_longitude")));
				map.put("Post_ratingRate", cursor.getString(cursor.getColumnIndex("Post_ratingRate")));
				map.put("Post_id", cursor.getString(cursor.getColumnIndex("Post_id")));
				map.put("Post_title", cursor.getString(cursor.getColumnIndex("Post_title")));
				map.put("Post_description", cursor.getString(cursor.getColumnIndex("Post_description")));
				map.put("Post_imagename", cursor.getString(cursor.getColumnIndex("Post_imagename")));
				map.put("Post_categoryid", cursor.getString(cursor.getColumnIndex("Post_categoryid")));
				map.put("isUploaded", cursor.getString(cursor.getColumnIndex("isUploaded")));
				map.put("Post_emailList", cursor.getString(cursor.getColumnIndex("Post_emailList")));
				menuItems.add(map);
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();
		mDb.close();

		// returning lables
		return menuItems;
	}

	public boolean deletePost(int mPosition) {
		Boolean b = mDb.delete("tblPost", "Post_id" + "=" + mPosition, null) > 0;
		mDb.close();
		return b;
	}
}
