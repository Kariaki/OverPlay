package com.overplay.overplay

class RegistrationUtil {

    companion object {

        lateinit var upperUsername: String
        lateinit var password:String
        lateinit var confirmPassword: String
        public val existingUsers = listOf("peter", "carl")
        fun validateRegistrationInput(
            username: String,
            password: String,
            confirmPassword: String
        ): Boolean {
            upperUsername=username
            this.password=password
            this.confirmPassword=confirmPassword

            return true
        }

    }
}