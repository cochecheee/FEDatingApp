package vn.iotstar.dating_fe.API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.iotstar.dating_fe.entities.Reports;

public interface ReportAPI {
    @POST("/report/reportUser")
    Call<Void> reportUser(@Body Reports report);
}
