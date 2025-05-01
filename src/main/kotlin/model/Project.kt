package com.berlin.model

data class Project(
    val id:String,
    val name:String,
    val description:String?,
    val statesId:List<String>?,
    val tasksId:List<String>?
)