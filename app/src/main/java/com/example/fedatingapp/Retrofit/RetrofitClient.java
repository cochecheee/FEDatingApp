package vn.iotstar.dating_fe.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String Base_URL = "http://10.0.2.2:8080/";

    public static Retrofit getRetrofit()
    {
        if (retrofit == null)
        {

            retrofit = new Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
