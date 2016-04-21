package com.mercadopago;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

public class BankDealsActivityTest extends BaseTest<BankDealsActivity> {

    public BankDealsActivityTest() {

        super(BankDealsActivity.class);
    }

    public void testBankDeals() {

        // Set activity
        BankDealsActivity activity = prepareActivity(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY);

        sleepThread();

        // Validate view
        RecyclerView list = (RecyclerView) activity.findViewById(R.id.bank_deals_list);
        if ((list != null) && (list.getAdapter() != null)) {
            assertTrue(list.getAdapter().getItemCount() > 0);
        } else {
            fail("Bank Deals test failed, no items found");
        }
    }

    private BankDealsActivity prepareActivity(String merchantPublicKey) {

        Intent intent = new Intent();
        if (merchantPublicKey != null) {
            intent.putExtra("merchantPublicKey", merchantPublicKey);
        }
        setActivityIntent(intent);
        return getActivity();
    }
}
