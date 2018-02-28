package cuteam17.cuteam17phone.BtTransferItems;

public enum TransferItemType {
	SMS(49), MMS(50), INCOMING_PHONE_CALL(51), NOTIFICATION(52);

	public final char header;

	TransferItemType(int header) {
		this.header = (char)header;
	}
}
