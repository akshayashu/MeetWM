package com.akshay.meetwm.ui.chatActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException


class ChatViewModelFactory(val application: Application, val uid: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChatViewModel::class.java)){
            return ChatViewModel(application, uid) as T
        }
        throw IllegalArgumentException("ViewModel error")
    }
}