package vn.iotstar.dating_fe.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.iotstar.dating_fe.entities.Image;
import vn.iotstar.dating_fe.entities.Users;

public interface UsersAPI {
    @POST("profile/get")
    Call<Users> getUserInfo(@Query("userId") Long userId);

    @POST("profile/updateProfile")
    Call<Void> UpdateUserInfo(@Body Users userInfo);

    @POST("profile/addImage")
    Call<Void> addImageProfile(@Body Image image);

    @POST("profile/removeImage")
    Call<Void> delImageProfile(@Body Image image);

    @POST("profile/getAllImage")
    Call<List<Image>> getAllImageProfile(@Query("userId") Long userId);
}
