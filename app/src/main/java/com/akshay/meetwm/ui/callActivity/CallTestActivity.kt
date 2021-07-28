package com.akshay.meetwm.ui.callActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.akshay.meetwm.R
import com.akshay.meetwm.appInterface.JavascriptInterfaceTest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_call_test.*
import java.util.*

class CallTestActivity : AppCompatActivity() {

    var isPeerConnected = true
    var uniqueID = "1"
    var canCall = false

    var userName = ""
    var friendUserName = ""
    var callType = ""

    var firebaseRef = Firebase.database.getReference("users")
    var isAudio = true
    var isVideo = true

    override fun onStart() {
        setWebView()
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_test)

        userName = intent.getStringExtra("username")!!
        friendUserName = intent.getStringExtra("friendUserName")!!
        callType = intent.getStringExtra("callType")!!
        Log.d("USERNAME", userName)

        if(callType == "outgoing"){ // come here because I want to call somebody
            sendCallRequest()
            callControlLayout1.visibility = View.VISIBLE
        }else{  // come here because someone has called me

        }

        callBtn.setOnClickListener {
//            friendUserName = friendNameEdit1.text.toString().trim()
            Log.d("Fiend username", friendUserName);

        }

        audioToggleBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            audioToggleBtn.setImageResource(
                if(isAudio)
                    R.drawable.ic_baseline_mic_24
                else
                    R.drawable.ic_baseline_mic_off_24
            )
        }
        videoToggleBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            videoToggleBtn.setImageResource(
                if(isVideo)
                    R.drawable.ic_baseline_videocam_24
                else
                    R.drawable.ic_baseline_videocam_off_24
            )
        }


        // waiting for friend's response on my call
        firebaseRef.child(friendUserName)
            .child("callStatus").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value.toString() == "accepted"){
                        listenCall()
                        callLayout.visibility = View.GONE
                        inputLayout1.visibility = View.GONE
                        callControlLayout1.visibility = View.VISIBLE
                    }else if (snapshot.value.toString() == "rejected"){
                        Toast.makeText(this@CallTestActivity, "He/She is busy", Toast.LENGTH_SHORT).show()
                        firebaseRef.child(userName).child("isAvailable").setValue(true)
                        finish()
                    } else if (snapshot.value.toString() == "ended"){
                        Toast.makeText(this@CallTestActivity, "Call Ended", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        // listen for incoming calls
        firebaseRef.child(userName)
            .child("incoming").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value.toString().trim() == null){
                        return
                    }
                    respondToCall(snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun respondToCall(callerId: String) {

        callLayout.visibility = View.VISIBLE
        incomingCalltext.text = "$callerId is calling..."

        acceptCall.setOnClickListener {
            firebaseRef.child(userName).child("connId").setValue(uniqueID);
            firebaseRef.child(userName).child("isAvailable").setValue(false)
            firebaseRef.child(userName).child("callStatus").setValue("accepted")

            callLayout.visibility = View.GONE
            inputLayout1.visibility = View.GONE
            callControlLayout1.visibility = View.VISIBLE
        }

        rejectCall.setOnClickListener {
            firebaseRef.child(userName).child("callStatus").setValue("rejected")
            finish()
        }
    }

    private fun listenCall() {
        firebaseRef.child(friendUserName)
            .child("connId").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value == null)
                        return

                    callJavascriptFunction("javascript:startCall(\"${snapshot.value.toString()}\")")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendCallRequest() {
        if(!isPeerConnected){
            Toast.makeText(this, "You're not connected", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseRef.child(friendUserName)
            .child("isAvailable")
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value.toString() == "true"){
                    setCanCallUser(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        if(canCall){
            firebaseRef.child(friendUserName).child("incoming").setValue(userName)
            firebaseRef.child(friendUserName).child("callStatus").setValue("waiting")

            firebaseRef.child(userName).child("isAvailable").setValue(false)
        }


    }

    private fun setCanCallUser(b : Boolean){
        canCall = b
    }

    private fun setWebView(){
        webView.webChromeClient = object : WebChromeClient(){
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavascriptInterfaceTest(this), "Android")

        loadWebView()
    }

    private fun loadWebView() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    private fun initializePeer() {
        uniqueID = getUID()
        callJavascriptFunction("javascript:init(\"${uniqueID}\")")
        webView.visibility = View.VISIBLE
    }

    private fun callJavascriptFunction(functionString : String){
        webView.post {
            webView.evaluateJavascript(functionString, null)
        }
    }
    private fun getUID() : String{
        return UUID.randomUUID().toString()
    }

    fun onPeerConnect() {
        isPeerConnected = true
        Log.d("Called Peer", "YES at test")
        Log.d("TOGGLEEE", isPeerConnected.toString())
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(userName).child("incoming").setValue("")
        firebaseRef.child(userName).child("callStatus").setValue("")
        firebaseRef.child(userName).child("isAvailable").setValue(true)
        firebaseRef.child(userName).child("connId").setValue("")
        webView.loadUrl("about:blank")
        super.onDestroy()
    }
}