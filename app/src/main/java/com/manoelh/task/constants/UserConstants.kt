package com.manoelh.task.constants

class UserConstants {
    object PATTERNS{
        val EMAIL_VALIDATION = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val PASSWORD_VALIDATION = "\\A(?=.*[A-Z])(?=.*\\d)(?!.*[^a-zA-Z0-9]).{8,}"
    }
}