package cuteam17.cuteam17rpi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

	private BluetoothAdapter mBluetoothAdapter;

	public final static int OVERLAY_REQUEST_CODE = 5463&0xffffff00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		establishPermissions();

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

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//ToDo: stop bluetooth
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

	public void startBT(View view) {
		Intent intent = new Intent(this, RpiBtTransferService.class);
		intent.setAction(RpiBtTransferService.BT_START);
		startService(intent);
	}

	public void connectBT(View view) {
		Intent intent = new Intent(this, RpiBtTransferService.class);
		intent.setAction(BtTransferService.BT_CONNECT);
		startService(intent);
	}

	public void stopBT(View view) {
		Intent intent = new Intent(this, RpiBtTransferService.class);
		intent.setAction(RpiBtTransferService.BT_STOP);
		startService(intent);
	}

	public void writeBT(View view) {
		SMSTransferItem msg = new SMSTransferItem("This is my msgdddd!!!", "3035550303");

		Intent intent = new Intent(this, RpiBtTransferService.class);
		intent.setAction(RpiBtTransferService.BT_WRITE);
		Bundle bundle = new Bundle();
		bundle.putSerializable(RpiBtTransferService.INTENT_EXTRA_WRITE, msg);
		intent.putExtras(bundle);
		startService(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//ToDo: add overlays permission request result
		switch (requestCode) {
			case REQUEST_CODE_DEVICE_LIST_ACTIVITY:
				if (resultCode == RESULT_OK) {
					try {
						String btDeviceAdr = data.getExtras().getString(BtFindDeviceActivity.EXTRA_DEVICE_ADDRESS);
						SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("BT_Connected_Device", btDeviceAdr);
						editor.apply();

						connectBT(null);
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
		if (!Settings.canDrawOverlays(this)) {
			Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivityForResult(i, OVERLAY_REQUEST_CODE);
		}

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
}
