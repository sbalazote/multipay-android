package com.mercadopago.model;

import java.math.BigDecimal;
import java.util.List;

public class PayerCost {

    private Integer installments;
    private BigDecimal installmentRate;
    private List<String> labels;
    private BigDecimal minAllowedAmount;
    private BigDecimal maxAllowedAmount;
    private String recommendedMessage;
    private BigDecimal installmentAmount;
    private BigDecimal totalAmount;

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public BigDecimal getInstallmentRate() {
        return installmentRate;
    }

    public void setInstallmentRate(BigDecimal installmentRate) {
        this.installmentRate = installmentRate;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public BigDecimal getMinAllowedAmount() {
        return minAllowedAmount;
    }

    public void setMinAllowedAmount(BigDecimal minAllowedAmount) {
        this.minAllowedAmount = minAllowedAmount;
    }

    public BigDecimal getMaxAllowedAmount() {
        return maxAllowedAmount;
    }

    public void setMaxAllowedAmount(BigDecimal maxAllowedAmount) {
        this.maxAllowedAmount = maxAllowedAmount;
    }

    public String getRecommendedMessage() {
        return recommendedMessage;
    }

    public void setRecommendedMessage(String recommendedMessage) {
        this.recommendedMessage = recommendedMessage;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return installments.toString();
    }
}
