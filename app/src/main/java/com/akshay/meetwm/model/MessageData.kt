package com.akshay.meetwm.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class MessageData(
    @PrimaryKey(autoGenerate = false) var id: String,
    var status: String,
    var sender_uid : String,
    var data_type: String,
    var data_url: String,
    var data_name: String,
    var replied_data_id: String,
    var data : String,
    var received_timestamp: String,
    var send_timestamp : String,
)