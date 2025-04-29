package com.berlin.helper

import com.berlin.model.State

fun createStateHelper(
    id: String = "1", name: String = "TODO", projectId: String = "1"
): State {
    return State(
        id = id, name = name, projectId = projectId
    )
}