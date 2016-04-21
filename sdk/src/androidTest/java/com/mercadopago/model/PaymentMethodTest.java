package com.mercadopago.model;

import com.mercadopago.VaultActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

public class PaymentMethodTest extends BaseTest<VaultActivity> {

    public PaymentMethodTest() {
        super(VaultActivity.class);
    }

    public void testIsIssuerRequired() {

        PaymentMethod visa = StaticMock.getPaymentMethod(getApplicationContext());
        PaymentMethod master = StaticMock.getPaymentMethod(getApplicationContext(), "_issuer_required");
        assertTrue(!visa.isIssuerRequired());
        assertTrue(master.isIssuerRequired());
    }

    public void testIsSecurityCodeRequired() {

        PaymentMethod visa = StaticMock.getPaymentMethod(getApplicationContext());
        PaymentMethod tarshop = StaticMock.getPaymentMethod(getApplicationContext(), "_cvv_not_required");
        assertTrue(visa.isSecurityCodeRequired("466057"));
        assertTrue(tarshop.isSecurityCodeRequired("603488"));
        assertTrue(!tarshop.isSecurityCodeRequired("27995"));
    }
}
