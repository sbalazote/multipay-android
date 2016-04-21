package com.mercadopago.model;

import java.util.List;

public class MerchantPayment {

    private Long cardIssuerId;
    private String cardToken;
    private Long campaignId;
    private Integer installments;
    private Item item;
    private String merchantAccessToken;
    private String paymentMethodId;

    public MerchantPayment() {}

    public MerchantPayment(Item item, Integer installments, Long cardIssuerId, String cardToken,
                           String paymentMethodId, Long campaignId, String merchantAccessToken) {

        this.item = item;
        this.installments = installments;
        this.cardIssuerId = cardIssuerId;
        this.cardToken = cardToken;
        this.merchantAccessToken = merchantAccessToken;
        this.paymentMethodId = paymentMethodId;
        this.campaignId = campaignId;
    }

    public Long getCardIssuerId() {
        return cardIssuerId;
    }

    public void setCardIssuerId(Long cardIssuerId) {
        this.cardIssuerId = cardIssuerId;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String card) {
        this.cardToken = card;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(List<Item> items) {
        this.item = item;
    }

    public String getMerchantAccessToken() {
        return merchantAccessToken;
    }

    public void setMerchantAccessToken(String merchantAccessToken) {
        this.merchantAccessToken = merchantAccessToken;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
