package com.manoelh.task.business

import android.content.Context
import android.widget.Toast
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.repository.UserRepository

class UserBusiness (var context: Context) {

    val userRepository = UserRepository.getInstance(context)

    fun registerUser(user: UserEntity){
        val idUser = userRepository.insert(user)
        Toast.makeText(context, "${user.name} with id $idUser registered", Toast.LENGTH_LONG).show()
    }
}