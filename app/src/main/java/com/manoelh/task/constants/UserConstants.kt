package com.manoelh.task.constants

import android.content.Context
import com.manoelh.task.util.SecurityPreferences

class UserConstants {
    object PATTERNS{
        val EMAIL_VALIDATION = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val PASSWORD_VALIDATION = "\\A(?=.*[A-Z])(?=.*\\d)(?!.*[^a-zA-Z0-9]).{8,}"
    }

    object PROFILE_PHOTO{
        fun returnPhotoName(context: Context): String{
            val securityPreferences = SecurityPreferences(context)
            return "photo${securityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!}.jpg"
        }

        fun returnProfilePhotoReference(context: Context): String{
            return "images/${returnPhotoName(context)}"
        }
    }
}