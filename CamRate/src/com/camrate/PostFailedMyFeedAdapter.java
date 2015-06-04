package com.camrate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.camrate.DataBase.DataBaseAdapter;
import com.camrate.global.Constant;
import com.camrate.global.checkInternet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PostFailedMyFeedAdapter extends ArrayAdapter<HashMap<String, String>> {

	DisplayImageOptions options, options1;
	String query;
	Context c;
	Date date1;

	private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();
	Constant cc = new Constant();
	ImageLoader imageLoader = ImageLoader.getInstance();

	ArrayList<HashMap<String, String>> items;
	private ArrayList<HashMap<String, Object>> itemsRate;
	checkInternet chkNet;
	String Rate;
	int width, height;
	RelativeLayout.LayoutParams paramImage;
	String pos;
	private String finalRate;
	private String count_rate;
	private String userName;
	private String strPostTag;
	private String strPostContent;
	protected String strMessage;
	double lat = 0;

	double lng = 0;
	String imgPath, countRating, AvgRating, strPostId, strUserId;
	ViewHolder holder;
	MyFeed myFeed;
	// VideoView videoview;
	// String
	// Video_Url="http://camrate.com/upload/video/imageOct28201418:051414499711.mp4";
	String Response;

	public PostFailedMyFeedAdapter(MyFeed myFeed, int resource, ArrayList<HashMap<String, String>> items) {
		super(myFeed, resource, items);
		this.c = myFeed;
		this.items = items;
		this.myFeed = myFeed;
		// imageLoader.init(ImageLoaderConfiguration.createDefault(this.c));

		// inflater =
		// (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageLoader.init(ImageLoaderConfiguration.createDefault(c));
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.noimageuser).showImageForEmptyUri(R.drawable.noimageuser).showImageOnFail(R.drawable.noimageuser).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();

		// d = new DrawableBackgroundDownloader();
		Display display = ((Activity) c).getWindowManager().getDefaultDisplay();
		/*
		 * Point size = new Point(); display.getSize(size);
		 */
		width = display.getWidth();
		height = display.getHeight();
		System.out.println("Width--->" + width);
		System.out.println("Height--->" + height);
		System.out.println("IMageview Height and width--->" + (width - 20) / 2);
		paramImage = new RelativeLayout.LayoutParams((width - 20) / 2, (width - 20) / 2);
		int round = (width - 20) / 2;
		chkNet = new checkInternet(c);
		options1 = new DisplayImageOptions.Builder().cacheOnDisk(true).considerExifParams(true).showImageOnLoading(null).showImageForEmptyUri(null).showImageOnFail(null).cacheInMemory(true).build();
	}

	public int getCount() {

		return items.size();
	}

	public HashMap<String, String> getItem(int position) {
		System.out.println("getItem-->" + position);
		return items.get(position);
	}

	public static class ViewHolder {

		TextView tvPostName;
		ProgressBar pd;
		ImageView userPostImage, userPostVideo;

		Button btnRetry, btnDelete;
		VideoView videoview;
		RelativeLayout rel_btn;
		boolean isUploaded = false;
	}

	public long getItemId(int position) {
		System.out.println("getItemID-->" + position);
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		// System.out.println("adapterpos--->"+position);

		if (v == null) {
			holder = new ViewHolder();
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.post_failed, null);
			holder.tvPostName = (TextView) v.findViewById(R.id.textView1);
			holder.userPostImage = (ImageView) v.findViewById(R.id.imgPostFailed);
			holder.userPostVideo = (ImageView) v.findViewById(R.id.imguserpostvideo);
			holder.videoview = (VideoView) v.findViewById(R.id.VideoViewPostFailed);
			holder.rel_btn = (RelativeLayout) v.findViewById(R.id.RelativeRight);
			// holder.videoview.setLayoutParams(paramImage);
			holder.pd = (ProgressBar) v.findViewById(R.id.progressBar1);
			// holder.progressBar = (ProgressBar) v.findViewById(R.id.progress);

			holder.btnRetry = (Button) v.findViewById(R.id.btnRetryPostFailed);
			holder.btnRetry.setTag("retryButton+" + position);
			holder.btnDelete = (Button) v.findViewById(R.id.btnDeletePostFailed);
			holder.btnDelete.setTag("deleteButton+" + position);
			holder.rel_btn.setTag("retryLayout+" + position);
			holder.pd.setTag("progress+" + position);
			holder.btnRetry.setTypeface(SplashScreen.ProxiNova_Bold);
			holder.btnDelete.setTypeface(SplashScreen.ProxiNova_Bold);
			v.setTag("mainTag+" + position);

			if (chkNet.isNetworkConnected()) {
				holder.rel_btn.setVisibility(View.GONE);

				String tagMain = (String) v.getTag();
				String tagValue = tagMain.substring(0, tagMain.indexOf("+"));
				String position1 = tagMain.substring(tagMain.indexOf("+") + 1, tagMain.length());
				if (items.get(position).get("isUploaded").toString().equalsIgnoreCase("0")) {
					holder.pd.setVisibility(View.VISIBLE);
				} else {
					holder.pd.setVisibility(View.INVISIBLE);
				}

			} else {
				System.out.println("No Internet");
				holder.rel_btn.setVisibility(View.VISIBLE);
				holder.pd.setVisibility(View.GONE);
			}

			v.setTag(holder);

		} else {
			holder = (ViewHolder) v.getTag();
		}
		if (holder.tvPostName != null) {
			String strPostTag1 = items.get(position).get("Post_title").toString();
			holder.tvPostName.setText(strPostTag1);
			holder.tvPostName.setTypeface(SplashScreen.ProxiNova_SemiBold);
		}

		if (holder.userPostImage != null) {
			// String path = "file:///mnt/sdcard/CamRate_Images/";
			if (items.get(position).get("Post_videoname").toString().equalsIgnoreCase("1")) {
				String img = (items.get(position).get("Post_imagename").toString());
				String path = "file:///mnt/sdcard/CamRate_Images/" + img;
				imageLoader.displayImage(path, holder.userPostImage, options1);
			} else {
				String img = (items.get(position).get("Post_videoname").toString());
				String path = "file:///mnt/sdcard/CamRate_Video/" + img;
				imageLoader.displayImage(path, holder.userPostImage, options1);
			}
		}
		if (holder.userPostVideo != null) {

			String video = (items.get(position).get("Post_videoname").toString());

			if (video.equalsIgnoreCase("1")) {
				holder.userPostVideo.setVisibility(View.VISIBLE);
				holder.userPostVideo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// holder.progressBar.setVisibility(View.VISIBLE);
						holder.videoview.setVisibility(View.VISIBLE);
						holder.userPostVideo.setVisibility(View.INVISIBLE);
						System.out.println("Buffer");
						try {
							// Start the MediaController
							MediaController mediacontroller = new MediaController(c);
							String Video_Url = (items.get(position).get("Post_videoname").toString());
							// System.out.println("videourl==>"+Video_Url);
							mediacontroller.setAnchorView(holder.videoview);
							// Get the URL from String VideoURL
							// Uri video = Uri.parse(video);
							// videoview.setMediaController(mediacontroller);
							holder.videoview.setMediaController(null);
							Uri video = Uri.parse(Video_Url);
							System.out.println("videourl URi==>" + video.toString());
							holder.videoview.setVideoURI(video);

						} catch (Exception e) {
							Log.e("Error", e.getMessage());
							e.printStackTrace();
						}
						holder.videoview.requestFocus();
						holder.videoview.setOnPreparedListener(new OnPreparedListener() {
							// Close the progress bar and play the video
							public void onPrepared(MediaPlayer mp) {
								// holder.progressBar.setVisibility(View.INVISIBLE);
								holder.userPostVideo.setVisibility(View.INVISIBLE);
								System.out.println("Bufferfinish");
								holder.videoview.start();
							}
						});
						holder.videoview.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO Auto-generated method stub
								holder.videoview.setVisibility(View.INVISIBLE);
								holder.userPostVideo.setVisibility(View.VISIBLE);
							}
						});

					}
				});
			} else {
				holder.userPostVideo.setVisibility(View.INVISIBLE);
			}

			holder.btnRetry.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int pos;

					// pos = v.getTag();
					String tagMain = (String) v.getTag();
					String tagValue = tagMain.substring(0, tagMain.indexOf("+"));
					// String position = tagMain.substring(
					// tagMain.indexOf("+") + 1, tagMain.length());

					System.out.println("tagMain is------" + tagMain);
					System.out.println("tagValue is------" + tagValue);
					System.out.println("position is------" + position);
					// holder.btnRetry = (Button) v.findViewWithTag(pos);
					// holder.btnDelete = (Button) v.findViewWithTag(pos);
					ProgressBar progressBar = (ProgressBar) ((View) v.getParent().getParent()).findViewWithTag("progress+" + position);
					RelativeLayout retryLayout = (RelativeLayout) v.getParent();
					retryLayout.setVisibility(View.GONE);

					progressBar.setVisibility(View.VISIBLE);
					if (chkNet.isNetworkConnected()) {

						myFeed.postDataFromAdapter(position);

					} else {
						System.out.println("No Internet");
						retryLayout.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
					}

					// holder.pd=(ProgressBar)v.findViewWithTag(pos);
					/*
					 * if (holder.btnRetry.getVisibility() == View.VISIBLE) { System.out.println("visible"); holder.btnRetry.setVisibility(View.GONE); holder.btnDelete.setVisibility(View.GONE); holder.pd.setVisibility(View.VISIBLE); if (chkNet.isNetworkConnected()) {
					 * 
					 * postData(pos);
					 * 
					 * } else { System.out.println("No Internet"); } } else {
					 * 
					 * System.out.println("NOtvisible"); holder.btnRetry.setVisibility(View.VISIBLE); holder.btnDelete.setVisibility(View.VISIBLE); }
					 */

					/*
					 * holder.btnRetry.setVisibility(View.GONE); holder.btnDelete.setVisibility(View.GONE);
					 * 
					 * 
					 * holder.pd.setVisibility(View.VISIBLE); invalidateViews(); System.out.println("Retry"); System.out.println("val==>" + items.get(pos).get("Post_title")); if (chkNet.isNetworkConnected()) {
					 * 
					 * postData(pos);
					 * 
					 * } else { holder.btnRetry.setVisibility(View.VISIBLE); holder.btnDelete.setVisibility(View.VISIBLE); pos = (Integer) v.getTag(); holder.pd.setVisibility(View.GONE); invalidateViews(); }
					 */

				}
			});
			holder.btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String tagMain = (String) v.getTag();
					String tagValue = tagMain.substring(0, tagMain.indexOf("+"));
					// String position = tagMain.substring(
					// tagMain.indexOf("+") + 1, tagMain.length());

					System.out.println("tagMain is------" + tagMain);
					System.out.println("tagValue is------" + tagValue);
					System.out.println("position is------" + position);
					System.out.println("Delete");
					System.out.println("Title==>" + items.get(Integer.parseInt(position + "")).get("Post_title"));
					deletPost(Integer.parseInt(position + ""));
					items.remove(position);
					Log.d("TAG", items.size() + " SIZE " + position);
					notifyDataSetChanged();
				}
			});
		}

		return v;

	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	public void invalidateViews() {
		holder.btnRetry.invalidate();
		holder.btnDelete.invalidate();
		holder.pd.invalidate();
	}

	public void deletPost(int pos) {
		DataBaseAdapter mDbHelper = new DataBaseAdapter(c);
		try {
			mDbHelper.createDatabase();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		boolean isDel = mDbHelper.deletePost(Integer.parseInt(items.get(pos).get("Post_id")));
		// if (isDel) {
		// Intent intent = new Intent(c, MyFeed.class);
		// c.startActivity(intent);
		// } else {
		//
		// }
	}

	public static byte[] convertFileToByteArray(File f) {
		byte[] byteArray = null;
		try {
			InputStream inputStream = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024 * 8];
			int bytesRead = 0;

			while ((bytesRead = inputStream.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}

			byteArray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;
	}
}