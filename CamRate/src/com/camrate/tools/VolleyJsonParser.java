package com.camrate.tools;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.camrate.R;
import com.camrate.volley.AppController;

public class VolleyJsonParser {
	Context context;
	public static SharedPreferences prefs;
	String s = null;
	JSONArray objJson = null;
	ProgressDialog pDialog;
	Map<String, String> mparams;
	public VolleyCallback mcallback;
	public VolleyCallback1 mcallback1;
	Dialog dialog;

	public VolleyJsonParser(Context context) {
		this.context = context;
		initProcessDialog();

	}

	public JSONArray getObjectRequest(final ProgressBar pd, String url, final HashMap<String, String> params, VolleyCallback1 volleyCallback) {
		Log.d("TAG", url);
		// pd.setVisibility(View.VISIBLE);
		this.mparams = params;
		this.mcallback1 = volleyCallback;
		JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				objJson = response;
				mcallback1.onSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d("TAG", "Error: " + error.getMessage());
				Log.e("onErrorResponse", "-->" + error.getMessage());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return mparams;
			}
		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(req, "jarray_req");
		return objJson;
	}

	public void makeStringReq(String url, final HashMap<String, String> params, VolleyCallback volleyCallback) {
		// pd.setVisibility(View.VISIBLE);
		showDialog();
		this.mparams = params;
		this.mcallback = volleyCallback;
		StringRequest strReq = new StringRequest(Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// Log.d("TAG", "Response==>" + response.toString());
				// pd.setVisibility(View.INVISIBLE);
				dismissDialog();
				s = response;
				mcallback.onSuccess(response);

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				VolleyLog.d("TAG", "Error: " + error.getMessage());
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return mparams;
			}

			// /**
			// * Passing some request headers
			// * */
			// @Override
			// public Map<String, String> getHeaders() throws AuthFailureError {
			// Map<String, String> params = new HashMap<String, String>();
			// params.put("Content-Type", "application/x-www-form-urlencoded");
			// return params;
			// }
		};

		// Adding request to request queue

		strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppController.getInstance().addToRequestQueue(strReq, "string_req");

	}

	public interface VolleyCallback {
		void onSuccess(String result);

		void onFailure(String jsonResponse);
	}

	public interface VolleyCallback1 {
		void onSuccess(JSONArray result);

		void onFailure(JSONArray jsonResponse);
	}

	public void initProcessDialog() {

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(context, android.R.style.Theme_Light_NoTitleBar);
		}

		dialog = new Dialog(themedContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custome_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		dialog.setCancelable(false);

	}

	public void showDialog() {
		try {
			if (dialog != null && (!dialog.isShowing())) {
				Log.d("TAG", "SHOW DIALOG");
				dialog.show();
			}
		} catch (Exception e) {

		}
	}

	public void dismissDialog() {
		try {
			if (dialog != null && dialog.isShowing()) {
				Log.d("TAG", "DISMISS DIALOG");
				dialog.dismiss();
			}
		} catch (Exception e) {
		}
	}
}
