package com.akshay.meetwm.respository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akshay.meetwm.model.UserContact
import com.akshay.meetwm.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserRepository(userContact: UserContact) {

    var callResponse = MutableLiveData<String>()
    var id = userContact.id
    var user_name = userContact.user_name
    var number = userContact.number
    var status = userContact.status
    var photo_url = userContact.photo_url

    suspend fun registerUser(){

        val call = RetrofitClient.apiInterface.registerUser(id, user_name, number, status, photo_url)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val newId : String
                if(response.isSuccessful){
                    newId = response.body().toString()
                    setID(newId)
                    Log.d("LOGGED IN", newId)
                    callResponse.value = newId
                }else{
                    Log.d("LOGGED IN", response.errorBody().toString())
                    callResponse.value = "ERROR"
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("LOGGED IN", t.localizedMessage)
                callResponse.value = "ERROR"
            }

        })
    }

    fun setID(uid : String){
        callResponse.value = uid
    }
}