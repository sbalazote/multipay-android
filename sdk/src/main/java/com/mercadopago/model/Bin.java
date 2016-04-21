package com.mercadopago.model;

public class Bin {

    private String exclusionPattern;
    private String installmentsPattern;
    private String pattern;

    public String getExclusionPattern() {
        return exclusionPattern;
    }

    public void setExclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern;
    }

    public String getInstallmentsPattern() {
        return installmentsPattern;
    }

    public void setInstallmentsPattern(String installmentsPattern) {
        this.installmentsPattern = installmentsPattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
