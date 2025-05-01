package com.berlin.model


data class Task(
    val id:String,
    val projectId:String,
    val title:String,
    val description:String?,
    val stateId:String,
    val assignedTo: String,
    val createBy: String
)
