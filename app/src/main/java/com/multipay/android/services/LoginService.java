package com.multipay.android.services;

import com.multipay.android.dtos.LoginRequestDTO;
import com.multipay.android.dtos.LoginResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Sebastian on 19/04/2016.
 */
public interface LoginService {
    @POST("/api/attemptNativeLogin")
    @Headers("Accept: application/json")
    Call<LoginResponseDTO> attemptNativeLogin(@Body LoginRequestDTO loginRequestDTO);

    @POST("/api/googleTokenInfo")
    @Headers("Accept: application/json")
    Call<LoginResponseDTO> googleTokenInfo(@Body LoginRequestDTO loginRequestDTO);

    @POST("/api/facebookTokenInfo")
    @Headers("Accept: application/json")
    Call<LoginResponseDTO> facebookTokenInfo(@Body LoginRequestDTO loginRequestDTO);
}
