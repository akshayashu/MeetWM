package com.akshay.meetwm.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshay.meetwm.R
import com.akshay.meetwm.model.MessageData
import com.akshay.meetwm.socket.SocketInstance
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.OkHttpClient
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class ChatActivity : AppCompatActivity() {

//    "http://192.168.0.4:9000/"
    private lateinit var mSocket : Socket
    private lateinit var username : String
    private lateinit var receiverUID : String
    private lateinit var senderUID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        username = intent.getStringExtra("name")!!
        receiverUID = intent.getStringExtra("receiverUID")!!
        senderUID = intent.getStringExtra("senderUID")!!

        val textview = findViewById<TextView>(R.id.showText)
        val editText = findViewById<TextView>(R.id.msgEditText)

        try {
            val socketInstance = application as SocketInstance
            mSocket = socketInstance.getSocketInstance()

//            mSocket.connect()

            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)

            mSocket.emit("join", senderUID)
//            Log.d("SOCKET", "CREATED ")
        }catch (e : Exception){
            Log.d("SOCKET EXCEPTION", e.localizedMessage)
        }


        sendBtn.setOnClickListener {
            val time = System.currentTimeMillis().toString()
            val id = senderUID + time
            val myMessage = MessageData(id,"sent", senderUID, "text_msg",
                "", "","",
                editText.text.toString(), "", time)
            Log.d("My message", Gson().toJson(myMessage))
            mSocket.emit("sendMessage", Gson().toJson(myMessage))
        }


    }

    private val onConnect =
        Emitter.Listener { Log.d("Tag-Socket", "Socket Connected!") }

    private val onConnectError = Emitter.Listener { Log.d("Tag-Socket", "Socket ERROR !")}
    private val onConnectTimeout = Emitter.Listener { runOnUiThread { Log.d("Tag-Socket", "Socket Timeout !") } }
    private val onDisconnect = Emitter.Listener { runOnUiThread {Log.d("Tag-Socket", "Socket Disconnected!") } }

    override fun onDestroy() {
        super.onDestroy()
//        mSocket.emit("disconnect", senderUID)
        Log.d("SOCKET", "Destroyed")
        mSocket.disconnect()
    }
}