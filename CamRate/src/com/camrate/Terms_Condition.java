package com.camrate;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.camrate.global.JSONParser;
import com.camrate.global.checkInternet;

public class Terms_Condition extends Fragment {

	Context context;
	checkInternet chkNet;

	JSONParser parser = new JSONParser();
	String url="";
	WebView webView;
	 ProgressBar pd;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.terms, container, false);
		context = getActivity();
		chkNet = new checkInternet(context);
		webView=(WebView)view.findViewById(R.id.webView1);
		pd=(ProgressBar)view.findViewById(R.id.progressBar1);
		url=getArguments().getString("url");
		startWebView(url);
		return view;
	}
    private void startWebView(String url) {
        
        //Create new webview Client to show progress dialog
        //When opening a url or click on link
         
        webView.setWebViewClient(new WebViewClient() {      
           
          
            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {              
                view.loadUrl(url);
                return true;
            }
        
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (pd == null) {
                    // in standard case YourActivity.this
                pd.setVisibility(View.VISIBLE);
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                if (pd.isShown()) {
                	pd.setVisibility(View.INVISIBLE);
                }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
             
        }); 
          
         // Javascript inabled on webview  
        webView.getSettings().setJavaScriptEnabled(true); 
         
      
        webView.loadUrl(url);
          
          
    }
     
    // Open previous opened link from history on webview when back button pressed
     
    
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
           
        }
    }
}
