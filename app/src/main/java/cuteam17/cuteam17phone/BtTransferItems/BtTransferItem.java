package cuteam17.cuteam17phone.BtTransferItems;

import java.io.Serializable;

public abstract class BtTransferItem implements Serializable {

	public TransferItemType type;

	public BtTransferItem(TransferItemType type) {
		this.type = type;
	}
}
