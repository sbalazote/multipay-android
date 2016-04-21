package com.mercadopago.model;

import java.math.BigDecimal;

public class TransactionDetails {

    private String externalResourceUrl;
    private String financialInstitution;
    private BigDecimal installmentAmount;
    private BigDecimal netReceivedAmount;
    private BigDecimal overpaidAmount;
    private String paymentMethodReferenceId;
    private BigDecimal totalPaidAmount;

    public String getExternalResourceUrl() {
        return externalResourceUrl;
    }

    public void setExternalResourceUrl(String externalResourceUrl) {
        this.externalResourceUrl = externalResourceUrl;
    }

    public String getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(String financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getNetReceivedAmount() {
        return netReceivedAmount;
    }

    public void setNetReceivedAmount(BigDecimal netReceivedAmount) {
        this.netReceivedAmount = netReceivedAmount;
    }

    public BigDecimal getOverpaidAmount() {
        return overpaidAmount;
    }

    public void setOverpaidAmount(BigDecimal overpaidAmount) {
        this.overpaidAmount = overpaidAmount;
    }

    public String getPaymentMethodReferenceId() {
        return paymentMethodReferenceId;
    }

    public void setPaymentMethodReferenceId(String paymentMethodReferenceId) {
        this.paymentMethodReferenceId = paymentMethodReferenceId;
    }

    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }
}
