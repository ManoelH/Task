package com.manoelh.task.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.DatabaseConnectionFirebase
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var user: UserEntity
    private lateinit var userBusiness: UserBusiness
    private var thePasswordsAreDifferent = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setListeners()
        userBusiness = UserBusiness(this)
    }

    private fun setListeners() {
        buttonRegister.setOnClickListener(this)
        editTextRewritePassword.addTextChangedListener(this)
        editTextPassword.addTextChangedListener(this)
    }

    override fun onClick(view: View) {
        if(view.id == R.id.buttonRegister)
            registerUser()
    }

    private fun registerUser(){
        try {
            if (thePasswordsAreDifferent)
                Toast.makeText(this, this.getText(R.string.the_passwords_are_not_equals), Toast.LENGTH_SHORT).show()
            else{
                val name = editTextName.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                user = UserEntity(name = name, email = email, password = password)
                val db = DatabaseConnectionFirebase()
                db.registerUser(user)
//                userBusiness.registerUser(user)
//                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }catch (ve: ValidationException){
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.genericError), Toast.LENGTH_LONG).show()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        verifyIfThePasswordAreEquals()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    private fun verifyIfThePasswordAreEquals(){
        textViewPasswordsAreNotEquals.isVisible =
            (editTextPassword.text.toString() != editTextRewritePassword.text.toString())
        thePasswordsAreDifferent = textViewPasswordsAreNotEquals.isVisible
    }

}
