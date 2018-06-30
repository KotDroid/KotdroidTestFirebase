package com.kotdroid.kotdroidtestfirebase

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val btnSignOut = findViewById<View>(R.id.btnSignOut) as Button
        val checkEmail = findViewById<View>(R.id.checkEmail) as Button
        val btnAddData = findViewById<View>(R.id.addData) as Button
        val btnGetData = findViewById<View>(R.id.getData) as Button
        val btnEmailVerification = findViewById<View>(R.id.btnEmailVerification) as Button

        btnSignOut.setOnClickListener { view ->
            signOut()
        }
        btnEmailVerification.setOnClickListener { view ->
            sendEmailVerification(view)
        }
        checkEmail.setOnClickListener { view ->
            showMessage(view, "Email Verification : ${auth!!.currentUser!!.isEmailVerified}")
        }
        btnAddData.setOnClickListener { view ->
            pushData(view)
        }
        btnGetData.setOnClickListener { view ->
            getData()
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


    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("FIRE", null).show()
    }


    private fun pushData(view: View) {

        try {
            val ref = FirebaseDatabase.getInstance().reference

            val myHashMap = HashMap<String, String>()
            myHashMap.put("userName", auth!!.currentUser!!.displayName.toString())
            myHashMap.put("userEmail", auth!!.currentUser!!.email.toString())
            myHashMap.put("userID", auth!!.currentUser!!.uid)

            ref.child("users/user-${auth!!.uid}").setValue(myHashMap)

            showMessage(view, "add Successful")
        } catch (e: Exception) {
            Log.e(TAG, "Error ${e.localizedMessage}")
        }

    }

    private fun getData() {
        val ref1 = FirebaseDatabase.getInstance().reference

        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.i(TAG, snapshot.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (keySnapshot in snapshot.children) {
                    for (ch in snapshot.child(keySnapshot.key.toString()).children) {
                        val key = ch.key
                        val value = ch.value
                        Log.i(TAG, value.toString())
                    }
                }
            }
        })
    }

//    private fun getData() {
//        val ref1 = FirebaseDatabase.getInstance().reference
//
//        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//
//
////            override fun onDataChange(snapshot: DataSnapshot?) {
////                val children = snapshot!!.children
////                // This returns the correct child count...
////                println("count: "+snapshot.children.count().toString())
////                children.forEach {
////                    println(it.toString())
////                }
////            }
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val children = snapshot.children
//
//
//                Log.i(TAG, "count: " + snapshot.children.count().toString())
//
//
////                children.forEach {
////                    Log.i("XXXX", it.key)
////                    Log.i("XXXX", it.value.toString())
////                }
//
//
//                for (keySnapshot in snapshot.children) {
//                    for (ch in snapshot.child(keySnapshot.key.toString()).children) {
//                        val key = ch.key
//                        val value = ch.value
//                        //val user = ch.getValue(UserModel::class.java)
//                        Log.i(TAG, value.toString())
//                    }
//                }
//            }
//        })


//        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Userlist = new ArrayList < String >();
//                // Result will be holded Here
//                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
//                Userlist.add(String.valueOf(dsp.geValue())); //add result into array list
//
//            }
//            }
//        }
//    }
}
