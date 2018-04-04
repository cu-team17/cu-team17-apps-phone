package cuteam17.cuteam17rpi.Overlays;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17rpi.R;

public class SMSOverlayService extends OverlayService {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SMSTransferItem msg = (SMSTransferItem) getBtTransferItem(intent);
		if (msg != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater != null) {
				View msgView = inflater.inflate(R.layout.transfer_sms_message, null);

				String msgName = msg.getContactName();
				if (msgName != null && !msgName.isEmpty()) {
					TextView header = msgView.findViewById(R.id.MsgHeaderText);
					header.setText(msgName);
				} else {
					try {
						String rawPhoneNumber = msg.getPhoneNumber();
						if (rawPhoneNumber != null && rawPhoneNumber.length() == 10) {
							TextView header = msgView.findViewById(R.id.MsgHeaderText);
							java.text.MessageFormat phoneNumberFmt = new java.text.MessageFormat("({0})-{1}-{2}");
							String[] phoneNumArr = {rawPhoneNumber.substring(0, 3),
									rawPhoneNumber.substring(3, 6),
									rawPhoneNumber.substring(6)};
							header.setText(phoneNumberFmt.format(phoneNumArr));
						}
					} catch (NullPointerException e) {

					}
				}
				TextView body = msgView.findViewById(R.id.MsgBody);
				body.setText(msg.getMsg());

				addOverlayView(msgView, 10);
			}
		}
		return START_NOT_STICKY;
	}
}
