package com.manoelh.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.entity.UserEntity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var user: UserEntity
    val userBusiness = UserBusiness(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        buttonRegister.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if(view.id == R.id.buttonRegister)
            registerUser()
    }

    private fun registerUser(){
        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        user = UserEntity(name, email, password)
        userBusiness.registerUser(user)
    }


}
