package cuteam17.cuteam17phone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {

		Notification newNot = sbn.getNotification();
		Log.d("newNot", newNot.extras.getString("android.title"));
		NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
		//mNotificationManager.notify(1, newNot);

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {

	}
}
