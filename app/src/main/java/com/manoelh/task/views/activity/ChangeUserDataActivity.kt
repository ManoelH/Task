package com.manoelh.task.views.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_change_user_data.*

private const val TAG = "ChangeUserDataActivity"
class ChangeUserDataActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private var thePasswordsAreDifferent: Boolean ?= null
    private lateinit var mUserRepository: UserRepository
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var mUserEntity: UserEntity
    private lateinit var mUserBusiness: UserBusiness

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_data)
        initializeVariables()
        setListeners()
        getUserName()
    }

    private fun initializeVariables() {
        mUserRepository = UserRepository(this)
        mSecurityPreferences = SecurityPreferences(this)
        mUserBusiness = UserBusiness(this)
    }

    private fun setListeners(){
        buttonEditUser.setOnClickListener(this)
        editTextNewPassword.addTextChangedListener(this)
        editTextRewriteNewPassword.addTextChangedListener(this)
    }

    private fun getUserName(){
        editTextNameEditUser.setText(mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_NAME))
    }

    override fun afterTextChanged(s: Editable?) {
        verifyIfTheNewPasswordAreEquals()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    private fun verifyIfTheNewPasswordAreEquals(){
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
        val currentPassword = editTextPasswordEditUser.text.toString()

        setUserEntity()
        verifyTheCurrentPasswordIsRight(currentPassword)
        verifyIfTheNewPasswordAreEquals()
        if (!thePasswordsAreDifferent!!){
            changeVisibilityProgressBar()
            updateUserObserver()
        }
    }

    private fun setUserEntity() {
        val userId = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)
        val username = editTextNameEditUser.text.toString()
        val email = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_EMAIL)
        val newPassword = editTextNewPassword.text.toString()
        mUserEntity =
            UserEntity(id = userId!!, name = username, email = email!!, password = newPassword)
    }

    private fun verifyTheCurrentPasswordIsRight(currentPassword: String){
        try {
            mUserBusiness.theCurrentPasswordTypedIsRight(currentPassword)
        }catch (ve: ValidationException){
            Log.e(TAG, ve.message, ve)
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUserObserver(){
        mUserRepository.updateUserPassword(mUserEntity).observe(this, Observer {
            if(it != null)
                finish()
            else
                changeVisibilityProgressBar()
        })
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
