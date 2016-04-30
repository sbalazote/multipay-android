package com.multipay.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.multipay.android.dtos.LoginResponseDTO;
import com.multipay.android.multipay.R;
import com.multipay.android.services.LoginService;
import com.multipay.android.services.UsersService;
import com.multipay.android.utils.Constant;
import com.multipay.android.utils.MultipayMenuItems;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SimpleVaultActivity extends AppCompatActivity {

	// Activity parameters
	protected String mMerchantAccessToken;
	protected String mMerchantBaseUrl;
	protected String mMerchantGetCustomerUri;
	protected String mMerchantPublicKey;

	// Input controls
	protected View mSecurityCodeCard;
	protected EditText mSecurityCodeText;
	protected FrameLayout mCustomerMethodsLayout;
	protected TextView mCustomerMethodsText;
	protected ImageView mCVVImage;
	protected TextView mCVVDescriptor;
	protected Button mSubmitButton;

	// Current values
	protected List<Card> mCards;
	protected CardToken mCardToken;
	protected PaymentMethodRow mSelectedPaymentMethodRow;
	protected PaymentMethod mSelectedPaymentMethod;
	protected PaymentMethod mTempPaymentMethod;

	// Local vars
	protected Activity mActivity;
	protected String mExceptionOnMethod;
	protected MercadoPago mMercadoPago;
	protected List<String> mSupportedPaymentTypes;

	private Retrofit retrofit;
	private UsersService usersService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView();

		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// Get activity parameters
		mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
		mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
		mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
		mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
		if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
			Gson gson = new Gson();
			Type listType = new TypeToken<List<String>>(){}.getType();
			mSupportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
		}

		if ((mMerchantPublicKey != null) && (!mMerchantPublicKey.equals(""))) {

			// Set activity
			mActivity = this;
			mActivity.setTitle(getString(R.string.mpsdk_title_activity_vault));

			// Set layout controls
			mSecurityCodeCard = findViewById(R.id.securityCodeCard);
			mCVVImage = (ImageView) findViewById(R.id.cVVImage);
			mCVVDescriptor = (TextView) findViewById(R.id.cVVDescriptor);
			mSubmitButton = (Button) findViewById(R.id.submitButton);
			mCustomerMethodsLayout = (FrameLayout) findViewById(R.id.customerMethodLayout);
			mCustomerMethodsText = (TextView) findViewById(R.id.customerMethodLabel);
			mSecurityCodeText = (EditText) findViewById(R.id.securityCode);

			// Init controls visibility
			mSecurityCodeCard.setVisibility(View.GONE);

			// Init MercadoPago object with public key
			mMercadoPago = new MercadoPago.Builder()
					.setContext(mActivity)
					.setPublicKey(mMerchantPublicKey)
					.build();

			// Set customer method first value
			mCustomerMethodsText.setText(getString(com.mercadopago.R.string.mpsdk_select_pm_label));

			// Set "Go" button
			setFormGoButton(mSecurityCodeText);

			// Hide main layout and go for customer's cards
			if ((mMerchantBaseUrl != null) && (!mMerchantBaseUrl.equals("") && (mMerchantGetCustomerUri != null) && (!mMerchantGetCustomerUri.equals("")))) {
				getCustomerCardsAsync();
			}
		}
		else {
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			returnIntent.putExtra("message", "Invalid parameters");
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_activity_buyer_signed_in, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				MultipayMenuItems.openAbout(getApplicationContext());
				return true;
			case R.id.action_make_payment:
				//makePayment(item.getActionView());
				return true;
			case R.id.action_logout:
				finish();
				//session.logoutUser();
				return true;
			case R.id.action_help:
				MultipayMenuItems.openHelp(getApplicationContext());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {

		Intent returnIntent = new Intent();
		returnIntent.putExtra("backButtonPressed", true);
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	public void refreshLayout(View view) {

		// Retry method call
		if (mExceptionOnMethod.equals("getCustomerCardsAsync")) {
			getCustomerCardsAsync();
		} else if (mExceptionOnMethod.equals("getCreateTokenCallback")) {
			if (mSelectedPaymentMethodRow != null) {
				createSavedCardToken();
			} else if (mCardToken != null) {
				createNewCardToken();
			}
		}
	}

	public void onCustomerMethodsClick(View view) {

		if ((mCards != null) && (mCards.size() > 0)) {  // customer cards activity

			new MercadoPago.StartActivityBuilder()
					.setActivity(mActivity)
					.setCards(mCards)
					.startCustomerCardsActivity();

		} else {  // payment method activity

			new MercadoPago.StartActivityBuilder()
					.setActivity(mActivity)
					.setPublicKey(mMerchantPublicKey)
					.setSupportedPaymentTypes(mSupportedPaymentTypes)
					.startPaymentMethodsActivity();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) {

			resolveCustomerCardsRequest(resultCode, data);

		} else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {

			resolvePaymentMethodsRequest(resultCode, data);

		} else if (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE) {

			resolveNewCardRequest(resultCode, data);
		}
	}

	protected void setContentView() {

		setContentView(R.layout.activity_simple_vault);
	}

	protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			PaymentMethodRow selectedPaymentMethodRow = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethodRow"), PaymentMethodRow.class);

			if (selectedPaymentMethodRow.getCard() != null) {

				// Set selection status
				mCardToken = null;
				mSelectedPaymentMethodRow = selectedPaymentMethodRow;
				mSelectedPaymentMethod = null;
				mTempPaymentMethod = null;

				// Set customer method selection
				setCustomerMethodSelection();

			} else {

				startPaymentMethodsActivity();
			}
		} else {

			if ((data != null) && (data.getStringExtra("apiException") != null)) {
				finishWithApiException(data);
			}
		}
	}

	protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			mTempPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

			// Call new card activity
			startNewCardActivity();

		} else {

			if ((data != null) && (data.getStringExtra("apiException") != null)) {
				finishWithApiException(data);
			} else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
				// if nothing is selected
				finish();
			}
		}
	}

	protected void resolveNewCardRequest(int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			// Set selection status
			mCardToken = JsonUtil.getInstance().fromJson(data.getStringExtra("cardToken"), CardToken.class);
			mSelectedPaymentMethodRow = null;
			mSelectedPaymentMethod = mTempPaymentMethod;

			// Set customer method selection
			mCustomerMethodsText.setText(CustomerCardsAdapter.getPaymentMethodLabel(mActivity, mSelectedPaymentMethod.getName(),
					mCardToken.getCardNumber().substring(mCardToken.getCardNumber().length() - 4, mCardToken.getCardNumber().length())));
			mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

			// Set security card visibility
			showSecurityCodeCard(mSelectedPaymentMethod);

			// Set button visibility
			mSubmitButton.setEnabled(true);

		} else {

			if (data != null) {
				if (data.getStringExtra("apiException") != null) {

					finishWithApiException(data);

				} else if (data.getBooleanExtra("backButtonPressed", false)) {

					startPaymentMethodsActivity();
				}
			}
		}
	}

	protected void getCustomerCardsAsync() {

		LayoutUtil.showProgressLayout(mActivity);
		//ErrorHandlingCallAdapter.MyCall<Customer> call = MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken);
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
		Call<String> call = usersService.getCustomer("211652599-qRKOz5YPnhvZwk");
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
				mCards = gson.fromJson(response.body(), Customer.class).getCards();
				LayoutUtil.showRegularLayout(mActivity);
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
				mExceptionOnMethod = "getCustomerCardsAsync";
				ApiUtil.finishWithApiException(mActivity, new ApiException());
			}
		});

		/*call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Customer>() {
			@Override
			public void success(Response<Customer> response) {

				mCards = response.body().getCards();
				LayoutUtil.showRegularLayout(mActivity);
			}

			@Override
			public void failure(ApiException apiException) {

				mExceptionOnMethod = "getCustomerCardsAsync";
				ApiUtil.finishWithApiException(mActivity, apiException);
			}
		});*/
	}

	protected void showSecurityCodeCard(PaymentMethod paymentMethod) {

		if (paymentMethod != null) {

			if (isSecurityCodeRequired()) {

				if ("amex".equals(paymentMethod.getId())) {
					mCVVDescriptor.setText(String.format(getString(com.mercadopago.R.string.mpsdk_cod_seg_desc_amex), 4));
				} else {
					mCVVDescriptor.setText(String.format(getString(com.mercadopago.R.string.mpsdk_cod_seg_desc), 3));
				}

				int res = MercadoPagoUtil.getPaymentMethodImage(mActivity, paymentMethod.getId());
				if (res != 0) {
					mCVVImage.setImageDrawable(getResources().getDrawable(res));
				}

				mSecurityCodeCard.setVisibility(View.VISIBLE);

			} else {

				mSecurityCodeCard.setVisibility(View.GONE);
			}
		}
	}

	protected String getSelectedPMBin() {

		if (mSelectedPaymentMethodRow != null) {
			return mSelectedPaymentMethodRow.getCard().getFirstSixDigits();
		} else {
			return mCardToken.getCardNumber().substring(0, MercadoPago.BIN_LENGTH);
		}
	}

	private boolean isSecurityCodeRequired() {

		if (mSelectedPaymentMethodRow != null) {
			return mSelectedPaymentMethodRow.getCard().isSecurityCodeRequired();
		} else {
			return mSelectedPaymentMethod.isSecurityCodeRequired(getSelectedPMBin());
		}
	}

	private void setFormGoButton(final EditText editText) {

		editText.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					submitForm(v);
				}
				return false;
			}
		});
	}

	protected void setCustomerMethodSelection() {

		// Set payment method
		mSelectedPaymentMethod = mSelectedPaymentMethodRow.getCard().getPaymentMethod();

		// Set customer method selection
		mCustomerMethodsText.setText(mSelectedPaymentMethodRow.getLabel());
		mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(mSelectedPaymentMethodRow.getIcon(), 0, 0, 0);

		// Set security card visibility
		showSecurityCodeCard(mSelectedPaymentMethodRow.getCard().getPaymentMethod());

		// Set button visibility
		mSubmitButton.setEnabled(true);
	}

	public void submitForm(View view) {

		LayoutUtil.hideKeyboard(mActivity);

		// Create token
		if (mSelectedPaymentMethodRow != null) {

			createSavedCardToken();

		} else if (mCardToken != null) {

			createNewCardToken();
		}
	}

	protected void createNewCardToken() {

		// Validate CVV
		try {
			mCardToken.setSecurityCode(mSecurityCodeText.getText().toString());
			mCardToken.validateSecurityCode(this, mSelectedPaymentMethod);
			mSecurityCodeText.setError(null);
		}
		catch (Exception ex) {
			mSecurityCodeText.setError(ex.getMessage());
			mSecurityCodeText.requestFocus();
			return;
		}

		// Create token
		LayoutUtil.showProgressLayout(mActivity);
		ErrorHandlingCallAdapter.MyCall<Token> call = mMercadoPago.createToken(mCardToken);
		call.enqueue(getCreateTokenCallback());
	}

	protected void createSavedCardToken() {

		SavedCardToken savedCardToken = new SavedCardToken(mSelectedPaymentMethodRow.getCard().getId(), mSecurityCodeText.getText().toString());

		// Validate CVV
		try {
			savedCardToken.validateSecurityCode(this, mSelectedPaymentMethodRow.getCard());
			mSecurityCodeText.setError(null);
		}
		catch (Exception ex) {
			mSecurityCodeText.setError(ex.getMessage());
			mSecurityCodeText.requestFocus();
			return;
		}

		// Create token
		LayoutUtil.showProgressLayout(mActivity);
		ErrorHandlingCallAdapter.MyCall<Token> call = mMercadoPago.createToken(savedCardToken);
		call.enqueue(getCreateTokenCallback());
	}

	protected ErrorHandlingCallAdapter.MyCallback<Token> getCreateTokenCallback() {

		return new ErrorHandlingCallAdapter.MyCallback<Token>() {
			@Override
			public void success(Response<Token> response) {

				Intent returnIntent = new Intent();
				returnIntent.putExtra("token", response.body().getId());
				returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
				setResult(RESULT_OK, returnIntent);
				finish();
			}

			@Override
			public void failure(ApiException apiException) {

				mExceptionOnMethod = "getCreateTokenCallback";
				ApiUtil.finishWithApiException(mActivity, apiException);
			}
		};
	}

	protected void finishWithApiException(Intent data) {

		setResult(RESULT_CANCELED, data);
		finish();
	}

	protected void startNewCardActivity() {

		new MercadoPago.StartActivityBuilder()
				.setActivity(mActivity)
				.setPublicKey(mMerchantPublicKey)
				.setPaymentMethod(mTempPaymentMethod)
				.setRequireSecurityCode(false)
				.startNewCardActivity();
	}

	protected void startPaymentMethodsActivity() {

		new MercadoPago.StartActivityBuilder()
				.setActivity(mActivity)
				.setPublicKey(mMerchantPublicKey)
				.setSupportedPaymentTypes(mSupportedPaymentTypes)
				.startPaymentMethodsActivity();
	}
}
