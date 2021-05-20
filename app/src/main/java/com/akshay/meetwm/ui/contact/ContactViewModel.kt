package com.akshay.meetwm.ui.contact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.respository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application : Application) : AndroidViewModel(application) {

    private val repo : ContactRepository
    val allContacts : LiveData<List<Contact>>

    init {
        val dao = ContactDatabase.getDatabase(application).getContactDao()
        repo = ContactRepository(dao)
        allContacts = repo.allContacts
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(contact)
    }

    fun insertContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(contact)
    }
}