package com.akshay.meetwm.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.app.ActivityCompat
import com.akshay.meetwm.R
import com.akshay.meetwm.ui.signInActivity.SignInActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    private var permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val requestCode = 1
    private lateinit var timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        timer = object: CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val text = "in " + (millisUntilFinished/1000).toString() + "sec"
            }

            override fun onFinish() {
                val intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        if(!isPermissionGranted()){
            askPermissions()
            permissionBtn.visibility = View.VISIBLE
        }else{
            timer.start()
        }

        permissionBtn.setOnClickListener {
            if(!isPermissionGranted()){
                askPermissions()
                permissionBtn.visibility = View.VISIBLE
            }else{
                timer.start()
            }
        }

//        Handler().postDelayed({
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//            finish()
//        },1000)
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