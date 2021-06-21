package com.akshay.meetwm.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshay.meetwm.R
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class ChatActivity : AppCompatActivity() {

    data class MessageClass(
        var senderUID: String,
        var receiverUID : String,
        var message : String
    )

    private val URL = "http://192.168.0.4:9000/"
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

//        val parsed: URL = Url.parse(URL)
//        val source: URI = try {
//            parsed.toURI()
//        } catch (e: URISyntaxException) {
//            throw RuntimeException(e)
//        }
//        val id = Url.extractId(parsed)
//        val path = parsed.path
//
//        val app: SocketInstance = application as SocketInstance
//        mSocket = app.getMSocket()
        try {

            val option = IO.Options()
            val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
            option.callFactory = clientBuilder.build()

//            creating socket instance

            mSocket = IO.socket(URL, option)
            Log.d("SOCKET", "CREATED ")
        }catch (e : Exception){
            Log.d("SOCKET EXCEPTION", e.localizedMessage)
        }

        val textview = findViewById<TextView>(R.id.showText)

        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)

        mSocket.emit("join", senderUID)

        sendBtn.setOnClickListener {
            val myMessage = MessageClass(senderUID, receiverUID, textview.text.toString())
            Log.d("My message", Gson().toJson(myMessage))
            mSocket.emit("sendMessage", Gson().toJson(myMessage))
        }

        mSocket.on("message"){
            if (it != null) {
                val data = it[0]

                Log.d("SOCKET MESSAGE", data.toString())
                runOnUiThread {
                    textview.text = data.toString()
                    Toast.makeText(this, "$data received from Socket", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mSocket.connect()

//        //receive msg
//        mSocket.on("msg") {
//            if (it != null) {
//                val data = it[0] as String
//
//                Log.d("SOCKET MESSAGE", data.toString())
//                runOnUiThread {
//                    showText.text = data
//                    Toast.makeText(this, "$data received from Socket", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        //send msg
//        sendBtn.setOnClickListener {
//            val jsonObject = Gson().toJson(msgEditText.text.toString())
//            Log.d("SOCKET MESSAGE SENDING", msgEditText.text.toString())
//            mSocket.emit("msg", jsonObject)
//        }

    }

    private val onConnect =
        Emitter.Listener { Log.d("Tag-Socket", "Socket Connected!") }

    private val onConnectError = Emitter.Listener { Log.d("Tag-Socket", "Socket ERROR !")}
    private val onConnectTimeout = Emitter.Listener { runOnUiThread { Log.d("Tag-Socket", "Socket Timeout !") } }
    private val onDisconnect = Emitter.Listener { runOnUiThread {Log.d("Tag-Socket", "Socket Disconnected!") } }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.emit("disconnect", senderUID)
        Log.d("SOCKET", "Destroyed")
        mSocket.disconnect()
//        mSocket?.off("msg")
    }
}
//
//package com.akshay.meetwm.appInterface
//
//import android.app.Application
//import android.util.Log
//import com.github.nkzawa.socketio.client.IO
//import com.github.nkzawa.socketio.client.Socket
//import java.net.URISyntaxException
//
//class SocketInstance : Application() {
//
//    private var mSocket : com.github.nkzawa.socketio.client.Socket? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        try {
//            //creating socket instance
//            mSocket = com.github.nkzawa.socketio.client.IO.socket(URL)
//            Log.d("SOCKET", "CREATED")
//        }catch (e : URISyntaxException){
//            throw RuntimeException(e)
//        }
//    }
//
//    fun getMSocket() : com.github.nkzawa.socketio.client.Socket? {
//        return mSocket
//    }
//}
//
//private const val URL = "http://192.168.0.4:9000"
