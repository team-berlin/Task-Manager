package com.berlin.data.dto

import com.berlin.domain.model.UserRole
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class UserDto(
    @BsonId val id: String,
    @BsonProperty("user_name") val userName: String,
    @BsonProperty("password") val password: String,
    @BsonProperty("role") val role: UserRole
)