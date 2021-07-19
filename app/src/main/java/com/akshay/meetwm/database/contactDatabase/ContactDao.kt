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

    @Delete
    suspend fun delete(contact: Contact)

    @Transaction
    @Query("Select * from contacts_table order by display_name ASC")
    fun getAllContact() : LiveData<List<Contact>>

    @Transaction
    @Query("Select * from ChatModel where uid = :uid")
    fun getAllChat(uid: String) : LiveData<List<ChatAndMessages>>
}