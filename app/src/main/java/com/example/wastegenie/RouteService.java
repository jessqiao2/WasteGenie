package com.example.wastegenie;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RouteService {
    @POST("json")
    Call<ResponseBody> getRoute(@Query("origin") String origin, @Query("destination") String destination,
                                @Query("waypoints") String waypoints, @Query("key") String key);
    @POST("json")
    Call<ResponseBody> getSimpleRoute(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);
}
