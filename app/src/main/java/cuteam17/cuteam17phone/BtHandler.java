package cuteam17.cuteam17phone;

import android.content.Context;
import android.os.Message;

import android.os.Handler;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import cuteam17.cuteam17phone.BtTransferItems.SMSTransferItem;

public class BtHandler extends Handler {

	private Context mContext;

	public BtHandler(Context context) {
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
		//Log.d("Msg", item.getMsg() + " " + item.getPhoneNumber());
		Toast.makeText(mContext, item.getMsg(), Toast.LENGTH_SHORT).show();

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
