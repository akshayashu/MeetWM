package com.akshay.meetwm.respository

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.UserContact
import com.akshay.meetwm.retrofit.RetrofitClient
import java.lang.Exception

class MainRepository(private val contentResolver: ContentResolver) {

    val uri = ContactsContract.Contacts.CONTENT_URI
    val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC"    //sort by ascending odr
    val cursor = contentResolver.query(
        uri, null, null, null, sort
    )

    private var list = ArrayList<Contact>()
    private var map = HashMap<String, Contact>()
    val numberList = ArrayList<String>()

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

                    map[phone] = Contact(
                        "", name,
                        phone, "",
                        "", "0",
                        true, "",
                        id
                    )
//                    list.add(Contact(
//                        "name", name,
//                        phone, "Hello everyone",
//                        "", "0",
//                        false, "",
//                        id
//                    ))
                    numberList.add(phone)
                    phoneCursor.close()
                }

            }
            cursor.close()
        }

        try {
            var response = RetrofitClient.apiInterface.getRegisteredUser(numberList)
            if(response.isSuccessful){
                setList(response.body()!!)
                Log.d("RESPONSE", response.body().toString())
            }
        }catch(e : Exception){
            Log.d("MAIN REPO EXCEPTION", e.localizedMessage.toString())
        }

        return list
    }

    fun setList(ll: ArrayList<UserContact>){
        for(cont in ll){
            val ele = map[cont.number]!!
            list.add(Contact(
                cont.user_name,
                ele.display_name,
                cont.number,
                cont.status,
                cont.photo_url,
                ele.unseen_msg_count,
                true,
                cont._id, ele.id)
            )
        }
    }
}