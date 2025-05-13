package com.berlin.domain.model.user

data class User(
    val id: String,
    val userName: String,
    val role: UserRole
) {
    enum class UserRole {
        ADMIN,MATE
    }
}