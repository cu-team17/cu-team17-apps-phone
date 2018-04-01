package cuteam17.cuteam17rpi.OverlayActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;

public abstract class OverlayActivity extends AppCompatActivity {

	public static final String BT_TRANSFER_ITEM_EXTRA = "EXTRA_BT_TRANSFER_ITEM";

	private View overlayView;

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
		wm.addView(overlayView, params);
	}

	protected void removeOverlayView() {
		if (overlayView != null) {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(overlayView);
		}
	}

	protected BtTransferItem getBtTransferItem() {
		Intent intent = getIntent();
		try {
			return (BtTransferItem) intent.getExtras().getSerializable(BT_TRANSFER_ITEM_EXTRA);
		} catch(Exception e) {
			return null;
		}

	}
}
