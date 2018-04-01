package cuteam17.cuteam17btlibrary;

public enum BtOperations {
	BT_READ, BT_WRITE, BT_STATE_UPDATE;

	public static BtOperations get(int value) {
		return BtOperations.values()[value];
	}
}
