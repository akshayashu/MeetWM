package com.akshay.meetwm.ui.RegisterUser

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.akshay.meetwm.model.UserContact
import com.akshay.meetwm.respository.RegisterUserRepository
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    var response : LiveData<String>? = null

    fun register(userContact: UserContact) = viewModelScope.launch{
        RegisterUserRepository(userContact).registerUser()
        Log.d("ViewModel - ", "RECEIVED $response")
    }
}