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

		//BtTransferService btTransfer = BtTransferService.getInstance();

		//SMSTransferItem msg = new SMSTransferItem("This is my msg!!!", "3035550303");
		//btTransfer.write(msg, (char)49);

		if(intentAction.equals("android.provider.Telephony.SMS_RECEIVED")){
			Log.d("Msg", "SMS");

			SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
			for (int i = 0; i < msgs.length; i++) {
				//Log.d("Msg", msgs[i].getOriginatingAddress() + " " + msgs[i].getMessageBody());
				SMSTransferItem msg = new SMSTransferItem(msgs[i].getMessageBody(), msgs[i].getOriginatingAddress());

				//ToDo: attempt to connect if btTransfer is not connected
				BtTransferService btTransfer = BtTransferService.getInstance();
				if (btTransfer.getState() == BtTransferService.STATE_CONNECTED) {
					btTransfer.write(msg, (char)49);
				}
			}

		} else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
			Log.d("Msg", "WAP");

			//ToDO: handle mms body
			Bundle extras = intent.getExtras();
			String s = new String((byte[])extras.get("header"));
		}

	}
}
