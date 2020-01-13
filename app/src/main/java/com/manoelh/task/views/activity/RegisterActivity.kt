package com.manoelh.task.views.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var userEntity: UserEntity
    private lateinit var userBusiness: UserBusiness
    private var thePasswordsAreDifferent = true
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setListeners()
        userBusiness = UserBusiness(this)
        auth = FirebaseAuth.getInstance()
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

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI (currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null)
            openMainActivity()
    }

    private fun registerUser(){
        try {
            if (thePasswordsAreDifferent)
                Toast.makeText(this, this.getText(R.string.the_passwords_are_not_equals), Toast.LENGTH_SHORT).show()
            else{
                val name = editTextName.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                userEntity = UserEntity(name = name, email = email, password = password)
                insertIntoFirebaseAuthenticationSystem(userEntity.email, userEntity.password)
            }
        }catch (ve: ValidationException){
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.genericError), Toast.LENGTH_LONG).show()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun insertIntoFirebaseAuthenticationSystem(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { authResult ->
                if (authResult.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val userId = user!!.uid
                    userEntity.id = userId
                    userBusiness.registerUser(userEntity, authResult.isSuccessful)
                    userBusiness.saveSharedPreferencesUser(userEntity)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", authResult.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }

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
