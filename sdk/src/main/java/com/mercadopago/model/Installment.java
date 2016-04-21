package com.mercadopago.model;

import java.util.List;

public class Installment {

    private Issuer issuer;
    private List<PayerCost> payerCosts;
    private String paymentMethodId;
    private String paymentTypeId;

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public List<PayerCost> getPayerCosts() {
        return payerCosts;
    }

    public void setPayerCosts(List<PayerCost> payerCosts) {
        this.payerCosts = payerCosts;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }
}
