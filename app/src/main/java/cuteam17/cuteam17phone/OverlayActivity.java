package cuteam17.cuteam17phone;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cuteam17.cuteam17phone.BtTransferItems.SMSTransferItem;

public class OverlayActivity extends AppCompatActivity {

	public static final String INTENT_EXTRA = "EXTRA";

	private View msgView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		SMSTransferItem msg;
		try {
			msg = (SMSTransferItem) intent.getExtras().getSerializable(INTENT_EXTRA);
		} catch(Exception e) {
			return;
		}

		//setContentView(R.layout.activity_overlay);
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		msgView = vi.inflate(R.layout.transfer_sms_message, null);

		TextView t = msgView.findViewById(R.id.MsgBody);
		t.setText(msg.getMsg());

		int overlayParam = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		if (Build.VERSION.SDK_INT >= 26) {
			overlayParam = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		}
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
		wm.addView(msgView, params);
		finish();
	}

	public void onDestroy(){
		super.onDestroy();
		if (msgView != null) {
			try {
				Thread.sleep(7500);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				wm.removeView(msgView);
			} catch(Exception e) {}

		}

	}
}
