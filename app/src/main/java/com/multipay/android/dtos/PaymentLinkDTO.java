package com.multipay.android.dtos;

import java.io.Serializable;

/**
 * Created by Sebastian on 14/09/2016.
 */
public class PaymentLinkDTO implements Serializable {
	private Integer areaCode;
	private String number;
	private String description;
	Float transactionAmount;
	String sellerEmail;

	public PaymentLinkDTO(Integer areaCode, String number, String description, Float transactionAmount, String sellerEmail) {
		this.areaCode = areaCode;
		this.number = number;
		this.description = description;
		this.transactionAmount = transactionAmount;
		this.sellerEmail = sellerEmail;
	}

	public Integer getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public String getSellerEmail() {
		return sellerEmail;
	}

	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}
}