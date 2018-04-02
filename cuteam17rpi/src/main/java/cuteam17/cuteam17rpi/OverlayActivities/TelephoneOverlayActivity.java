package cuteam17.cuteam17rpi.OverlayActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17rpi.R;

public class TelephoneOverlayActivity extends OverlayActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TelephoneTransferItem item;
		try {
			item = (TelephoneTransferItem) getBtTransferItem();
		} catch(NullPointerException e) {
			return;
		}

		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = vi.inflate(R.layout.transfer_telephone_call, null);

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

		finish();
	}

	protected void onDestroy() {
		super.onDestroy();

		try {
			Thread.sleep(7500);
			removeOverlayView();
		} catch(Exception e) {}
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
