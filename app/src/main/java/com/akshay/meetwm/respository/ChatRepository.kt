package com.akshay.meetwm.respository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.akshay.meetwm.database.chatDatabase.ChatDao
import com.akshay.meetwm.database.contactDatabase.ContactDao
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.MessageData

class ChatRepository(private var chatDao: ChatDao, uid: String) {

    val allChatMessages : LiveData<List<ChatAndMessages>> = chatDao.getChat(uid)
    val allMessages : PagingSource<Int, MessageData> = chatDao.getMessages(uid)

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