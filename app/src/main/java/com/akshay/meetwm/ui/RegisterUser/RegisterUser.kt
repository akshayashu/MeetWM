package com.akshay.meetwm.ui.RegisterUser

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.akshay.meetwm.R
import com.akshay.meetwm.model.UserContact
import com.akshay.meetwm.ui.contact.ContactActivity
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_register_user.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegisterUser : AppCompatActivity() {

    lateinit var viewModel: RegistrationViewModel
    private val IMAGE_REQUEST_ID = 1
    private  var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        val phoneNumber = "+919871736205"
        //intent.getStringExtra("phone")!!

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RegistrationViewModel::class.java)

        viewModel.response?.observe(this, {
            Log.d("ViewModel - ", "RECEIVED $it")
            if(it == "ERROR" || it == null ){
                progressBar.visibility = View.GONE
                Toast.makeText(this, "SOME ERROR OCCURED!", Toast.LENGTH_LONG).show()
            }else{
                progressBar.visibility = View.GONE
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        uploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(Intent.createChooser(intent, "Choose Image"),
            IMAGE_REQUEST_ID)
        }

        continueBtn.setOnClickListener {

            val userName = userNameEditText.text.toString().trim()
            val status = statusEditText.text.toString().trim()
            if(userName.isNotEmpty()){
                progressBar.visibility = View.VISIBLE
                val userContact = UserContact("", userName, phoneNumber, status, imageUrl)
                viewModel.register(userContact)
            }else{
                userNameEditText.error = "Fill this field"
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK){

            val inputStream = data?.data?.let { contentResolver.openInputStream(it) }
            val image = BitmapFactory.decodeStream(inputStream)

            if(image != null) {
                profileImage.setImageBitmap(image)
                sendImage(image)
            }

        }
    }

    private fun sendImage(image: Bitmap?) {
        val opStream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 75, opStream)

        val base64String = Base64.encodeToString(opStream.toByteArray(), Base64.DEFAULT)
        imageUrl = base64String

//        Log.d("IMAGE BIT CODE", imageUrl)
//        val jsonObject = JSONObject()
//        jsonObject.put()
    }
}