package com.berlin.domain.model

data class Task(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String?,
    val stateId: String,
    val assignedToUserId: String,
    val createByUserId: String,
)