package com.berlin.model


data class User(
    val id:Int,
    val userName:String,
    val password:String,
    val role:UserRole
)
