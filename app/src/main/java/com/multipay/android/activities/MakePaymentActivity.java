package com.multipay.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.util.LayoutUtil;
import com.multipay.android.R;
import com.multipay.android.dtos.LoginResponseDTO;
import com.multipay.android.dtos.PaymentLinkDTO;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.services.PaymentLinkService;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.MultipayMenuItems;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MakePaymentActivity extends AppCompatActivity {
	private SessionManager session;

	public static final int MAKE_PAYMENT_REQUEST_CODE = 7;

	private EditText priceEditText;
	private EditText itemTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_payment);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();
		// Enable the Up button
		ab.setDisplayHomeAsUpEnabled(true);

		session = SessionManager.getInstance(this.getApplicationContext());

		itemTitle = (EditText)findViewById(R.id.item_title_edittext);
		priceEditText = (EditText)findViewById(R.id.price_edittext);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_seller_signed_in, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				MultipayMenuItems.openAbout(getApplicationContext());
				return true;
			case R.id.action_logout:
				finish();
				session.logoutUser();
				return true;
			case R.id.action_help:
				MultipayMenuItems.openHelp(getApplicationContext());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MAKE_PAYMENT_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				//llamo a notificar al comprador.
				callToNotifyBuyer();

			} else {

				if ((data != null) && (data.getStringExtra("apiException") != null)) {
					Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
				}
			}
		} else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

			LayoutUtil.showRegularLayout(this);
		}
	}

	private void callToNotifyBuyer() {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);

		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Constant.MERCHANT_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(httpClient.build())
				.build();

		PaymentLinkService paymentLinkService = retrofit.create(PaymentLinkService.class);

		String sellerEmail = session.getUsernameEMail();
		String itemTitle = this.itemTitle.getText().toString();
		Float priceEditText = Float.valueOf(this.priceEditText.getText().toString());

		PaymentLinkDTO paymentLinkDTO = new PaymentLinkDTO(session.retrievePhoneAreaCode(), session.retrievePhoneNumber(), itemTitle, priceEditText, sellerEmail);
		Call<LoginResponseDTO> call = paymentLinkService.paymentLink(paymentLinkDTO);
		call.enqueue(new Callback<LoginResponseDTO>() {
			@Override
			public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
				if (response.body() != null) {
					if (!response.body().getValid()) {
						Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			}

			@Override
			public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
				Toast.makeText(getApplicationContext(), "Multipay no ha podido verificar el numero. Intente nuevamente.", Toast.LENGTH_LONG).show();
			}
		});
	}

	// Envio link de pago a comprador
	public void sendPaymentLink(View view) {
		Intent enterPhoneNumberIntent = new Intent(this, EnterPhoneNumberActivity.class);
		startActivityForResult(enterPhoneNumberIntent, MAKE_PAYMENT_REQUEST_CODE);
	}
}