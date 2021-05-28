package com.akshay.meetwm.ui.main

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.akshay.meetwm.database.contactDatabase.ContactDatabase
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.respository.ContactRepository
import com.akshay.meetwm.respository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo : ContactRepository
//    val allContacts : LiveData<List<Contact>>
    private val mainRepo : MainRepository
    val list = MutableLiveData<ArrayList<Contact>>()

    init {
        val dao = ContactDatabase.getDatabase(application).getContactDao()
        repo = ContactRepository(dao)
        mainRepo = MainRepository(application.contentResolver)
//        allContacts = repo.allContacts
    }

    fun getContact() = viewModelScope.launch(Dispatchers.IO) {
        list.value = mainRepo.loadContacts()
    }

    fun insertContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(contact)
    }
}