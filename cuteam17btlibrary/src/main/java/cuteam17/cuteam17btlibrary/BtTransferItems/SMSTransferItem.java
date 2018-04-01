package cuteam17.cuteam17btlibrary.BtTransferItems;

import android.content.Context;

import java.io.Serializable;

public class SMSTransferItem extends BtTransferItem implements Serializable {

	private String msg;
	private String phoneNumber;
	private String contactName;

	public SMSTransferItem(String msg, String phoneNumber) {
		super(TransferItemType.SMS);
		this.msg = msg;
		this.phoneNumber = phoneNumber;
	}

	public SMSTransferItem(String msg, String phoneNumber, String contactName) {
		this(msg,phoneNumber);
		this.contactName = contactName;
	}

	public String getMsg() {
		return msg;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getContactName() {
		return contactName;
	}
}
