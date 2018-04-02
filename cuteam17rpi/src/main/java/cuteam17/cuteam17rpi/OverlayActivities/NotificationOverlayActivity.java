package cuteam17.cuteam17rpi.OverlayActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cuteam17.cuteam17btlibrary.BtTransferItems.NotificationTransferItem;
import cuteam17.cuteam17rpi.R;

public class NotificationOverlayActivity extends OverlayActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NotificationTransferItem msg;
		try {
			msg = (NotificationTransferItem) getBtTransferItem();
		} catch(NullPointerException e) {
			return;
		}

		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//ToDo: inflate correct layout for notification
		View msgView = vi.inflate(R.layout.transfer_sms_message, null);

		/*
		String msgName = msg.getContactName();
		if (msgName != null && !msgName.isEmpty()) {
			TextView header = msgView.findViewById(R.id.MsgHeaderText);
			header.setText(msgName);
		} else {
			try {
				String rawPhoneNumber = msg.getPhoneNumber();
				if (rawPhoneNumber != null) {
					TextView header = msgView.findViewById(R.id.MsgHeaderText);
					java.text.MessageFormat phoneNumberFmt = new java.text.MessageFormat("({0})-{1}-{2}");
					String[] phoneNumArr={rawPhoneNumber.substring(0, 3),
							rawPhoneNumber.substring(3,6),
							rawPhoneNumber.substring(6)};
					header.setText(phoneNumberFmt.format(phoneNumArr));
				}
			} catch (NullPointerException e) {

			}
		}

		TextView body = msgView.findViewById(R.id.MsgBody);
		body.setText(msg.getMsg());
		*/

		addOverlayView(msgView);

		finish();
	}

	protected void onDestroy() {
		super.onDestroy();

		try {
			Thread.sleep(7500);
			removeOverlayView();
		} catch(Exception e) {}
	}
}
