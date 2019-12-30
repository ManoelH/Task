package com.manoelh.task.business

import android.content.Context
import android.widget.Toast
import com.manoelh.task.R
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException

class UserBusiness (var context: Context) {

    private val userRepository = UserRepository.getInstance(context)
    private val mSecurityPreferences: SecurityPreferences = SecurityPreferences(context)

    fun registerUser(user: UserEntity){
        validateRegisterUser(user)
        user.id = userRepository.insert(user)
        Toast.makeText(context, context.getString(R.string.userSaved), Toast.LENGTH_LONG).show()
        saveSharedPreferencesUser(user)
    }

    private fun validateRegisterUser(user: UserEntity){
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
        if (user.name.isEmpty() || user.email.isEmpty() || user.password.isEmpty())
            throw ValidationException(context.getString(R.string.userValidationException))
        if (!user.email.trim().matches(emailPattern.toRegex()))
            throw ValidationException(context.getString(R.string.invalidEmail))
        if (user.password.length < 8)
            throw ValidationException(context.getString(R.string.validationPasswordCharacters))
        if (!user.password.trim().matches(passwordPattern.toRegex()))
            throw ValidationException(context.getString(R.string.validationSecurityPassword))
        if (userRepository.thisEmailExist(user.email))
            throw ValidationException(context.getString(R.string.emailExist))
    }

    private fun saveSharedPreferencesUser(user: UserEntity){
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_ID, user.id.toString())
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, user.name)
        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_EMAIL, user.email)
    }
    
    fun login(email: String, password: String): Boolean{
        val user = userRepository.login(email, password)
        if (user != null){
            saveSharedPreferencesUser(user)
            return true
        }
        return false
    }
}