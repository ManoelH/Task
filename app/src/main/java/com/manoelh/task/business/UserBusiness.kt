package com.manoelh.task.business

import android.content.Context
import android.widget.Toast
import com.manoelh.task.R
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.ValidationException

class UserBusiness (var context: Context) {

    val userRepository = UserRepository.getInstance(context)

    fun registerUser(user: UserEntity){
        validateRegisterUser(user)
        userRepository.insert(user)
        Toast.makeText(context, context.getString(R.string.userSaved), Toast.LENGTH_LONG).show()
    }

    private fun validateRegisterUser(user: UserEntity){
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passwordPattern = "[a-z][a-z0-9]+"
        if (user.name.isEmpty() || user.email.isEmpty() || user.password.isEmpty())
            throw ValidationException(context.getString(R.string.userValidationException))
        if (!user.email.trim().matches(emailPattern.toRegex()))
            throw ValidationException(context.getString(R.string.invalidEmail))
        if (user.password.length < 8)
            throw ValidationException(context.getString(R.string.validationPasswordCharacters))
    }
}