package com.mercadopago.model;

public class PaymentMethodRow {

    private String label;
    private Card card;
    private int icon;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PaymentMethodRow(Card card, String label, int icon) {

        this.card = card;
        this.label = label;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
