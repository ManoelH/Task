package com.manoelh.task.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.util.SecurityPreferences
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUserBusiness: UserBusiness
    private lateinit var mSecurityPreferences: SecurityPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLogin.setOnClickListener(this)
        mUserBusiness = UserBusiness(this)
        mSecurityPreferences = SecurityPreferences(this)
        verifyIfUserIsLogged()
        textViewCreateAccount.setOnClickListener(this)
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
        if (mUserBusiness.login(email, password))
            openMainActivity()

        else
            Toast.makeText(this, this.getString(R.string.messageWrongEmailOrPassword), Toast.LENGTH_LONG).show()
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openRegisterActivity(){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
