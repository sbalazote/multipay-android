package com.mercadopago.model;

import android.content.Context;

public class Device {

    Fingerprint fingerprint;

    public Device(Context context) {
        this.fingerprint = new Fingerprint(context);
    }
}
