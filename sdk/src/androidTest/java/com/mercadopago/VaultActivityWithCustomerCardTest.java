package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.EditText;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

public class VaultActivityWithCustomerCardTest extends BaseTest<VaultActivity> {

    public VaultActivityWithCustomerCardTest() {
        super(VaultActivity.class);
    }

    // * Scenario 1:
    // * With all correct parameters
    // * select a stored credit card
    // * use 1 installment by default
    // * enter a security code
    // * push the button and generate a card token
    public void testHappyPath() {

        final VaultActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY,
                StaticMock.DUMMY_MERCHANT_BASE_URL, StaticMock.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                StaticMock.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), null);

        // Assume a pre-selected credit card

        // Wait for get customer cards and installments api call
        sleepThread();  // customer cards
        sleepThread();  // installments

        // Mock up an ActivityResult:
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethodRow", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethodRow(getApplicationContext())));
        Instrumentation.ActivityResult mockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        // Create an ActivityMonitor that catch CustomerCardsActivity and return mock ActivityResult:
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(CustomerCardsActivity.class.getName(), mockedResult , true);

        // Simulate customer card selection
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.onCustomerMethodsClick(null);
            }
        });

        // Wait for the ActivityMonitor to be hit, Instrumentation will then return the mock ActivityResult:
        CustomerCardsActivity childActivity = (CustomerCardsActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);

        // Wait for installments api call
        sleepThread();

        // Complete security code
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                EditText securityCodeText = (EditText) activity.findViewById(R.id.securityCode);
                securityCodeText.setText(StaticMock.DUMMY_SECURITY_CODE);
            }
        });

        // Simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.submitForm(null);
            }
        });

        // Wait for create token api call
        sleepThread();

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(!activityResult.getExtras().getString("token").equals(""));
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("paymentMethod"), PaymentMethod.class);
            assertTrue(paymentMethod.getId().equals("master"));
            assertTrue(activityResult.getExtras().getString("installments").equals("1"));
        } catch (Exception ex) {
            fail("Regular start test failed, cause: " + ex.getMessage());
        }
    }

    // * Alt Scenario 2:
    // * Null merchant public key

    // * Alt Scenario 3:
    // * Null amount

    private VaultActivity prepareActivity(String merchantPublicKey, String merchantBaseUrl,
                                     String merchantGetCustomerUri, String merchantAccessToken,
                                     BigDecimal amount, List<String> supportedPaymentTypes) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        if (merchantBaseUrl != null) {
            intent.putExtra("merchantBaseUrl", merchantBaseUrl);
        }
        if (merchantGetCustomerUri != null) {
            intent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        }
        if (merchantAccessToken != null) {
            intent.putExtra("merchantAccessToken", merchantAccessToken);
        }
        if (amount != null) {
            intent.putExtra("amount", amount.toString());
        }
        putListExtra(intent, "supportedPaymentTypes", supportedPaymentTypes);
        setActivityIntent(intent);
        return getActivity();
    }
}
