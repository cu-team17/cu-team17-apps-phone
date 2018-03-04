package cuteam17.cuteam17phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import cuteam17.cuteam17phone.BtTransferItems.SMSTransferItem;

public class MessageBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String intentAction = intent.getAction();
		if (intentAction == null) return;

		SMSTransferItem msg = new SMSTransferItem("Hi Hi", "3035550303");

		Intent intentTest = new Intent(context, BtTransferService.class);
		intentTest.setAction(BtTransferService.BT_WRITE);
		Bundle bundle = new Bundle();
		bundle.putSerializable(BtTransferService.INTENT_EXTRA_WRITE, msg);
		intentTest.putExtras(bundle);
		context.startService(intentTest);


		if(intentAction.equals("android.provider.Telephony.SMS_RECEIVED")){
			Log.d("Msg", "SMS");

			SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
			for (int i = 0; i < msgs.length; i++) {
				//Log.d("Msg", msgs[i].getOriginatingAddress() + " " + msgs[i].getMessageBody());
				//SMSTransferItem msg = new SMSTransferItem(msgs[i].getMessageBody(), msgs[i].getOriginatingAddress());

			}

		} else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
			Log.d("Msg", "WAP");

			//ToDO: handle mms body
			Bundle extras = intent.getExtras();
			String s = new String((byte[])extras.get("header"));
		}

	}
}
