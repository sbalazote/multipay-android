package com.mercadopago.model;

import java.math.BigDecimal;

public class Item {

    private String id;
    private Integer quantity;
    private BigDecimal unitPrice;

    public Item() {}

    public Item(String id, Integer quantity, BigDecimal unitPrice) {

        this.id = id;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
