package com.berlin.data

object UserIndex {
    const val ID = 0
    const val USER_NAME = 1
    const val PASSWORD = 2
    const val ROLE = 3
}

object TaskIndex {
    const val ID = 0
    const val PROJECT_ID = 1
    const val TITLE = 2
    const val DESCRIPTION = 3
    const val STATE_ID = 4
    const val ASSIGNED_TO_USER_ID = 5
    const val CREATE_BY_USER_ID = 6
}

object StateIndex {
    const val ID = 0
    const val NAME = 1
    const val PROJECT_ID = 2
}

object ProjectIndex {
    const val ID = 0
    const val NAME = 1
    const val DESCRIPTION = 2
    const val STATES_ID = 3
    const val TASKS_ID = 4
}

object AuditLogIndex {
    const val ID = 0
    const val TIMES_TAMP = 1
    const val CREATE_BY = 2
    const val AUDIT_ACTION = 3
    const val CHANGES_DESCRIPTION = 4
    const val ENTITY_TYPE = 5
    const val ENTITY_ID = 6
}