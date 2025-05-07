package com.berlin.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class State(
    @BsonId val id: String,
    val name: String,
    @BsonProperty("project_id") val projectId: String,
)