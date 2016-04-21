package com.mercadopago.model;

import java.util.List;

public class Setting {

    private Bin bin;
    private CardNumber cardNumber;
    private SecurityCode securityCode;

    public Bin getBin() {
        return bin;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public CardNumber getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(CardNumber cardNumber) {
        this.cardNumber = cardNumber;
    }

    public SecurityCode getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(SecurityCode securityCode) {
        this.securityCode = securityCode;
    }

    public static Setting getSettingByBin(List<Setting> settings, String bin) {

        Setting selectedSetting = null;

        if (settings != null && settings.size() > 0 ) {

            for (Setting setting : settings) {

                if (!"".equals(bin) && bin.matches(setting.getBin().getPattern() + ".*") &&
                        (setting.getBin().getExclusionPattern() == null ||
                                !bin.matches(setting.getBin().getExclusionPattern() + ".*"))) {
                    selectedSetting = setting;
                }
            }
        }

        return selectedSetting;
    }
}
