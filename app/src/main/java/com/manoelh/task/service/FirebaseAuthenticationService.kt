package com.manoelh.task.service

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.manoelh.task.R

class FirebaseAuthenticationService(val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun userAuthentication( email: String, password: String, callback: (FirebaseUser?) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, context.getString(R.string.login_successful))
                    val user = auth.currentUser
                    callback(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, context.getString(R.string.login_unsuccessful), task.exception)
                    Toast.makeText(context, context.getString(R.string.message_wrong_email_or_password),
                        Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
    }
}