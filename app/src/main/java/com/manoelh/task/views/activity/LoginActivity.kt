package com.manoelh.task.views.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.util.SecurityPreferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUserBusiness: UserBusiness
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLogin.setOnClickListener(this)
        mUserBusiness = UserBusiness(this)
        mSecurityPreferences = SecurityPreferences(this)
        verifyIfUserIsLogged()
        textViewCreateAccount.setOnClickListener(this)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null)
            openMainActivity()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.buttonLogin -> login()
            R.id.textViewCreateAccount -> openRegisterActivity()
        }
    }

    private fun verifyIfUserIsLogged(){
        if (!mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID).isNullOrEmpty())
            openMainActivity()
    }

    private fun login(){
        val email = editTextLoginEmail.text.toString()
        val password = editTextLoginPassword.text.toString()
       // if (mUserBusiness.login(email, password))
            userAuthentication(email, password)

        //else
         //   Toast.makeText(this, this.getString(R.string.messageWrongEmailOrPassword), Toast.LENGTH_LONG).show()
    }

    private fun userAuthentication(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openRegisterActivity(){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
