package cuteam17.cuteam17rpi;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.Looper;

import cuteam17.cuteam17btlibrary.BtTransferService;

public class RpiBtTransferService extends BtTransferService {

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("Thread name", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		Looper looper = thread.getLooper();
		setHandler(new BtHandler(this, looper));
	}

	protected void connectionRestart() {
		super.connectionRestart();
		connectByPref();
	}
}
