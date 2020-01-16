package com.manoelh.task.business

import android.content.Context
import com.manoelh.task.R
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.UserConstants
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException

class UserBusiness (var context: Context) {

    private val mSecurityPreferences: SecurityPreferences = SecurityPreferences(context)

    fun validateRegisterUser(name: String, email: String, password: String){
        if (name.isBlank() || email.isBlank() || password.isBlank())
            throw ValidationException(context.getString(R.string.userValidationException))
        if (!email.trim().matches(UserConstants.PATTERNS.EMAIL_VALIDATION.toRegex()))
            throw ValidationException(context.getString(R.string.invalidEmail))
        if (password.length < 8)
            throw ValidationException(context.getString(R.string.validationPasswordCharacters))
        if (password.trim().matches(UserConstants.PATTERNS.PASSWORD_VALIDATION.toRegex()))
            throw ValidationException(context.getString(R.string.validationSecurityPassword))
    }

    fun saveSharedPreferencesUser(user: UserEntity){
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_ID, user.id)
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, user.name)
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_EMAIL, user.email)
    }
}