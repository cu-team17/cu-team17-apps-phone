package cuteam17.cuteam17rpi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

import cuteam17.cuteam17btlibrary.BtOperations;
import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.NotificationTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TransferItemType;

public class RpiBtHandler extends Handler {

	private Context mContext;

	public RpiBtHandler(Context context, Looper looper) {
		super(looper);
		mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		BtOperations op = BtOperations.get(msg.what);
		switch (op) {
			case BT_READ:
				TransferItemType type = TransferItemType.getByHeader(msg.arg1);
				switch (type) {
					case SMS:
						handleSMS(msg);
						break;
					case TELEPHONE_CALL:
						handleTelephone(msg);
						break;
					case NOTIFICATION:
						handleNotification(msg);
						break;
				}
				break;
			case BT_WRITE:
				break;
			case BT_STATE_UPDATE:
				break;
		}
	}

	private void handleSMS(Message msg) {
		SMSTransferItem item = deserializeMessageObject(msg, SMSTransferItem.class);
		if (item != null) {
			Intent overlay = new Intent(mContext, OverlayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(OverlayActivity.INTENT_EXTRA, item);
			overlay.putExtras(bundle);
			mContext.startActivity(overlay);
		}
	}

	private void handleTelephone(Message msg) {
		TelephoneTransferItem itme = deserializeMessageObject(msg, TelephoneTransferItem.class);
		Intent i = new Intent("cuteam17.cuteam17btlibrary.Click");
		mContext.sendBroadcast(i);
	}

	private void handleNotification(Message msg) {
		NotificationTransferItem item = deserializeMessageObject(msg, NotificationTransferItem.class);
		if (item != null) {
			Log.d("Really this worked", item.getPackageName());
		}

	}

	private <T extends BtTransferItem> T deserializeMessageObject(Message msg, Class<T> type) {
		Object returnObject;
		try {
			ObjectInputStream objectStream;
			ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])msg.obj);
			objectStream = new ObjectInputStream(inputStream);
			returnObject = objectStream.readObject();
		} catch (IOException e) {
			Log.e("BtHandler", "IOException");
			return null;
			//ToDo: handle exception
		} catch (ClassNotFoundException e) {
			Log.e("BtHandler", "ClassNotFoundException");
			return null;
		}

		return type.cast(returnObject);

	}
}