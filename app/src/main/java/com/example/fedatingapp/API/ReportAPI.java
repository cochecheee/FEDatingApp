package com.example.fedatingapp.api;

import com.example.fedatingapp.entities.Reports;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ReportAPI {
    @POST("/report/reportUser")
    Call<Void> reportUser(@Body Reports report);
}
