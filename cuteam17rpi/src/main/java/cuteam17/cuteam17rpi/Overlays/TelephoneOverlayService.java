package cuteam17.cuteam17rpi.Overlays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17rpi.R;

public class TelephoneOverlayService extends OverlayService {

	public static final String INTENT_ACTION_UPDATE = "cuteam17.cuteam17rpi.TelephoneOverlay.UPDATE";

	private View overlayView;

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
						TextView telephone_call_header = overlayView.findViewById(R.id.telephone_call_header);
						//ToDO: set text and color from values
						telephone_call_header.setText("Missed Call");
						telephone_call_header.setTextColor(Color.parseColor("#C13A43"));

						cancelScheduledRemoval();
						createScheduledRemoval(5);
					}
				}

			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		registerReceiver(telephoneUpdateReceiver, new IntentFilter(INTENT_ACTION_UPDATE));

		TelephoneTransferItem item = (TelephoneTransferItem) getBtTransferItem(intent);
		if (item != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater != null) {
				overlayView = inflater.inflate(R.layout.transfer_telephone_call, null);

				String contactName = item.getContactName();
				String phoneNumber = formatPhoneNumber(item.getPhoneNumber());
				if (contactName != null && !contactName.isEmpty()) {
					TextView caller_id = overlayView.findViewById(R.id.caller_id);
					caller_id.setText(contactName);
					if (phoneNumber != null && !phoneNumber.isEmpty()) {
						TextView caller_id_extra = overlayView.findViewById(R.id.caller_id_extra);
						caller_id_extra.setVisibility(View.VISIBLE);
						caller_id_extra.setText(phoneNumber);
					}
				} else {
					if (phoneNumber != null && !phoneNumber.isEmpty()) {
						TextView caller_id_extra = overlayView.findViewById(R.id.caller_id);
						caller_id_extra.setText(phoneNumber);
					}
				}

				addOverlayView(overlayView, 20);
			}
		}
		return START_NOT_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(telephoneUpdateReceiver);
	}

	private String formatPhoneNumber(String rawPhoneNumber) {

		if (rawPhoneNumber != null && rawPhoneNumber.length() == 10) {
			java.text.MessageFormat phoneNumberFmt = new java.text.MessageFormat("({0})-{1}-{2}");
			String[] phoneNumArr={rawPhoneNumber.substring(0, 3),
					rawPhoneNumber.substring(3,6),
					rawPhoneNumber.substring(6)};
			return phoneNumberFmt.format(phoneNumArr);
		}

		return null;
	}
}
