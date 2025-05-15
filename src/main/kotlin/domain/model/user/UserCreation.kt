package com.berlin.domain.model.user

data class UserCreation(
    val id: String,
    val userName: String,
    val role: User.UserRole,
    val hashedPassword: String
)