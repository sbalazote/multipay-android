package com.mercadopago;

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

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.adapters.IdentificationTypesAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

import retrofit2.Response;

public class NewCardActivity extends AppCompatActivity {

    // Activity parameters
    protected String mKey;
    protected String mKeyType;
    protected PaymentMethod mPaymentMethod;
    protected Boolean mRequireSecurityCode;

    // Input controls
    protected EditText mCardHolderName;
    protected EditText mCardNumber;
    protected TextView mCVVDescriptor;
    protected ImageView mCVVImage;
    protected TextView mExpiryError;
    protected EditText mExpiryMonth;
    protected EditText mExpiryYear;
    protected EditText mIdentificationNumber;
    protected RelativeLayout mIdentificationLayout;
    protected Spinner mIdentificationType;
    protected RelativeLayout mSecurityCodeLayout;
    protected EditText mSecurityCode;

    // Local vars
    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        mActivity = this;

        // Get activity parameters
        String paymentMethod = this.getIntent().getStringExtra("paymentMethod");
        mKeyType = this.getIntent().getStringExtra("keyType");
        mKey = this.getIntent().getStringExtra("key");
        mRequireSecurityCode = this.getIntent().getBooleanExtra("requireSecurityCode", true);
        if ((paymentMethod == null) || (mKeyType == null) || (mKey == null)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }
        mPaymentMethod = JsonUtil.getInstance().fromJson(paymentMethod, PaymentMethod.class);

        // Set input controls
        mCardNumber = (EditText) findViewById(R.id.cardNumber);
        mCardHolderName = (EditText) findViewById(R.id.cardholderName);
        mIdentificationNumber = (EditText) findViewById(R.id.identificationNumber);
        mIdentificationType = (Spinner) findViewById(R.id.identificationType);
        mIdentificationLayout = (RelativeLayout) findViewById(R.id.identificationLayout);
        mSecurityCodeLayout = (RelativeLayout) findViewById(R.id.securityCodeLayout);
        mCVVImage = (ImageView) findViewById(R.id.cVVImage);
        mCVVDescriptor = (TextView) findViewById(R.id.cVVDescriptor);
        mSecurityCode = (EditText) findViewById(R.id.securityCode);
        mExpiryError = (TextView) findViewById(R.id.expiryError);
        mExpiryMonth = (EditText) findViewById(R.id.expiryMonth);
        mExpiryYear = (EditText) findViewById(R.id.expiryYear);

        // Set identification type listener to control identification number keyboard
        setIdentificationNumberKeyboardBehavior();

        // Error text cleaning hack
        setErrorTextCleaner(mCardHolderName);

        // Get identification types
        getIdentificationTypesAsync();

        // Set payment method image
        if (mPaymentMethod.getId() != null) {
            ImageView pmImage = (ImageView) findViewById(R.id.pmImage);
            if (pmImage != null) {
                pmImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId()));
            }
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

        // Set security code visibility
        setSecurityCodeLayout();
    }

    protected void setContentView() {

        setContentView(R.layout.activity_new_card);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        getIdentificationTypesAsync();
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Set card token
        CardToken cardToken = new CardToken(getCardNumber(), getMonth(), getYear(), getSecurityCode(), getCardHolderName(),
                getIdentificationTypeId(getIdentificationType()), getIdentificationNumber());

        if (validateForm(cardToken)) {
            // Return to parent
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(cardToken));
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    protected boolean validateForm(CardToken cardToken) {

        boolean result = true;
        boolean focusSet = false;

        // Validate card number
        try {
            validateCardNumber(cardToken);
            mCardNumber.setError(null);
        }
        catch (Exception ex) {
            mCardNumber.setError(ex.getMessage());
            mCardNumber.requestFocus();
            result = false;
            focusSet = true;
        }

        // Validate security code
        if (mRequireSecurityCode) {
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
            mCardHolderName.setError(getString(R.string.mpsdk_invalid_field));
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
            if (!cardToken.validateIdentificationNumber(getIdentificationType())) {
                mIdentificationNumber.setError(getString(R.string.mpsdk_invalid_field));
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

    protected void getIdentificationTypesAsync() {

        LayoutUtil.showProgressLayout(mActivity);

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setKey(mKey, mKeyType)
                .build();

        ErrorHandlingCallAdapter.MyCall<List<IdentificationType>> call = mercadoPago.getIdentificationTypes();
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<List<IdentificationType>>() {
            @Override
            public void success(Response<List<IdentificationType>> response) {

                mIdentificationType.setAdapter(new IdentificationTypesAdapter(mActivity, response.body()));

                // Set form "Go" button
                if (mSecurityCodeLayout.getVisibility() == View.GONE) {
                    setFormGoButton(mIdentificationNumber);
                }

                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(ApiException apiException) {

                if (apiException.getStatus() == 404) {

                    // No identification type for this country
                    mIdentificationLayout.setVisibility(View.GONE);

                    // Set form "Go" button
                    if (mSecurityCodeLayout.getVisibility() == View.GONE) {
                        setFormGoButton(mCardHolderName);
                    }

                    LayoutUtil.showRegularLayout(mActivity);

                } else {

                    ApiUtil.finishWithApiException(mActivity, apiException);
                }
            }
        });
    }

    protected void setFormGoButton(final EditText editText) {

        editText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitForm(v);
                    return true;
                }
                return false;
            }
        });
    }

    protected void setIdentificationNumberKeyboardBehavior() {

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

    protected void setErrorTextCleaner(final EditText editText) {

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

    protected void setSecurityCodeLayout() {

        if (mPaymentMethod != null) {

            if (mRequireSecurityCode) {

                // Set CVV descriptor
                mCVVDescriptor.setText(MercadoPagoUtil.getCVVDescriptor(this, mPaymentMethod));

                // Set CVV image
                mCVVImage.setImageDrawable(getResources().getDrawable(MercadoPagoUtil.getCVVImageResource(this, mPaymentMethod)));

                // Set layout visibility
                mSecurityCodeLayout.setVisibility(View.VISIBLE);
                setFormGoButton(mSecurityCode);

                return;
            }
        }
        mSecurityCodeLayout.setVisibility(View.GONE);
    }

    protected String getCardNumber() {

        return this.mCardNumber.getText().toString();
    }

    protected String getSecurityCode() {

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

    protected String getCardHolderName() {

        return this.mCardHolderName.getText().toString();
    }

    protected IdentificationType getIdentificationType() {

        return (IdentificationType) mIdentificationType.getSelectedItem();
    }

    protected String getIdentificationTypeId(IdentificationType identificationType) {

        if (identificationType != null) {
            return identificationType.getId();
        } else {
            return null;
        }
    }

    protected String getIdentificationNumber() {

        if (!this.mIdentificationNumber.getText().toString().equals("")) {
            return this.mIdentificationNumber.getText().toString();
        } else {
            return null;
        }
    }
}
