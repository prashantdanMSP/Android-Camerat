package com.camrate.share;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.camrate.R;
import com.camrate.SplashScreen;
import com.camrate.share.PinnedSectionListView.PinnedSectionListAdapter;

public class ContactListAdapter extends ArrayAdapter<Item> implements PinnedSectionListAdapter {

	ArrayList<Item> items;
	private LayoutInflater inflater;

	public ContactListAdapter(Context context, int resource, int textViewResourceId, ArrayList<Item> items) {
		super(context, resource, textViewResourceId, items);
		this.items = items;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void updateListView(ArrayList<Item> items) {
		this.items = items;
		notifyDataSetChanged();

	}

	protected void prepareSections(int sectionsNumber) {
	}

	protected void onSectionAdded(Item section, int sectionPosition) {
	}

	public class Holder {

		private TextView title;
	}

	public class HolderChild {

		private TextView title;
		private CheckBox checkBox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Holder holder;
		HolderChild holderChield;
		final Item item = items.get(position);
		if (item != null) {
			if (item.isSection()) {

				if (convertView == null) {
					holder = new Holder();
					v = inflater.inflate(R.layout.list_item_section, null, false);
					holder.title = (TextView) v.findViewById(R.id.title);
					v.setTag(holder);
				} else {
					holder = (Holder) v.getTag();
				}

				SectionItem si = (SectionItem) item;

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				holder.title.setText(si.getTitle());

			} else {

				if (convertView == null) {
					holderChield = new HolderChild();
					v = inflater.inflate(R.layout.list_style_people, null, false);
					holderChield.title = (TextView) v.findViewById(R.id.title);
					holderChield.checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
					v.setTag(holderChield);
				} else {
					holderChield = (HolderChild) v.getTag();
				}

				final EntryItem ei = (EntryItem) item;

				holderChield.title.setText(ei.lawyer.getEmail());
				holderChield.title.setTypeface(SplashScreen.ProxiNova_Regular);

				if (ei.lawyer.isChecked()) {
					holderChield.checkBox.setChecked(true);
				} else {
					holderChield.checkBox.setChecked(false);
				}

				holderChield.checkBox.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						CheckBox ch = (CheckBox) v;
						EntryItem entyItem = (EntryItem) item;

						if (ch.isChecked()) {
							entyItem.lawyer.setChecked(true);
						} else {
							entyItem.lawyer.setChecked(false);
						}

					}
				});

			}

		}
		return v;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {

		if (items.get(position).isSection()) {
			return 1;
		}

		return 0;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {

		if (viewType == 0) {
			return false;
		} else {
			return true;
		}

	}

}