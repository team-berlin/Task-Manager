package com.berlin.helper

import com.berlin.model.Project

fun projectHelper(
    id: String = "123",
    name: String = "TODO",
    description:String? = null,
    statesId:List<String>? = listOf("1","2"),
    tasksId:List<String>? = listOf("3","4")
): Project {
    return Project(
        id = id,
        title = name,
        description = description,
        statesId = statesId,
        tasksId = tasksId
    )
}