package com.akshay.meetwm.ui.chatListFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainChatSharedViewModel : ViewModel() {
    val changeHeadline : LiveData<String> get() = _changedQuery
    private val _changedQuery = MutableLiveData<String>()

    fun changeQuery(position : String){
        Log.d("Got thee query", position)
        _changedQuery.value = position
    }
}