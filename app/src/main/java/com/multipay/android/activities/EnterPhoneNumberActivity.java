package com.multipay.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.multipay.android.R;
import com.multipay.android.helpers.SessionManager;


public class EnterPhoneNumberActivity extends AppCompatActivity {

	private EditText phoneAreaCode;
	private EditText phoneNumber;


	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_phone_number);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		session = SessionManager.getInstance(this.getApplicationContext());

		phoneAreaCode = (EditText) findViewById(R.id.phone_area_code_id);
		phoneNumber = (EditText) findViewById(R.id.phone_number_id);


	}

	public void enterPhoneNumber(View view) {
		Boolean cancel = false;
		String phoneAreaCode = this.phoneAreaCode.getText().toString();
		String phoneNumber = this.phoneNumber.getText().toString();
		View focusView = null;

		// Se validan los campos.
		if (TextUtils.isEmpty(phoneAreaCode)) {
			this.phoneAreaCode.setError("El campo cod. area es obligatorio.");
			focusView = this.phoneAreaCode;
			cancel = true;
		}
		if (TextUtils.isEmpty(phoneNumber)) {
			this.phoneNumber.setError("El campo numero es obligatorio.");
			focusView = this.phoneNumber;
			cancel = true;
		}

		Intent returnIntent = new Intent();

		if (!cancel) {
			// Ajusto en preferencias de MultiPay el numero de telefono de comprador.
			session.storePhoneAreaCode(Integer.parseInt(phoneAreaCode));
			session.storePhoneNumber(phoneNumber);
			/*Intent signInActivityIntent = new Intent(this, SignInActivity.class);
			startActivity(signInActivityIntent);*/

			setResult(Activity.RESULT_OK, returnIntent);
		} else {
			// Se devuelve el foco al campo que no fue completado.
			focusView.requestFocus();
			setResult(Activity.RESULT_CANCELED, returnIntent);
		}

		finish();
	}
}
