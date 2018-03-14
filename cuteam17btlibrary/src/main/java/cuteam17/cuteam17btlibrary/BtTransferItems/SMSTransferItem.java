package cuteam17.cuteam17btlibrary.BtTransferItems;

import android.content.Context;

import java.io.Serializable;

public class SMSTransferItem extends BtTransferItem implements Serializable {

	//public TransferItemType type = TransferItemType.SMS;

	private String msg;
	private String phoneNumber;
	private String name;

	public SMSTransferItem(String msg, String phoneNumber) {
		super(TransferItemType.SMS);
		this.msg = msg;
		this.phoneNumber = phoneNumber;
	}

	public SMSTransferItem(String msg, String phoneNumber, String name) {
		this(msg,phoneNumber);
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getName() {
		return name;
	}
}
