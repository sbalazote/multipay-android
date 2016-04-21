package com.mercadopago.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class Token {

    private String id;
    private String publicKey;
    private String cardId;
    private String luhnValidation;
    private String status;
    private String usedDate;
    private Integer cardNumberLength;
    private Date creationDate;
    private String truncCardNumber;
    private Integer securityCodeLength;
    private Integer expirationMonth;
    private Integer expirationYear;
    private Date lastModifiedDate;
    private Date dueDate;

    public Integer getSecurityCodeLength() {
        return securityCodeLength;
    }

    public void setSecurityCodeLength(Integer securityCodeLength) {
        this.securityCodeLength = securityCodeLength;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getLuhnValidation() {
        return luhnValidation;
    }

    public void setLuhnValidation(String luhnValidation) {
        this.luhnValidation = luhnValidation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(String usedDate) {
        this.usedDate = usedDate;
    }

    public Integer getCardNumberLength() {
        return cardNumberLength;
    }

    public void setCardNumberLength(Integer cardNumberLength) {
        this.cardNumberLength = cardNumberLength;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getTruncCardNumber() {
        return truncCardNumber;
    }

    public void setTruncCardNumber(String truncCardNumber) {
        this.truncCardNumber = truncCardNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public static Token parseJson(String json) {

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        return gson.fromJson(json, Token.class);
    }

    public String toJson() {

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        return gson.toJson(this);
    }
}