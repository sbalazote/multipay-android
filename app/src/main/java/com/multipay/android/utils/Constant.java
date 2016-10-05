package com.multipay.android.utils;

public class Constant {
	/**
     *  Clave Publica del Vendedor asociado.
     */
    public static final String MERCHANT_PUBLIC_KEY = "TEST-ec122dc5-a802-43c1-92d5-c4e5670f3c8c";

    /**
     *  MultiPay parametros
     */
    public static String CLIENT_ID = "5196180579796665";
    public static String OAUTH_URL = "https://auth.mercadolibre.com.ar/authorization";

	/**
     *  Merchant server vars
     */
    public static final String MERCHANT_BASE_URL = "https://3651a0a0.ngrok.io";
    public static final String MERCHANT_GET_CUSTOMER_URI = "/api/getCustomer";
    public static final String MERCHANT_CREATE_PAYMENT_URI = "/api/doPayment";
    public static final String MERCHANT_GET_DISCOUNT_URI = "/api/getDiscounts";
    public static final String MERCHANT_REDIRECT_URI = "/api/authorization";

    public static final String  GOOGLE_OAUTH_SERVER_CLIENT_ID = "437797444824-6ovnf5l1l1he589sv32tm21qciamiqml.apps.googleusercontent.com";

}
