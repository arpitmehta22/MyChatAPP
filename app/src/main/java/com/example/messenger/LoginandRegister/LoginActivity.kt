package com.example.messenger.LoginandRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.Messages.LatestMessageAcivity
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)


        main_login_button.setOnClickListener({
            val email = login_email.text.toString()
            val  password = login_password.text.toString()

            performLogin(email, password)
        })

        main_register_ACCOUNT.setOnClickListener({
            val intent = Intent(this,
                RegisterAcitvity::class.java)
            startActivity(intent)
            finish()
        })


    }
    private fun performLogin(email: String, password: String) :Boolean{
        if (email.isEmpty()) {
            login_email.error = "please fill!!"
            return true

        }
        if (password.isEmpty()) {
            login_password.error = "please fill!!"
            return true

        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener({
                if (!it.isSuccessful) {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener

                } else {
                    val intent = Intent(this,
                        LatestMessageAcivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this, "LEt's dive in", Toast.LENGTH_SHORT).show()
                    finish()

                }
            }
            )
return true
    }

}