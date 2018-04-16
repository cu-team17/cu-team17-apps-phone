package cuteam17.cuteam17rpi.Overlays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cuteam17.cuteam17btlibrary.BtTransferItems.NotificationTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17rpi.R;

public class NotificationsOverlayService extends OverlayService {

	public static final String INTENT_ACTION_UPDATE = "cuteam17.cuteam17rpi.NotificationsOverlay.UPDATE";

	private View overlayView;

	//ToDo: append new notifications to the linearlayout if there is already a notification being overlayed. That way you can view multiple notifications at once
	private BroadcastReceiver notificationsUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == null) return;

			if (action.equals(INTENT_ACTION_UPDATE)) {
				NotificationTransferItem item = (NotificationTransferItem) getBtTransferItem(intent);
				if (item != null) {
					LinearLayout list = overlayView.findViewById(R.id.notifications_list);
					list.addView(createNotificationView(item));
				}
				cancelScheduledRemoval();
				createScheduledRemoval(10);
			}

		}
	};

	private View createNotificationView(NotificationTransferItem item) {
		View view = null;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater != null) {
			view = inflater.inflate(R.layout.transfer_notification_tile, null);

			setViewText(item.getApplicationLabel(), view, R.id.app_label, null);
			setViewText(item.getSummaryText(), view, R.id.summary_text, R.id.summary_text_spacer);
			//setViewText(item.getW);
			setViewText(item.getTitle(), view, R.id.notification_title, null);
			setViewText(item.getText(), view, R.id.notification_text, null);
		}
		return view;
	}

	private void setViewText(String text, View parent, Integer viewId, Integer enableViewId) {
		if (text != null) {
			TextView textView = parent.findViewById(viewId);
			textView.setText(text);
			textView.setVisibility(View.VISIBLE);
			if (enableViewId != null) {
				View enableView = parent.findViewById(enableViewId);
				enableView.setVisibility(View.VISIBLE);
			}

		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		registerReceiver(notificationsUpdateReceiver, new IntentFilter(INTENT_ACTION_UPDATE));

		NotificationTransferItem item = (NotificationTransferItem) getBtTransferItem(intent);
		if (item != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater != null) {
				overlayView = inflater.inflate(R.layout.transfer_notification_container, null);

				LinearLayout list = overlayView.findViewById(R.id.notifications_list);
				list.addView(createNotificationView(item));

				addOverlayView(overlayView, 10);
			}
		}
		return START_NOT_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(notificationsUpdateReceiver);
	}

}
