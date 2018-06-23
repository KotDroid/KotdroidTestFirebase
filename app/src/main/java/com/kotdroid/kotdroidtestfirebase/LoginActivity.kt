package com.kotdroid.kotdroidtestfirebase

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.internal.FirebaseAppHelper.getUid
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var editForgotPassword: EditText? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val TAG = "GoogleActivity"
    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()


        initEmail()
        initGoogle()

    }

    private fun initGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<View>(R.id.btnGoogleSignIn).setOnClickListener{view->
            signIn()
        }
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth!!.getCurrentUser()
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account, btnGoogleSignIn.rootView)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)

            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, view: View) {
        showMessage(view, "Authentication... ")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth!!.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        showMessage(view, "Sign In Success")
                        val user = auth!!.getCurrentUser()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        showMessage(view, "Error ${task.exception?.message}")
                    }
                })
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun initEmail() {
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

    private fun passwordForget(view: View) {

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
