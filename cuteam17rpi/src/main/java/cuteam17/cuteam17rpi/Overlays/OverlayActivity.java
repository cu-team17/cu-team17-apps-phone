package cuteam17.cuteam17rpi.Overlays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class OverlayActivity extends AppCompatActivity {

	// Key for extra that is a BtTransferItem
	public static final String BT_TRANSFER_ITEM_EXTRA = "EXTRA_BT_TRANSFER_ITEM";

	// Key for extra that signifies what OverlayService(SMS, Telephone, Notification, etc.) to be started
	public static final String OVERLAY_CLASS_TYPE = "class";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String classname = extras.getString(OVERLAY_CLASS_TYPE);
			Serializable item = extras.getSerializable(BT_TRANSFER_ITEM_EXTRA);
			//ToDo: potentially make subclasses, of this class, for each overlay type(SMS, Telephone, etc.) and
			//just start the respective service from there instead of passing a type of class
			try {
				Class<?> classType = Class.forName(classname);
				Intent intent = new Intent(this, classType);
				Bundle bundle = new Bundle();
				bundle.putSerializable(BT_TRANSFER_ITEM_EXTRA, item);
				intent.putExtras(bundle);
				startService(intent);
				finish();
			} catch (ClassNotFoundException e) {
				finish();
			}
		}
	}
}
