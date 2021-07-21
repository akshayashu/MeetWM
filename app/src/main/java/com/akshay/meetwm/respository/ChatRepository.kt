package com.akshay.meetwm.respository

import androidx.lifecycle.LiveData
import com.akshay.meetwm.database.contactDatabase.ContactDao
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData

class ChatRepository(private var chatDao : ContactDao, private var uid: String) {

    val allChatMessages : LiveData<List<ChatAndMessages>> = chatDao.getAllChat(uid)

    suspend fun insertChat(chatModel: ChatModel){
        chatDao.insertChat(chatModel)
    }

    suspend fun insertMessage(messageData: MessageData){
        chatDao.insertMessage(messageData);
    }

    suspend fun updateReceiveTime(receivedTime : String, messageId: String){
        chatDao.setReceivedMessageTime(receivedTime, messageId, "received");
    }

    suspend fun updateSeenTime(seenTime : String, messageId: String){
        chatDao.setSeenMessageTime(seenTime, messageId, "seen");
    }

    fun getUnseenMessageID(chat_uid: String) : List<MessageData>{
        return chatDao.getUnseenMessageId(chat_uid, "not_yet");
    }

}