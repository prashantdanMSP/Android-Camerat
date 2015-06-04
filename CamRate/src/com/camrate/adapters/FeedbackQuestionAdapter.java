package com.camrate.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.settings.FeedbackQuestionActivity;

public class FeedbackQuestionAdapter extends BaseAdapter {
	Context con;
	private LayoutInflater mInflater;
	ViewHolder holder;
	List<HashMap<String, String>> lstResult;
	String desc;
	HashMap<Integer, String> map = new HashMap<Integer, String>();
	List<HashMap<String, String>> lstMap = new ArrayList<HashMap<String, String>>();
	JSONArray arrJSON = new JSONArray();
	StringBuilder sb = new StringBuilder();
	int size;
	String[] store = new String[] {};
	HashMap<Integer, String> mapAns = new HashMap<Integer, String>();

	public FeedbackQuestionAdapter(Context context,
			List<HashMap<String, String>> result) {
		con = context;
		lstResult = result;
		size = lstResult.size();
	}

	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		String type = lstResult.get(position).get(
				FeedbackQuestionActivity.TAG_QUE_TYPE);
		if (convertView == null) {
			holder = new ViewHolder();
			mInflater = (LayoutInflater) con
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.feedback_question_item,
					null);

			holder.lblTitle = (TextView) convertView
					.findViewById(R.id.lblFeedbackQuestionNumber);
			holder.lblQue = (TextView) convertView
					.findViewById(R.id.lblFeedbackQue);
			holder.lblReasonAns = (TextView) convertView
					.findViewById(R.id.lblFeedbackReasonAnswer);
			holder.rdYes = (RadioButton) convertView
					.findViewById(R.id.rdFeedbackYes);
			holder.rdNo = (RadioButton) convertView
					.findViewById(R.id.rdFeedbackNo);
			holder.rdNotSure = (RadioButton) convertView
					.findViewById(R.id.rdFeedbackNotSure);
			holder.txtDesc = (TextView) convertView
					.findViewById(R.id.txtFeedbackAnswer);
			holder.linear = (LinearLayout) convertView
					.findViewById(R.id.linearFeedbackRadio);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		if (!mapAns.isEmpty()) {
			String text = mapAns.get((position + 1));
			holder.txtDesc.setText(text);
		} else {

		}

		if (type.equals("D")) {
			holder.linear.setVisibility(View.GONE);
			holder.lblReasonAns.setText("Click Me to Give Answer");
		} else {
			holder.linear.setVisibility(View.VISIBLE);
			holder.lblReasonAns.setText("Click Me to Give Reason");
		}

		// holder.txtDesc.setHint("Enter the Answer of " + (position + 1));
		holder.lblTitle.setText("Question " + (position + 1) + " of "
				+ lstResult.size());
		holder.lblQue.setText(lstResult.get(position).get(
				FeedbackQuestionActivity.TAG_QUE_DESC));

		holder.lblReasonAns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DisplayDialog((position + 1), holder.lblQue.getText()
						.toString());
			}
		});

		return convertView;
	}

	@SuppressWarnings("deprecation")
	protected void DisplayDialog(final int no, String que) {
		final EditText input = new EditText(con);

		AlertDialog alertDialog = new AlertDialog.Builder(con).create();
		alertDialog.setTitle("Question No." + no);
		alertDialog.setMessage("Question :\n" + que);
		alertDialog.setView(input, 10, 0, 10, 0); // 10 spacing, left and right
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mapAns.put(no, input.getText().toString());
				FeedbackQuestionAdapter.this.notifyDataSetChanged();
			}
		});
		alertDialog.show();
	}

	public void onClick() {
		// for (int i = 0; i < txt.length; i++) {
		// String str = txt[i].getText().toString();
		// if (!str.equals("") || str != null) {
		// System.out.println(str);
		// }
		// }
		// for (int i = 0; i < lstMap.size(); i++) {
		// HashMap<String, String> mp = lstMap.get(i);
		// String qId = mp.get(FeedbackQuestionActivity.TAG_QUE_ID + i);
		// String type = mp.get("Type" + i);
		//
		// writeJSON("", qId, "", type);
		// }
		//
		// System.out.println("" + arrJSON.toString());

		for (int i = 0; i < lstResult.size(); i++) {
			System.out.println("...." + lstResult.get(i).get("Question_ID"));
		}

		for (Map.Entry<Integer, String> e : mapAns.entrySet()) {
			Integer k = e.getKey();
			String ke = e.getValue();
			System.out.println(k + "---------" + ke);
		}

		mapAns.clear();
		FeedbackQuestionAdapter.this.notifyDataSetChanged();

	}

	static class ViewHolder {
		View rowView;
		TextView txtDesc;
		TextView lblQue, lblTitle, lblReasonAns;
		Button btnSubmit;
		RadioButton rdYes, rdNo, rdNotSure;
		LinearLayout linear;
	}

	public void writeJSON(String ans, String qId, String reason, String type) {
		JSONObject object = new JSONObject();
		try {
			object.put("Answer", ans);
			object.put("Question_ID", qId);
			object.put("Reason", reason);
			object.put("Type", type);

			arrJSON.put(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
