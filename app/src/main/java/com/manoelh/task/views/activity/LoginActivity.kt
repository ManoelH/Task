package com.manoelh.task.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.service.FirebaseAuthenticationService
import com.manoelh.task.util.SecurityPreferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUserBusiness: UserBusiness
    private lateinit var mSecurityPreferences: SecurityPreferences
    private var auth = FirebaseAuth.getInstance()
    private lateinit var firebaseAuthenticationService: FirebaseAuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLogin.setOnClickListener(this)
        mUserBusiness = UserBusiness(this)
        mSecurityPreferences = SecurityPreferences(this)
        firebaseAuthenticationService = FirebaseAuthenticationService(this)
        verifyIfUserIsLogged()
        textViewCreateAccount.setOnClickListener(this)
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
        changeVisibilityProgressBar()
        firebaseAuthenticationService.userAuthentication(email, password, saveUserIdAndEmailToSharedPreferences())
    }

    private fun saveUserIdAndEmailToSharedPreferences(): (FirebaseUser?) -> Unit {
        return {
            if (it != null){
                mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_ID, it!!.uid)
                mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_EMAIL, it.email!!)
                updateUI(it)
            }
            else{
                changeVisibilityProgressBar()
                updateUI(null)
            }
        }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openRegisterActivity(){
        startActivity(Intent(this, RegisterUserActivity::class.java))
    }

    private fun changeVisibilityProgressBar(){
        if (progressBar.isVisible) {
            progressBar.visibility = ProgressBar.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            progressBar.visibility = ProgressBar.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
