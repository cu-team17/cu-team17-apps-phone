package cuteam17.cuteam17phone;

import java.io.Serializable;

public class SMSTransferItem implements Serializable {

	private static TransferItemType type = TransferItemType.SMS;

	private String msg;
	private String phoneNumber;
	private String name;

	public SMSTransferItem(String msg, String phoneNumber) {
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
