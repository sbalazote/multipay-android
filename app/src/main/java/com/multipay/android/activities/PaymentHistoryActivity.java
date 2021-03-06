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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Discount;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.multipay.android.dtos.PaymentDataDTO;
import com.multipay.android.R;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.services.CustomerService;
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

	protected List<String> mSupportedPaymentTypes = new ArrayList<String>(){{
		add("credit_card");
		/*add("debit_card");
		add("prepaid_card");*/
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
		if (session.isSignedIn()) {
			if (session.getMode().equals("BUYER")) {
				Intent simpleVaultIntent = new Intent(this, SimpleVaultActivity.class);
				simpleVaultIntent.putExtra("merchantPublicKey", Constant.MERCHANT_PUBLIC_KEY);
				simpleVaultIntent.putExtra("merchantBaseUrl", Constant.MERCHANT_BASE_URL);
				simpleVaultIntent.putExtra("merchantGetCustomerUri", Constant.MERCHANT_GET_CUSTOMER_URI);
				simpleVaultIntent.putExtra("sellerEmail", this.getIntent().getStringExtra("sellerEmail"));
				putListExtra(simpleVaultIntent, "supportedPaymentTypes", mSupportedPaymentTypes);
				startActivityForResult(simpleVaultIntent, SIMPLE_VAULT_REQUEST_CODE);
			} else {
				session.logoutUser();
				session.checkLogin(this.getApplicationContext());
				Toast.makeText(getApplicationContext(), "Debe autenticarse primero como comprador", Toast.LENGTH_LONG).show();
			}
		} else {
			session.checkLogin(this.getApplicationContext());
			Toast.makeText(getApplicationContext(), "ERROR! Debe estar autenticado como comprador", Toast.LENGTH_LONG).show();
		}
	}

	public void createPayment(final Activity activity, String token, Integer installments, Long cardIssuerId, final PaymentMethod paymentMethod, Discount discount) {

		if (paymentMethod != null) {

			LayoutUtil.showProgressLayout(activity);

			// Set payment method id
			String paymentMethodId = paymentMethod.getId();

			// Create payment
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.setLevel(HttpLoggingInterceptor.Level.BODY);
			OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
			httpClient.addInterceptor(logging);

			Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl(Constant.MERCHANT_BASE_URL)
					.addConverterFactory(GsonConverterFactory.create(gson))
					.client(httpClient.build())
					.build();
			CustomerService customerService = retrofit.create(CustomerService.class);
			String buyerEmail = session.getUsernameEMail();
			String sellerEmail = this.getIntent().getStringExtra("sellerEmail");
			String description = this.getIntent().getStringExtra("description");
			String transactionAmount = this.getIntent().getStringExtra("transactionAmount");
			PaymentDataDTO paymentDataDTO = new PaymentDataDTO(token, description, Float.parseFloat(transactionAmount), paymentMethodId, buyerEmail, sellerEmail);
			Call<Payment> call = customerService.doPayment(paymentDataDTO);

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
