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

import cuteam17.cuteam17btlibrary.BtOperations;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;

public class BtHandler extends Handler {

	private Context mContext;

	public BtHandler(Context context, Looper looper) {
		super(looper);
		mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		BtOperations op = BtOperations.values()[msg.what];
		switch (op) {
			case BT_READ:
				switch (msg.arg1) {
					//ToDo: change to use header from TransferItemType
					case 49:
						handleSMS(msg);
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
		ObjectInputStream objectStream = getObjectInputStream(msg);
		SMSTransferItem item;
		try {
			item = (SMSTransferItem) objectStream.readObject();
		} catch (Exception e) {
			return;
			//ToDo: do nothing on the msg
		}
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
