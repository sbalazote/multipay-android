package com.mercadopago.model;

import com.mercadopago.R;
import com.mercadopago.VaultActivity;
import com.mercadopago.test.BaseTest;
import com.mercadopago.test.StaticMock;

public class SavedCardTokenTest extends BaseTest<VaultActivity> {

    public SavedCardTokenTest() {
        super(VaultActivity.class);
    }

    public void testConstructor() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        assertTrue(savedCardToken.getCardId().equals(StaticMock.DUMMY_CARD_ID));
        assertTrue(savedCardToken.getSecurityCode().equals(StaticMock.DUMMY_SECURITY_CODE));
    }

    public void testValidate() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        assertTrue(savedCardToken.validate());
    }

    // * Card id

    public void testValidateNullCardId() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setCardId(null);
        assertTrue(!savedCardToken.validate());
    }

    public void testValidateWrongCardId() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setCardId("john");
        assertTrue(!savedCardToken.validate());
    }

    // * Security code

    public void testSecurityCode() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        assertTrue(savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeEmpty() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("");

        assertTrue(!savedCardToken.validate());
        assertTrue(!savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeMinLength() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("4");

        assertTrue(!savedCardToken.validate());
        assertTrue(!savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeMaxLength() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        savedCardToken.setSecurityCode("44444");

        assertTrue(!savedCardToken.validate());
        assertTrue(!savedCardToken.validateSecurityCode());
    }

    public void testSecurityCodeLengthZero() {

        SavedCardToken savedCardToken = StaticMock.getSavedCardToken();
        Card card = StaticMock.getCard(getApplicationContext());

        savedCardToken.setSecurityCode(null);

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
            fail("Should have failed on security code length zero test");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals("Security code is null"));
        }

        savedCardToken.setSecurityCode("4444");

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
            fail("Should have failed on security code length zero test");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().equals(getActivity().getString(R.string.mpsdk_invalid_cvv_length, 3)));
        }

        // Simulate a card with security code not required
        savedCardToken.setSecurityCode(StaticMock.DUMMY_SECURITY_CODE);
        card.getSecurityCode().setLength(0);

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
        } catch (Exception ex) {
            fail("Security code length zero test failed, cause: " + ex.getMessage());
        }

        card.setSecurityCode(null);

        try {
            savedCardToken.validateSecurityCode(getApplicationContext(), card);
        } catch (Exception ex) {
            fail("Security code length zero test failed, cause: " + ex.getMessage());
        }
    }
}
