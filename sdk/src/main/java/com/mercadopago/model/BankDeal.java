package com.mercadopago.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BankDeal {

    private Date dateExpired;
    private Date dateStarted;
    private String id;
    private List<Integer> installments;
    private CardIssuer issuer;
    private String legals;
    private int maxInstallments;
    private List<PaymentMethod> paymentMethods;
    private Picture picture;
    private String recommendedMessage;
    private BigDecimal totalFinancialCost;

    public Date getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getInstallments() {
        return installments;
    }

    public void setInstallments(List<Integer> installments) {
        this.installments = installments;
    }

    public CardIssuer getIssuer() {
        return issuer;
    }

    public void setIssuer(CardIssuer issuer) {
        this.issuer = issuer;
    }

    public String getLegals() {
        return legals;
    }

    public void setLegals(String legals) {
        this.legals = legals;
    }

    public int getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(int maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public String getRecommendedMessage() {
        return recommendedMessage;
    }

    public void setRecommendedMessage(String recommendedMessage) {
        this.recommendedMessage = recommendedMessage;
    }

    public BigDecimal getTotalFinancialCost() {
        return totalFinancialCost;
    }

    public void setTotalFinancialCost(BigDecimal totalFinancialCost) {
        this.totalFinancialCost = totalFinancialCost;
    }
}
