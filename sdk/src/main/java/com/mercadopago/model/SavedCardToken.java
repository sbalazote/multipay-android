package com.mercadopago.model;

import android.content.Context;
import android.text.TextUtils;

import com.mercadopago.R;

public class SavedCardToken {

    private String cardId;
    private String securityCode;
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Context context) {
        this.device = new Device(context);
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardNumber) {
        this.cardId = cardNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public SavedCardToken(String cardId, String securityCode) {
        this.cardId = cardId;
        this.securityCode = securityCode;
    }

    public boolean validate() {
        return validateCardId() && validateSecurityCode();
    }

    public boolean validateCardId(){
        return !TextUtils.isEmpty(cardId) && TextUtils.isDigitsOnly(cardId);
    }

    public boolean validateSecurityCode(){

        return CardToken.validateSecurityCode(securityCode);
    }

    public void validateSecurityCode(Context context, Card card) throws Exception {

        // Validate security code length
        if (securityCode != null) {
            int cvvLength = (card.getSecurityCode() != null) ? card.getSecurityCode().getLength() : 0;
            if ((cvvLength != 0) && (securityCode.length() != cvvLength)) {
                throw new Exception(context.getString(R.string.mpsdk_invalid_cvv_length, cvvLength));
            }
        } else {
            throw new Exception("Security code is null");
        }
    }
}
