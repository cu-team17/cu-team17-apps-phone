package cuteam17.cuteam17phone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import cuteam17.cuteam17btlibrary.BtTransferItems.NotificationTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;

public class NotificationListener extends NotificationListenerService {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {

		PreferenceManager prefs = new PreferenceManager(this);
		if (prefs.getNotificationPref()) {
			String pack = sbn.getPackageName();
			Notification notification = sbn.getNotification();
			Bundle extras = notification.extras;


			String title = extras.getString("android.title");
			String titleBig = extras.getString("android.title.big");
			String text = extras.getString("android.text");
			Icon id = notification.getLargeIcon();
			Log.d("newNot", pack);
			//NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
			//mNotificationManager.notify(1, newNot);

			/*
			NotificationTransferItem notificationToTransfer = new NotificationTransferItem(pack, title, text);

			Intent btIntent = new Intent(this, PhoneBtTransferService.class);
			btIntent.setAction(PhoneBtTransferService.BT_WRITE);
			Bundle bundle = new Bundle();
			bundle.putSerializable(PhoneBtTransferService.INTENT_EXTRA_WRITE, notificationToTransfer);
			btIntent.putExtras(bundle);
			startService(btIntent);*/
		}

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {

	}
}
