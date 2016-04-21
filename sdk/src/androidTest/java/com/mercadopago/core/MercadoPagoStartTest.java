package com.mercadopago.core;

import com.mercadopago.VaultActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

public class MercadoPagoStartTest extends BaseTest<VaultActivity> {

    public MercadoPagoStartTest() {
        super(VaultActivity.class);
    }

    public void testStartOk() {

        try {
            new MercadoPago.Builder()
                .setContext(getApplicationContext())
                .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();
        } catch (Exception ex) {
            fail("Failed on regular start:" + ex.getMessage());
        }
    }

    public void testContextNull() {

        try {
            new MercadoPago.Builder()
                .setPublicKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();
            fail("Start should have failed on context null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("context is null"));
        }
    }

    public void testNullKeyType() {

        try {
            new MercadoPago.Builder()
                .setContext(getApplicationContext())
                .setKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, null)
                .build();
            fail("Start should have failed on key type null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("key type is null"));
        }
    }

    public void testInvalidKeyType() {

        try {
            new MercadoPago.Builder()
                .setContext(getApplicationContext())
                .setKey(StaticMock.DUMMY_MERCHANT_PUBLIC_KEY, "wrong type")
                .build();
            fail("Start should have failed on invalid key type");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("invalid key type"));
        }
    }

    public void testNullKey() {

        try {
            new MercadoPago.Builder()
                .setContext(getApplicationContext())
                .setKey(null, MercadoPago.KEY_TYPE_PUBLIC)
                .build();
            fail("Start should have failed on key null");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("key is null"));
        }
    }
}
