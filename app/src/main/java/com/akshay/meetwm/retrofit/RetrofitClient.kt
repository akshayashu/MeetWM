package com.akshay.meetwm.retrofit

import com.akshay.meetwm.appInterface.ApiInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val url = "http://192.168.0.4:9000/api/"

    private val retrofitClient : Retrofit.Builder by lazy{

        val logging = HttpLoggingInterceptor()
        logging.setLevel(Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logging)

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))

    }

    val apiInterface : ApiInterface by lazy {
        retrofitClient.build().create(ApiInterface::class.java)
    }
}