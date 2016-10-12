package com.multipay.android.dtos;

import java.io.Serializable;

public class PaymentDataDTO implements Serializable {
	String cardToken;
	String description;
	Float transactionAmount;
	String paymentMethodId;
	String buyerEmail;
	String sellerEmail;

	public PaymentDataDTO(String cardToken, String description, Float transactionAmount, String paymentMethodId, String buyerEmail, String sellerEmail) {
		this.cardToken = cardToken;
		this.description = description;
		this.transactionAmount = transactionAmount;
		this.paymentMethodId = paymentMethodId;
		this.buyerEmail = buyerEmail;
		this.sellerEmail = sellerEmail;
	}

	public String getCardToken() {
		return cardToken;
	}

	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	public String getSellerEmail() {
		return sellerEmail;
	}

	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}
}