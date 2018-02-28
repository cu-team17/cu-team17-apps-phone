package cuteam17.cuteam17phone;

import android.os.Message;

import android.os.Handler;
import android.util.Log;

import java.util.Arrays;

public class BtHandler extends Handler {

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case 4:
				byte[] writeBuf = (byte[]) msg.obj;
				//Log.d("Handler2", new String(writeBuf, "UTF-8"));

				String s = new String(writeBuf);
				Log.d("Handler", s);
		}
	}
}
