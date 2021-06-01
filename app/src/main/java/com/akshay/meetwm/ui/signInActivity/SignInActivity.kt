package com.akshay.meetwm.ui.signInActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.akshay.meetwm.R
import com.akshay.meetwm.ui.main.MainActivity
import com.akshay.meetwm.ui.otpVerify.OTPVerify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private var user : FirebaseUser? = null
    private var permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val requestCode = 1


    override fun onStart() {
        super.onStart()
        if(!isPermissionGranted()){
            askPermissions()
        }
        if(user != null){
            Log.d("USERRRRR", user.toString())
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        user = FirebaseAuth.getInstance().currentUser

        val code = countryCodePicker.selectedCountryCode

        verifyBtn.setOnClickListener {
            val phoneNo = phoneNumber.text.toString()
            if(!isPermissionGranted()){
                askPermissions()
            }else {
                if (phoneNo.length != 10){
                    Toast.makeText(this, "Check your number again", Toast.LENGTH_LONG).show()
                }else{
                    val intent = Intent(this, OTPVerify::class.java)
                    intent.putExtra("phone", "+$code$phoneNo")
                    startActivity(intent)
                }
            }
        }
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