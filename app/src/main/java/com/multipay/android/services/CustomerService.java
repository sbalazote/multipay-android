package com.multipay.android.services;

import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payment;
import com.multipay.android.dtos.PaymentDataDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CustomerService {
	
	/* Saves a new card to the customer.
	 */
	@Headers("Accept: application/json")
	@POST("/api/customers/{email}/cards")
	Call<Card> AddNewCardToCustomer(@Path("email") String email, @Body String cardToken);

	@Headers("Accept: application/json")
	@POST("/api/doPayment")
	Call<Payment> doPayment(@Body PaymentDataDTO paymentData);

	@GET("/api/getCustomer")
	@Headers("Accept: application/json")
	Call<Customer> getCustomer(@Query("seller_email") String sellerEmail, @Query("buyer_email") String buyerEmail);
}