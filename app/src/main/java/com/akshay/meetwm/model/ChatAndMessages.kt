package com.akshay.meetwm.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

data class ChatAndMessages(
    @Embedded
    var chat : ChatModel,
    @Relation(
        parentColumn = "uid",
        entityColumn = "sender_uid"
    )
    var messages : List<MessageData>
)
