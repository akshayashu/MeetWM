package com.akshay.meetwm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts_table")
class Contact(
    var global_name : String,
    var display_name : String,
    var number : String,
    var status : String,
    var dp_bitmap : String,
    var unseen_msg_count : String,
    var is_meet_user : Boolean,
    var uid : String,
    @PrimaryKey var id : String
    ){
}