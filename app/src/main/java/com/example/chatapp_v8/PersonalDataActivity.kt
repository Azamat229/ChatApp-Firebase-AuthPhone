package com.example.chatapp_v8

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.chatapp_v8.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_personal_data.*
import java.util.*

class PersonalDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)

        register_button_register.setOnClickListener {
//            performRegister()
            uploadImageToFirebaseStorage()
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("TAG", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

//    private fun performRegister() {
////        val email = email_edittext_register.text.toString()
////        val password = password_edittext_register.text.toString()
//
////        if (email.isEmpty() || password.isEmpty()) {
////            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
////            return
////        }
//
////        Log.d(TAG, "Attempting to create user with email: $email")
//
//        // Firebase Authentication to create a user with email and password
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
//
//                // else if successful
//                Log.d(TAG, "Successfully created user with uid: ${it.result.user.uid}")
//
////                uploadImageToFirebaseStorage()
//            }
//            .addOnFailureListener {
//                Log.d(TAG, "Failed to create user: ${it.message}")
//                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
//                    .show()
//            }
//    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d("TAG", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f // ?

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref =
            FirebaseStorage.getInstance().getReference("/images/$filename") // was FirebaseStorage
//        val ref = FirebaseStorage.getInstance().reference.child("image").child(filename)

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("TAG", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("TAG", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        Log.d("TAG", "SaveUserTOFirebaseDatabase")
        val uid = FirebaseAuth.getInstance().uid ?: ""

        Log.d("TAG", uid)
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        Log.d("TAG", ref.toString())
        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        Log.d("TAG", user.toString())
        ref.setValue(user)

            .addOnSuccessListener {
                Log.d("TAG", "Finally we saved the user to Firebase Database")

                val intent = Intent(this, Home::class.java) // go to verify
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to set value to database: ${it.message}")
            }
    }
}