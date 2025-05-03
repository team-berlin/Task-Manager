package com.berlin.domain.model

import com.berlin.domain.permission.assignPermissions

data class User(
    val id:String,
    val userName:String,
    val password:String,
    val permission: Permission= assignPermissions(UserRole.MATE),
    val role: UserRole
)
