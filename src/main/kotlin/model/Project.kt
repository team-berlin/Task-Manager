package com.berlin.model

data class Project(
    val id:String,
    var name:String,
    var description:String?,
    val statesId:List<String>?,
    val tasksId:List<String>?
)
