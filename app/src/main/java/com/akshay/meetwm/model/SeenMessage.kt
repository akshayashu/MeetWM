package com.akshay.meetwm.model

data class SeenMessage(
    var chat_uid: String,
    var my_uid: String,
    var message_id: String,
    var seenTime: String
)
