package com.berlin

import com.berlin.model.User
import com.berlin.model.UserRole

fun userDummyData(
    id: String = "1",
    userName: String,
    password: String ,
    role: UserRole = UserRole.MATE
): User {
   return User(
        id = id,
        userName = userName,
        password = password,
        role = role
    )
}