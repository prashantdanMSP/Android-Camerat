package com.camrate.share;

public class InviteEntryItem implements Item {

	public final EmailAllInvite lawyer;

	public InviteEntryItem(EmailAllInvite layers) {
		this.lawyer = layers;

	}

	@Override
	public boolean isSection() {
		return false;
	}

}
