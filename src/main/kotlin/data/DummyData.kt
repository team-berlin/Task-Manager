package com.berlin.data

import com.berlin.domain.model.Permission
import com.berlin.domain.model.Project
import com.berlin.domain.model.State
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import java.util.Collections

object DummyData {

    /* ------------  Static demo data  ------------ */
    val users = mutableListOf(
        User("U1", "alice", "secret", permission = Permission(), UserRole.ADMIN),
        User("U2", "bob", "secret", permission = Permission(),UserRole.MATE),
        User("U3", "carol", "secret", permission = Permission() ,UserRole.MATE),
        User("U1", "fatma", "secret12345", permission = Permission(), UserRole.ADMIN),
    )

    val projects = mutableListOf(
        Project("P1", "Berlin Core", "The back-end", listOf("S1", "S2", "S3", "S4"), mutableListOf()),
        Project("P2", "Android App", "Customer mobile app", listOf("S5", "S6", "S7"), mutableListOf()),
        Project("P3", "Web Admin", null, emptyList(), mutableListOf())
    )

    val states = mutableListOf(
        State("S1", "TODO", "P1"),
        State("S2", "IN_PROGRESS", "P1"),
        State("S3", "REVIEW", "P1"),
        State("S4", "DONE", "P1"),
        State("S5", "IDEA", "P2"),
        State("S6", "DEV", "P2"),
        State("S7", "QA", "P2")
    )

    /* ------------  Mutable data  ------------ */
    val tasks: MutableList<Task> = Collections.synchronizedList(mutableListOf())
}
