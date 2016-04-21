package com.mercadopago.model;

import com.mercadopago.VaultActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

public class CardTest extends BaseTest<VaultActivity> {

    public CardTest() {
        super(VaultActivity.class);
    }

    public void testIsSecurityCodeRequired() {

        Card card = StaticMock.getCard(getApplicationContext());

        assertTrue(card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredNull() {

        Card card = StaticMock.getCard(getApplicationContext());
        card.setSecurityCode(null);
        assertTrue(!card.isSecurityCodeRequired());
    }

    public void testIsSecurityCodeRequiredLengthZero() {

        Card card = StaticMock.getCard(getApplicationContext());
        card.getSecurityCode().setLength(0);
        assertTrue(!card.isSecurityCodeRequired());
    }
}
