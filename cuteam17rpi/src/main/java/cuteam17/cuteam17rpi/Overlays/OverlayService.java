package cuteam17.cuteam17rpi.Overlays;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;

public abstract class OverlayService extends Service {

	private ScheduledFuture<?> scheduledRemoval;
	private View overlayView;

	public void onDestroy() {
		super.onDestroy();
		removeOverlayView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected void addOverlayView(View overlayView) {
		addOverlayView(overlayView, 15);
	}

	protected void addOverlayView(View overlayView, int scheduledRemovalDelay) {
		this.overlayView = overlayView;

		int overlayParam = (Build.VERSION.SDK_INT >= 26) ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				overlayParam,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
						WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER | Gravity.TOP;
		params.setTitle("Message");

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		wm.addView(this.overlayView, params);

		createScheduledRemoval(scheduledRemovalDelay);
	}

	//ToDo: overlays are not always removed correctly
	protected void removeOverlayView() {
		cancelScheduledRemoval();

		if (overlayView != null) {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			if (wm != null) {
				wm.removeView(overlayView);
				overlayView = null;
			}
		}

		//ToDo: make sure the service is stopped because it is no longer needed
		stopSelf();
	}

	protected BtTransferItem getBtTransferItem(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return (BtTransferItem) intent.getExtras().getSerializable(OverlayActivity.BT_TRANSFER_ITEM_EXTRA);
		} else {
			return null;
		}
	}

	protected void createScheduledRemoval(int delay) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		scheduledRemoval = scheduler.schedule(
				new Runnable() {
					public void run() {
						removeOverlayView();
					}
				}, delay, TimeUnit.SECONDS);
	}

	protected void cancelScheduledRemoval() {
		if (scheduledRemoval != null) {
			scheduledRemoval.cancel(false);
		}
	}
}
