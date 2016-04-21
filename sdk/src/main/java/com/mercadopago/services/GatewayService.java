package com.mercadopago.services;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GatewayService {

    @POST("/v1/card_tokens")
    ErrorHandlingCallAdapter.MyCall<Token> getToken(@Query("public_key") String publicKey, @Body CardToken cardToken);

    @POST("/v1/card_tokens")
    ErrorHandlingCallAdapter.MyCall<Token> getToken(@Query("public_key") String publicKey, @Body SavedCardToken savedCardToken);

}