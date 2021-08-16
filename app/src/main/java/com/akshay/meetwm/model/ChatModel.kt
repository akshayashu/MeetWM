package com.akshay.meetwm.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatModel(
    @PrimaryKey(autoGenerate = false) var uid: String,
    var number: String,
    var name: String,
    var global_name : String,
    var timeline_status : String,
    var status : String,
    var dp_url : String,
    var unseen_msg_count : String,
    var is_meet_user : Boolean,
)
