package com.camrate.settings;

import java.net.URI;

import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.global.Constant;
import com.camrate.global.JSONParser;
import com.camrate.tools.Dialogs;

public class ContactUsActivity extends Activity {
	EditText txtName, txtEmail, txtSub, txtMessage;
	String strName, strEmail, strSub, strMessage;
	public String result;

	JSONParser jparser = new JSONParser();
	public ProgressDialog pd;
	TextView txtbtnContactus, txtlblMain, txtlblemail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
		init();

		findViewById(R.id.btncontactUS).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.btnContactSubmit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getVals();

				if (strName.equals("") && strName.length() == 0) {
					Dialogs.getOKDialog(ContactUsActivity.this, "Please enter your name");
				} else if (strEmail.equals("") && strName.length() == 0) {
					Dialogs.getOKDialog(ContactUsActivity.this, "Please enter your email");
				} else if (strSub.equals("") && strName.length() == 0) {
					Dialogs.getOKDialog(ContactUsActivity.this, "Please enter your subject");
				} else if (strMessage.equals("") && strName.length() == 0) {
					Dialogs.getOKDialog(ContactUsActivity.this, "Please enter your message");
				} else if (!txtEmail.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z0-9]+.[a-z._]+")) {
					Dialogs.getOKDialog(ContactUsActivity.this, "Please enter valid email");
				} else {
					String api = "api=ContactUs&Name=" + strName + "&User_Mail=" + strEmail + "&Subject=" + strSub + "&Message=" + strMessage + "";
					new Post().execute(api);
				}
			}
		});
	}

	protected void getVals() {
		strName = txtName.getText().toString();
		strEmail = txtEmail.getText().toString();
		strSub = txtSub.getText().toString();
		strMessage = txtMessage.getText().toString();
	}

	private void init() {
		txtName = (EditText) findViewById(R.id.txtContactName);
		txtEmail = (EditText) findViewById(R.id.txtContactEmail);
		txtSub = (EditText) findViewById(R.id.txtContactSub);
		txtMessage = (EditText) findViewById(R.id.txtContactMessage);
		txtlblMain = (TextView) findViewById(R.id.lblInviteFriTitle);
		txtbtnContactus = (TextView) findViewById(R.id.btnContactSubmit);
		txtlblemail = (TextView) findViewById(R.id.lblFeedbackQuestion);
		txtName.setTypeface(SplashScreen.ProxiNova_Regular);
		txtEmail.setTypeface(SplashScreen.ProxiNova_Regular);
		txtSub.setTypeface(SplashScreen.ProxiNova_Regular);
		txtMessage.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblemail.setTypeface(SplashScreen.ProxiNova_Regular);
		txtlblMain.setTypeface(SplashScreen.ProxiNova_Bold);
		txtbtnContactus.setTypeface(SplashScreen.ProxiNova_SemiBold);

	}

	class Post extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			pd = ProgressDialog.show(ContactUsActivity.this, null, null, true);

		}

		@Override
		protected String doInBackground(String... params) {

			Constant cc = new Constant();
			String url = cc.GetBaseUrl() + params[0];
			System.out.println(url);
			// cc.GetUrl(url);
			URI encoded_url = cc.Geturl(url);
			System.out.println(encoded_url);
			JSONObject json = jparser.getJSONFromUrl(encoded_url.toString());
			System.out.println(json);
			try {
				result = json.getString("result");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;

		}

		@Override
		protected void onPostExecute(String status) {
			pd.dismiss();
			if (status.equals("1")) {
				Dialogs.getOKDialog(ContactUsActivity.this, "Your message has been sent successfully");

				clearText();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public void clearText() {
		txtName.setText("");
		txtEmail.setText("");
		txtSub.setText("");
		txtMessage.setText("");
	}
}
