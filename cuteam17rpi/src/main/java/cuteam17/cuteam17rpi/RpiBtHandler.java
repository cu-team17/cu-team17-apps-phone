package cuteam17.cuteam17rpi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import cuteam17.cuteam17btlibrary.BtAppWidget;
import cuteam17.cuteam17btlibrary.BtOperations;
import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.NotificationTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TransferItemType;
import cuteam17.cuteam17rpi.Overlays.NotificationsOverlayService;
import cuteam17.cuteam17rpi.Overlays.OverlayActivity;
import cuteam17.cuteam17rpi.Overlays.OverlayService;
import cuteam17.cuteam17rpi.Overlays.SMSOverlayService;
import cuteam17.cuteam17rpi.Overlays.TelephoneOverlayService;

//ToDo: make RpiBtHandler and PhoneBtHandler subclasses of a BtHandler class
public class RpiBtHandler extends Handler {

	private Context mContext;

	private boolean temp = false;

	public RpiBtHandler(Context context, Looper looper) {
		super(looper);
		mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		BtOperations operation = BtOperations.get(msg.what);
		switch (operation) {
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
				// Potential implementation options after BluetoothTransferService writes to the bluetooth socket
				break;
			case BT_STATE_UPDATE:
				Intent intent = new Intent(mContext, RpiBtAppWidget.class);
				intent.setAction(BtAppWidget.BT_UPDATE);
				intent.putExtra(BtAppWidget.EXTRA_STATE, (String) msg.obj);
				mContext.sendBroadcast(intent);
				break;
		}
	}

	private void handleSMS(Message msg) {
		SMSTransferItem item = deserializeMessageObject(msg, SMSTransferItem.class);
		if (item != null) {
			startOverlayActivity(item, SMSOverlayService.class);
		}
	}

	private void handleTelephone(Message msg) {
		TelephoneTransferItem item = deserializeMessageObject(msg, TelephoneTransferItem.class);

		if (item != null) {
			Intent intent;
			//ToDo: check who the phone call is from, don't assume it is from the previous TelephoneTransferItem
			switch (item.getTelephoneState()) {
				case TelephonyManager.CALL_STATE_RINGING:
					startOverlayActivity(item, TelephoneOverlayService.class);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
				case TelephonyManager.CALL_STATE_IDLE:
					intent = new Intent(TelephoneOverlayService.INTENT_ACTION_UPDATE);
					Bundle bundle = new Bundle();
					bundle.putSerializable(OverlayActivity.BT_TRANSFER_ITEM_EXTRA, item);
					intent.putExtras(bundle);
					mContext.sendBroadcast(intent);
					break;
			}

		}
	}

	private void handleNotification(Message msg) {
		if (!temp) {
			NotificationTransferItem item = deserializeMessageObject(msg, NotificationTransferItem.class);
			if (item != null) {
				startOverlayActivity(item, NotificationsOverlayService.class);
			}
			temp = true;
		}

	}

	// Deserialize input message obj to the specified type
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

	//ToDo: try to change type to Class that then gets the respective class name
	private <T extends OverlayService> void startOverlayActivity(BtTransferItem item, Class<T> overlayServiceType) {
		if (item != null) {
			Intent overlay = new Intent(mContext, OverlayActivity.class);
			Bundle bundle = new Bundle();
			// Unable to transfer a class as an extra, so instead pass the string name which is later used to create the class
			// This could be avoided by making subclasses of OverlayActivity for each respective Transfer item overlay type(telephone, sms, etc.)
			// and instead starting those activities here
			bundle.putString(OverlayActivity.OVERLAY_CLASS_TYPE, overlayServiceType.getName());
			bundle.putSerializable(OverlayActivity.BT_TRANSFER_ITEM_EXTRA, item);
			overlay.putExtras(bundle);
			mContext.startActivity(overlay);
		}
	}
}