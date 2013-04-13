package hu.sch.kresshy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class myLocation extends Activity {

	EditText address;
	Button btnOk;
	Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		address = (EditText) findViewById(R.id.address);
		btnOk = (Button) findViewById(R.id.btnok);

//		address.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View arg0) {
//				address.setText("");
//				
//			}
//		});
		
		btnOk.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				String todecode = address.getText().toString();
				if (todecode == null | todecode == "") {
					setResult(RESULT_CANCELED);
					finish();
				}
				Intent resultIntent = new Intent();
				resultIntent.putExtra(getPackageName(), todecode);
				setResult(RESULT_OK, resultIntent);
				finish();

			}
		});

		btnCancel = (Button) findViewById(R.id.btncancel);

		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

}
