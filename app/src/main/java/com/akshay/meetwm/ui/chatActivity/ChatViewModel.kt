package com.akshay.meetwm.ui.chatActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.akshay.meetwm.database.contactDatabase.ContactDao
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.model.*
import com.akshay.meetwm.respository.ChatRepository
import com.akshay.meetwm.respository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatViewModel(application: Application, uid: String) : AndroidViewModel(application) {

    private val repo : ChatRepository
    private val contactRepo : ContactRepository

    var currentContact = MutableLiveData<Contact>()
    val allChatMessages : LiveData<List<ChatAndMessages>>
    val allMessages : Flow<PagingData<MessageData>>

    init {
        val dao = ContactDatabase.getDatabase(application).getChatDao()
        val contactDao = ContactDatabase.getDatabase(application).getContactDao()

        contactRepo = ContactRepository(contactDao)
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

    fun getContact(uid : String) = viewModelScope.launch(Dispatchers.IO){
        currentContact.postValue(contactRepo.getContact(uid))
    }
}