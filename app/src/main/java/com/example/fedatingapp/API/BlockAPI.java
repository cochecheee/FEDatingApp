package vn.iotstar.dating_fe.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.iotstar.dating_fe.entities.MatchList;

public interface BlockAPI {
    // POST /block/ - Block user
    @POST("/block/")
    Call<Void> blockUser(@Body MatchList blockUser);

    // POST /block/getAll - Get all blocked users
    @POST("/block/getAll")
    Call<List<MatchList>> getAllBlockUser(@Query("userId") Long userId);

    // POST /block/delete - Unblock user
    @POST("/block/delete")
    Call<Void> unBlockUser(@Body MatchList unBlockUser);
}
