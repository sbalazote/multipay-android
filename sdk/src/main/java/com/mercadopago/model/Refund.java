package com.mercadopago.model;

import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.util.Date;

public class Refund {

    private BigDecimal amount;
    private Date dateCreated;
    private Long id;
    private JsonObject metadata;
    private long paymentId;
    private String source;
    private String uniqueSequenceNumber;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUniqueSequenceNumber() {
        return uniqueSequenceNumber;
    }

    public void setUniqueSequenceNumber(String uniqueSequenceNumber) {
        this.uniqueSequenceNumber = uniqueSequenceNumber;
    }
}
