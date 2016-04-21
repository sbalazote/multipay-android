package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

public class VaultActivityWithOfflinePMTest extends BaseTest<VaultActivity> {

    public VaultActivityWithOfflinePMTest() {
        super(VaultActivity.class);
    }

    // * Scenario:
    // * With all correct parameters
    // * select pay with other payment method
    // * select an off-line payment method
    // * push the button and get the result

    public void testHappyPath() {

        final VaultActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY,
                StaticMock.DUMMY_MERCHANT_BASE_URL, StaticMock.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                StaticMock.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), null);

        // Assume a pre-selected credit card

        // Wait for get customer cards and installments api call
        sleepThread();  // customer cards
        sleepThread();  // installments

        // Mock up an ActivityResult with select other payment method
        Intent returnIntent = new Intent();

        PaymentMethodRow paymentMethodRow = new PaymentMethodRow(null, "label", 0);
        returnIntent.putExtra("paymentMethodRow", JsonUtil.getInstance().toJson(paymentMethodRow));
        Instrumentation.ActivityResult customerCardsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethod(getApplicationContext(), "_off_line")));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        Instrumentation.ActivityMonitor paymentMethodsActivityMonitor = getInstrumentation().addMonitor(PaymentMethodsActivity.class.getName(), paymentMethodsMockedResult , true);
        Instrumentation.ActivityMonitor customerCardsActivityMonitor = getInstrumentation().addMonitor(CustomerCardsActivity.class.getName(), customerCardsMockedResult , true);

        // Simulate customer card selection
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.onCustomerMethodsClick(null);
            }
        });

        getInstrumentation().waitForMonitorWithTimeout(customerCardsActivityMonitor, 5);
        getInstrumentation().waitForMonitorWithTimeout(paymentMethodsActivityMonitor, 5);

        // Simulate button click
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                activity.submitForm(null);
            }
        });

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("paymentMethod"), PaymentMethod.class);
            assertTrue(paymentMethod.getId().equals("pagofacil"));
        } catch (Exception ex) {
            fail("Regular start test failed, cause: " + ex.getMessage());
        }
    }

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
