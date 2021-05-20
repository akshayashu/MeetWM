package com.akshay.meetwm.database.contactDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.akshay.meetwm.model.Contact

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("Select * from contacts_table order by name ASC")
    fun getAllContact() : LiveData<List<Contact>>
}