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
import android.telephony.TelephonyManager;
import android.util.Log;

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;

public class PhoneBroadcastReceiver extends BroadcastReceiver {

	private Context context;

	private int prevTelephoneState = TelephonyManager.CALL_STATE_IDLE;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		String intentAction = intent.getAction();
		if (intentAction == null) return;

		PreferenceManager prefs = new PreferenceManager(context);

		if(intentAction.equals("android.provider.Telephony.SMS_RECEIVED") && prefs.getMessagePref()){
			SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
			for (SmsMessage msg: msgs) {
				SMSTransferItem msgToTransfer;

				String msgBody = msg.getMessageBody();
				String msgAddress = msg.getOriginatingAddress();
				String msgName = getContactName(msgAddress);

				msgToTransfer = (msgName != null && !msgName.isEmpty()) ? new SMSTransferItem(msgBody, msgAddress, msgName) : new SMSTransferItem(msgBody, msgAddress);

				startTransferServiceWithExtra(msgToTransfer);
			}

		} else if (intentAction.equals("android.provider.Telephony.WAP_PUSH_RECEIVED") && prefs.getMessagePref()) {
			//ToDO: handle mms body
			Bundle extras = intent.getExtras();
			//String s = new String((byte[])extras.get("header"));

		} else if (intentAction.equals("android.intent.action.PHONE_STATE") && prefs.getPhonePref()) {
			String stateExtra;
			String stateNumber;
			try {
				stateExtra = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
				stateNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
			} catch (NullPointerException e) {
				return;
			}

			if (stateExtra.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				updateTelephoneState(TelephonyManager.CALL_STATE_RINGING, stateNumber);
			} else if (stateExtra.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				updateTelephoneState(TelephonyManager.CALL_STATE_OFFHOOK, stateNumber);
			} else if (stateExtra.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				updateTelephoneState(TelephonyManager.CALL_STATE_IDLE, stateNumber);
			}

		}/* else if (intentAction.equals("android.intent.action.NEW_OUTGOING_CALL")) {
			Log.d("Call", "Outgoing");
		}*/

	}

	// Starts PhoneBtTransferService with a write action and the parameter item serialized as an extra
	private void startTransferServiceWithExtra(BtTransferItem item) {
		Intent btIntent = new Intent(context, PhoneBtTransferService.class);
		btIntent.setAction(PhoneBtTransferService.BT_WRITE);
		Bundle bundle = new Bundle();
		bundle.putSerializable(PhoneBtTransferService.INTENT_EXTRA_WRITE, item);
		btIntent.putExtras(bundle);
		context.startService(btIntent);
	}

	// Gets the contact name for an associated phone number
	private String getContactName(String phoneNumber) {
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

	// Determines and updates the system based on telephone state changes
	private void updateTelephoneState(int state, String phoneNumber) {
		//ToDo: check phoneNumber and multiple simultaneous calls
		if (prevTelephoneState == state) return;
		switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// Incoming call
				startTransferServiceWithExtra(new TelephoneTransferItem(state, phoneNumber));
				Log.d("Phone", "incoming");
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (prevTelephoneState == TelephonyManager.CALL_STATE_RINGING) {
					// Incoming call answered
					startTransferServiceWithExtra(new TelephoneTransferItem(state, phoneNumber));
					Log.d("Phone", "incoming answered");
				} else {
					Log.d("Phone", "outgoing");
					// Outgoing call
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (prevTelephoneState == TelephonyManager.CALL_STATE_RINGING) {
					// Incoming call missed
					startTransferServiceWithExtra(new TelephoneTransferItem(state, phoneNumber));
					Log.d("Phone", "incoming missed");
				} else {
					// Incoming or Outgoing call ended
					Log.d("Phone", "ended");
				}
				break;
		}
		prevTelephoneState = state;
	}
}
