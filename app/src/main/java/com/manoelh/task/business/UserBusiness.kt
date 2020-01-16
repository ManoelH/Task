package com.manoelh.task.business

import android.content.Context
import android.view.View
import android.widget.Toast
import com.manoelh.task.R
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.UserConstants
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException

class UserBusiness (var context: Context) {

    private val mUserRepository = UserRepository.getInstance(context)
    private val mSecurityPreferences: SecurityPreferences = SecurityPreferences(context)

    fun registerUser(user: UserEntity, isAuthenticated: Boolean){
        if(isAuthenticated){
            validateRegisterUser(user)
            mUserRepository.insert(user.name, user.id)
            Toast.makeText(context, context.getString(R.string.userSaved), Toast.LENGTH_LONG).show()
        }
    }

    private fun validateRegisterUser(user: UserEntity){
        if (user.name.isBlank() || user.email.isBlank() || user.password.isBlank())
            throw ValidationException(context.getString(R.string.userValidationException))
        if (!user.email.trim().matches(UserConstants.PATTERNS.EMAIL_VALIDATION.toRegex()))
            throw ValidationException(context.getString(R.string.invalidEmail))
        if (user.password.length < 8)
            throw ValidationException(context.getString(R.string.validationPasswordCharacters))
        if (!user.password.trim().matches(UserConstants.PATTERNS.PASSWORD_VALIDATION.toRegex()))
            throw ValidationException(context.getString(R.string.validationSecurityPassword))
        if (mUserRepository.thisEmailExist(user.email))
            throw ValidationException(context.getString(R.string.emailExist))
    }

    fun saveSharedPreferencesUser(user: UserEntity){
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_ID, user.id)
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, user.name)
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_EMAIL, user.email)
    }
}