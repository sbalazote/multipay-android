package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.model.Card;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.test.ActivityResult;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;
import com.mercadopago.util.JsonUtil;

import java.util.List;

public class CustomerCardsActivityTests extends BaseTest<CustomerCardsActivity> {

    public CustomerCardsActivityTests() {
        super(CustomerCardsActivity.class);
    }

    public void testGetCard() {

        // Set activity
        Activity activity = prepareActivity(StaticMock.getCards(getApplicationContext()));

        sleepThread();

        // Validate list
        RecyclerView list = (RecyclerView) activity.findViewById(R.id.customer_cards_list);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Get card test failed, no items found");
        }

        // Simulate click on first item
        CustomerCardsAdapter adapter = (CustomerCardsAdapter) list.getAdapter();
        View row = new TextView(getApplicationContext());
        row.setTag(adapter.getItem(0));
        adapter.getListener().onClick(row);

        // Validate result
        try {
            ActivityResult activityResult = getActivityResult(activity);
            PaymentMethodRow pmRow = JsonUtil.getInstance().fromJson(activityResult.getExtras().getString("paymentMethodRow"), PaymentMethodRow.class);
            assertTrue(activityResult.getResultCode() == Activity.RESULT_OK);
            assertTrue(pmRow.getCard().getPaymentMethod().getId().equals("master"));
        } catch (Exception ex) {
            fail("Get card test failed, cause: " + ex.getMessage());
        }
    }

    public void testNullCards() {

        Activity activity = prepareActivity(null);
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    public void testWrongCards() {

        Activity activity = prepareActivityWrongCards();
        assertFinishCalledWithResult(activity, Activity.RESULT_CANCELED);
    }

    private Activity prepareActivity(List<Card> cards) {

        Intent intent = new Intent();
        putListExtra(intent, "cards", cards);
        setActivityIntent(intent);
        return getActivity();
    }

    private Activity prepareActivityWrongCards() {

        Intent intent = new Intent();
        intent.putExtra("cards", "1234");
        setActivityIntent(intent);
        return getActivity();
    }
}
