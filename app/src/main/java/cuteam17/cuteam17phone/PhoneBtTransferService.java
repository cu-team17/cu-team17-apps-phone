package cuteam17.cuteam17phone;

import android.os.HandlerThread;
import android.os.Looper;

import cuteam17.cuteam17btlibrary.*;

public class PhoneBtTransferService extends BtTransferService {

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("Thread name", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		Looper looper = thread.getLooper();
		setHandler(new PhoneBtHandler(this, looper));
	}

	protected void connectionRestart() {
		super.connectionRestart();
		start();
	}


}
