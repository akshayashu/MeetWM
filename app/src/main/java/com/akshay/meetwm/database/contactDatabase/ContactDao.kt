package com.akshay.meetwm.database.contactDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.ChatModel
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData
import retrofit2.http.GET

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Transaction
    @Query("Select * from contacts_table order by display_name ASC")
    fun getAllContact() : LiveData<List<Contact>>

    @Delete
    suspend fun delete(contact: Contact)

    @Query("Select * from contacts_table where uid = :curUID")
    suspend fun getContact(curUID : String) : Contact
}