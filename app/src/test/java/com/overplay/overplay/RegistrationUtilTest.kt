package com.overplay.overplay

import android.util.Log
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest {

    @Test
    fun emptyUserName() {
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "password",
            "password"
        )
        assertThat(result).isTrue()
    }
    @Test
    fun `existing username`() {
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "password",
            "password"
        )
        val nResult=RegistrationUtil.existingUsers.contains(RegistrationUtil.upperUsername)

        assertThat(nResult).isFalse()

    }
    @Test
    fun `password does not match`(){
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "password",
            "password"
        )
        assertThat(RegistrationUtil.password).isEqualTo(RegistrationUtil.confirmPassword)

    }
}