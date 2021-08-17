package com.akshay.meetwm.database.chatDatabase

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.MessageData


@Dao
interface ChatDao {

    @Transaction
    @Query("Select * from MessageData where chat_uid = :uid order by send_timestamp ASC")
    fun getMessages(uid : String) : PagingSource<Int, MessageData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chatModel: ChatModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageData)

    @Transaction
    @Query("Update MessageData Set received_timestamp = :receivedTime, status = :status where id = :messageId")
    suspend fun setReceivedMessageTime(receivedTime : String, messageId: String, status: String)

    @Transaction
    @Query("Update MessageData Set read_timestamp = :seenTime, status = :status where id = :messageId")
    suspend fun setSeenMessageTime(seenTime : String, messageId: String, status: String)

    @Transaction
    @Query("Select * from MessageData where chat_uid = :chat_uid and read_timestamp = :time")
    fun getUnseenMessageId(chat_uid: String, time: String) : List<MessageData>

    @Transaction
    @Query("Select * from ChatModel where uid = :uid")
    fun getChat(uid: String) : LiveData<List<ChatAndMessages>>

    @Transaction
    @Query("Select * from ChatModel where uid in (Select chat_uid from MessageData group by chat_uid)")
    fun getAllChatList() : LiveData<List<ChatAndMessages>>

    @Transaction
    @Query("Select unseen_msg_count from ChatModel where uid = :chat_uid")
    fun getUnseenMessageCount(chat_uid: String) : String

    @Transaction
    @Query("Update ChatModel set unseen_msg_count = :value where uid = :chat_uid")
    fun updateUnseenMessageCount(chat_uid: String, value : String)

}