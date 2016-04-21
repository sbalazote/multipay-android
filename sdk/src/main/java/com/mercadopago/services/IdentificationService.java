package com.mercadopago.services;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.model.IdentificationType;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IdentificationService {

    @GET("/v1/identification_types")
    ErrorHandlingCallAdapter.MyCall<List<IdentificationType>> getIdentificationTypes(@Query("public_key") String publicKey, @Query("access_token") String privateKey);

}
