package com.camrate.share;

public class EntryItem implements Item {

	public final EmailAll lawyer;

	public EntryItem(EmailAll layers) {
		this.lawyer = layers;

	}

	@Override
	public boolean isSection() {
		return false;
	}

}
