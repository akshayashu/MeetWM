package com.akshay.meetwm.ui.chatActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData
import com.akshay.meetwm.respository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(application: Application, uid: String) : AndroidViewModel(application) {

    private val repo : ChatRepository
    val allChatMessages : LiveData<List<ChatAndMessages>>

    init {
        val dao = ContactDatabase.getDatabase(application).getContactDao()

        repo = ChatRepository(dao, uid)
        allChatMessages = repo.allChatMessages
    }

    fun insertChat(chatModel: ChatModel) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertChat(chatModel)
    }

    fun insertMessage(messageData: MessageData) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertMessage(messageData)
    }
}