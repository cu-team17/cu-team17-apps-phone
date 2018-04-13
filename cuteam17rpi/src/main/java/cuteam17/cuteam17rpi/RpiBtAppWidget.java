package cuteam17.cuteam17rpi;

import android.content.Context;
import android.content.Intent;

import cuteam17.cuteam17btlibrary.BtAppWidget;
import cuteam17.cuteam17btlibrary.BtTransferService;


public class RpiBtAppWidget extends BtAppWidget {

	protected void startBt(Context context) {
		Intent intent = new Intent(context, RpiBtTransferService.class);
		intent.setAction(BtTransferService.BT_CONNECT);
		context.startService(intent);
	}

	protected void stopBt(Context context) {
		Intent intent = new Intent(context, RpiBtTransferService.class);
		intent.setAction(BtTransferService.BT_STOP);
		context.startService(intent);
	}

	protected void startMainActivity(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);

	}
}
