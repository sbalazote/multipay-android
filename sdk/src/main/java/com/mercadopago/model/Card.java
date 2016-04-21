package com.mercadopago.model;

import java.util.Date;

public class Card {

    private Cardholder cardHolder;
    private String customerId;
    private Date dateCreated;
    private Date dateLastUpdated;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String firstSixDigits;
    private String id;
    private Issuer issuer;
    private String lastFourDigits;
    private PaymentMethod paymentMethod;
    private SecurityCode securityCode;

    public Cardholder getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(Cardholder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getFirstSixDigits() {
        return firstSixDigits;
    }

    public void setFirstSixDigits(String firstSixDigits) {
        this.firstSixDigits = firstSixDigits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public SecurityCode getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(SecurityCode securityCode) {
        this.securityCode = securityCode;
    }

    public boolean isSecurityCodeRequired() {

        if (securityCode != null) {
            if (securityCode.getLength() != 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}