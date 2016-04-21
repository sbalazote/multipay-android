package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.InstallmentsAdapter;
import com.mercadopago.model.PayerCost;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import java.util.List;

public class InstallmentsActivityTest extends BaseTest<InstallmentsActivity> {

    public InstallmentsActivityTest() {
        super(InstallmentsActivity.class);
    }

    public void testGetInstallment() {

        // Set activity
        Activity activity = prepareActivity(StaticMock.getPayerCosts(getApplicationContext()));

        sleepThread();

        // Validate list
        RecyclerView list = (RecyclerView) activity.findViewById(R.id.installments_list);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Get installment test failed, no items found");
        }

        // Simulate click on first item
        InstallmentsAdapter adapter = (InstallmentsAdapter) list.getAdapter();
        View row = new TextView(getApplicationContext());
        row.setTag(adapter.getItem(0));
        adapter.getListener().onClick(row);

        try {
            ActivityResult activityResult = getActivityResult(activity);
            PayerCost payerCost = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("payerCost"), PayerCost.class);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
            assertTrue(payerCost.getInstallments() == 1);
        } catch (Exception ex) {
            fail("Get installment test failed, cause: " + ex.getMessage());
        }
    }

    public void testNullPayerCosts() {

        Activity activity = prepareActivity(null);
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    public void testWrongPayerCosts() {

        Activity activity = prepareActivityWrongPayerCosts();
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    private Activity prepareActivity(List<PayerCost> payerCosts) {

        Intent intent = new Intent();
        putListExtra(intent, "payerCosts", payerCosts);
        setActivityIntent(intent);
        return getActivity();
    }

    private Activity prepareActivityWrongPayerCosts() {

        Intent intent = new Intent();
        intent.putExtra("payerCosts", "1234");
        setActivityIntent(intent);
        return getActivity();
    }
}
