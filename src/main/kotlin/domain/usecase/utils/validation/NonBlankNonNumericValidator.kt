package com.berlin.domain.usecase.utils.validation

class NonBlankNonNumericValidator : Validator{
    override fun isValid(input: String): Boolean =
      input.isNotBlank() && !(input.all {it.isDigit()})
    }