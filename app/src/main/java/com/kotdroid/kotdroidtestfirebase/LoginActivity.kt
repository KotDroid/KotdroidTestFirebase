package com.kotdroid.kotdroidtestfirebase

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var editForgotPassword :EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val btnSignUp = findViewById<View>(R.id.btnSignUp) as Button
        val btnSignIn = findViewById<View>(R.id.btnSignIn) as Button
        val btnForgotPassword = findViewById<View>(R.id.btnForgotPassword) as Button
        val edtEmail = findViewById<View>(R.id.edt_email) as EditText
        val edtPassword = findViewById<View>(R.id.edt_password) as EditText
        editForgotPassword = findViewById<View>(R.id.EditForgotPassword) as EditText

        btnSignUp.setOnClickListener { view ->
            if (validateForm(edtEmail.text.toString(), edtPassword.text.toString(), view)) {
                signUp(view, edtEmail.text.toString(), edtPassword.text.toString())
            }
        }

        btnSignIn.setOnClickListener { view ->
            if (validateForm(edtEmail.text.toString(), edtPassword.text.toString(), view)) {
                signIn(view, edtEmail.text.toString(), edtPassword.text.toString())
            }
        }

        btnForgotPassword.setOnClickListener { view ->
            passwordForget(view)
        }

    }

    private fun passwordForget(view :View) {

        val email = editForgotPassword!!.text.toString()

        if (email.isBlank()) {
            showMessage(view, "Enter your email!")
            return
        }

        auth!!.sendPasswordResetEmail(email).addOnCompleteListener(this,
                OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showMessage(view, "Check your email ")
                    } else {
                        showMessage(view, "Fail to send reset password  ${task.exception?.message}")

                    }
        })
    }

    private fun signUp(view: View, email: String, pass: String) {
        showMessage(view, "Authentication... ")
        auth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                showMessage(view, "Successful")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                showMessage(view, "Error ${task.exception?.message}")

            }
        })
    }

    private fun signIn(view: View, email: String, pass: String) {
        showMessage(view, "Authentication... ")
        auth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful) {
                showMessage(view, "Successful")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                showMessage(view, "Error ${task.exception?.message}")
            }
        })
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("FIRE", null).show()
    }

    private fun validateForm(email: String, password: String, view: View): Boolean {

        if (TextUtils.isEmpty(email)) {
            showMessage(view, "Enter email address!")
            return false
        }

        if (TextUtils.isEmpty(password)) {
            showMessage(view, "Enter password!")
            return false
        }

        if (password.length < 8) {
            showMessage(view, "Password too short, enter minimum 8 characters!")
            return false
        }

        return true
    }
}
