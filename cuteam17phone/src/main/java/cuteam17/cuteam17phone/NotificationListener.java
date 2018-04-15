package cuteam17.cuteam17phone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;

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
			Notification notification = sbn.getNotification();
			Bundle extras = notification.extras;

			NotificationTransferItem item = new NotificationTransferItem();

			item.setApplicationLabel(getApplicationLabel(sbn.getPackageName()));
			item.setWhen(notification.when);
			item.setShowWhen(extras.getBoolean("android.showWhen"));
			item.setTitle(extras.getString("android.title"));
			item.setTitleBig(extras.getString("android.title.big"));
			item.setText(extras.getString("android.text"));
			item.setBigText(extras.getString("android.bigText"));
			item.setSummaryText(extras.getString("android.summaryText"));

			//item.setLargeIcon(notification.getLargeIcon());
			//item.setSmallIcon(notification.getSmallIcon());



			//android.template=android.app.Notification$InboxStyle
			//android.subText

			//android.reduced.images
			//android.picture




			Intent btIntent = new Intent(this, PhoneBtTransferService.class);
			btIntent.setAction(PhoneBtTransferService.BT_WRITE);
			Bundle bundle = new Bundle();
			bundle.putSerializable(PhoneBtTransferService.INTENT_EXTRA_WRITE, item);
			btIntent.putExtras(bundle);
			startService(btIntent);

		}

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {

	}

	private String getApplicationLabel(String packageName) {
		PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo info;
		try {
			info = pm.getApplicationInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			info = null;
		}
		return (String) (info != null ? pm.getApplicationLabel(info) : "");
	}
}
