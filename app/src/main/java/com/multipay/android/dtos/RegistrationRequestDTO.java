package com.multipay.android.dtos;

import java.io.Serializable;

/**
 * Created by Sebastian on 02/05/2016.
 */
public class RegistrationRequestDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mobileId;
	private String registrationId;
	private String email;
	private String name;
	private String lastName;
	private String password;
	private String identificationType;
	private String identificationNumber;
	private String addressName;
	private Integer addressNumber;
	private String addressZipCode;
	private Integer phoneAreaCode;
	private String phoneNumber;
	private boolean isSeller;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getIdentificationNumber() {
		return identificationNumber;
	}

	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public Integer getAddressNumber() {
		return addressNumber;
	}

	public void setAddressNumber(Integer addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getAddressZipCode() {
		return addressZipCode;
	}

	public void setAddressZipCode(String addressZipCode) {
		this.addressZipCode = addressZipCode;
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

	public boolean isSeller() {
		return isSeller;
	}

	public void setSeller(boolean seller) {
		isSeller = seller;
	}
}
