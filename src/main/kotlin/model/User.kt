package com.berlin.model

import com.berlin.domain.model.UserRole

data class User(
    val id:String,
    val userName:String,
    val password:String,
    val role: UserRole
)