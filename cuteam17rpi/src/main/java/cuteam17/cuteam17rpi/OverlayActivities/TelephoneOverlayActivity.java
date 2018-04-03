package cuteam17.cuteam17rpi.OverlayActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17rpi.R;

public class TelephoneOverlayActivity extends OverlayActivity {

	public static final String INTENT_ACTION_UPDATE = "cuteam17.cuteam17rpi.TelephoneOverlay.UPDATE";

	public static final String EXTRA_OBJ = "state";

	private View view;

	private long startTime;
	private long endTime = 30000;

	private BroadcastReceiver telephoneUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == null) return;

			if (action.equals(INTENT_ACTION_UPDATE)) {
				TelephoneTransferItem item = (TelephoneTransferItem) getBtTransferItem(intent);
				if (item != null) {
					if (item.getTelephoneState() == TelephonyManager.CALL_STATE_OFFHOOK) {
						removeOverlayView();
					} else if (item.getTelephoneState() == TelephonyManager.CALL_STATE_IDLE) {
						TextView telephone_call_header = view.findViewById(R.id.telephone_call_header);
						//ToDO: set text and color from values
						telephone_call_header.setText("Missed Call");
						telephone_call_header.setTextColor(Color.parseColor("#C13A43"));

						startTime = System.currentTimeMillis();
						endTime = 4000;
					}
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		registerReceiver(telephoneUpdateReceiver, new IntentFilter(INTENT_ACTION_UPDATE));

		TelephoneTransferItem item = (TelephoneTransferItem) getBtTransferItem(getIntent());
		if (item != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater != null) {
				view = inflater.inflate(R.layout.transfer_telephone_call, null);

				String contactName = item.getContactName();
				String phoneNumber = formatPhoneNumber(item.getPhoneNumber());
				if (contactName != null && !contactName.isEmpty()) {
					TextView caller_id = view.findViewById(R.id.caller_id);
					caller_id.setText(contactName);
					if (phoneNumber != null && !phoneNumber.isEmpty()) {
						TextView caller_id_extra = view.findViewById(R.id.caller_id_extra);
						caller_id_extra.setVisibility(View.VISIBLE);
						caller_id_extra.setText(phoneNumber);
					}
				} else {
					if (phoneNumber != null && !phoneNumber.isEmpty()) {
						TextView caller_id_extra = view.findViewById(R.id.caller_id);
						caller_id_extra.setText(phoneNumber);
					}
				}

				addOverlayView(view);
			}
		}

		finish();
	}

	protected void onDestroy() {
		super.onDestroy();

		//ToDo: convert to a job scheduler where broadcast receiver is checked, this is a bad way of doing it
		startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < endTime) {
			try {
				Thread.sleep(1000);
			} catch(Exception e) {}
		}
		removeOverlayView();

		unregisterReceiver(telephoneUpdateReceiver);
	}

	private String formatPhoneNumber(String rawPhoneNumber) {

		if (rawPhoneNumber != null) {
			java.text.MessageFormat phoneNumberFmt = new java.text.MessageFormat("({0})-{1}-{2}");
			String[] phoneNumArr={rawPhoneNumber.substring(0, 3),
					rawPhoneNumber.substring(3,6),
					rawPhoneNumber.substring(6)};
			return phoneNumberFmt.format(phoneNumArr);
		}

		return null;
	}

}
