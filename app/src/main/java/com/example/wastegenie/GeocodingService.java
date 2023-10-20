package com.example.wastegenie;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingService {
    @GET("json")
    Call<ResponseBody> getLongLat(@Query("address") String address, @Query("key") String key);
}
