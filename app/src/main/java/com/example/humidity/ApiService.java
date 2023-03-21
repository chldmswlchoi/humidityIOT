package com.example.humidity;




import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
public interface ApiService {

    @Headers("Accept: application/json")
    @GET("rpi/get_relativeHumidity.php")
    //    () 안에 디테일한 url 주소 설정
    Call<DTOHDT> setRelativeHumidity(
            @Query("specific_date") String specific_date
    );



}

