package cuteam17.cuteam17rpi;

import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import cuteam17.cuteam17btlibrary.BtTransferService;

public class RpiBtTransferService extends BtTransferService {

	private HandlerThread thread;

	@Override
	public void onCreate() {
		super.onCreate();
		thread = new HandlerThread("Thread name", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		Looper looper = thread.getLooper();
		setHandler(new RpiBtHandler(this, looper));
	}

	protected void connectionRestart() {
		super.connectionRestart();
		//connectByPref();
	}

	public void onDestroy() {
		super.onDestroy();
		thread.quit();
		thread.interrupt();
	}
}
