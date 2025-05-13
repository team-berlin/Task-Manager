package com.berlin.data.dto

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class TaskStateDto(
    @BsonId val id: String,
    @BsonProperty("name") val name: String,
    @BsonProperty("project_id") val projectId: String,
)