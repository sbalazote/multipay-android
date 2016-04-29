package com.multipay.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.multipay.android.dtos.PaymentDataDTO;
import com.multipay.android.multipay.R;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.services.UsersService;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.MultipayMenuItems;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentHistoryActivity extends AppCompatActivity {

	private SessionManager session;

	public static final int SIMPLE_VAULT_REQUEST_CODE = 10;

	private Retrofit retrofit;
	private UsersService usersService;

	protected List<String> mSupportedPaymentTypes = new ArrayList<String>(){{
		add("credit_card");
		add("debit_card");
		add("prepaid_card");
	}};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_history);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();
		// Enable the Up button
		ab.setDisplayHomeAsUpEnabled(true);
		session = SessionManager.getInstance(this.getApplicationContext());

		session.getUsernameEMail();
		Intent simpleVaultIntent = new Intent(this, SimpleVaultActivity.class);
		simpleVaultIntent.putExtra("merchantPublicKey", Constant.MERCHANT_PUBLIC_KEY);
		simpleVaultIntent.putExtra("merchantBaseUrl", Constant.MERCHANT_BASE_URL);
		simpleVaultIntent.putExtra("merchantGetCustomerUri", Constant.MERCHANT_GET_CUSTOMER_URI);
		simpleVaultIntent.putExtra("merchantAccessToken", "211652599-qRKOz5YPnhvZwk");
		putListExtra(simpleVaultIntent, "supportedPaymentTypes", mSupportedPaymentTypes);
		startActivityForResult(simpleVaultIntent, SIMPLE_VAULT_REQUEST_CODE);
	}

	public void createPayment(final Activity activity, String token, Integer installments, Long cardIssuerId, final PaymentMethod paymentMethod, Discount discount) {

		if (paymentMethod != null) {

			LayoutUtil.showProgressLayout(activity);

			// Set item
			Item item = new Item("id1", 1, new BigDecimal(1000));

			// Set payment method id
			String paymentMethodId = paymentMethod.getId();

			// Set campaign id
			Long campaignId = (discount != null) ? discount.getId() : null;

			// Set merchant payment
			MerchantPayment payment = new MerchantPayment(item, installments, cardIssuerId, token, paymentMethodId, campaignId, "211652599-qRKOz5YPnhvZwk");

			// Create payment
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.setLevel(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
			httpClient.addInterceptor(logging);

			retrofit = new Retrofit.Builder()
					.baseUrl(Constant.MERCHANT_BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.client(httpClient.build())
					.build();
			usersService = retrofit.create(UsersService.class);
			PaymentDataDTO paymentDataDTO = new PaymentDataDTO(token, 100.0f, paymentMethodId, "211652599-qRKOz5YPnhvZwk");
			Call<Payment> call = usersService.doPayment(paymentDataDTO);

			call.enqueue(new Callback<Payment>() {
				@Override
				public void onResponse(Call<Payment> call, Response<Payment> response) {
					new MercadoPago.StartActivityBuilder()
							.setActivity(activity)
							.setPayment(response.body())
							.setPaymentMethod(paymentMethod)
							.startCongratsActivity();
				}

				@Override
				public void onFailure(Call<Payment> call, Throwable t) {
					LayoutUtil.showRegularLayout(activity);
					Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
				}
			});

			/*ErrorHandlingCallAdapter.MyCall<Payment> call = MerchantServer.createPayment(activity, Constant.MERCHANT_BASE_URL, Constant.MERCHANT_CREATE_PAYMENT_URI, payment);
			call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Payment>() {
				@Override
				public void success(Response<Payment> response) {

					new MercadoPago.StartActivityBuilder()
							.setActivity(activity)
							.setPayment(response.body())
							.setPaymentMethod(paymentMethod)
							.startCongratsActivity();
				}

				@Override
				public void failure(ApiException apiException) {

					LayoutUtil.showRegularLayout(activity);
					Toast.makeText(activity, apiException.getMessage(), Toast.LENGTH_LONG).show();
				}
			});*/
		} else {

			Toast.makeText(activity, "Invalid payment method", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == SIMPLE_VAULT_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				// Create payment
				createPayment(this, data.getStringExtra("token"),
						1, null, JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class), null);

			} else {

				if ((data != null) && (data.getStringExtra("apiException") != null)) {
					Toast.makeText(getApplicationContext(), data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
				}
			}
		} else if (requestCode == MercadoPago.CONGRATS_REQUEST_CODE) {

			LayoutUtil.showRegularLayout(this);
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_activity_buyer_signed_in, menu);
    	    return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
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

	private static void putListExtra(Intent intent, String listName, List<String> list) {

		if (list != null) {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>(){}.getType();
			intent.putExtra(listName, gson.toJson(list, listType));
		}
	}
}
