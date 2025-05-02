package com.berlin.logic.helper

import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.model.Permission

fun userDummyData(
    id: String = "1",
    userName: String,
    password: String,
    permission: Permission,
    role: UserRole = UserRole.MATE
): User {
   return User(
        id = id,
        userName = userName,
        password = password,
       permission = permission,
        role = role
    )
}