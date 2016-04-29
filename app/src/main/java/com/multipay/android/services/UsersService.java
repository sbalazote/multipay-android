package com.multipay.android.services;

import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.Token;
import com.multipay.android.dtos.PaymentDataDTO;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsersService {
	
	@FormUrlEncoded
	@Headers("Accept: application/json")
	@POST("/api/users")
	Call<String> addSeller(
    		@Field("Email") String email,
    		@Field("Name") String name,
    		@Field("Date") String date,
    		@Field("Password") String password,
    		@Field("Active") Boolean active,
    		@Field("AuthCode") String authCode,
    		@Field("Token") String token);
	
	@GET("/api/users")
	@Headers("Accept: application/json")
    void getUsers(Callback<List<String>> callback);

	@GET("/api/getCustomer")
	@Headers("Accept: application/json")
	Call<String> getCustomer(@Query("customerId") String customerId);

	/* Saves a new card to the customer.
	 */
	@Headers("Accept: application/json")
	@POST("/api/customers/{customerId}/cards")
	Call<Card> AddNewCardToCustomer(@Path("customerId") String customerId, @Body String cardToken);

	@Headers("Accept: application/json")
	@POST("/api/doPayment")
	Call<Payment> doPayment(@Body PaymentDataDTO paymentData);

	/*	Looks for customers by many criterias.
	 */
	@FormUrlEncoded
	@Headers("Accept: application/json")
	@GET("/v1/customers/search")
	Call<String> customers(@Query("access_token") String access_token, @Body String email);


	/*	Makes a new customer.
	 */
	@Headers("Accept: application/json")
	@POST("/v1/customers")
	Call<Customer> newCustomer(@Query("access_token") String access_token, @Body Customer customer);

	/* Retrieves information about a customer.
	 */
	@Headers("Accept: application/json")
	@GET("/v1/customers/{id}")
	Call<Customer> getCustomerById(@Path("id") String id, @Query("access_token") String access_token);

	/* Updates a customer.
	 */
	@Headers("Accept: application/json")
	@PUT("/v1/customers/{id}")
	Call<Customer> updateCustomer(@Path("id") String id, @Query("access_token") String access_token, @Body Customer customer);

	/* Removes a customer.
	 */
	@Headers("Accept: application/json")
	@DELETE("/v1/customers/{id}")
	Call<Customer> deleteCustomer(@Path("id") String id, @Query("access_token") String access_token);

	/* Retrieves all cards from a customer.
	 */
	@Headers("Accept: application/json")
	@GET("/v1/customers/{id}/cards")
	Call<List<Card>> retrieveAllCards(@Path("id") String id, @Query("access_token") String access_token);

	/* Saves a new card to the customer.
	 */
	@Headers("Accept: application/json")
	@POST("/v1/customers/{id}/cards")
	Call<Card> newCard(@Path("id") String id, @Query("access_token") String access_token, @Body String cardToken);

	/* Retrieves information about a customer's card.
	 */
	@Headers("Accept: application/json")
	@GET("/v1/customers/{customer_id}/cards/{card_id}")
	Call<Card> getCardById(@Path("customer_id") String customerId, @Path("card_id") String cardId, @Query("access_token") String access_token);

	/* Updates a customer's card.
	 */
	@Headers("Accept: application/json")
	@PUT("/v1/customers/{id}/cards/{card_id}")
	Call<Card> updateCard(@Path("id") String id, @Query("access_token") String access_token, @Path("card_id") String cardId, Token cardToken);

	/* Removes a customer's card.
	 */
	@Headers("Accept: application/json")
	@DELETE("/v1/customers/{id}/cards/{card_id}")
	Call<Card> deleteCard(@Path("id") String id, @Query("access_token") String access_token, @Path("card_id") Token cardToken);
}
