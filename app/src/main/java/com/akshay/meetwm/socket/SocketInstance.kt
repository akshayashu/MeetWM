package com.akshay.meetwm.socket

import android.app.Application
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class SocketInstance : Application() {

    private val URL = "https://meet-wm.herokuapp.com/"
    private lateinit var mSocket : Socket

    override fun onCreate() {
        super.onCreate()
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
    }

    fun getSocketInstance(): Socket {
        return mSocket
    }
}