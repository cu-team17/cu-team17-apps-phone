package cuteam17.cuteam17phone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_PERMISSIONS_LIST = 117;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			establishPermissions();
			//ToDo: start bluetooth
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_PERMISSIONS_LIST: {
				for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
					permissionResult(permissions[i],grantResults[i]);
				}

			}
		}
	}

	private void permissionResult(String permission, int grantResult) {
		switch (permission) {
			case Manifest.permission.RECEIVE_SMS: {
				if (grantResult == PackageManager.PERMISSION_GRANTED) {
					//ToDo: messaging permission granted
				} else {
					//ToDo: no messaging permission
				}
			}
			case Manifest.permission.READ_PHONE_STATE: {
				if (grantResult == PackageManager.PERMISSION_GRANTED) {
					//ToDo: phone permission granted
				} else {
					//ToDo: no phone permission
				}
			}
			case Manifest.permission.READ_CONTACTS: {
				if (grantResult == PackageManager.PERMISSION_GRANTED) {
					//ToDo: contacts permission granted
				} else {
					//ToDo: no contacts permission
				}
			}
		}
	}

	private void establishPermissions() {
		List<String> permissionsList = new ArrayList<String>();

		addPermissionToList(permissionsList, Manifest.permission.RECEIVE_SMS);
		addPermissionToList(permissionsList, Manifest.permission.READ_PHONE_STATE);
		addPermissionToList(permissionsList, Manifest.permission.READ_CONTACTS);

		if (!permissionsList.isEmpty()) {
			requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_PERMISSIONS_LIST);
		}

	}

	private boolean addPermissionToList(List<String> permissionsList, String permission) {
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			return true;
		} else return false;
	}
}
