package cuteam17.cuteam17phone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	MessagingServiceReceiver s = new MessagingServiceReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			//ToDo: start bluetooth
			//MessageService.startActionFoo(getApplicationContext(), "no", "noo");
		}
	}
}
