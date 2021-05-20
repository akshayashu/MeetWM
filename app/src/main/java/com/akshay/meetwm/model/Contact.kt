package com.akshay.meetwm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts_table")
class Contact(
    var name : String,
    var number : String,
    var is_meet_user : Boolean,
    var call_id : String
    ){
    @PrimaryKey(autoGenerate = true) var id = 0
}