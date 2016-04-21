package com.multipay.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;
import com.multipay.android.multipay.R;
import com.multipay.android.services.UsersService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddCardActivity extends AppCompatActivity {

    // Activity parameters
    private PaymentMethod mPaymentMethod;

    // Input controls
    private EditText mCardHolderName;
    private EditText mCardNumber;
    private TextView mExpiryError;
    private EditText mExpiryMonth;
    private EditText mExpiryYear;
    private RelativeLayout mIdentificationLayout;
    private EditText mIdentificationNumber;
    private Spinner mIdentificationType;
    private EditText mSecurityCode;

    // Current values
    private CardToken mCardToken;

    // Local vars
    private Activity mActivity;
    private String mExceptionOnMethod;
    private MercadoPago mMercadoPago;
    private String mMerchantPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        mActivity = this;

        // Get activity parameters
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");

        // Set input controls
        mCardNumber = (EditText) findViewById(R.id.cardNumber);
        mSecurityCode = (EditText) findViewById(R.id.securityCode);
        mCardHolderName = (EditText) findViewById(R.id.cardholderName);
        mIdentificationNumber = (EditText) findViewById(R.id.identificationNumber);
        mIdentificationType = (Spinner) findViewById(R.id.identificationType);
        mIdentificationLayout = (RelativeLayout) findViewById(R.id.identificationLayout);
        mExpiryError = (TextView) findViewById(R.id.expiryError);
        mExpiryMonth = (EditText) findViewById(R.id.expiryMonth);
        mExpiryYear = (EditText) findViewById(R.id.expiryYear);

        // Init MercadoPago object with public key
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .build();

        // Set identification type listener to control identification number keyboard
        setIdentificationNumberKeyboardBehavior();

        // Error text cleaning hack
        setErrorTextCleaner(mCardHolderName);

        // Set payment method image
        mPaymentMethod = JsonUtil.getInstance().fromJson(this.getIntent().getStringExtra("paymentMethod"), PaymentMethod.class);
        if (mPaymentMethod.getId() != null) {
            ImageView pmImage = (ImageView) findViewById(com.mercadopago.R.id.pmImage);
            pmImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId()));
        }

        // Set up expiry edit texts
        mExpiryMonth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mExpiryError.setError(null);
                return false;
            }
        });
        mExpiryYear.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mExpiryError.setError(null);
                return false;
            }
        });

        // Get identification types
        getIdentificationTypesAsync();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        if (mExceptionOnMethod.equals("getIdentificationTypesAsync")) {
            getIdentificationTypesAsync();
        } else if (mExceptionOnMethod.equals("createTokenAsync")) {
            createTokenAsync();
        }
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Set card token
        mCardToken = new CardToken(getCardNumber(), getMonth(), getYear(), getSecurityCode(), getCardHolderName(),
                getIdentificationTypeId(getIdentificationType()), getIdentificationNumber());

        if (validateForm(mCardToken)) {

            // Create token
            createTokenAsync();
        }
    }

    private boolean validateForm(CardToken cardToken) {

        boolean result = true;
        boolean focusSet = false;

        // Validate card number
        try {
            validateCardNumber(cardToken);
            mCardNumber.setError(null);
        } catch (Exception ex) {
            mCardNumber.setError(ex.getMessage());
            mCardNumber.requestFocus();
            result = false;
            focusSet = true;
        }

        // Validate security code
        try {
            validateSecurityCode(cardToken);
            mSecurityCode.setError(null);
        } catch (Exception ex) {
            mSecurityCode.setError(ex.getMessage());
            if (!focusSet) {
                mSecurityCode.requestFocus();
                focusSet = true;
            }
            result = false;
        }

        // Validate expiry month and year
        if (!cardToken.validateExpiryDate()) {
            mExpiryError.setVisibility(View.VISIBLE);
            mExpiryError.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
            if (!focusSet) {
                mExpiryMonth.requestFocus();
                focusSet = true;
            }
            result = false;
        } else {
            mExpiryError.setError(null);
            mExpiryError.setVisibility(View.GONE);
        }

        // Validate card holder name
        if (!cardToken.validateCardholderName()) {
            mCardHolderName.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
            if (!focusSet) {
                mCardHolderName.requestFocus();
                focusSet = true;
            }
            result = false;
        } else {
            mCardHolderName.setError(null);
        }

        // Validate identification number
        if (getIdentificationType() != null) {
            if (!cardToken.validateIdentificationNumber()) {
                mIdentificationNumber.setError(getString(com.mercadopago.R.string.mpsdk_invalid_field));
                if (!focusSet) {
                    mIdentificationNumber.requestFocus();
                }
                result = false;
            } else {
                mIdentificationNumber.setError(null);
            }
        }

        return result;
    }

    protected void validateCardNumber(CardToken cardToken) throws Exception {

        cardToken.validateCardNumber(this, mPaymentMethod);
    }

    protected void validateSecurityCode(CardToken cardToken) throws Exception {

        cardToken.validateSecurityCode(this, mPaymentMethod);
    }

    private void getIdentificationTypesAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        ErrorHandlingCallAdapter.MyCall<List<IdentificationType>> call = mMercadoPago.getIdentificationTypes();
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<List<IdentificationType>>() {
            @Override
            public void success(Response<List<IdentificationType>> response) {

                mIdentificationType.setAdapter(new IdentificationTypesAdapter(mActivity, response.body()));

                // Set form "Go" button
                setFormGoButton(mIdentificationNumber);

                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(ApiException apiException) {

                if (apiException.getStatus() == 404) {

                    // No identification type for this country
                    mIdentificationLayout.setVisibility(View.GONE);

                    // Set form "Go" button
                    setFormGoButton(mCardHolderName);

                    LayoutUtil.showRegularLayout(mActivity);

                } else {

                    mExceptionOnMethod = "getIdentificationTypesAsync";
                    ApiUtil.finishWithApiException(mActivity, apiException);
                }
            }
        });
    }

    private void createTokenAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        ErrorHandlingCallAdapter.MyCall<Token> call = mMercadoPago.createToken(mCardToken);
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<Token>() {
            @Override
            public void success(Response<Token> response) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("token", response.body().getId());
                returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mPaymentMethod));
                setResult(RESULT_OK, returnIntent);
                finish();

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);

                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.mercadopago.com")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(httpClient.build())
                        .build();

                final UsersService service = retrofit.create(UsersService.class);

                // Grabo la tarjeta al usuario.
                String jsonObject = "{'token': '" + response.body().getId() + "'}";

                /*Call<List<Card>> call3 = service.retrieveAllCards("211652599-qRKOz5YPnhvZwk", "TEST-3108634673635661-040716-33a906fea1e9862927e85dffed01eaae__LB_LC__-73449193");
                call3.enqueue(new Callback<List<Card>>() {
                    @Override
                    public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<Card>> call, Throwable t) {

                    }
                });*/

                /*Call<Card> call4 = service.newCard("211652599-qRKOz5YPnhvZwk", "TEST-3108634673635661-040716-33a906fea1e9862927e85dffed01eaae__LB_LC__-73449193", jsonObject);
                call4.enqueue(new Callback<Card>() {
                    @Override
                    public void onResponse(Call<Card> call, Response<Card> response) {

                    }

                    @Override
                    public void onFailure(Call<Card> call, Throwable t) {

                    }
                });*/


                Call<Card> call4 = service.getCardById("211652599-qRKOz5YPnhvZwk", "1461032414134", "TEST-3108634673635661-040716-33a906fea1e9862927e85dffed01eaae__LB_LC__-73449193");
                call4.enqueue(new Callback<Card>() {
                    @Override
                    public void onResponse(Call<Card> call, Response<Card> response) {

                    }

                    @Override
                    public void onFailure(Call<Card> call, Throwable t) {

                    }
                });
            }

            @Override
            public void failure(ApiException apiException) {

                mExceptionOnMethod = "createTokenAsync";
                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        });
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

    private void setIdentificationNumberKeyboardBehavior() {

        mIdentificationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IdentificationType identificationType = getIdentificationType();
                if (identificationType != null) {
                    if (identificationType.getType().equals("number")) {
                        mIdentificationNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        mIdentificationNumber.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setErrorTextCleaner(final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable edt) {
                if (editText.getText().length() > 0) {
                    editText.setError(null);
                }
            }
        });
    }

    private String getCardNumber() {

        return this.mCardNumber.getText().toString();
    }

    private String getSecurityCode() {

        return this.mSecurityCode.getText().toString();
    }

    private Integer getMonth() {

        Integer result;
        try {
            result = Integer.parseInt(this.mExpiryMonth.getText().toString());
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    private Integer getYear() {

        Integer result;
        try {
            result = Integer.parseInt(this.mExpiryYear.getText().toString());
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    private String getCardHolderName() {

        return this.mCardHolderName.getText().toString();
    }

    private IdentificationType getIdentificationType() {

        return (IdentificationType) mIdentificationType.getSelectedItem();
    }

    private String getIdentificationTypeId(IdentificationType identificationType) {

        if (identificationType != null) {
            return identificationType.getId();
        } else {
            return null;
        }
    }

    private String getIdentificationNumber() {

        if (!this.mIdentificationNumber.getText().toString().equals("")) {
            return this.mIdentificationNumber.getText().toString();
        } else {
            return null;
        }
    }
}