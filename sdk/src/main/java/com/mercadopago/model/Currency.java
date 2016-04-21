package com.mercadopago.model;

public class Currency {

    private String id;
    private String description;
    private String symbol;
    private int decimalPlaces;
    private Character decimalSeparator;
    private Character thousandsSeparator;

    public Currency() {

    }

    public Currency(String id, String description, String symbol,
                    int decimalPlaces, Character decimalSeparator, Character thousandsSeparator) {

        this.id = id;
        this.description = description;
        this.symbol = symbol;
        this.decimalPlaces = decimalPlaces;
        this.decimalSeparator = decimalSeparator;
        this.thousandsSeparator = thousandsSeparator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Character getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(Character decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public Character getThousandsSeparator() {
        return thousandsSeparator;
    }

    public void setThousandsSeparator(Character thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

    @Override
    public String toString() {
        return "Currency [id=" + id + ", description=" + description
                + ", symbol=" + symbol + ", decimalPlaces=" + decimalPlaces
                + "]";
    }
}
