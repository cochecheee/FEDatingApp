package com.example.fedatingapp.Service;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.fedatingapp.api.ReportAPI;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.entities.Reports;
import com.example.fedatingapp.utils.TokenManager;

public class ReportService {
    private String token = "";
    public ReportService(String token){
        this.token = token;
        Log.d("ReportService", "ReportService: " + token);
    }
    private final ReportAPI reportAPI = RetrofitClient.getRetrofit().create(ReportAPI.class);

    // Report a user
    public void reportUser(Reports report, Callback<Void> callback) {
        Call<Void> call = reportAPI.reportUser(token,report);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ReportService", "Report user thanh cong: " + response.body());
                    callback.onResponse(call, Response.success(null));
                } else {
                    Log.e("ReportService", "Report user that bai: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ReportService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }
}