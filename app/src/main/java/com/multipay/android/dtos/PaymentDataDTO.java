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
	String buyerEmail;
	String sellerEmail;

	public PaymentDataDTO(String cardToken, Float transactionAmount, String paymentMethodId, String buyerEmail, String sellerEmail) {
		this.cardToken = cardToken;
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
