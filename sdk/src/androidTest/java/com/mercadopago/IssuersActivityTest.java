package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.IssuersAdapter;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

public class IssuersActivityTest extends BaseTest<IssuersActivity> {

    public IssuersActivityTest() {
        super(IssuersActivity.class);
    }

    public void testGetIssuer() {

        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY,
                StaticMock.getPaymentMethod(getApplicationContext()));

        sleepThread();

        RecyclerView list = (RecyclerView) activity.findViewById(R.id.issuers_list);

        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Get issuer failed, no items found");
        }

        // Simulate click on first item
        IssuersAdapter issuersAdapter = (IssuersAdapter) list.getAdapter();
        View row = new TextView(getApplicationContext());
        row.setTag(issuersAdapter.getItem(0));
        Long issuerId = issuersAdapter.getItem(0).getId();
        issuersAdapter.getListener().onClick(row);

        try {
            ActivityResult activityResult = getActivityResult(activity);
            Issuer issuer = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("issuer"), Issuer.class);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
            assertTrue(issuer.getId().equals(issuerId));
        } catch (Exception ex) {
            fail("Get issuer test failed, cause: " + ex.getMessage());
        }
    }

    public void testMerchantPublicKeyNull() {

        Activity activity = prepareActivity(null, StaticMock.getPaymentMethod(getApplicationContext()));
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    public void testWrongMerchantPublicKey() {

        Activity activity = prepareActivity("wrong_public_key", StaticMock.getPaymentMethod(getApplicationContext()));

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

    public void testPaymentMethodNull() {

        Activity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null);
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    private Activity prepareActivity(String merchantPublicKey, PaymentMethod paymentMethod) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        if (paymentMethod != null) {
            intent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        }
        setActivityIntent(intent);
        return getActivity();
    }
}
