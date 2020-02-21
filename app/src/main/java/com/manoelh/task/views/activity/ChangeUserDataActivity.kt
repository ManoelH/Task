package com.manoelh.task.views.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.manoelh.task.R
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.SecurityPreferences
import kotlinx.android.synthetic.main.activity_change_user_data.*

class ChangeUserDataActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private var thePasswordsAreDifferent: Boolean ?= null
    private lateinit var mUserRepository: UserRepository
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var mUserEntity: UserEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_data)
        initializeVariables()
        setListeners()
        getUserName()
        setupObservers()
    }

    private fun initializeVariables() {
        mUserRepository = UserRepository(this)
        mSecurityPreferences = SecurityPreferences(this)
    }

    private fun setListeners(){
        buttonEditUser.setOnClickListener(this)
        editTextNewPassword.addTextChangedListener(this)
        editTextRewriteNewPassword.addTextChangedListener(this)
    }

    private fun getUserName(){
        editTextNameEditUser.setText(mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_NAME))
    }

    private fun setupObservers(){
        mUserRepository.updateUserPassword(mUserEntity).observe(this, Observer {
            finish()
        })
    }

    override fun afterTextChanged(s: Editable?) {
        verifyIfThePasswordAreEquals()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    private fun verifyIfThePasswordAreEquals(){
        textViewPasswordsAreNotEqualsEditUser.isVisible =
            (editTextNewPassword.text.toString() != editTextRewriteNewPassword.text.toString())
        thePasswordsAreDifferent = textViewPasswordsAreNotEqualsEditUser.isVisible
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.buttonEditUser -> updateUser()
        }
    }

    private fun updateUser(){
        changeVisibilityProgressBar()
    }

    private fun changeVisibilityProgressBar(){
        if (progressBarEditUser.isVisible) {
            progressBarEditUser.visibility = ProgressBar.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        else{
            progressBarEditUser.visibility = ProgressBar.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
