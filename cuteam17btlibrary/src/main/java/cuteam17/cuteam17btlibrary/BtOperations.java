package cuteam17.cuteam17btlibrary;

import java.util.HashMap;
import java.util.Map;

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TransferItemType;

public enum BtOperations {
	BT_READ, BT_WRITE, BT_STATE_UPDATE;

	public static BtOperations get(int value) {
		return BtOperations.values()[value];
	}
}
