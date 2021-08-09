package com.akshay.meetwm.ui.chatListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.respository.ChatListRepository

class ChatListViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : ChatListRepository
    val allChat : LiveData<List<ChatAndMessages>>

    init {
        val dao = ContactDatabase.getDatabase(application).getChatDao()
        repo = ChatListRepository(dao)

        allChat = repo.allChat
    }
}