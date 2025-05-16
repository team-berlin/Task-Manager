package com.berlin.domain.usecase.utils.validation

interface Validator {
    fun isValid(input: String):Boolean
}