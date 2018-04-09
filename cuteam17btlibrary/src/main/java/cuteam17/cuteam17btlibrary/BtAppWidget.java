package cuteam17.cuteam17btlibrary;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BtAppWidget extends AppWidgetProvider {

	private static final String SYNC_CLICKED = "buttonclick";

	private static boolean btOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (SYNC_CLICKED.equals(intent.getAction())) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bt_app_widget);
			if (btOn) {
				views.setImageViewResource(R.id.widget_bt_connected_state_icon, R.drawable.bt_phone_not_connected_icon);
				btOn = false;
			} else {
				views.setImageViewResource(R.id.widget_bt_connected_state_icon, R.drawable.bt_phone_connected_icon);
				btOn = true;
			}

			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(new ComponentName(context, BtAppWidget.class), views);
		}
	}

	protected PendingIntent getPendingSelfIntent(Context context, String action) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
								int appWidgetId) {

		// Construct the RemoteViews object
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bt_app_widget);

		views.setOnClickPendingIntent(R.id.widget_bt_connected_state_icon, getPendingSelfIntent(context, SYNC_CLICKED));

		// Instruct the widget manager to update the widget
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
		Log.d("App", "Created");
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}
}

