package com.akshay.meetwm.ui.contact

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akshay.meetwm.ContactsRVAdapter
import com.akshay.meetwm.IContactsRVAdapter
import com.akshay.meetwm.R
import com.akshay.meetwm.model.Contact
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity(), IContactsRVAdapter {

    private var permissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val requestCode = 1

    lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        contactRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ContactsRVAdapter(this)
        contactRecyclerView.adapter = adapter

        if(!isPermissionGranted()){
            askPermissions()
        }

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
//        val name = nameEditText.text.toString().trim()
//        val number = numberEditText.text.toString().trim()
//
//        if(name.isNotEmpty() && number.isNotEmpty()){
//            viewModel.insertContact(Contact(name, number, "true", "sakjsa31323j", "", "0", false,"121","999090"))
//            Toast.makeText(this, "Contact Inserted!", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if(ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }
}