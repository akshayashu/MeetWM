package com.akshay.meetwm.ui.RegisterUser

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.akshay.meetwm.model.UserContact
import com.akshay.meetwm.respository.RegisterUserRepository
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    var response = MutableLiveData<String>()
    val repo : RegisterUserRepository

    init {
        repo = RegisterUserRepository()
    }

    fun register(userContact: UserContact) = viewModelScope.launch{
        response.value = repo.registerUser(userContact)
        Log.d("ViewModel - ", "RECEIVED ${response.value}")
    }
}