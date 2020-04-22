package com.example.messenger.LoginandRegister

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.messenger.Messages.LatestMessageAcivity
import com.example.messenger.Model.User
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterAcitvity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        main_login_button.setOnClickListener({
            val email =login_email.text.toString()
            val password = login_password.text.toString()

            if (performRegister(email, password)) return@setOnClickListener
        })
        main_register_ACCOUNT.setOnClickListener({
            Log.d("MAIN_ACTIVITY","already have am account")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        })
        select_photo_button.setOnClickListener({

            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent,0)
            select_photo_button.isVisible=false


        })

    }
    var Selectedphotouri:Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0&&resultCode==Activity.RESULT_OK&&data!=null)
        {
            Toast.makeText(this,"Photo was selected",Toast.LENGTH_SHORT).show()
            Selectedphotouri= data.data
            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,Selectedphotouri)
           val bitmapDrawable= BitmapDrawable(bitmap)
//            select_photo_button.setBackgroundDrawable(bitmapDrawable)
            Select_photo_imageView.setImageBitmap(bitmap)


        }
    }

    private fun performRegister(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            login_email.error = "please fill!!"
            return true

        }
        if (password.isEmpty()) {
            login_password.error = "please fill!!"
            return true

        }

        var mAuth = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener({
                if (!it.isSuccessful) {
                    Toast.makeText(this, "failed ot create", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener

                } else {
                    Toast.makeText(this, "created", Toast.LENGTH_SHORT).show()

                upload_photo_to_firebase()

                }
            }
            )




        Log.d("main_activity", "EMail is" + email)
        Log.d("MAIN_ACTIVITY", "PASSSWORD" + password)
        return false
    }

    private fun upload_photo_to_firebase() {
        if(Selectedphotouri==null)
            return



        val filename = UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(Selectedphotouri!!).addOnSuccessListener {

            Toast.makeText(this,"Photo_uploaded",Toast.LENGTH_SHORT).show()
            Log.d("Register","Successfully uploaded")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("registerAcitvity","file LOacation$it")


                saveUserToFirebaseDatabase(it.toString())
            }

        }

    }

    private fun saveUserToFirebaseDatabase(ProfileImageurl: String) {
        val uid=FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user= User(
            uid.toString(),
            main_username.text.toString(),
            ProfileImageurl
        )
        ref.setValue(user).addOnSuccessListener {



            Log.d("Register","finally user has access to the database")
            Toast.makeText(this,"yay!! Added to database",Toast.LENGTH_SHORT).show()

            val intent= Intent(this, LatestMessageAcivity::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }.addOnFailureListener({
            Toast.makeText(this," Sorry cannot create",Toast.LENGTH_SHORT).show()
        })

    }
}
