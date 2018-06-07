package com.kotdroid.kotdroidtestfirebase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    var auth :FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<View>(R.id.btnSignUp) as Button
        val btnSignIn = findViewById<View>(R.id.btnSignIn) as Button
        val btnSignOut = findViewById<View>(R.id.btnSignOut) as Button

        btnSignUp.setOnClickListener { view ->
            signUp(view, "syriadev3@gmail.com", "aaaaaa")
        }

        btnSignIn.setOnClickListener { view ->
            signIn(view, "syriadev3@gmail.com", "aaaaaa")
        }

        btnSignOut.setOnClickListener { view ->
            signOut()
        }


    }

    private fun signUp(view :View, email :String, pass :String) {
        auth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                showMessage(view, "Successful")
                sendEmailVerfivation(view)
            } else {
                showMessage(view, "Error ${task.exception?.message}")
                
            }
        })
    }

    private fun signIn(view :View, email :String, pass :String) {
        auth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                showMessage(view, "Successful")
            } else {
                showMessage(view, "Error ${task.exception?.message}")
            }
        })
    }

    private fun sendEmailVerfivation(view :View) {
        var user = auth!!.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(this) {
            task ->
            if (task.isSuccessful) {
                showMessage(view, "Email Send")
            }
        }
    }

    private fun signOut() {
        auth!!.signOut()
    }

    private fun showMessage(view : View,  message:String) {
        Snackbar.make(view, message , Snackbar.LENGTH_INDEFINITE).setAction("FIRE", null).show()
    }

}
