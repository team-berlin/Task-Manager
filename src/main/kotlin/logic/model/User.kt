package com.berlin.domain.model

import domain.model.Permission


data class User(
    val id:String,
    val userName:String,
    val password:String,
    val permission: Permission,
    val role: UserRole
)
