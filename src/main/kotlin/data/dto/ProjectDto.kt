package com.berlin.data.dto

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class ProjectDto(
    @BsonId val id: String,
    @BsonProperty("title") val title: String,
    @BsonProperty("description") val description: String?,
    @BsonProperty("states_id") val statesId: List<String>?,
    @BsonProperty("tasks_id") val tasksId: List<String>?
)