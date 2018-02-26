package cuteam17.cuteam17phone;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_PERMISSIONS_LIST = 117;
	private static final int REQUEST_CODE_DEVICE_LIST_ACTIVITY = 255;
	private static final int REQUEST_ENABLE_BLUETOOTH = 59;

	private BluetoothAdapter mBluetoothAdapter;

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
			Intent serverIntent = new Intent(this, BTFindDeviceActivity.class);
			startActivityForResult(serverIntent, REQUEST_CODE_DEVICE_LIST_ACTIVITY);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_DEVICE_LIST_ACTIVITY:
				if (resultCode == RESULT_OK) {
					try {
						String btDeviceAdr = data.getExtras().getString(BTFindDeviceActivity.EXTRA_DEVICE_ADDRESS);
						SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("BT_Connected_Device", btDeviceAdr);
						editor.apply();
					} catch (NullPointerException e) {
						return;
					}
				}
				break;
			case REQUEST_ENABLE_BLUETOOTH:
				if (resultCode == RESULT_OK) {
					Intent serverIntent = new Intent(this, BTFindDeviceActivity.class);
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
}
