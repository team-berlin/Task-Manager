package com.berlin.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class Task(
    @BsonId val id: String,
    @BsonProperty("project_id") val projectId: String,
    val title: String,
    val description: String?,
    @BsonProperty("state_id") val stateId: String,
    @BsonProperty("assigned_to_user_id") val assignedToUserId: String,
    @BsonProperty("create_by_user_id") val createByUserId: String,
)