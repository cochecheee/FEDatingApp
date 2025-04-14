package com.example.fedatingapp.API;

import com.example.fedatingapp.models.ImgurResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImgurAPI {
    @Multipart
    @POST("3/image")
    Call<ImgurResponse> uploadImage(
            @Header("Authorization") String auth,
            @Part MultipartBody.Part image,
            @Part("type") RequestBody type,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description
    );
}
