package cuteam17.cuteam17phone;

import android.Manifest;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cuteam17.cuteam17btlibrary.BtFindDeviceActivity;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferItems.TelephoneTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferService;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_MESSAGE_PERMISSIONS = 200;
	private static final int REQUEST_PHONE_PERMISSIONS = 210;
	private static final int REQUEST_LOCATION_PERMISSIONS = 220;
	private static final int REQUEST_CODE_DEVICE_LIST_ACTIVITY = 100;
	private static final int REQUEST_ENABLE_BLUETOOTH = 110;

	private BluetoothAdapter mBluetoothAdapter;

	private PreferenceManager prefs = new PreferenceManager(this);

	private final CompoundButton.OnCheckedChangeListener notificationSwitchListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			if (isChecked) {
				if (!notificationListenerEnabled()) {
					Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
					startActivity(intent);
					view.setChecked(false);
					return;
				}
			}
			prefs.setNotificationPref(isChecked);
		}
	};

	private final CompoundButton.OnCheckedChangeListener messageSwitchListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			if (isChecked) {
				if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS}, REQUEST_MESSAGE_PERMISSIONS);
					view.setChecked(false);
					return;
				}
			}
			prefs.setMessagePref(isChecked);
		}
	};

	private final CompoundButton.OnCheckedChangeListener phoneSwitchListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			if (isChecked) {
				if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS}, REQUEST_PHONE_PERMISSIONS);
					view.setChecked(false);
					return;
				}
			}
			prefs.setPhonePref(isChecked);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
		}

		setupSwitches();
	}

	public void setupBluetooth(View view) {
		if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
			} else {
				Intent serverIntent = new Intent(this, BtFindDeviceActivity.class);
				startActivityForResult(serverIntent, REQUEST_CODE_DEVICE_LIST_ACTIVITY);
			}
		}
	}

	public void startBTAccept() {
		Intent intent = new Intent(this, PhoneBtTransferService.class);
		intent.setAction(PhoneBtTransferService.BT_START);
		startService(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_DEVICE_LIST_ACTIVITY:
				if (resultCode == RESULT_OK) {
					try {
						String btDeviceAdr = data.getExtras().getString(BtFindDeviceActivity.EXTRA_DEVICE_ADDRESS);
						SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("BT_Connected_Device", btDeviceAdr);
						editor.apply();

						startBTAccept();
					} catch (NullPointerException e) {
						return;
					}
				}
				break;
			case REQUEST_ENABLE_BLUETOOTH:
				if (resultCode == RESULT_OK) {
					Intent serverIntent = new Intent(this, BtFindDeviceActivity.class);
					startActivityForResult(serverIntent, REQUEST_CODE_DEVICE_LIST_ACTIVITY);
				} else {
					Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
					finish();
				}
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_LOCATION_PERMISSIONS:
				for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
					if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						//ToDO: possible better way of setting up bluetooth
						setupBluetooth(null);
					}
				}
				break;
			case REQUEST_MESSAGE_PERMISSIONS:
				for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
					if (permissions[i].equals(Manifest.permission.RECEIVE_SMS) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						Switch messageSwitch = findViewById(R.id.message_switch);
						messageSwitch.setChecked(true);
					}
				}
				break;
			case REQUEST_PHONE_PERMISSIONS:
				for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
					if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						Switch phoneSwitch = findViewById(R.id.phone_call_switch);
						phoneSwitch.setChecked(true);
					}
				}
		}
	}

	private void setupSwitches() {
		PreferenceManager prefs = new PreferenceManager(this);

		Switch messageSwitch = findViewById(R.id.message_switch);
		if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED && prefs.getMessagePref()) {
			messageSwitch.setChecked(true);
		}
		messageSwitch.setOnCheckedChangeListener(messageSwitchListener);

		Switch notificationSwitch = findViewById(R.id.notification_switch);
		if (notificationListenerEnabled() && prefs.getNotificationPref()) {
			notificationSwitch.setChecked(true);
		}
		notificationSwitch.setOnCheckedChangeListener(notificationSwitchListener);

		Switch phoneSwitch = findViewById(R.id.phone_call_switch);
		if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && prefs.getPhonePref()) {
			phoneSwitch.setChecked(true);
		}
		phoneSwitch.setOnCheckedChangeListener(phoneSwitchListener);
	}

	private boolean notificationListenerEnabled() {
		ComponentName cn = new ComponentName(this, NotificationListener.class);
		String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
		return flat != null && flat.contains(cn.flattenToString());
	}
}
