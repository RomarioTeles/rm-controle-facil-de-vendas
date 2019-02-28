package br.com.rm.cfv.utils

import java.util.regex.Pattern

class EmailValidate{
    companion object {
        val VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

        fun validate(emailStr: String): Boolean {
            val matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
            return matcher.find()
        }
    }
}