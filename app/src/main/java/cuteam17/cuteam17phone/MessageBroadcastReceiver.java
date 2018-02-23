package cuteam17.cuteam17phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String intentAction = intent.getAction();
		if (intentAction == null) return;

		if(intentAction.equals("android.provider.Telephony.SMS_RECEIVED")){
			Log.d("Msg", "SMS");

			SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
			for (int i = 0; i < msgs.length; i++) {
				Log.d("Msg", msgs[i].getOriginatingAddress() + " " + msgs[i].getMessageBody());
				//ToDO: send sms
			}

		} else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
			Log.d("Msg", "WAP");
			Bundle bundle = intent.getExtras();
			//Object[] b = (Object[]) bundle.get("header");
			//ToDO: handle mms body
		}

	}
}
