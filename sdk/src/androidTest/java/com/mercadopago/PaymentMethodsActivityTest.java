package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsActivityTest extends BaseTest<PaymentMethodsActivity> {

    public PaymentMethodsActivityTest() {
        super(PaymentMethodsActivity.class);
    }

    public void testGetPaymentMethod() {

        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null);

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.payment_methods_list);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Get payment method test failed, no items found");
        }

        // Simulate click on first item
        PaymentMethodsAdapter paymentMethodsAdapter = (PaymentMethodsAdapter) list.getAdapter();
        View row = new TextView(getApplicationContext());
        row.setTag(paymentMethodsAdapter.getItem(0));
        paymentMethodsAdapter.getListener().onClick(row);

        try {
            ActivityResult activityResult = getActivityResult(activity);
            PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("paymentMethod"), PaymentMethod.class);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
            assertTrue(paymentMethod.getId().equals("visa"));
        } catch (Exception ex) {
            fail("Get payment method test failed, cause: " + ex.getMessage());
        }
    }

    public void testNullMerchantPublicKey() {

        Activity activity = prepareActivity(null, null);
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    public void testWrongMerchantPublicKey() {

        Activity activity = prepareActivity("wrong_public_key", null);

        sleepThread();

        try {
            ActivityResult activityResult = getActivityResult(activity);
            ApiException apiException = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("apiException"), ApiException.class);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_CANCELED);
            assertTrue(apiException.getStatus() == 404);
        } catch (Exception ex) {
            fail("Wrong merchant public key test failed, cause: " + ex.getMessage());
        }
    }

    public void testSupportedPaymentTypesFilter() {

        List<String> supportedPaymentTypes = new ArrayList<String>(){{
            add("credit_card");
        }};
        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, supportedPaymentTypes);

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.payment_methods_list);
        PaymentMethodsAdapter adapter = (PaymentMethodsAdapter) list.getAdapter();
        if (adapter != null) {
            assertTrue(adapter.getItemCount() > 0);
            boolean incorrectPaymentTypeFound = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (!adapter.getItem(i).getPaymentTypeId().equals("credit_card")) {
                    incorrectPaymentTypeFound = true;
                    break;
                }
            }
            assertTrue(!incorrectPaymentTypeFound);
        } else {
            fail("Supported payment types filter test failed, no items found");
        }
    }

    public void testBackPressed() {

        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null);
        activity.onBackPressed();
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    private Activity prepareActivity(String merchantPublicKey, List<String> supportedPaymentTypes) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        putListExtra(intent, "supportedPaymentTypes", supportedPaymentTypes);
        setActivityIntent(intent);
        return getActivity();
    }
}
