package com.akshay.meetwm.appInterface

import android.webkit.JavascriptInterface
import com.akshay.meetwm.ui.callActivity.CallActivity

class JavascriptInterface(val callActivity: CallActivity) {

    @JavascriptInterface
    public fun onPeerConnected(){
        callActivity.onPeerConnected()
    }

}