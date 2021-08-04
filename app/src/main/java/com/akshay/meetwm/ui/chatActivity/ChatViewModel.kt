package com.akshay.meetwm.ui.chatActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData
import com.akshay.meetwm.respository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application, uid: String) : AndroidViewModel(application) {

    private val repo : ChatRepository
    val allChatMessages : LiveData<List<ChatAndMessages>>
    val allMessages : Flow<PagingData<MessageData>>

    init {
        val dao = ContactDatabase.getDatabase(application).getChatDao()

        repo = ChatRepository(dao, uid)
        allChatMessages = repo.allChatMessages
        allMessages = Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            )
        ){
            dao.getMessages(uid)
        }.flow.cachedIn(viewModelScope)
    }

    fun insertChat(chatModel: ChatModel) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertChat(chatModel)
    }

    fun insertMessage(messageData: MessageData) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertMessage(messageData)
    }
}