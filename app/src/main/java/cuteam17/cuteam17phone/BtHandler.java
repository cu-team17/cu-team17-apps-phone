package cuteam17.cuteam17phone;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Message;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
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
		Toast.makeText(mContext, item.getMsg(), Toast.LENGTH_SHORT).show();


		LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vd = vi.inflate(R.layout.transfer_sms_message, null);

		TextView t = (TextView) vd.findViewById(R.id.Text);
		t.setText(item.getMsg());

		int overlayParam = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		if (Build.VERSION.SDK_INT >= 26) {
			overlayParam = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		}
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				overlayParam,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
						WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		//params.width = 300;
		//params.height = 300;
		params.gravity = Gravity.CENTER | Gravity.TOP;
		params.setTitle("Load Average");

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		wm.addView(vd, params);

	}

	public void test() {
		Toast.makeText(mContext, "This is a test", Toast.LENGTH_SHORT).show();
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
