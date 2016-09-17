package com.multipay.android.dtos;

import java.io.Serializable;

public class LoginRequestDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
	private String mobileId;
	private String registrationId;
	private String socialToken;
	private boolean isSeller;
	private Integer phoneAreaCode;
	private String phoneNumber;

	public String getUserEmail() {
		return email;
	}

	public void setUserEmail(String userEmail) {
		this.email = userEmail;
	}

	public String getUserPassword() {
		return password;
	}

	public void setUserPassword(String userPassword) {
		this.password = userPassword;
	}

	public String getMobileId() {
		return mobileId;
	}

	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getSocialToken() {
		return socialToken;
	}

	public void setSocialToken(String socialToken) {
		this.socialToken = socialToken;
	}

	public boolean isSeller() {
		return isSeller;
	}

	public void setSeller(boolean seller) {
		isSeller = seller;
	}

	public Integer getPhoneAreaCode() {
		return phoneAreaCode;
	}

	public void setPhoneAreaCode(Integer phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}