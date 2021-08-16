package com.akshay.meetwm.respository

import androidx.lifecycle.LiveData
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.database.contactDatabase.ContactDao

class ContactRepository(private val contactDao: ContactDao) {

    val allContacts : LiveData<List<Contact>> = contactDao.getAllContact()

    suspend fun insert(contact: Contact){
        contactDao.insert(contact)
    }

    suspend fun getContact(uid : String) : Contact{
        return contactDao.getContact(uid)
    }

    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }
}