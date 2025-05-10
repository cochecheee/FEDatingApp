package com.example.fedatingapp.api;

import com.example.fedatingapp.entities.Reports;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ReportAPI {
    @Headers({"Accept: application/json"})
    @POST("/report/reportUser")
    Call<Void> reportUser(@Header("Authorization") String authToken, @Body Reports report);
}
