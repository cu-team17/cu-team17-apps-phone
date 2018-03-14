package cuteam17.cuteam17phone;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cuteam17.cuteam17btlibrary.BtFindDeviceActivity;
import cuteam17.cuteam17btlibrary.BtTransferItems.SMSTransferItem;
import cuteam17.cuteam17btlibrary.BtTransferService;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_PERMISSIONS_LIST = 101;
	private static final int REQUEST_CODE_DEVICE_LIST_ACTIVITY = 105;
	private static final int REQUEST_ENABLE_BLUETOOTH = 110;

	private static final String TRANSFER_MESSAGE_PREF = "TRANSFER_MESSAGE_PREF";
	private static final String TRANSFER_NOTIFICATIONS_PREF = "TRANSFER_NOTIFICATIONS_PREF";
	private static final String TRANSFER_PHONE_PREF = "TRANSFER_PHONE_PREF";

	private BluetoothAdapter mBluetoothAdapter;

	private final CompoundButton.OnCheckedChangeListener notificationSwitchListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				if (!notificationListenerEnabled()) {
					Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
					startActivity(intent);
				} else {
					//ToDo: enable sending notifications in prefs
				}
			} else {
				
			}
		}
	};

	private final CompoundButton.OnCheckedChangeListener messageSwitchListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				if (!notificationListenerEnabled()) {
					Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
					startActivity(intent);
				} else {
					//ToDo: enable sending notifications in prefs
				}
			} else {
				//ToDo: disable sending notifications in prefs
			}
		}
	};

	private final CompoundButton.OnCheckedChangeListener phoneSwitchListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				if (!notificationListenerEnabled()) {
					Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
					startActivity(intent);
				} else {
					//ToDo: enable sending notifications in prefs
				}
			} else {
				//ToDo: disable sending notifications in prefs
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		establishPermissions();

		setupSwitches();

		/*
		SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		String btDeviceAdr = prefs.getString("BT_Connected_Device", null);
		if (btDeviceAdr != null) {
			// bluetooth setup
		}
		*/

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private boolean notificationListenerEnabled() {
		ComponentName cn = new ComponentName(this, NotificationListener.class);
		String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
		return flat != null && flat.contains(cn.flattenToString());
	}

	public void setupBluetooth(View view) {
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
		} else {
			Intent serverIntent = new Intent(this, BtFindDeviceActivity.class);
			startActivityForResult(serverIntent, REQUEST_CODE_DEVICE_LIST_ACTIVITY);
		}
	}

	//ToDo: remove just for testing
	public void startBT(View view) {
		Intent intent = new Intent(this, PhoneBtTransferService.class);
		intent.setAction(PhoneBtTransferService.BT_START);
		startService(intent);
	}

	//ToDo: remove just for testing
	public void connectBT(View view) {
		Intent intent = new Intent(this, PhoneBtTransferService.class);
		intent.setAction(BtTransferService.BT_CONNECT);
		startService(intent);
	}

	//ToDo: remove just for testing
	public void stopBT(View view) {
		//Intent intent = new Intent(this, PhoneBtTransferService.class);
		//intent.setAction(PhoneBtTransferService.BT_STOP);
		//startService(intent);
	}

	//ToDo: remove, just for testing
	public void writeBT(View view) {
		SMSTransferItem msg = new SMSTransferItem("connected", null);

		Intent intent = new Intent(this, PhoneBtTransferService.class);
		intent.setAction(PhoneBtTransferService.BT_WRITE);
		Bundle bundle = new Bundle();
		bundle.putSerializable(PhoneBtTransferService.INTENT_EXTRA_WRITE, msg);
		intent.putExtras(bundle);
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

						startBT(null);
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

	private boolean establishPermissions() {
		List<String> permissionsList = new ArrayList<String>();

		addPermissionToList(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION);
		addPermissionToList(permissionsList, Manifest.permission.RECEIVE_SMS);
		addPermissionToList(permissionsList, Manifest.permission.READ_PHONE_STATE);
		addPermissionToList(permissionsList, Manifest.permission.READ_CONTACTS);

		if (!permissionsList.isEmpty()) {
			requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_PERMISSIONS_LIST);
			return true;
		}
		return false;

	}

	private void addPermissionToList(List<String> permissionsList, String permission) {
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_PERMISSIONS_LIST:
				for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
					permissionResult(permissions[i],grantResults[i]);
				}
				break;
		}
	}

	private void permissionResult(String permission, int grantResult) {
		switch (permission) {
			case Manifest.permission.RECEIVE_SMS:
				if (grantResult == PackageManager.PERMISSION_GRANTED) {
					//ToDo: messaging permission granted
				} else {
					//ToDo: no messaging permission
				break;
			}
			case Manifest.permission.READ_PHONE_STATE:
				if (grantResult == PackageManager.PERMISSION_GRANTED) {
					//ToDo: phone permission granted
				} else {
					//ToDo: no phone permission
				}
				break;
			case Manifest.permission.READ_CONTACTS:
				if (grantResult == PackageManager.PERMISSION_GRANTED) {
					//ToDo: contacts permission granted
				} else {
					//ToDo: no contacts permission
				}
				break;
		}
	}

	private void setupSwitches() {
		SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);

		Switch messageSwitch = findViewById(R.id.message_switch);
		if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED && prefs.getBoolean(TRANSFER_MESSAGE_PREF, false)) {
			messageSwitch.setChecked(true);
		}
		messageSwitch.setOnCheckedChangeListener(messageSwitchListener);

		Switch notificationSwitch = findViewById(R.id.notification_switch);
		if (notificationListenerEnabled() && prefs.getBoolean(TRANSFER_NOTIFICATIONS_PREF, false)) {
			notificationSwitch.setChecked(true);
		}
		notificationSwitch.setOnCheckedChangeListener(notificationSwitchListener);

		Switch phoneSwitch = findViewById(R.id.phone_call_switch);
		if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && prefs.getBoolean(TRANSFER_PHONE_PREF, false)) {
			phoneSwitch.setChecked(true);
		}
		phoneSwitch.setOnCheckedChangeListener(phoneSwitchListener);
	}
}
