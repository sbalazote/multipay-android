package com.mercadopago;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

public class NewCardActivityTest extends BaseTest<NewCardActivity> {

    private NewCardActivity mActivity;
    private EditText mCardNumberText;
    private EditText mCardholderNameText;
    private EditText mExpiryMonthText;
    private EditText mExpiryYearText;
    private TextView mExpiryErrorText;
    private EditText mIdentificationNumberText;
    private EditText mSecurityCodeText;

    public NewCardActivityTest() {

        super(NewCardActivity.class);
    }

    public void testGetCardToken() {

        // Set activity
        doRegularStart();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                EditText fieldText;
                fieldText = (EditText) mActivity.findViewById(R.id.cardNumber);
                fieldText.setText(StaticMock.DUMMY_CARD_NUMBER);
                fieldText = (EditText) mActivity.findViewById(R.id.expiryMonth);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                fieldText = (EditText) mActivity.findViewById(R.id.expiryYear);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                fieldText = (EditText) mActivity.findViewById(R.id.cardholderName);
                fieldText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                fieldText = (EditText) mActivity.findViewById(R.id.identificationNumber);
                fieldText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

                mActivity.submitForm(null);
            }
        });

        // Validate the result
        try {
            ActivityResult activityResult = getActivityResult(mActivity);
            CardToken cardToken = JsonUtil.getInstance().fromJson(
                    activityResult.getExtras().getString("cardToken"), CardToken.class);

            assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
            assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
            assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
            assertTrue(cardToken.getCardholder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getType().equals(StaticMock.DUMMI_IDENTIFICATION_TYPE_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
        } catch (Exception ex) {
            fail("Get card token test failed, cause: " + ex.getMessage());
        }
    }

    public void testEmptyCardNumber() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText("");
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_empty_card)));
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongCardNumber() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText("5678000123456789");
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_card_bin)));
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongExpiryDate() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText("12");
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongCardholderName() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText("");
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);

                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
    }

    public void testWrongIdentificationNumber() {

        // Set activity and set fields
        doRegularStart();
        setFields();

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText("");

                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_field)));
    }

    public void testGetCardTokenWithSecurityCode() {

        // Set activity and set fields
        doSecurityCodeStart();
        setFields();
        mSecurityCodeText = (EditText) mActivity.findViewById(R.id.securityCode);

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                EditText fieldText;
                fieldText = (EditText) mActivity.findViewById(R.id.cardNumber);
                fieldText.setText(StaticMock.DUMMY_CARD_NUMBER);
                fieldText = (EditText) mActivity.findViewById(R.id.expiryMonth);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                fieldText = (EditText) mActivity.findViewById(R.id.expiryYear);
                fieldText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                fieldText = (EditText) mActivity.findViewById(R.id.cardholderName);
                fieldText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                fieldText = (EditText) mActivity.findViewById(R.id.identificationNumber);
                fieldText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                fieldText = (EditText) mActivity.findViewById(R.id.securityCode);
                fieldText.setText(StaticMock.DUMMY_SECURITY_CODE);

                mActivity.submitForm(null);
            }
        });

        // Validate the result
        try {
            ActivityResult activityResult = getActivityResult(mActivity);
            CardToken cardToken = JsonUtil.getInstance().fromJson(
                    activityResult.getExtras().getString("cardToken"), CardToken.class);

            assertTrue(cardToken.getCardNumber().equals(StaticMock.DUMMY_CARD_NUMBER));
            assertTrue(cardToken.getExpirationMonth() == StaticMock.DUMMY_EXPIRATION_MONTH);
            assertTrue(cardToken.getExpirationYear() == StaticMock.DUMMY_EXPIRATION_YEAR_LONG);
            assertTrue(cardToken.getCardholder().getName().equals(StaticMock.DUMMY_CARDHOLDER_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getType().equals(StaticMock.DUMMI_IDENTIFICATION_TYPE_NAME));
            assertTrue(cardToken.getCardholder().getIdentification().getNumber().equals(StaticMock.DUMMY_IDENTIFICATION_NUMBER));
            assertTrue(cardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
        } catch (Exception ex) {
            fail("Get card token with security code test failed, cause: " + ex.getMessage());
        }
    }

    public void testEmptySecurityCode() {

        // Set activity and set fields
        doSecurityCodeStart();
        setFields();
        mSecurityCodeText = (EditText) mActivity.findViewById(R.id.securityCode);

        // Fill the form and simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {

                mCardNumberText.setText(StaticMock.DUMMY_CARD_NUMBER);
                mCardholderNameText.setText(StaticMock.DUMMY_CARDHOLDER_NAME);
                mExpiryMonthText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_MONTH));
                mExpiryYearText.setText(Integer.toString(StaticMock.DUMMY_EXPIRATION_YEAR_SHORT));
                mIdentificationNumberText.setText(StaticMock.DUMMY_IDENTIFICATION_NUMBER);
                mSecurityCodeText.setText("");

                mActivity.submitForm(null);
            }
        });

        // Validate error message
        assertTrue(mCardNumberText.getError() == null);
        assertTrue(mCardholderNameText.getError() == null);
        assertTrue(mExpiryErrorText.getError() == null);
        assertTrue(mIdentificationNumberText.getError() == null);
        assertTrue(mSecurityCodeText.getError().equals(mActivity.getString(R.string.mpsdk_invalid_cvv_length, 3)));
    }

    private NewCardActivity prepareActivity(PaymentMethod paymentMethod, String keyType,
                                     String key, Boolean requireSecurityCode) {

        Intent intent = new Intent();
        if (paymentMethod != null) {
            intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        }
        if (keyType != null) {
            intent.putExtra("keyType", keyType);
        }
        if (key != null) {
            intent.putExtra("key", key);
        }
        if (requireSecurityCode != null) {
            intent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        setActivityIntent(intent);
        return getActivity();
    }

    private void doRegularStart() {

        // Set activity
        mActivity = prepareActivity(StaticMock.getPaymentMethod(getApplicationContext()),
                MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, false);

        // Wait for identification types
        sleepThread();
    }

    private void doSecurityCodeStart() {

        // Set activity
        mActivity = prepareActivity(StaticMock.getPaymentMethod(getApplicationContext()),
                MercadoPago.KEY_TYPE_PUBLIC, StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, true);

        // Wait for identification types
        sleepThread();
    }

    private void setFields() {

        mCardNumberText = (EditText) mActivity.findViewById(R.id.cardNumber);
        mCardholderNameText = (EditText) mActivity.findViewById(R.id.cardholderName);
        mExpiryMonthText = (EditText) mActivity.findViewById(R.id.expiryMonth);
        mExpiryYearText = (EditText) mActivity.findViewById(R.id.expiryYear);
        mExpiryErrorText = (TextView) mActivity.findViewById(R.id.expiryError);
        mIdentificationNumberText = (EditText) mActivity.findViewById(R.id.identificationNumber);
    }
}
