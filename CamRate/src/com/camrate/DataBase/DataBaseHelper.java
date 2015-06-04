package com.camrate.DataBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static String TAG = "TAG";
	private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";
	private static String DB_NAME = "CountryDB.sqlite";
	private SQLiteDatabase mDataBase;
	private final Context mContext;

	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.mContext = context;
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

	}

	public void createDataBase() throws IOException {
		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException mIOException) {
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}

	public boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	private void copyDataBase() throws IOException {
		InputStream mInput = mContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = mInput.read(mBuffer)) > 0) {
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}
	
	private void copyDatabase1() throws IOException{
		
	}

	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		mDataBase = SQLiteDatabase.openDatabase(mPath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return mDataBase != null;
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	private void downloadDatabase(String sUrl){
		DownloadWebPageTask task = new DownloadWebPageTask();
		//task.execute(new String[] { sUrl });
	}

	public void updateDatabase() throws IOException {
		try {

			copyDataBase();

		} catch (IOException e) {

			throw new Error("Error copying database");

		}

	}

}