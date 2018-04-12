package cuteam17.cuteam17phone;

import android.content.Context;
import android.content.Intent;

import cuteam17.cuteam17btlibrary.BtAppWidget;

public class PhoneBtAppWidget extends BtAppWidget {

	protected void startBt(Context context) {
		Intent intent = new Intent(context, PhoneBtTransferService.class);
		intent.setAction(PhoneBtTransferService.BT_START);
		context.startService(intent);
	}

	protected void stopBt(Context context) {
		Intent intent = new Intent(context, PhoneBtTransferService.class);
		intent.setAction(PhoneBtTransferService.BT_STOP);
		context.startService(intent);
	}

	protected void startMainActivity(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);

	}
}
