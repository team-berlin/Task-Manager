package com.berlin.helper

fun stateHelper(
    name: String = "TODO", projectId: String = "1"
): Pair<String, String> {
    return (name to projectId)
}