package com.berlin.model


data class User(
    val id:String,
    val userName:String,
    val password:String,
    val role:UserRole
)
