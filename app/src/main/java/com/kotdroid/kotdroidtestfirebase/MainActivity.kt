package com.kotdroid.kotdroidtestfirebase

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    var auth :FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

       Log.i("MAIN", auth!!.currentUser!!.displayName)

        val btnSignOut = findViewById<View>(R.id.btnSignOut) as Button
        val checkEmail = findViewById<View>(R.id.checkEmail) as Button
        val btnEmailVerification = findViewById<View>(R.id.btnEmailVerification) as Button

        btnSignOut.setOnClickListener { view ->
            signOut()
        }
        btnEmailVerification.setOnClickListener { view ->
            sendEmailVerification(view)
        }
        checkEmail.setOnClickListener { view ->
            showMessage(view, "Email Verification : ${auth!!.currentUser!!.isEmailVerified}" )
        }

    }


    private fun signOut() {
        auth!!.signOut()
        finish()
    }

    private fun sendEmailVerification(view: View) {
        val user = auth!!.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                showMessage(view, "Email Send")
            }
        }
    }


    private fun showMessage(view : View,  message:String) {
        Snackbar.make(view, message , Snackbar.LENGTH_INDEFINITE).setAction("FIRE", null).show()
    }





}
