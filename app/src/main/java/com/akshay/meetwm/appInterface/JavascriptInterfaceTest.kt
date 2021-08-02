package com.akshay.meetwm.appInterface

import android.util.Log
import android.webkit.JavascriptInterface
import com.akshay.meetwm.ui.callActivity.CallTestActivity

class JavascriptInterfaceTest(val callTestActivity: CallTestActivity) {

    @JavascriptInterface
    public fun onPeerConnected(){
        callTestActivity.onPeerConnect()
        Log.d("Called Peer", "YESS")
    }

}