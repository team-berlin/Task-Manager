package com.berlin.domain.model


data class User(
    val id:String,
    val userName:String,
    val password:String,
    val role: UserRole
)
