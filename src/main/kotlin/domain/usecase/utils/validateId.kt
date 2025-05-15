package com.berlin.domain.usecase.utils

 fun isIDValid(id: String): Boolean =
        id.isNotBlank() && !id.all { it.isDigit() }
