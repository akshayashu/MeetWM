package com.akshay.meetwm.ui.contact

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.meetwm.ContactsRVAdapter
import com.akshay.meetwm.IContactsRVAdapter
import com.akshay.meetwm.R
import com.akshay.meetwm.model.Contact
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity(), IContactsRVAdapter {

    lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        contactRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ContactsRVAdapter(this)
        contactRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ContactViewModel::class.java)

        viewModel.allContacts.observe(this, Observer { list ->
            list?.let {
                adapter.update(list)
            }

        })

    }

    override fun onItemClick(contact: Contact) {
        viewModel.deleteContact(contact)
        Toast.makeText(this, "Contact Deleted!", Toast.LENGTH_SHORT).show()
    }

    fun addData(view: View) {
        val name = nameEditText.text.toString().trim()
        val number = numberEditText.text.toString().trim()

        if(name.isNotEmpty() && number.isNotEmpty()){
            viewModel.insertContact(Contact(name, number, true, "sakjsa31323j"))
            Toast.makeText(this, "Contact Inserted!", Toast.LENGTH_SHORT).show()
        }

    }
}