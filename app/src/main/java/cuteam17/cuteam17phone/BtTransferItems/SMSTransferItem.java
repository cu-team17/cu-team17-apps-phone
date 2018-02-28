package cuteam17.cuteam17phone.BtTransferItems;

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

	public String getMsg() {
		return msg;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	//ToDo: set name based off contacts
	public String name() {
		return name;
	}
}
