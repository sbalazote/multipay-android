package com.multipay.android.dtos;

import java.io.Serializable;

import retrofit2.http.Field;

/**
 * Created by Sebastian on 29/04/2016.
 */
public class PaymentDataDTO implements Serializable {
	String cardToken;
	Float transactionAmount;
	String paymentMethodId;
	String customerId;

	public PaymentDataDTO(String cardToken, Float transactionAmount, String paymentMethodId, String customerId) {
		this.cardToken = cardToken;
		this.transactionAmount = transactionAmount;
		this.paymentMethodId = paymentMethodId;
		this.customerId = customerId;
	}

	public String getCardToken() {
		return cardToken;
	}

	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}

	public Float getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Float transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(String paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}
