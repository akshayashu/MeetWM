package com.akshay.meetwm.ui.main

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData
import com.akshay.meetwm.respository.ChatListRepository
import com.akshay.meetwm.respository.ChatRepository
import com.akshay.meetwm.respository.ContactRepository
import com.akshay.meetwm.respository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : ContactRepository
    private val chatRepo : ChatRepository

    private val mainRepo : MainRepository
    val list = MutableLiveData<ArrayList<Contact>>()
    val callerDetails = MutableLiveData<Contact>()

    init {
        val dao = ContactDatabase.getDatabase(application).getContactDao()
        val chatDao = ContactDatabase.getDatabase(application).getChatDao()

        repo = ContactRepository(dao)
        mainRepo = MainRepository(application.contentResolver)
        chatRepo = ChatRepository(chatDao, "nothing")

    }

    //for contact sections
    fun getContact() = viewModelScope.launch(Dispatchers.IO) {
        list.postValue(mainRepo.loadContacts())
    }

    fun insertContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(contact)
    }

    fun getCallingContact(uid : String) = viewModelScope.launch(Dispatchers.IO){
        callerDetails.postValue(repo.getContact(uid))
    }

    //for chat and message sections
    fun insertMessage(messageData: MessageData) = viewModelScope.launch(Dispatchers.IO) {
        chatRepo.insertMessage(messageData)
    }

    fun updateReceivedMessageTime(receivedTime : String, messageId: String) = viewModelScope.launch(Dispatchers.IO){
        chatRepo.updateReceiveTime(receivedTime, messageId)
    }

    fun updateSeenMessageTime(seenTime : String, messageId: String) = viewModelScope.launch(Dispatchers.IO){
        chatRepo.updateSeenTime(seenTime, messageId)
    }

    fun getUnseenMessageId(chat_uid: String) : List<MessageData>{
        return chatRepo.getUnseenMessageID(chat_uid)
    }

}