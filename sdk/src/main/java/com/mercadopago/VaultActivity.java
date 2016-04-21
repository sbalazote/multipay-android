package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import retrofit2.Response;

public class VaultActivity extends AppCompatActivity {

    // Activity parameters
    protected BigDecimal mAmount;
    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantPublicKey;
    protected boolean mShowBankDeals;

    // Input controls
    protected View mInstallmentsCard;
    protected View mSecurityCodeCard;
    protected EditText mSecurityCodeText;
    protected FrameLayout mCustomerMethodsLayout;
    protected TextView mCustomerMethodsText;
    protected FrameLayout mInstallmentsLayout;
    protected TextView mInstallmentsText;
    protected ImageView mCVVImage;
    protected TextView mCVVDescriptor;
    protected Button mSubmitButton;

    // Current values
    protected List<Card> mCards;
    protected List<PayerCost> mPayerCosts;
    protected CardToken mCardToken;
    protected PaymentMethodRow mSelectedPaymentMethodRow;
    protected PayerCost mSelectedPayerCost;
    protected PaymentMethod mSelectedPaymentMethod;
    protected Issuer mSelectedIssuer;
    protected List<String> mSupportedPaymentTypes;
    protected Issuer mTempIssuer;
    protected PaymentMethod mTempPaymentMethod;

    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        // Get activity parameters
        try {
            mAmount = new BigDecimal(this.getIntent().getStringExtra("amount"));
        } catch (Exception ex) {
            mAmount = null;
        }
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        mMerchantBaseUrl = this.getIntent().getStringExtra("merchantBaseUrl");
        mMerchantGetCustomerUri = this.getIntent().getStringExtra("merchantGetCustomerUri");
        mMerchantAccessToken = this.getIntent().getStringExtra("merchantAccessToken");
        if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mSupportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
        }
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        if ((mMerchantPublicKey != null) && (mAmount != null)) {

            // Set activity
            mActivity = this;
            mActivity.setTitle(getString(R.string.mpsdk_title_activity_vault));

            // Set layout controls
            mInstallmentsCard = findViewById(R.id.installmentsCard);
            mSecurityCodeCard = findViewById(R.id.securityCodeCard);
            mCVVImage = (ImageView) findViewById(R.id.cVVImage);
            mCVVDescriptor = (TextView) findViewById(R.id.cVVDescriptor);
            mSubmitButton = (Button) findViewById(R.id.submitButton);
            mCustomerMethodsLayout = (FrameLayout) findViewById(R.id.customerMethodLayout);
            mCustomerMethodsText = (TextView) findViewById(R.id.customerMethodLabel);
            mInstallmentsLayout = (FrameLayout) findViewById(R.id.installmentsLayout);
            mInstallmentsText = (TextView) findViewById(R.id.installmentsLabel);
            mSecurityCodeText = (EditText) findViewById(R.id.securityCode);

            // Init MercadoPago object with public key
            mMercadoPago = new MercadoPago.Builder()
                    .setContext(mActivity)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            // Init controls visibility
            mInstallmentsCard.setVisibility(View.GONE);
            mSecurityCodeCard.setVisibility(View.GONE);

            // Set customer method first value
            mCustomerMethodsText.setText(getString(com.mercadopago.R.string.mpsdk_select_pm_label));

            // Set "Go" button
            setFormGoButton(mSecurityCodeText);

            // Hide main layout and go for customer's cards
            if ((mMerchantBaseUrl != null) && (!mMerchantBaseUrl.equals("") && (mMerchantGetCustomerUri != null) && (!mMerchantGetCustomerUri.equals("")))) {
                getCustomerCardsAsync();
            } else {
                startPaymentMethodsActivity();
            }
        }
        else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("message", "Invalid parameters");
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    protected void setContentView() {

        setContentView(R.layout.activity_vault);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowBankDeals) {
            getMenuInflater().inflate(R.menu.vault, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bank_deals) {
            startBankDealsActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
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
        } else if (mExceptionOnMethod.equals("getInstallmentsAsync")) {
            getInstallmentsAsync();
        } else if (mExceptionOnMethod.equals("getCreateTokenCallback")) {
            if (mSelectedPaymentMethodRow != null) {
                createSavedCardToken();
            } else if (mCardToken != null) {
                createNewCardToken();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) {

            resolveCustomerCardsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {

            resolvePaymentMethodsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {

            resolveInstallmentsRequest(resultCode, data);

        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {

            resolveIssuersRequest(resultCode, data);

        } else if (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE) {

            resolveNewCardRequest(resultCode, data);
        }
    }

    protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            PaymentMethodRow selectedPaymentMethodRow = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethodRow"), PaymentMethodRow.class);

            if (selectedPaymentMethodRow.getCard() != null) {

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = selectedPaymentMethodRow;
                mSelectedPayerCost = null;
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

            // Set selection status
            mTempIssuer = null;
            mTempPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            if (MercadoPagoUtil.isCardPaymentType(mTempPaymentMethod.getPaymentTypeId())) {  // Card-like methods

                if (mTempPaymentMethod.isIssuerRequired()) {

                    // Call issuer activity
                    startIssuersActivity();

                } else {

                    // Call new card activity
                    startNewCardActivity();
                }
            } else {  // Off-line methods

                // Set selection status
                mPayerCosts = null;
                mCardToken = null;
                mSelectedPaymentMethodRow = null;
                mSelectedPayerCost = null;
                mSelectedPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                mSelectedIssuer = null;

                // Set customer method selection
                mCustomerMethodsText.setText(mSelectedPaymentMethod.getName());
                mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

                // Set security card visibility
                mSecurityCodeCard.setVisibility(View.GONE);

                // Set installments visibility
                mInstallmentsCard.setVisibility(View.GONE);

                // Set button visibility
                mSubmitButton.setEnabled(true);
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedPaymentMethodRow == null) && (mCardToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    protected void resolveInstallmentsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mSelectedPayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);

            // Update installments view
            mInstallmentsText.setText(mSelectedPayerCost.getRecommendedMessage());

        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

    protected void resolveIssuersRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mTempIssuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);

            // Call new card activity
            startNewCardActivity();

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

    protected void resolveNewCardRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mPayerCosts = null;
            mCardToken = JsonUtil.getInstance().fromJson(data.getStringExtra("cardToken"), CardToken.class);
            mSelectedPaymentMethodRow = null;
            mSelectedPayerCost = null;
            mSelectedPaymentMethod = mTempPaymentMethod;
            mSelectedIssuer = mTempIssuer;

            // Set customer method selection
            mCustomerMethodsText.setText(CustomerCardsAdapter.getPaymentMethodLabel(mActivity, mSelectedPaymentMethod.getName(),
                    mCardToken.getCardNumber().substring(mCardToken.getCardNumber().length() - 4, mCardToken.getCardNumber().length())));
            mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()), 0, 0, 0);

            // Set security card visibility
            showSecurityCodeCard(mSelectedPaymentMethod);

            // Get installments
            getInstallmentsAsync();

        } else {

            if (data != null) {
                if (data.getStringExtra("apiException") != null) {

                    finishWithApiException(data);

                } else if (data.getBooleanExtra("backButtonPressed", false)) {

                    if (mTempPaymentMethod.isIssuerRequired()) {

                        startIssuersActivity();

                    } else {

                        startPaymentMethodsActivity();
                    }
                }
            }
        }
    }

    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        ErrorHandlingCallAdapter.MyCall<Customer> call = MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken);
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Customer>() {
            @Override
            public void success(Response<Customer> response) {

                mCards = response.body().getCards();

                // If the customer has saved cards show the first one, else show the payment methods step
                if ((mCards != null) && (mCards.size() > 0)) {

                    // Set selected payment method row
                    mSelectedPaymentMethodRow = CustomerCardsAdapter.getPaymentMethodRow(mActivity, mCards.get(0));

                    // Set customer method selection
                    setCustomerMethodSelection();

                } else {

                    // Show payment methods step
                    startPaymentMethodsActivity();

                    LayoutUtil.showRegularLayout(mActivity);
                }
            }

            @Override
            public void failure(ApiException apiException) {

                mExceptionOnMethod = "getCustomerCardsAsync";
                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        });
    }

    protected void getInstallmentsAsync() {

        String bin = getSelectedPMBin();
        BigDecimal amount = mAmount;
        Long issuerId = (mSelectedIssuer != null) ? mSelectedIssuer.getId() : null;
        String paymentTypeId = mSelectedPaymentMethod.getPaymentTypeId();

        if (bin.length() == MercadoPago.BIN_LENGTH) {
            LayoutUtil.showProgressLayout(mActivity);
            ErrorHandlingCallAdapter.MyCall<List<Installment>> call = mMercadoPago.getInstallments(bin, amount, issuerId, paymentTypeId);
            call.enqueue(new ErrorHandlingCallAdapter.MyCallback<List<Installment>>() {
                @Override
                public void success(Response<List<Installment>> response) {

                    LayoutUtil.showRegularLayout(mActivity);

                    if ((response.body().size() > 0) && (response.body().get(0).getPayerCosts().size() > 0)) {

                        // Set installments card data and visibility
                        mPayerCosts = response.body().get(0).getPayerCosts();
                        mSelectedPayerCost = response.body().get(0).getPayerCosts().get(0);

                        if (response.body().get(0).getPayerCosts().size() == 1) {

                            mInstallmentsCard.setVisibility(View.GONE);

                        } else {

                            mInstallmentsText.setText(mSelectedPayerCost.getRecommendedMessage());
                            mInstallmentsCard.setVisibility(View.VISIBLE);
                        }

                        // Set button visibility
                        mSubmitButton.setEnabled(true);
                    } else {

                        Toast.makeText(getApplicationContext(), getString(com.mercadopago.R.string.mpsdk_invalid_pm_for_current_amount), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void failure(ApiException apiException) {

                    mExceptionOnMethod = "getInstallmentsAsync";
                    ApiUtil.finishWithApiException(mActivity, apiException);
                }
            });
        }
    }

    public void onCustomerMethodsClick(View view) {

        if ((mCards != null) && (mCards.size() > 0)) {  // customer cards activity

            startCustomerCardsActivity();

        } else {  // payment method activity

            startPaymentMethodsActivity();
        }
    }

    public void onInstallmentsClick(View view) {

        startInstallmentsActivity();
    }

    protected void showSecurityCodeCard(PaymentMethod paymentMethod) {

        if (paymentMethod != null) {

            if (isSecurityCodeRequired()) {

                // Set CVV descriptor
                mCVVDescriptor.setText(MercadoPagoUtil.getCVVDescriptor(this, paymentMethod));

                // Set CVV image
                mCVVImage.setImageDrawable(getResources().getDrawable(MercadoPagoUtil.getCVVImageResource(this, paymentMethod)));

                // Set card visibility
                mSecurityCodeCard.setVisibility(View.VISIBLE);

                return;
            }
        }
        mSecurityCodeCard.setVisibility(View.GONE);
    }

    protected boolean isSecurityCodeRequired() {

        if (mSelectedPaymentMethodRow != null) {
            return mSelectedPaymentMethodRow.getCard().isSecurityCodeRequired();
        } else {
            return mSelectedPaymentMethod.isSecurityCodeRequired(getSelectedPMBin());
        }
    }

    protected String getSelectedPMBin() {

        if (mSelectedPaymentMethodRow != null) {
            return mSelectedPaymentMethodRow.getCard().getFirstSixDigits();
        } else {
            return mCardToken.getCardNumber().substring(0, MercadoPago.BIN_LENGTH);
        }
    }

    protected void setFormGoButton(final EditText editText) {

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

        // Set payment method and issuer
        mSelectedPaymentMethod = mSelectedPaymentMethodRow.getCard().getPaymentMethod();
        mSelectedIssuer = mSelectedPaymentMethodRow.getCard().getIssuer();

        // Set customer method selection
        mCustomerMethodsText.setText(mSelectedPaymentMethodRow.getLabel());
        mCustomerMethodsText.setCompoundDrawablesWithIntrinsicBounds(mSelectedPaymentMethodRow.getIcon(), 0, 0, 0);

        // Set security card visibility
        showSecurityCodeCard(mSelectedPaymentMethodRow.getCard().getPaymentMethod());

        // Get installments
        getInstallmentsAsync();
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Validate installments
        if (((mSelectedPaymentMethodRow != null) || (mCardToken != null)) && mSelectedPayerCost == null) {
            return;
        }

        // Create token
        if (mSelectedPaymentMethodRow != null) {

            createSavedCardToken();

        } else if (mCardToken != null) {

            createNewCardToken();

        } else {  // Off-line methods

            // Return payment method id
            LayoutUtil.showRegularLayout(mActivity);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
            setResult(RESULT_OK, returnIntent);
            finish();
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
                if (mSelectedIssuer != null) {
                    returnIntent.putExtra("issuerId", Long.toString(mSelectedIssuer.getId()));
                }
                returnIntent.putExtra("installments", Integer.toString(mSelectedPayerCost.getInstallments()));
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

    protected void startBankDealsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(mMerchantPublicKey)
                .startBankDealsActivity();
    }

    protected void startCustomerCardsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setCards(mCards)
                .startCustomerCardsActivity();
    }

    protected void startInstallmentsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPayerCosts(mPayerCosts)
                .startInstallmentsActivity();
    }

    protected void startIssuersActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentMethod(mTempPaymentMethod)
                .startIssuersActivity();
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
                .setShowBankDeals(mShowBankDeals)
                .startPaymentMethodsActivity();
    }
}
