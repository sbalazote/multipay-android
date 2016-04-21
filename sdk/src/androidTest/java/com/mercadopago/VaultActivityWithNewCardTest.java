package com.mercadopago;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.EditText;

import com.mercadopago.model.ApiException;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

public class VaultActivityWithNewCardTest extends BaseTest<VaultActivity> {

    public VaultActivityWithNewCardTest() {
        super(VaultActivity.class);
    }

    // * Scenario:
    // * With all correct parameters
    // * select pay with other payment method
    // * select a payment method
    // * select a card issuer
    // * fill the card form
    // * select installment 6
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

        // Mock up an ActivityResult with select other payment method
        Intent returnIntent = new Intent();

        PaymentMethodRow paymentMethodRow = new PaymentMethodRow(null, "label", 0);
        returnIntent.putExtra("paymentMethodRow", JsonUtil.getInstance().toJson(paymentMethodRow));
        Instrumentation.ActivityResult customerCardsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(StaticMock.getCardToken(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult newCardMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(StaticMock.getPayerCosts(getApplicationContext()).get(2)));
        Instrumentation.ActivityResult installmentsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(StaticMock.getIssuer(getApplicationContext())));
        Instrumentation.ActivityResult issuersMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        Instrumentation.ActivityMonitor installmentsActivityMonitor = getInstrumentation().addMonitor(InstallmentsActivity.class.getName(), installmentsMockedResult , true);
        Instrumentation.ActivityMonitor newCardActivityMonitor = getInstrumentation().addMonitor(NewCardActivity.class.getName(), newCardMockedResult , true);
        Instrumentation.ActivityMonitor issuersActivityMonitor = getInstrumentation().addMonitor(IssuersActivity.class.getName(), issuersMockedResult , true);
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
        getInstrumentation().waitForMonitorWithTimeout(issuersActivityMonitor, 5);
        getInstrumentation().waitForMonitorWithTimeout(newCardActivityMonitor, 5);

        // Wait for installments api call
        sleepThread();

        // Simulate installment selection
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
            activity.onInstallmentsClick(null);
            }
        });

        getInstrumentation().waitForMonitorWithTimeout(installmentsActivityMonitor, 5);

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
            assertTrue(activityResult.getExtras().getString("installments").equals("6"));
            assertTrue(activityResult.getExtras().getString("issuerId").equals("313"));
        } catch (Exception ex) {
            fail("Regular start test failed, cause: " + ex.getMessage());
        }
    }

    public void testGetCustomerCardsFailure() {

        final VaultActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY,
                StaticMock.DUMMY_MERCHANT_BASE_URL, StaticMock.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                "", new BigDecimal("20"), null);

        // Assume a pre-selected credit card

        // Wait for get customer cards and installments api call
        sleepThread();  // customer cards
        sleepThread();  // installments

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            ApiException apiException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(apiException != null);
        } catch (Exception ex) {
            fail("Get customer cards failure test failed, cause: " + ex.getMessage());
        }
    }

    public void testGetPaymentMethodFailure() {

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
        ApiException apiException = new ApiException("some message", 500, null, null);
        returnIntent.putExtra("apiException", JsonUtil.getInstance().toJson(apiException));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, returnIntent);

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

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            ApiException resultException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(resultException.getMessage().equals("some message"));
        } catch (Exception ex) {
            fail("Get payment method failure failed, cause: " + ex.getMessage());
        }
    }

    public void testGetIssuerFailure() {

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
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        ApiException apiException = new ApiException("some message", 500, null, null);
        returnIntent.putExtra("apiException", JsonUtil.getInstance().toJson(apiException));
        Instrumentation.ActivityResult issuersMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, returnIntent);

        Instrumentation.ActivityMonitor issuersActivityMonitor = getInstrumentation().addMonitor(IssuersActivity.class.getName(), issuersMockedResult , true);
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
        getInstrumentation().waitForMonitorWithTimeout(issuersActivityMonitor, 5);

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            ApiException resultException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(resultException.getMessage().equals("some message"));
        } catch (Exception ex) {
            fail("Get issuer failure failed, cause: " + ex.getMessage());
        }
    }

    public void testGetInstallmentsFailure() {

        final VaultActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY,
                StaticMock.DUMMY_MERCHANT_BASE_URL, StaticMock.DUMMY_MERCHANT_GET_CUSTOMER_URI,
                StaticMock.DUMMY_MERCHANT_ACCESS_TOKEN, new BigDecimal("20"), null);

        // Assume a pre-selected credit card

        // Wait for get customer cards and installments api call
        sleepThread();  // customer cards
        sleepThread();  // installments

        // Mock up an ActivityResult with select other payment method
        PaymentMethodRow paymentMethodRow = new PaymentMethodRow(null, "label", 0);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("paymentMethodRow", JsonUtil.getInstance().toJson(paymentMethodRow));
        Instrumentation.ActivityResult customerCardsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        CardToken wrongToken = StaticMock.getCardToken(getApplicationContext(), "_issuer_required");
        wrongToken.setCardNumber("8888880000000000");
        returnIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(wrongToken));
        Instrumentation.ActivityResult newCardMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(StaticMock.getIssuer(getApplicationContext())));
        Instrumentation.ActivityResult issuersMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        Instrumentation.ActivityMonitor newCardActivityMonitor = getInstrumentation().addMonitor(NewCardActivity.class.getName(), newCardMockedResult , true);
        Instrumentation.ActivityMonitor issuersActivityMonitor = getInstrumentation().addMonitor(IssuersActivity.class.getName(), issuersMockedResult , true);
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
        getInstrumentation().waitForMonitorWithTimeout(issuersActivityMonitor, 5);
        getInstrumentation().waitForMonitorWithTimeout(newCardActivityMonitor, 5);

        // Wait for installments api call
        sleepThread();

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            ApiException apiException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(apiException.getStatus() == 400);
        } catch (Exception ex) {
            fail("Get installments failure failed, cause: " + ex.getMessage());
        }
    }

    public void testNewCardFailure() {

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
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        ApiException apiException = new ApiException("some message", 500, null, null);
        returnIntent.putExtra("apiException", JsonUtil.getInstance().toJson(apiException));
        Instrumentation.ActivityResult newCardMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(StaticMock.getIssuer(getApplicationContext())));
        Instrumentation.ActivityResult issuersMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        Instrumentation.ActivityMonitor newCardActivityMonitor = getInstrumentation().addMonitor(NewCardActivity.class.getName(), newCardMockedResult , true);
        Instrumentation.ActivityMonitor issuersActivityMonitor = getInstrumentation().addMonitor(IssuersActivity.class.getName(), issuersMockedResult , true);
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
        getInstrumentation().waitForMonitorWithTimeout(issuersActivityMonitor, 5);
        getInstrumentation().waitForMonitorWithTimeout(newCardActivityMonitor, 5);

        // Validate activity result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            ApiException resultException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(resultException.getMessage().equals("some message"));
        } catch (Exception ex) {
            fail("Get new card failure failed, cause: " + ex.getMessage());
        }
    }

    public void testCreateTokenFailure() {

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
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult paymentMethodsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("cardToken", JsonUtil.getInstance().toJson(StaticMock.getCardToken(getApplicationContext(), "_issuer_required")));
        Instrumentation.ActivityResult newCardMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(StaticMock.getPayerCosts(getApplicationContext()).get(2)));
        Instrumentation.ActivityResult installmentsMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        returnIntent = new Intent();
        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(StaticMock.getIssuer(getApplicationContext())));
        Instrumentation.ActivityResult issuersMockedResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        Instrumentation.ActivityMonitor installmentsActivityMonitor = getInstrumentation().addMonitor(InstallmentsActivity.class.getName(), installmentsMockedResult , true);
        Instrumentation.ActivityMonitor newCardActivityMonitor = getInstrumentation().addMonitor(NewCardActivity.class.getName(), newCardMockedResult , true);
        Instrumentation.ActivityMonitor issuersActivityMonitor = getInstrumentation().addMonitor(IssuersActivity.class.getName(), issuersMockedResult , true);
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
        getInstrumentation().waitForMonitorWithTimeout(issuersActivityMonitor, 5);
        getInstrumentation().waitForMonitorWithTimeout(newCardActivityMonitor, 5);

        // Wait for installments api call
        sleepThread();

        // Simulate installment selection
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
            activity.onInstallmentsClick(null);
            }
        });

        getInstrumentation().waitForMonitorWithTimeout(installmentsActivityMonitor, 5);

        // Complete security code
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                EditText securityCodeText = (EditText) activity.findViewById(R.id.securityCode);
                securityCodeText.setText(StaticMock.DUMMY_SECURITY_CODE);
            }
        });

        // Force error
        activity.mSelectedPaymentMethod = null;
        activity.mCardToken.setCardNumber("8888880000000000");

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
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            ApiException apiException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(apiException.getMessage().equals("Invalid Value for Field: cardNumber"));
        } catch (Exception ex) {
            fail("Create token failure test failed, cause: " + ex.getMessage());
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
