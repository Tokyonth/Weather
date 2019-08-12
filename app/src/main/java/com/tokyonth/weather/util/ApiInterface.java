package com.tokyonth.weather.util;

import com.tokyonth.weather.model.bean.CityList;
import com.tokyonth.weather.model.bean.Weather;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("city")
    Observable<CityList> getCityList(@Query("appkey") String appkey);

    @GET("query")
    Observable<Weather> getWeather(@Query("appkey") String appkey, @Query("citycode") String cityCode);

    @GET("query")
    Observable<ResponseBody> getLocationWeather(@Query("appkey") String appkey, @Query("city") String location);

   // @GET("query")
  //  Observable<ResponseBody> getLocationWeatherJson(@Query("appkey") String appkey, @Query("location") String location);

}
