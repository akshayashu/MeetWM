package com.akshay.meetwm.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatModel(
    @PrimaryKey(autoGenerate = false) var uid: String,
    var number: String,
    var staus: String,
    var name: String,
    var new_messages: Int,
)
