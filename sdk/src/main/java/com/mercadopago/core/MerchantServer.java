package com.mercadopago.core;

import android.content.Context;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.services.MerchantService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import retrofit2.Retrofit;

public class MerchantServer {

    public static ErrorHandlingCallAdapter.MyCall<Customer> getCustomer(Context context, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken) {

        MerchantService service = getService(context, merchantBaseUrl);
        return service.getCustomer(ripFirstSlash(merchantGetCustomerUri), merchantAccessToken);
    }

    public static ErrorHandlingCallAdapter.MyCall<Payment> createPayment(Context context, String merchantBaseUrl, String merchantCreatePaymentUri, MerchantPayment payment) {

        MerchantService service = getService(context, merchantBaseUrl);
        return service.createPayment(ripFirstSlash(merchantCreatePaymentUri), payment);
    }

    public static ErrorHandlingCallAdapter.MyCall<Discount> getDiscount(Context context, String merchantBaseUrl, String merchantGetDiscountUri, String merchantAccessToken, String itemId, Integer itemQuantity) {

        MerchantService service = getService(context, merchantBaseUrl);
        return service.getDiscount(ripFirstSlash(merchantGetDiscountUri), merchantAccessToken, itemId, itemQuantity);
    }

    private static String ripFirstSlash(String uri) {

        return uri.startsWith("/") ? uri.substring(1, uri.length()) : uri;
    }

    private static Retrofit getRestAdapter(Context context, String endPoint) {

        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .client(HttpClientUtil.getClient(context))
                .addConverterFactory(JsonUtil.getInstance().getGsonConverterFactory())
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    private static MerchantService getService(Context context, String endPoint) {

        Retrofit restAdapter =getRestAdapter(context, endPoint);
        return restAdapter.create(MerchantService.class);
    }
}
