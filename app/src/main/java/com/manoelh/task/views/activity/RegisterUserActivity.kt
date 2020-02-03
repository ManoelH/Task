package com.manoelh.task.views.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.android.synthetic.main.activity_register_user.progressBar

class RegisterUserActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var mUserEntity: UserEntity
    private lateinit var mUserBusiness: UserBusiness
    private var thePasswordsAreDifferent = true
    private lateinit var auth: FirebaseAuth
    private lateinit var mUserRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        setListeners()
        intanceMyObjectsWithContext()
        auth = FirebaseAuth.getInstance()
    }

    private fun intanceMyObjectsWithContext() {
        mUserBusiness = UserBusiness(this)
        mUserRepository = UserRepository(this)
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
                changeVisibilityProgressBar()
                mUserEntity = UserEntity(name = name, email = email, password = password)
                setupObservers()
            }
        }catch (ve: ValidationException){
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this, getString(R.string.generic_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupObservers(){
        mUserRepository.insertIntoFirebaseAuthenticationSystem(mUserEntity).observe(this, Observer { firebaseUser ->
            if (firebaseUser != null){
                mUserRepository.insertUser(true, mUserEntity).observe(this, Observer {
                    if (it != null)
                        updateUI(firebaseUser)
                    else
                        changeVisibilityProgressBar()
                })
            }
            else
                changeVisibilityProgressBar()
        })
    }

    private fun changeVisibilityProgressBar(){
        if (progressBar.isVisible) {
            progressBar.visibility = ProgressBar.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        else{
            progressBar.visibility = ProgressBar.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
