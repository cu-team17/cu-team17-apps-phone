package cuteam17.cuteam17btlibrary.BtTransferItems;

import android.telephony.TelephonyManager;

public class TelephoneTransferItem extends BtTransferItem {

	private int telephoneState;
	private String phoneNumber;

	public TelephoneTransferItem(int telephoneState, String phoneNumber) {
		super(TransferItemType.INCOMING_PHONE_CALL);
		this.telephoneState = telephoneState;
		this.phoneNumber = phoneNumber;
	}

	public int getTelephoneState() {
		return telephoneState;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
