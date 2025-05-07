package com.berlin.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class User(
    @BsonId val id: String,
    @BsonProperty("user_name") val userName: String,
    val password: String,
    val role: UserRole
)