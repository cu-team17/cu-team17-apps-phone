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

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;

public abstract class OverlayService extends Service {

	private View overlayView;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected void addOverlayView(View overlayView) {
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
	}

	protected void removeOverlayView() {
		if (overlayView != null) {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			if (wm != null) wm.removeView(overlayView);
			overlayView = null;
		}
	}

	protected BtTransferItem getBtTransferItem(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			return (BtTransferItem) intent.getExtras().getSerializable(OverlayActivity.BT_TRANSFER_ITEM_EXTRA);
		} else {
			return null;
		}
	}
}
