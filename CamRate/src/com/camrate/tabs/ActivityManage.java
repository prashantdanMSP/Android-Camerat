package com.camrate.tabs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.camrate.R;

public class ActivityManage {

	public static boolean isFromPostDetail;
	public static boolean isFromComment;
	public static boolean isFromEditPost;
	public static boolean isFromListViewActivity1;
	public static boolean isFromThankyou;

	public static String Post_ID;
	public static int postIndex;
	public static boolean isForDelete;
	public static int deletePostion;

	public static int lastSeletedTab;

	public static String Email_list;

	public static void clearData() {
		isFromPostDetail = false;
		isFromComment = false;
		isFromEditPost = false;
		isForDelete = false;

		Post_ID = null;
		postIndex = 0;
		deletePostion = 0;

	}

}