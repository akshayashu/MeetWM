package com.akshay.meetwm.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.akshay.meetwm.R
import com.akshay.meetwm.ui.SharedPref
import com.akshay.meetwm.ui.callActivity.CallActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureTabLayout()
        if(!isPermissionGranted()){
            askPermissions()
        }

        val pref = SharedPref(this)

        Log.d("Username", pref.getUserName().toString())
        Log.d("UserPhoto", pref.getUserImageBitmap().toString())
        Log.d("UserStatus", pref.getUserStatus().toString())
        Log.d("UserPhone", pref.getUserNumber().toString())
        Log.d("UserID", pref.getUserID().toString())

        Firebase.initialize(this)
        
    }

    private fun configureTabLayout() {

        val adapter = PageAdapter(supportFragmentManager, lifecycle)
        pager.adapter = adapter

        TabLayoutMediator(tabLayout, pager){tab, position ->
            when(position){
                0 -> {
                    tab.text = "Chat"
                }
                1 -> {
                    tab.text = "Status"
                }
                2 -> {
                    tab.text = "History"
                }
            }
        }.attach()
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if(ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }
}