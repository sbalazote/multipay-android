package com.multipay.android.services;

import com.multipay.android.dtos.LoginResponseDTO;
import com.multipay.android.dtos.RegistrationRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Sebastian on 02/05/2016.
 */
public interface RegistrationService {

	@POST("/api/attemptNativeRegistration")
	@Headers("Accept: application/json")
	Call<LoginResponseDTO> attemptNativeRegistration(@Body RegistrationRequestDTO registrationRequestDTO);
}