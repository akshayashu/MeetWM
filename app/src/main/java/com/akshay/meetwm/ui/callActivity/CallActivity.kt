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
import com.akshay.meetwm.appInterface.JavascriptInterface
import com.akshay.meetwm.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_call.*
import java.util.*

class CallActivity : AppCompatActivity() {

    var userName = ""
    var friendUserName = ""

    var isPeerConnected = false

    private val db = FirebaseFirestore.getInstance()

    var firebaseRef = Firebase.database.getReference("users")
    var isAudio = true
    var isVideo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        userName = intent.getStringExtra("username")!!
        friendUserName = intent.getStringExtra("friendUserName")!!
        Log.d("USERNAME", userName)

        callBtn.setOnClickListener {
            friendUserName = friendNameEdit.text.toString().trim()
            sendCallRequest()
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

        setupWebView()
    }

    private fun sendCallRequest() {
        if(!isPeerConnected){
            Toast.makeText(this, "You're not connected! Check your internet.", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseRef.child(friendUserName).child("incoming").setValue(userName)
        firebaseRef.child(friendUserName).child("isAvailable").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value.toString() == "true"){
                    listenForCall()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun listenForCall() {
        firebaseRef.child(friendUserName).child("connId").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == null)
                    return

                switchToControl()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value.toString()}\")")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setupWebView() {

        webView.webChromeClient = object : WebChromeClient(){
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()

    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueID = ""

    private fun initializePeer() {

        uniqueID = getUID()

        callJavascriptFunction("javascript:init(\"${uniqueID}\")")

        firebaseRef.child(userName).child("incoming").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                onCallRequest(snapshot.value as? String)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun onCallRequest(caller: String?) {

        if(caller == null) return

        callLayout.visibility = View.VISIBLE
        incomingCalltext.text = "$caller is calling ..."

        acceptCall.setOnClickListener {
            firebaseRef.child(userName).child("connId").setValue(uniqueID)
            firebaseRef.child(userName).child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControl()
        }

        rejectCall.setOnClickListener {
            firebaseRef.child(userName).child("connId").setValue(null)
//            firebaseRef.child(userName).child("isAvailable").setValue(false)
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControl() {
        inputLayout.visibility = View.GONE
        callControlLayout.visibility = View.VISIBLE
    }

    private fun getUID() : String{
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString : String){
        webView.post {
            webView.evaluateJavascript(functionString, null)
        }
    }

    fun onPeerConnected() {
        isPeerConnected = true
        Log.d("TOGGLEEE", isPeerConnected.toString())
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(userName).setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }
}