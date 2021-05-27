package com.akshay.meetwm.ui

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context : Context) {

    private val preferenceFile = "MySharedPreferences"
    private val userName = "name"
    private val userNumber = "number"
    private val userImageBitmap = "bitmap"
    private val userStatus = "offline"
    private val userUnseenMsg = "0"

    private val preferences : SharedPreferences = context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE)
    private val editor : SharedPreferences.Editor = preferences.edit()

    fun getUserName() : String?{
        return preferences.getString(userName, "name")
    }

    fun setUserName(name : String){
        editor.putString(userName, name)
        editor.apply()
    }

    fun getUserNumber() : String?{
        return preferences.getString(userNumber, "name")
    }

    fun setUserNumber(name : String){
        editor.putString(userNumber, name)
        editor.apply()
    }
    fun getUserImageBitmap() : String?{
        return preferences.getString(userImageBitmap, "name")
    }

    fun setUserImageBitmap(name : String){
        editor.putString(userImageBitmap, name)
        editor.apply()
    }
    fun getUserStatus() : String?{
        return preferences.getString(userStatus, "name")
    }

    fun setUserStatus(name : String){
        editor.putString(userStatus, name)
        editor.apply()
    }

    fun getUserUnseenMsg(): String? {
        return preferences.getString(userUnseenMsg, ".")
    }

    fun setUserUnseenMsg(name : String){
        editor.putString(userUnseenMsg, name)
        editor.apply()
    }

}