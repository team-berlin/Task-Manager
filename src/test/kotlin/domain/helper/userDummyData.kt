package com.berlin.domain.helper

import com.berlin.domain.model.user.User

fun userDummyData(
    id: String = "1",
    userName: String,
    role: User.UserRole = User.UserRole.MATE
): User {
   return User(
        id = id,
        userName = userName,
        role = role
    )
}