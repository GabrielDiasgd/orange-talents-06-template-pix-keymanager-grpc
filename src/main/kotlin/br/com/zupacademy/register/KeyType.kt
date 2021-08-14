package br.com.zupacademy.register

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class KeyType {

    CPF {
        override fun validator(keyValue: String?): Boolean {
            if (keyValue.isNullOrBlank()) {
                return false
            }
            if (!keyValue.matches("[0-9]{11}".toRegex())) {
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(keyValue, null)
            }
        }
    }, PHONE {
        override fun validator(keyValue: String?): Boolean {
            if (keyValue.isNullOrBlank()) {
                return false
            }
            return keyValue.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    }, EMAIL {
        override fun validator(keyValue: String?): Boolean {
            if (keyValue.isNullOrBlank()) {
                return false
            }
            return  EmailValidator().run {
                initialize(null)
                isValid(keyValue, null)
            }

        }
    }, RANDOM {
        override fun validator(keyValue: String?) = keyValue.isNullOrBlank()
    };

    constructor()

    abstract fun validator(keyValue: String?): Boolean
}