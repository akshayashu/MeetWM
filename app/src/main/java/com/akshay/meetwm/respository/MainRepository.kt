package com.akshay.meetwm.respository

import android.content.ContentResolver
import android.provider.ContactsContract
import com.akshay.meetwm.model.Contact

class MainRepository(private val contentResolver: ContentResolver) {

    val uri = ContactsContract.Contacts.CONTENT_URI
    val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC"    //sort by ascending odr
    val cursor = contentResolver.query(
        uri, null, null, null, sort
    )

    val list = ArrayList<Contact>()

    suspend fun loadContacts() : ArrayList<Contact>{

        if(cursor?.count!! > 0){
            while (cursor.moveToNext()){

                val id = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts._ID
                ))

                val name = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME
                ))

                val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?"

                val phoneCursor = contentResolver.query(
                    uriPhone, null, selection, arrayOf(id), null
                )

                if(phoneCursor?.moveToNext()!!){

                    val phone = phoneCursor.let {
                        phoneCursor.getString(
                            it.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            ))
                    }

                    list.add(Contact(
                        "name", name,
                        phone, "Hello everyone",
                        "", "0",
                        false, "",
                        id
                    ))
                    phoneCursor.close()
                }

            }
            cursor.close()
        }
        return list
    }
}