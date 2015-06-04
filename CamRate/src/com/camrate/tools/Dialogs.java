package com.camrate.tools;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class Dialogs {
	public static void getOKDialog(Context con, String msg) {
		Builder alertDialog = new AlertDialog.Builder(con);
		alertDialog.setTitle("CamRate");
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		alertDialog.show();
	}
}
