package com.multipay.android.utils;

/**
 * Created by Sebastian on 19/04/2016.
 */
public class Constant {
    public static final String MERCHANT_PUBLIC_KEY = "444a9ef5-8a6b-429f-abdf-587639155d88";

    /**
     *  MultiPay parametros
     */
    public static String CLIENT_ID = "3108634673635661";
    public static String REDIRECT_URI = "https://676221d5.ngrok.io/api/authorization";
    public static String OAUTH_URL = "https://auth.mercadolibre.com.ar/authorization";

    // * Merchant server vars
    //public static final String MERCHANT_BASE_URL = "http://10.0.2.2:5000/";
    public static final String MERCHANT_BASE_URL = "https://676221d5.ngrok.io/";
    public static final String MERCHANT_GET_CUSTOMER_URI = "/api/getCustomer";
    public static final String MERCHANT_CREATE_PAYMENT_URI = "/api/doPayment";
    public static final String MERCHANT_GET_DISCOUNT_URI = "/api/getDiscounts";

    public static final String  GOOGLE_OAUTH_SERVER_CLIENT_ID = "437797444824-6ovnf5l1l1he589sv32tm21qciamiqml.apps.googleusercontent.com";

}
