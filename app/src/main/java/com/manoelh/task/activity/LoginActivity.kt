package com.manoelh.task.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.entity.UserEntity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUserBusiness: UserBusiness
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLogin.setOnClickListener(this)
        mUserBusiness = UserBusiness(this)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.buttonLogin)
            login()
    }

    private fun login(){
        val email = editTextLoginEmail.text.toString()
        val password = editTextLoginPassword.text.toString()
        if (mUserBusiness.login(email, password)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
            Toast.makeText(this, this.getString(R.string.messageWrongEmailOrPassword), Toast.LENGTH_LONG).show()

    }
}
