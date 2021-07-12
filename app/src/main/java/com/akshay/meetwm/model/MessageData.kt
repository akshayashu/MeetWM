package com.akshay.meetwm.model

data class MessageData(
    var id: String,
    var status: String,
    var senderUID: String,
    var receiverUID : String,
    var data_type: String,
    var data_url: String,
    var data_name: String,
    var replied_data_id: String,
    var data : String,
    var received_timestamp: String,
    var send_timestamp : String,
)