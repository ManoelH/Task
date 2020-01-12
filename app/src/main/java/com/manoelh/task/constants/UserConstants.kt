package com.manoelh.task.constants

class UserConstants {
    object PATTERNS{
        val EMAIL_VALIDATION = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val PASSWORD_VALIDATION = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    }
}