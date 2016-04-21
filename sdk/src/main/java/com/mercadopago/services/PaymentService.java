package com.mercadopago.services;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaymentService {

    @GET("/v1/payment_methods")
    ErrorHandlingCallAdapter.MyCall<List<PaymentMethod>> getPaymentMethods(@Query("public_key") String publicKey);

    @GET("/v1/payment_methods/installments")
    ErrorHandlingCallAdapter.MyCall<List<Installment>> getInstallments(@Query("public_key") String publicKey, @Query("bin") String bin, @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId, @Query("payment_type_id") String paymentTypeId, @Query("locale") String locale);

    @GET("/v1/payment_methods/card_issuers")
    ErrorHandlingCallAdapter.MyCall<List<Issuer>> getIssuers(@Query("public_key") String publicKey, @Query("payment_method_id") String paymentMethodId);

}