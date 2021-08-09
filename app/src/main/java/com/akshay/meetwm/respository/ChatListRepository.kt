package com.akshay.meetwm.respository

import androidx.lifecycle.LiveData
import com.akshay.meetwm.database.chatDatabase.ChatDao
import com.akshay.meetwm.model.ChatAndMessages

class ChatListRepository(private var chatDao: ChatDao) {

    var allChat : LiveData<List<ChatAndMessages>> = chatDao.getAllChatList()
}