package cuteam17.cuteam17phone;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

	private final Context mContext;

	private static final String TRANSFER_MESSAGE_PREF = "TRANSFER_MESSAGE_PREF";
	private static final String TRANSFER_NOTIFICATIONS_PREF = "TRANSFER_NOTIFICATIONS_PREF";
	private static final String TRANSFER_PHONE_PREF = "TRANSFER_PHONE_PREF";

	public PreferenceManager(Context context) {
		mContext = context;
	}

	public boolean getMessagePref() {
		SharedPreferences prefs = mContext.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		return prefs.getBoolean(TRANSFER_MESSAGE_PREF, false);
	}

	public void setMessagePref(boolean value) {
		SharedPreferences prefs = mContext.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TRANSFER_MESSAGE_PREF, value);
		editor.apply();
	}

	public boolean getNotificationPref() {
		SharedPreferences prefs = mContext.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		return prefs.getBoolean(TRANSFER_NOTIFICATIONS_PREF, false);
	}

	public void setNotificationPref(boolean value) {
		SharedPreferences prefs = mContext.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TRANSFER_NOTIFICATIONS_PREF, value);
		editor.apply();
	}

	public boolean getPhonePref() {
		SharedPreferences prefs = mContext.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		return prefs.getBoolean(TRANSFER_PHONE_PREF, false);
	}

	public void setPhonePref(boolean value) {
		SharedPreferences prefs = mContext.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TRANSFER_PHONE_PREF, value);
		editor.apply();
	}


}
