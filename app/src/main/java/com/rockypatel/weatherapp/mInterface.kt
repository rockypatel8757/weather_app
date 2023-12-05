package com.rockypatel.weatherapp

import com.rockypatel.weatherapp.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface mInterface {

    @GET("weather")
    fun getdata(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String
    ) : Call<Weather>
}