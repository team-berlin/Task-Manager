package com.berlin.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class Project(
    @BsonId val id: String,
    val name: String,
    val description: String?,
    @BsonProperty("states_id") val statesId: List<String>?,
    @BsonProperty("tasks_id") val tasksId: List<String>?
)