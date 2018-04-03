package cuteam17.cuteam17btlibrary.BtTransferItems;

import android.telephony.TelephonyManager;

public class TelephoneTransferItem extends BtTransferItem {

	private int telephoneState = TelephonyManager.CALL_STATE_IDLE;
	private String phoneNumber;
	private String contactName;

	public TelephoneTransferItem(int telephoneState, String phoneNumber) {
		super(TransferItemType.TELEPHONE_CALL);
		this.telephoneState = telephoneState;
		this.phoneNumber = phoneNumber;
	}

	public TelephoneTransferItem(int telephoneState, String phoneNumber, String contactName) {
		this(telephoneState, phoneNumber);
		this.contactName = contactName;
	}

	public int getTelephoneState() {
		return telephoneState;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getContactName() {
		return contactName;
	}
}
