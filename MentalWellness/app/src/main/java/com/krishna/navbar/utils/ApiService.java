package com.krishna.navbar.utils;

import com.krishna.navbar.models.PredictionRequest;
import com.krishna.navbar.models.PredictionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    
    @POST("predict")
    Call<PredictionResponse> getPrediction(@Body PredictionRequest request);
} 