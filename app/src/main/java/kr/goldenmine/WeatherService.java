package kr.goldenmine;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst")
    Call<String> getWeather(
            @Query("serviceKey") String ServiceKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numRows,
            @Query("dataType") String dataType,
            @Query("base_date") String BaseDate,
            @Query("base_time") String BaseTime,
            @Query("nx") int nx,
            @Query("ny") int ny
    );
}
