package cuteam17.cuteam17phone;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;

public class MessageBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String intentAction = intent.getAction();
		if (intentAction == null) return;

		if(intentAction.equals("android.provider.Telephony.SMS_RECEIVED")){

			SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
			for (SmsMessage msg: msgs) {
				SMSTransferItem msgToTransfer;

				String msgBody = msg.getMessageBody();
				String msgAddress = msg.getOriginatingAddress();
				String msgName = getContactName(context, msgAddress);
				msgToTransfer = (msgName != null && !msgName.isEmpty()) ? new SMSTransferItem(msgBody, msgAddress, msgName) : new SMSTransferItem(msgBody, msgAddress);

				Intent btIntent = new Intent(context, PhoneBtTransferService.class);
				btIntent.setAction(PhoneBtTransferService.BT_WRITE);
				Bundle bundle = new Bundle();
				bundle.putSerializable(PhoneBtTransferService.INTENT_EXTRA_WRITE, msgToTransfer);
				btIntent.putExtras(bundle);
				context.startService(btIntent);
			}

		} else if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
			//ToDO: handle mms body
			Bundle extras = intent.getExtras();
			String s = new String((byte[])extras.get("header"));
		}

	}

	private String getContactName(Context context, String phoneNumber) {
		if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
			String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

			String contactName="";
			Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					contactName = cursor.getString(0);
				}
				cursor.close();
			}

			return contactName;
		}
		return null;
	}
}
