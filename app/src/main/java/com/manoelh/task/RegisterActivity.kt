package com.manoelh.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var user: UserEntity
    lateinit var userBusiness: UserBusiness

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        buttonRegister.setOnClickListener(this)
        userBusiness = UserBusiness(this)
    }

    override fun onClick(view: View) {
        if(view.id == R.id.buttonRegister)
            registerUser()
    }

    private fun registerUser(){
        try {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            user = UserEntity(name, email, password)
            userBusiness.registerUser(user)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }catch (ve: ValidationException){
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.genericError), Toast.LENGTH_LONG).show()
        }
    }

}
