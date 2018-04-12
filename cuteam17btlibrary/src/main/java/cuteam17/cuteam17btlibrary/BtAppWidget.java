package cuteam17.cuteam17btlibrary;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public abstract class BtAppWidget extends AppWidgetProvider {

	private static final String BT_CONNECTED_STATE_ICON_CLICK = "cuteam17btlibrary.widget.state_icon_click";
	private static final String SETTINGS_BUTTON_CLICK = "cuteam17btlibrary.widget.settings_button_click";

	//ToDo: move to BtTransferService, strings referring to connection state make more sense there
	public static final String BT_UPDATE_STATE_CONNECT_SUCCESS = "cuteam17btlibrary.widget.bt_connect_success";
	public static final String BT_UPDATE_STATE_CONNECT_FAIL = "cuteam17btlibrary.widget.bt_connect_fail";
	public static final String BT_UPDATE_STATE_CONNECT_LOST = "cuteam17btlibrary.widget.bt_connect_lost";
	public static final String BT_UPDATE_STATE_CONNECT_DISCONNECTED = "cuteam17btlibrary.widget.bt_connect_disconnected";


	private static boolean btEnabled = false;

	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
								 int appWidgetId) {

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bt_app_widget);

		views.setOnClickPendingIntent(R.id.widget_bt_connected_state_icon, getPendingSelfIntent(context, BT_CONNECTED_STATE_ICON_CLICK));
		views.setOnClickPendingIntent(R.id.widget_settings_button, getPendingSelfIntent(context, SETTINGS_BUTTON_CLICK));

		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String intentAction = intent.getAction();
		if (intentAction == null) return;

		if (intentAction.equals(BT_CONNECTED_STATE_ICON_CLICK)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bt_app_widget);
			//ToDo: update connected text, also don't update until BtTransferService handler updates
			if (btEnabled) {
				startBt(context);
				views.setImageViewResource(R.id.widget_bt_connected_state_icon, R.drawable.bt_phone_not_connected_icon);
				btEnabled = false;
			} else {
				stopBt(context);
				views.setImageViewResource(R.id.widget_bt_connected_state_icon, R.drawable.bt_phone_connected_icon);
				btEnabled = true;
			}

			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(new ComponentName(context, this.getClass().getName()), views);
		} else if (intentAction.equals(SETTINGS_BUTTON_CLICK)) {
			startMainActivity(context);
		}
	}

	protected abstract void startBt(Context context);

	protected abstract void stopBt(Context context);

	protected abstract void startMainActivity(Context context);

	private PendingIntent getPendingSelfIntent(Context context, String action) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}
}

