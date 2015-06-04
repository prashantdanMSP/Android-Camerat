package com.camrate.global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class checkInternet {
	 Context mContext;
	 

	    // constructor
	    public checkInternet(Context context){
	        this.mContext = context;
	    }

	    public String getUserName(){
	        return "test";
	    }

	    public boolean isNetworkConnected() {
	          ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	         NetworkInfo ni = cm.getActiveNetworkInfo();
	          if (ni == null) {
	           // There are no active networks.
	           return false;
	          } else
	          {
	           return true;
	          }
	         
	    }
	    
	}