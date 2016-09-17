package com.multipay.android.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.util.LayoutUtil;
import com.multipay.android.R;
import com.multipay.android.dtos.PaymentDataDTO;
import com.multipay.android.dtos.PaymentLinkDTO;
import com.multipay.android.helpers.SessionManager;
import com.multipay.android.services.CustomerService;
import com.multipay.android.services.PaymentLinkService;
import com.multipay.android.tasks.LoadImages;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.ItemCategories;
import com.multipay.android.utils.ItemCategories.ItemCategory;
import com.multipay.android.utils.MultipayMenuItems;
import com.multipay.android.utils.PaymentMethods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MakePaymentActivity extends AppCompatActivity implements OnItemSelectedListener {
	private Spinner paymentMethodsSpinner;
	private Spinner itemCategoriesSpinner;
	private Spinner maxInstallmentsSpinner;
	private String[] methodsArray;
	private String[] thumbnailsArray;
	private Bitmap[] thumbnails;
	private SessionManager session;

	public static final int MAKE_PAYMENT_REQUEST_CODE = 7;

	private Retrofit retrofit;
	private PaymentLinkService paymentLinkService;

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
		maxInstallmentsSpinner = (Spinner)findViewById(R.id.max_installments_spinner);
		paymentMethodsSpinner = (Spinner)findViewById(R.id.payment_methods_spinner);
		itemCategoriesSpinner = (Spinner)findViewById(R.id.item_category_spinner);
		Iterator<ItemCategory> it = ItemCategories.getInstance().getItems().iterator();
		List<String> items = new ArrayList<String>();
		while (it.hasNext()) {
			ItemCategory item = it.next();
			items.add(item.getDescription());
		}
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		itemCategoriesSpinner.setAdapter(adapter1);
		itemCategoriesSpinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.installments_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxInstallmentsSpinner.setAdapter(adapter);
        maxInstallmentsSpinner.setOnItemSelectedListener(this);
		
		int methodsArraySize = PaymentMethods.getInstance().getPaymentMethodNames().size();
		methodsArray = new String[methodsArraySize];
		methodsArray = PaymentMethods.getInstance().getPaymentMethodNames().toArray(methodsArray);
		int thumbnailsArraySize = PaymentMethods.getInstance().getSecureThumbnails().size();
		thumbnailsArray = new String[thumbnailsArraySize];
		thumbnailsArray = PaymentMethods.getInstance().getSecureThumbnails().toArray(thumbnailsArray);
		try {
			thumbnails = new LoadImages().execute(thumbnailsArray).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paymentMethodsSpinner.setAdapter(new MyAdapter(MakePaymentActivity.this, R.layout.spinner_row, methodsArray));
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

	public class MyAdapter extends ArrayAdapter<String>{
		 
        public MyAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }
 
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        public View getCustomView(int position, View convertView, ViewGroup parent) {
 
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_row, parent, false);
            TextView label = (TextView)row.findViewById(R.id.name);
            label.setText(methodsArray[position]);
 
            ImageView icon=(ImageView)row.findViewById(R.id.image);
            icon.setImageBitmap(thumbnails[position]);
 
            return row;
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

		retrofit = new Retrofit.Builder()
				.baseUrl(Constant.MERCHANT_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(httpClient.build())
				.build();
		paymentLinkService = retrofit.create(PaymentLinkService.class);
		String buyerEmail = "test_payer_12345789@testuser.com";
		String sellerEmail = "test_user_88250708@testuser.com";
		String description = "test02";
		PaymentLinkDTO paymentLinkDTO = new PaymentLinkDTO(session.retrievePhoneAreaCode(), session.retrievePhoneNumber(), description, 100.0f, sellerEmail);
		Call<Object> call = paymentLinkService.paymentLink(paymentLinkDTO);

		call.enqueue(new Callback<Object>() {
			@Override
			public void onResponse(Call<Object> call, Response<Object> response) {
			}

			@Override
			public void onFailure(Call<Object> call, Throwable t) {
			}
		});
	}

	// Envio link de pago a comprador
	public void sendPaymentLink(View view) {
		Intent enterPhoneNumberIntent = new Intent(this, EnterPhoneNumberActivity.class);
		startActivityForResult(enterPhoneNumberIntent, MAKE_PAYMENT_REQUEST_CODE);
	}

	public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}