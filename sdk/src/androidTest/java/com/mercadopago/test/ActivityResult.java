package com.mercadopago.test;

import android.os.Bundle;

public class ActivityResult {

    private Integer resultCode;
    private Bundle extras;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Bundle getExtras() {
        return extras;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }
}
