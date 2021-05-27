package com.akshay.meetwm.ui.signInActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.akshay.meetwm.R
import com.akshay.meetwm.ui.contact.ContactActivity
import com.akshay.meetwm.ui.otpVerify.OTPVerify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private var user : FirebaseUser? = null

    override fun onStart() {
        super.onStart()
        if(user != null){
            startActivity(Intent(this, ContactActivity::class.java))
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