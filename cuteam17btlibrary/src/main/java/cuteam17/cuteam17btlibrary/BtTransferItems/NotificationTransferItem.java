package cuteam17.cuteam17btlibrary.BtTransferItems;

import android.app.Notification;
import android.app.NotificationManager;

import java.io.Serializable;

public class NotificationTransferItem extends BtTransferItem implements Serializable {

	private String packageName;
	private String title;
	private String text;

	public NotificationTransferItem() {
		super(TransferItemType.NOTIFICATION);
	}

	public NotificationTransferItem(String packageName, String title, String text) {
		super(TransferItemType.NOTIFICATION);
		this.packageName = packageName;
		this.title = title;
		this.text = text;
	}

	public String getPackageName() {
		return packageName;
	}



}
