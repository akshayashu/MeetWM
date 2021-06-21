package com.akshay.meetwm.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.akshay.meetwm.R
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
//import com.github.nkzawa.emitter.Emitter
//import com.github.nkzawa.socketio.client.IO
//import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

private const val URL = "http://192.168.0.4:9000/"
//ws://192.168.0.4:3000/
class ChatActivity : AppCompatActivity() {

    private lateinit var mSocket : Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//
//        val app: SocketInstance = application as SocketInstance
//        mSocket = app.getMSocket()
        try {
            val option = IO.Options()
//            option.reconnection = true
//            option.forceNew = true
            option.transports = arrayOf(WebSocket.NAME, Polling.NAME)
            //creating socket instance
            mSocket = IO.socket(URL, option)
            Log.d("SOCKET", "CREATED ")
        }catch (e : Exception){
//            throw RuntimeException(e)
            Log.d("SOCKET EXCEPTION", e.localizedMessage)
        }


        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)

        mSocket.connect()
        
//
//        if (mSocket.connected()) {
//            showText.text = "Connected"
//            Log.d("SOCKET", "CONNECTED")
//            Toast.makeText(this, "Socket is connected", Toast.LENGTH_SHORT).show()
//        }
//
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
//
//        //Disconnect
//        mSocket.on(Socket.EVENT_DISCONNECT) {
//            runOnUiThread {
//                Log.d("SOCKET", "DISCONNECTED")
//                Toast.makeText(this, "Socket Disconnected", Toast.LENGTH_SHORT).show()
//            }
//            //Reconnect
//            mSocket.on(Socket.EVENT_RECONNECT) {
//                mSocket.connect()
//                runOnUiThread {
//                    Log.d("SOCKET", "RECONNECTED")
//                    Toast.makeText(this, "Socket Reconnected", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }

    private val onConnect =
        Emitter.Listener { Log.d("Tag-Socket", "Socket Connected!") }

    private val onConnectError = Emitter.Listener { Log.d("Tag-Socket", "Socket ERROR !")}
    private val onConnectTimeout = Emitter.Listener { runOnUiThread { Log.d("Tag-Socket", "Socket Timeout !") } }
    private val onDisconnect = Emitter.Listener { runOnUiThread {Log.d("Tag-Socket", "Socket Disconnected!") } }

    override fun onDestroy() {
        super.onDestroy()
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
