package com.akshay.meetwm.ui.otpVerify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.akshay.meetwm.R
import com.akshay.meetwm.ui.RegisterUser.RegisterUser
import com.akshay.meetwm.ui.contact.ContactActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otpverify.*
import java.util.concurrent.TimeUnit

class OTPVerify : AppCompatActivity() {

    private lateinit var phoneNo : String
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var verificationId : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private var canSend = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverify)

        val timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.visibility = View.VISIBLE
                timerText.text = "in " + (millisUntilFinished/1000).toString() + "sec"
            }

            override fun onFinish() {
                canSend = true
                timerText.visibility = View.GONE
            }
        }
        timer.start()

        backBtn.setOnClickListener {
            finish()
        }


        phoneNo = intent.getStringExtra("phone")!!
        Log.d("PHONE NUMBER", phoneNo)

        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT).show()
                Log.d("ERROR FIREBASE", p0.localizedMessage!!)
                p0.printStackTrace()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
                resendToken = p1
            }
        }

        val option = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNo)
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callback)
            .build()

        // verifying the entered number
        PhoneAuthProvider.verifyPhoneNumber(option)

        //resend OTP
        resendOtpBtn.setOnClickListener {
            if(canSend){
                timer.start()
                val optionResend = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNo)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callback)
                    .setForceResendingToken(resendToken)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(optionResend)
                Toast.makeText(this,"OTP Sent!", Toast.LENGTH_LONG).show()
            }else{
                return@setOnClickListener
            }
        }

        submitBtn.setOnClickListener {
            val code = pinView.value
            if (code != null) {
                if(code.length < 6){
                    Toast.makeText(this, "Enter complete OTP!", Toast.LENGTH_SHORT).show()
                }else {
                    verifyCode(code)
                }
            }else{
                Toast.makeText(this, "Enter the OTP!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signIn(credential)
    }

    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                //start New Activity
                val intent = Intent(this, RegisterUser::class.java)
                intent.putExtra("phone", phoneNo)
                startActivity(intent)
                Log.d("OTP", "OTP IS VERIFIED !")
            }else{
                Toast.makeText(this, "Wrong OTP!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}