package com.camrate.global;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;

public class Function {
	Context mContext;
	String s;
	public static SharedPreferences prefs;

	// constructor
	public Function(Context context) {
		this.mContext = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public String getUserName() {
		return "test";
	}

	// show alert
	public void SimpleAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(mContext);
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

	// show alert
	public void SimpleAlert1(String t, String b) {
		Builder builder = new AlertDialog.Builder(mContext);
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

	// show alert
	public void PassAlert(String t, String b) {
		Builder builder = new AlertDialog.Builder(mContext);
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

	public String getDateFromString(String dateString) {
		if (dateString != null) {

			String[] dateRoot = dateString.split(" ");
			String ymd = dateRoot[0];
			String hms = dateRoot[1];
			String[] calRoot = ymd.split("-");
			int year = Integer.parseInt(calRoot[0]);
			int month = Integer.parseInt(calRoot[1]);
			int day = Integer.parseInt(calRoot[2]);

			String[] timeRoot = hms.split(":");
			int hour = Integer.parseInt(timeRoot[0]);
			int minute = Integer.parseInt(timeRoot[1]);
			int second = Integer.parseInt(timeRoot[2]);

			String mnth = String.valueOf(month);
			String newFormat = day + "-" + mnth + "-" + year;
			if (mnth.equalsIgnoreCase("12")) {
				s = "December " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("11")) {
				s = "November " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("10")) {
				s = "October " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("9")) {
				s = "September " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("8")) {
				s = "August " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("7")) {
				s = "July " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("6")) {
				s = "June " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("5")) {
				s = "May " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("4")) {
				s = "April " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("3")) {
				// s="    "+day + "\nMarch "+ +year;
				s = "March " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("2")) {
				// s="    "+day + "\nFebruary "+ +year;
				s = "February " + day + "," + year;
			} else if (mnth.equalsIgnoreCase("1")) {
				s = "January " + day + "," + year;

			}

			return s;
		}
		return null;
	}

	public static String checkcurrency_Code(String country_Code) {
		if (country_Code.equalsIgnoreCase("USD")) {
			country_Code = "$";
		} else if (country_Code.equalsIgnoreCase("AUD")) {
			country_Code = "AU$";
		} else if (country_Code.equalsIgnoreCase("EUR")) {
			country_Code = "€";
		} else if (country_Code.equalsIgnoreCase("INR")) {
			country_Code = "Rs";
		} else if (country_Code.equalsIgnoreCase("MYR")) {
			country_Code = "RM";
		} else if (country_Code.equalsIgnoreCase("PKR")) {
			country_Code = "PKRs";
		} else if (country_Code.equalsIgnoreCase("SGD")) {
			country_Code = "S$";
		}
		return country_Code;

	}

	public static String checkcurrency_Rates(String Rate) {
		float currencyRate = Float.parseFloat(Rate);
		float totalRate = currencyRate * (Float.parseFloat(prefs.getString("currency_Rate", "0")));

		return totalRate + "";

	}

	public Bitmap getCroppedBitmap(Bitmap bitmap) {
		// bitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, false);
		/*
		 * final int width = bitmap.getWidth(); final int height = bitmap.getHeight();
		 */
		Bitmap dstBmp = null;
		// Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		if (bitmap != null) {

			if (bitmap.getWidth() >= bitmap.getHeight()) {

				dstBmp = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());

			} else {

				dstBmp = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
			}
			final Canvas canvas = new Canvas(dstBmp);
			// canvas.clipPath(path);
			canvas.drawBitmap(dstBmp, 0, 0, null);
		}
		return dstBmp;
	}

	public Bitmap getCroppedBitmap1(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
		// return _bmp;
		return output;
	}

	public String getBirthDate(String mnth, String day, String year) {

		String newFormat = day + "-" + mnth + "-" + year;
		if (mnth.equalsIgnoreCase("12")) {
			s = "December " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("11")) {
			s = "November " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("10")) {
			s = "October " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("9")) {
			s = "September " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("8")) {
			s = "August " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("7")) {
			s = "July " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("6")) {
			s = "June " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("5")) {
			s = "May " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("4")) {
			s = "April " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("3")) {
			// s="    "+day + "\nMarch "+ +year;
			s = "March " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("2")) {
			// s="    "+day + "\nFebruary "+ +year;
			s = "February " + day + "," + year;
		} else if (mnth.equalsIgnoreCase("1")) {
			s = "January " + day + "," + year;

		}

		return s;

	}

	public String checkDate_Diffrences(Date postDate) {
		String diffVal = "";
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date serverDate = new Date();
			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df1.setTimeZone(TimeZone.getTimeZone("gmt"));
			Calendar cal1 = Calendar.getInstance(); // creates calendar
			cal1.setTime(serverDate); // sets calendar time/date=====> you can set your own date here
			cal1.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
			serverDate = cal1.getTime();
			String gmtTime1 = df1.format(serverDate);

			System.out.println("this is the server time - " + gmtTime1);

			Date serDate = simpleDateFormat.parse(gmtTime1);

			diffVal = printDifference(serDate, postDate);
			Log.d("Diff", "val==>" + diffVal);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diffVal;
	}

	public String printDifference(Date date, Date curDate) {
		// difference in milliseconds
		long different = Math.abs(date.getTime() - curDate.getTime());
		long abc = 2592000;
		long l = different / abc;

		System.out.println("this is the different - " + different);
		System.out.println("this is the difference - " + abc);
		System.out.println("this is the difference - " + l / 1000);

		String diffrences = "";

		if ((different / 1000) < 60) {
			// print seconds
			long diff = different / 1000;
			if (diff > 1 && diff < 60) {
				diffrences = diff + " secs ago";
				return diffrences;
			} else {
				diffrences = diff + " sec ago";
				return diffrences;
			}

		} else if ((different / (1000 * 60)) < 60) {
			// minutes
			long diff = different / (1000 * 60);
			if (diff == 1) {
				diffrences = diff + " min ago";
				return diffrences;
			}
			if (diff > 1) {
				diffrences = diff + " mins ago";
				return diffrences;

			}

			// return (different / (1000 * 60)) + " min ago";
		} else if ((different / (1000 * 60 * 60)) < 24) {
			// hours
			long diff = different / (1000 * 60 * 60);
			if (diff == 1) {
				diffrences = diff + " hr ago";
				return diffrences;

			}
			if (diff > 1) {
				diffrences = diff + " hrs ago";
				return diffrences;
			}

			// return (different / (1000 * 60 * 60)) + " hrs ago";
		} else if ((different / (60 * 60 * 24)) / 1000 < 7) {
			// days
			long diff = (different / (60 * 60 * 24)) / 1000;
			if (diff == 1) {
				diffrences = diff + " day ago";
				return diffrences;
			}
			if (diff > 1) {
				diffrences = diff + " days ago";
				return diffrences;
			}

			// return (different / (60 * 60 * 24)) / 1000 + " day ago";
		} else if (((different / (60 * 60 * 24 * 7)) / 1000 < 4)) {
			// weeks
			long diff = (different / (60 * 60 * 24 * 7)) / 1000;
			if (diff == 1) {
				diffrences = diff + " week ago";
				return diffrences;
			}
			if (diff > 1) {
				diffrences = diff + " weeks ago";
				return diffrences;
			}
			// return (different / (60 * 60 * 24 * 7)) / 1000 + " week ago";
		} else if ((different / (60 * 60 * 24 * 30)) / 1000 < 12) {
			// months

			long diff = (different / (60 * 60 * 24 * 30)) / 1000;
			if (diff == 1) {
				diffrences = diff + " month ago";
				return diffrences;
			}
			if (diff > 1) {
				diffrences = diff + " months ago";
				return diffrences;
			}
			// return (different / (60 * 60 * 24 * 30)) / 1000 + " month ago";
		} else if ((different / (60 * 60 * 24 * 30 * 12)) / 1000 < 365) {
			long diff = (different / (60 * 60 * 24 * 30 * 12)) / 1000;
			if (diff == 1) {
				diffrences = diff + " yr ago";
				return diffrences;
			}
			if (diff > 1) {
				diffrences = diff + " yrs ago";
				return diffrences;
			}
			// return (different / (60 * 60 * 24 * 30 * 12)) / 1000 + " yrs ago";
		} else {
			return diffrences;
		}
		return diffrences;

	}

	public Date ConvertToDate(String dateString) {
		Date convertedDate = new Date();
		try {
			convertedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;

	}

	public String getDateFromStringVal(String dateString) {
		String date = null;
		try {
			if (dateString != null) {
				String[] dateRoot = dateString.split(" ");
				String ymd = dateRoot[0];
				String[] calRoot = ymd.split("-");
				int month = Integer.parseInt(calRoot[0]);
				int day = Integer.parseInt(calRoot[1]);
				int year = Integer.parseInt(calRoot[2]);

				String mnth = String.valueOf(month);
				String newFormat = day + "-" + mnth + "-" + year;
				if (mnth.equalsIgnoreCase("12")) {
					date = "December " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("11")) {
					date = "November " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("10")) {
					date = "October " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("9")) {
					date = "September " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("8")) {
					date = "August " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("7")) {
					date = "July " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("6")) {
					date = "June " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("5")) {
					date = "May " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("4")) {
					date = "April " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("3")) {
					date = "March " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("2")) {
					date = "February " + day + "," + year;
				} else if (mnth.equalsIgnoreCase("1")) {
					date = "January " + day + "," + year;
				}

				return date;
			}

		} catch (Exception e) {
		}
		return date;
	}

	public boolean isAuthor(String strPostAuthor2, String UserID) {
		if (strPostAuthor2.equals(UserID)) {
			return true;
		} else {
			return false;
		}
	}

	public int pxToDp(int px) {

		int val = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, mContext.getResources().getDisplayMetrics());
		return val;
		// return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}

	private Bitmap getStarImage(int average) {
		switch (average) {
		case 1:
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.one_star_fb);
		case 2:
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.two_star_fb);
		case 3:
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.three_star_fb);
		case 4:
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.four_star_fb);
		case 5:
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.five_star_fb);

		default:
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.three_star_fb);
		}
	}

	public Bitmap joinImages(Bitmap mainImage, Bitmap topBar, Bitmap overlay, String starRating, String CountRating) {
		if (mainImage == null || topBar == null || overlay == null)
			return mainImage;

		Bitmap bmpStar = getStarImage(Integer.parseInt(starRating));

		Bitmap resizedmain = Bitmap.createScaledBitmap(mainImage, 580, 580, true);
		Bitmap resizedbar = Bitmap.createScaledBitmap(topBar, 580, 54, true);
		Bitmap resizedstar = Bitmap.createScaledBitmap(bmpStar, 173, 31, true);
		Bitmap resizedoverlay = Bitmap.createScaledBitmap(overlay, 580, 580, true);

		Bitmap finalImage = Bitmap.createBitmap(580, 635, Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		paint.setTypeface(SplashScreen.ProxiNova_Regular);
		String strRate = "";
		if (CountRating.equalsIgnoreCase("1")) {
			strRate = CountRating + " Rate";
		} else {
			strRate = CountRating + " Rates";
		}

		Path path = new Path();
		// path.addCircle(199, 18, 0, null);
		Canvas canvas = new Canvas(finalImage);
		canvas.drawBitmap(resizedbar, 0, 0, null);
		canvas.drawBitmap(resizedstar, 19, 11, null);
		canvas.drawText(strRate, 210, 35, paint);
		canvas.drawBitmap(resizedmain, 0, 53, null);
		canvas.drawBitmap(resizedoverlay, 0, 54, null);
		return finalImage;
	}

	public String encodePostID(String postID) {
		String EncodedPostID = Base64.encodeToString(postID.getBytes(), Base64.DEFAULT);
		return EncodedPostID;
	}

	public SpannableStringBuilder getclickableText(String comment) {

		String stringBuilder = "";
		SpannableStringBuilder builder = new SpannableStringBuilder();

		for (int i = 0; i < comment.length(); i++) {

			String later = comment.substring(i, i + 1);
			Log.d("TAG", "Char " + later);

			if (later.equalsIgnoreCase("http") || later.equalsIgnoreCase("www")) {

				String remailString = comment.substring(i, comment.length());

				String aTString = "";

				for (int j = 0; j < remailString.length(); j++) {

					if (!remailString.substring(j, j + 1).equalsIgnoreCase(" ")) {
						aTString = aTString + remailString.substring(j, j + 1);
					} else {
						break;
					}

				}
				// stringBuilder = stringBuilder + aTString;
				i = i + aTString.length() - 1;

				SpannableString ss = new SpannableString(aTString);

				ss.setSpan(new MyClickableSpan(aTString), 0, aTString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				builder.append(ss);

				stringBuilder = stringBuilder + ss;

			} else {
				stringBuilder = stringBuilder + later;
				SpannableString ss = new SpannableString(later);
				builder.append(ss);
			}

		}

		return builder;
	}

	class MyClickableSpan extends ClickableSpan {// extend ClickableSpan

		String clicked;

		public MyClickableSpan(String string) {
			// TODO Auto-generated constructor stub
			super();
			clicked = string;
		}

		public void onClick(View textView) {
			TextView tv = (TextView) textView;
			Spanned s = (Spanned) tv.getText();
			int start = s.getSpanStart(this);
			int end = s.getSpanEnd(this);

			String link = s.subSequence(start + 1, end).toString();
			// ClickedUserName = userName;
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			mContext.startActivity(browserIntent);

		}

		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#26BDBE"));
			ds.setUnderlineText(false);

		}
	}
}
