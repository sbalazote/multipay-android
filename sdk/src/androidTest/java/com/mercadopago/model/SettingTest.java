package com.mercadopago.model;

import com.mercadopago.VaultActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

import java.util.ArrayList;

public class SettingTest extends BaseTest<VaultActivity> {

    public SettingTest() {
        super(VaultActivity.class);
    }

    public void testGetSettingByBin() {

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());
        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), "466057");
        assertTrue(setting != null);
    }

    public void testGetSettingByBinWrongBin() {

        PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());
        Setting setting = Setting.getSettingByBin(paymentMethod.getSettings(), "888888");
        assertTrue(setting == null);
    }

    public void testGetSettingByBinNullSettings() {

        Setting setting = Setting.getSettingByBin(new ArrayList<Setting>(), "466057");
        assertTrue(setting == null);
        setting = Setting.getSettingByBin(null, "466057");
        assertTrue(setting == null);
    }
}
