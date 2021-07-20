package com.akshay.meetwm.model

data class ReceivedMessage(
    var chat_uid: String,
    var my_uid: String,
    var message_id: String,
    var receivedTime: String
)
