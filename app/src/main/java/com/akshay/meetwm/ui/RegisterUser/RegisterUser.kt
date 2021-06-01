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
import com.akshay.meetwm.ui.SharedPref
import com.akshay.meetwm.ui.contact.ContactActivity
import com.akshay.meetwm.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_register_user.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegisterUser : AppCompatActivity() {

    lateinit var viewModel: RegistrationViewModel
    private val IMAGE_REQUEST_ID = 1
    private  var imageUrl = ""
    private val firebaseUser = FirebaseAuth.getInstance().currentUser
    private val folder = FirebaseStorage.getInstance().reference.child("Images").child(firebaseUser.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        val pref = SharedPref(this)
        val phoneNumber = intent.getStringExtra("phone")!!

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RegistrationViewModel::class.java)

        viewModel.response.observe(this, {

            if(it == "ERROR" || it == null ){
                progressBar.visibility = View.GONE
                Toast.makeText(this, "SOME ERROR OCCURED!", Toast.LENGTH_LONG).show()
            }else{
                pref.setUserID(it.toString())
                progressBar.visibility = View.GONE
                val intent = Intent(this, MainActivity::class.java)
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

                pref.setUserName(userName)
                pref.setUserImageBitmap(imageUrl)
                pref.setUserNumber(phoneNumber)
                pref.setUserStatus(status)

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

            val imageUri = data!!.data
            val imageName = folder.child("dp-${imageUri!!.path.toString()}")

            try {
                progressBar.visibility = View.VISIBLE
                imageName.putFile(imageUri).addOnCompleteListener{
                    if(it.isSuccessful){
                        progressBar.visibility = View.GONE
                        imageName.downloadUrl.addOnCompleteListener { it1 ->
                            if(it1.isSuccessful){
                                val imageURL = it1.result.toString()
                                Log.d("ImageURL", imageURL)
                                setUrl(imageURL)
                            }else{
                                Toast.makeText(this, "Try uploading the image again!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e : Exception){
                progressBar.visibility = View.GONE
                Log.d("ERROR FIREBASE", e.localizedMessage)
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }


            val inputStream = data.data?.let { contentResolver.openInputStream(it) }
            val image = BitmapFactory.decodeStream(inputStream)

            if(image != null) {
                profileImage.setImageBitmap(image)
            }

        }
    }

    private fun setUrl(image: String) {

        imageUrl = image

    }
}