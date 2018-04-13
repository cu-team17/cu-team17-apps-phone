package cuteam17.cuteam17btlibrary;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class BtAppWidget extends AppWidgetProvider {

	private static final String BT_CONNECTED_STATE_ICON_CLICK = "cuteam17btlibrary.widget.state_icon_click";
	private static final String SETTINGS_BUTTON_CLICK = "cuteam17btlibrary.widget.settings_button_click";

	//ToDo: maybe move to be in bluetooth service and not the widget
	public static final String BT_UPDATE = "cuteam17btlibrary.widget.bt_update";
	public static final String EXTRA_STATE = "cuteam17btlibrary.widget.bt_extra";

	private static boolean btConnected = false;

	private ScheduledFuture<?> scheduledReset;

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
		if (scheduledReset != null) {
			scheduledReset.cancel(false);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String intentAction = intent.getAction();
		if (intentAction == null) return;

		if (intentAction.equals(BT_CONNECTED_STATE_ICON_CLICK)) {
			//ToDo: set timer between allowed touches so user can't spam start/stop
			if (btConnected) {
				stopBt(context);
				setViewsContent(context, true, "Disconnecting...");
			} else {
				startBt(context);
				setViewsContent(context, false, "Connecting...");
			}
			createScheduledReset(context);

		} else if (intentAction.equals(SETTINGS_BUTTON_CLICK)) {
			startMainActivity(context);

		} else if (intentAction.equals(BT_UPDATE)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String state = bundle.getString(EXTRA_STATE, "");
				if (state.equals(BtTransferService.STATE_UPDATE_CONNECTION_SUCCESS)) {
					btConnected = true;
					setViewsContent(context, true, "Connected");
				} else if (state.equals(BtTransferService.STATE_UPDATE_CONNECTION_FAIL)) {
					btConnected = false;
					setViewsContent(context, false, "Failed to Connect", Color.parseColor("#F75D36"));
					createScheduledReset(context);
				} else if (state.equals(BtTransferService.STATE_UPDATE_CONNECTION_DISCONNECTED)) {
					btConnected = false;
					setViewsContent(context, false, "Disconnected");
					createScheduledReset(context);
				}
			}
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

	private void setViewsContent(Context context, boolean isConnected, String connectedStateText) {
		setViewsContent(context, isConnected, connectedStateText, Color.parseColor("#FFFFFF"));

	}

	private void setViewsContent(Context context, boolean isConnected, String connectedStateText, int connectedStateTextColor) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bt_app_widget);

		if (isConnected) {
			views.setImageViewResource(R.id.widget_bt_connected_state_icon, R.drawable.bt_phone_connected_icon);
		} else {
			views.setImageViewResource(R.id.widget_bt_connected_state_icon, R.drawable.bt_phone_not_connected_icon);
		}
		views.setTextViewText(R.id.widget_bt_connected_state_text, connectedStateText);
		views.setTextColor(R.id.widget_bt_connected_state_text, connectedStateTextColor);

		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(new ComponentName(context, this.getClass().getName()), views);
	}

	private void createScheduledReset(final Context context) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		scheduledReset = scheduler.schedule(
				new Runnable() {
					public void run() {
						setViewsContent(context, false, "Not Connected");
					}
				}, 6, TimeUnit.SECONDS);
	}

}

