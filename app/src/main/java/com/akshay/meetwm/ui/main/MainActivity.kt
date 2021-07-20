package com.akshay.meetwm.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akshay.meetwm.R
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.model.MessageData
import com.akshay.meetwm.model.ReceivedMessage
import com.akshay.meetwm.model.SeenMessage
import com.akshay.meetwm.socket.SocketInstance
import com.akshay.meetwm.ui.SharedPref
import com.akshay.meetwm.ui.contact.ContactActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS)
    private val requestCode = 1
    lateinit var pref : SharedPref

    private lateinit var mSocket : Socket

    private lateinit var viewModel: MainViewModel
    private var list = ArrayList<Contact>()

    override fun onStart() {
        super.onStart()
        if(!isPermissionGranted()){
            askPermissions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set up view
        viewModel = ViewModelProvider(this,
               ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(MainViewModel::class.java)


        viewModel.getContact()
        viewModel.list.observe(this, {
            list = it
            updateList()
        })

        val intent = Intent("my-event")
        configureTabLayout()

        showPref()

        Firebase.initialize(this)

        fab.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        try {
            val socketInstance = application as SocketInstance
            mSocket = socketInstance.getSocketInstance()

            mSocket.connect()

            mSocket.on(Socket.EVENT_CONNECT, onConnect)
//            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
//            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
            val myUID = SharedPref(applicationContext).getUserID()
            mSocket.emit("join", myUID)
            Log.d("SOCKET", "CREATED in Main")
        }catch (e : Exception){
            Log.d("SOCKET EXCEPTION", e.localizedMessage)
        }

        // someone has received my message
        mSocket.on("received"){
            // update msg receive time
            if(it != null) {
                val data = it[0]

                val receivedData = Gson().fromJson(data.toString(), ReceivedMessage::class.java)
                viewModel.updateReceivedMessageTime(receivedData.receivedTime, receivedData.message_id)
                Log.d("MESSAGE", "Received $receivedData")
            }
        }

        // someone has seen my message
        mSocket.on("seen"){
            // update msg receive time
            if(it != null) {
                val data = it[0]
                val seenData = Gson().fromJson(data.toString(), SeenMessage::class.java)
                Log.d("MESSAGE", "Seen $seenData")

                val list = viewModel.getUnseenMessageId(seenData.chat_uid)
                Log.d("SIZE OF UNSEEN MSGS", list.size.toString());
                for(ele in list){
                    viewModel.updateSeenMessageTime(seenData.seenTime, ele.id)
                }
            }
        }

        //received message
        mSocket.on("message"){
            if (it != null) {
                val data = it[0]
                val time = System.currentTimeMillis().toString()

                Log.d("SOCKET MESSAGE", data.toString())
                val messageData = Gson().fromJson(data.toString(), MessageData::class.java)
                viewModel.insertChat(messageData)

                // emitting the other person that I've received his/her message
                Log.d("MY IDDDDD", pref.getUserID()!!)
                val receivedMessage = ReceivedMessage(messageData.chat_uid, pref.getUserID()!!, messageData.id,time)
                mSocket.emit("receivedMessage", Gson().toJson(receivedMessage))

                runOnUiThread {
                    Toast.makeText(this, "$data received from Socket", Toast.LENGTH_SHORT).show()
                }
                intent.putExtra("data", "newMessage,${messageData.chat_uid},${messageData.id}");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    private fun updateList() {
        for(item in list) {
            viewModel.insertContact(item)
            Log.d("Contacts", "${item.display_name} , ${item.number}, ${item.id}")
        }
    }

    private fun showPref() {
        pref = SharedPref(this)
        Log.d("Username", pref.getUserName().toString())
        Log.d("UserPhoto", pref.getUserImageBitmap().toString())
        Log.d("UserStatus", pref.getUserStatus().toString())
        Log.d("UserPhone", pref.getUserNumber().toString())
        Log.d("UserID", pref.getUserID().toString())
    }

    private fun configureTabLayout() {

        val adapter = PageAdapter(supportFragmentManager, lifecycle)
        pager.adapter = adapter

        TabLayoutMediator(tabLayout, pager){tab, position ->
            when(position){
                0 -> {
                    tab.text = "Chat"
                }
                1 -> {
                    tab.text = "Status"
                }
                2 -> {
                    tab.text = "History"
                }
            }
        }.attach()
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

    private val onConnect = Emitter.Listener { Log.d("Tag-Socket", "Socket Connected!") }
    private val onConnectError = Emitter.Listener { Log.d("Tag-Socket", "Socket ERROR !")}
    private val onConnectTimeout = Emitter.Listener { runOnUiThread { Log.d("Tag-Socket", "Socket Timeout !") } }
    private val onDisconnect = Emitter.Listener { runOnUiThread {Log.d("Tag-Socket", "Socket Disconnected!") } }

    override fun onDestroy() {
        super.onDestroy()
        val pref = SharedPref(this)
        mSocket.emit("disconnect", pref.getUserID())
        Log.d("SOCKET", "Destroyed")
    }
}