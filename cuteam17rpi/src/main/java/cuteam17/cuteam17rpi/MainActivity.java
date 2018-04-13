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
import cuteam17.cuteam17btlibrary.BtTransferService;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_DEVICE_LIST_ACTIVITY = 105;
	private static final int REQUEST_ENABLE_BLUETOOTH = 110;
	private static final int REQUEST_LOCATION_PERMISSIONS = 220;

	private BluetoothAdapter mBluetoothAdapter;

	public final static int OVERLAY_REQUEST_CODE = 5463&0xffffff00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		establishPermissions();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
		}
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

	public void connectBtTransferService() {
		Intent intent = new Intent(this, RpiBtTransferService.class);
		intent.setAction(BtTransferService.BT_CONNECT);
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

						connectBtTransferService();
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

	private void establishPermissions() {
		if (!Settings.canDrawOverlays(this)) {
			Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivityForResult(i, OVERLAY_REQUEST_CODE);
		}

		if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_LOCATION_PERMISSIONS:
				//ToDo: handle lack of location permission, location permission is needed for bluetooth discovery
				break;
		}
	}

}
