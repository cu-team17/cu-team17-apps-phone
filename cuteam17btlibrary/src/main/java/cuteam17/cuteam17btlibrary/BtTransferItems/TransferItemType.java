package cuteam17.cuteam17btlibrary.BtTransferItems;

import java.util.HashMap;
import java.util.Map;

public enum TransferItemType {
	SMS(49),
	MMS(50),
	TELEPHONE_CALL(51),
	NOTIFICATION(52);

	public final char header;

	private static final Map<Character, TransferItemType> lookup = new HashMap<Character, TransferItemType>();

	static {
		for (TransferItemType type : TransferItemType.values()) {
			lookup.put(type.header, type);
		}
	}

	TransferItemType(int header) {
		this.header = (char)header;
	}

	public static TransferItemType getByHeader(int headerInt) {
		return lookup.get((char)headerInt);
	}

	public static TransferItemType getByHeader(char headerChar) {
		return lookup.get(headerChar);
	}
}
