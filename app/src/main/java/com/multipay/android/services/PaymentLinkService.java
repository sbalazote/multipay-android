package com.multipay.android.services;

import com.multipay.android.dtos.PaymentLinkDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Sebastian on 14/09/2016.
 */
public interface PaymentLinkService {
	@Headers("Accept: application/json")
	@POST("/api/paymentLink")
	Call<Object> paymentLink(@Body PaymentLinkDTO paymentLinkDTO);
}
