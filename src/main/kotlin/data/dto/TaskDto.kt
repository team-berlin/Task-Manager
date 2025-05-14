package com.berlin.data.dto

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class TaskDto(
    @BsonId val id: String,
    @BsonProperty("project_id") val projectId: String,
    @BsonProperty("title") val title: String,
    @BsonProperty("description") val description: String?,
    @BsonProperty("state_id") val stateId: String,
    @BsonProperty("assigned_to_user_id") val assignedToUserId: String,
    @BsonProperty("create_by_user_id") val createByUserId: String,
)