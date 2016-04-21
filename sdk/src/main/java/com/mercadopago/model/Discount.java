package com.mercadopago.model;

import java.math.BigDecimal;

public class Discount {

    private BigDecimal amountOff;
    private BigDecimal couponAmount;
    private String currencyId;
    private Long id;
    private String name;
    private BigDecimal percentOff;

    public BigDecimal getAmountOff() {
        return amountOff;
    }

    public void setAmountOff(BigDecimal amountOff) {
        this.amountOff = amountOff;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(BigDecimal percentOff) {
        this.percentOff = percentOff;
    }
}
