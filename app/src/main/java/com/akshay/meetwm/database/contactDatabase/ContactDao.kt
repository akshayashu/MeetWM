package com.akshay.meetwm.database.contactDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chatModel: ChatModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageData)

    @Transaction
    @Query("Update MessageData Set received_timestamp = :receivedTime where id = :messageId")
    suspend fun setReceivedMessageTime(receivedTime : String, messageId: String)

    @Transaction
    @Query("Update MessageData Set read_timestamp = :seenTime where id = :messageId")
    suspend fun setSeenMessageTime(seenTime : String, messageId: String)

    @Transaction
    @Query("Select * from MessageData where chat_uid = :chat_uid and read_timestamp = :time")
    fun getUnseenMessageId(chat_uid: String, time: String) : List<MessageData>

    @Transaction
    @Query("Select * from contacts_table order by display_name ASC")
    fun getAllContact() : LiveData<List<Contact>>

    @Transaction
    @Query("Select * from ChatModel where uid = :uid")
    fun getAllChat(uid: String) : LiveData<List<ChatAndMessages>>

    @Delete
    suspend fun delete(contact: Contact)
}