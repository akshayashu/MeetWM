package com.akshay.meetwm.respository

import com.akshay.meetwm.model.UserContact
import com.akshay.meetwm.retrofit.RetrofitClient

class RegisterUserRepository() {

    suspend fun registerUser(userContact: UserContact) : String{
        val id = userContact.id
        val user_name = userContact.user_name
        val number = userContact.number
        val status = userContact.status
        val photo_url = userContact.photo_url

        return try {
            val call = RetrofitClient.apiInterface.registerUser(id, user_name, number, status, photo_url)

            if(call.isSuccessful){
                call.body().toString()
            }else{
                "ERROR"
            }
        }catch (e : Exception){
            e.localizedMessage.toString()
        }


    }

}