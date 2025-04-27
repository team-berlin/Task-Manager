package com.berlin.model

data class Project(
    val id:Int,
    val name:String,
    val description:String?,
    val statesId:List<Int>,
    val tasksId:List<Int>
)
