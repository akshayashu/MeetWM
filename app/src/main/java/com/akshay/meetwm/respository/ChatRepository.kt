package com.akshay.meetwm.respository

import androidx.lifecycle.LiveData
import com.akshay.meetwm.database.contactDatabase.ContactDao
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData

class ChatRepository(private var chatDao : ContactDao) {

    val allChatMessages : LiveData<List<ChatAndMessages>> = chatDao.getAllChat("60b558a04d110e0015eb301b")

    suspend fun insertChat(chatModel: ChatModel){
        chatDao.insertChat(chatModel)
    }

    suspend fun insertMessage(messageData: MessageData){
        chatDao.insertMessage(messageData);
    }

}