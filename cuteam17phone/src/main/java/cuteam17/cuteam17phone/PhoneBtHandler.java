package cuteam17.cuteam17phone;

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

import cuteam17.cuteam17btlibrary.BtAppWidget;
import cuteam17.cuteam17btlibrary.BtOperations;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TransferItemType;

//ToDo: make RpiBtHandler and PhoneBtHandler subclasses of a BtHandler class
public class PhoneBtHandler extends Handler {

	private Context mContext;

	public PhoneBtHandler(Context context, Looper looper) {
		super(looper);
		mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		BtOperations operation = BtOperations.values()[msg.what];
		switch (operation) {
			case BT_READ:
				TransferItemType type = TransferItemType.getByHeader(msg.arg1);
				switch (type) {
					case SMS:
						handleSMS(msg);
						break;
				}

				break;
			case BT_WRITE:
				break;
			case BT_STATE_UPDATE:
				Intent intent = new Intent(mContext, PhoneBtAppWidget.class);
				intent.setAction(BtAppWidget.BT_UPDATE);
				intent.putExtra(BtAppWidget.EXTRA_STATE, (String) msg.obj);
				mContext.sendBroadcast(intent);
				break;
		}
	}

	private void handleSMS(Message msg) {
		ObjectInputStream objectStream = getObjectInputStream(msg);
		SMSTransferItem item;
		try {
			item = (SMSTransferItem) objectStream.readObject();
		} catch (Exception e) {
			return;
			//ToDo: do nothing on the msg
		}
		//ToDo: reply sms
	}

	private ObjectInputStream getObjectInputStream(Message msg) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])msg.obj);
			return new ObjectInputStream(inputStream);
		} catch (IOException e) {
			//ToDo: handle exception
		}
		return null;
	}

}
