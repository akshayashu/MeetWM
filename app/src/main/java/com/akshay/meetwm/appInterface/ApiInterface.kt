package com.akshay.meetwm.appInterface

import com.akshay.meetwm.model.UserContact
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiInterface {

    @FormUrlEncoded
    @POST("register_user")
    suspend fun registerUser(
        @Field("_id") id : String,
        @Field("user_name") userName: String,
        @Field("number") number: String,
        @Field("status") status: String,
        @Field("photo_url") url : String
    ) : Response<String>


    @FormUrlEncoded
    @POST("get_registered_user")
    suspend fun getRegisteredUser(
        @Field("number") number : ArrayList<String>
    ) : Response<ArrayList<UserContact>>
}