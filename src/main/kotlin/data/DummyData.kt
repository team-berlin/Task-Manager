package com.berlin.data

import com.berlin.domain.model.*
import com.berlin.domain.model.Permission
import java.util.*

object DummyData {

    /* ------------  Static demo data  ------------ */
    val users = mutableListOf(
        User("U1", "alice", "secret", permission = Permission(), UserRole.ADMIN),
        User("U2", "bob", "secret", permission = Permission(),UserRole.MATE),
        User("U3", "carol", "secret", permission = Permission() ,UserRole.MATE)
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

    val initialDemoTasks = mutableListOf(
        Task("T1", "P1", "Implement API Layer", "Set up all base services", "S1", "U2", "U1"),
        Task("T2", "P2", "Design Login UI", "Simple and clean login screen", "S5", "U3", "U1"),
        Task("T3", "P2", "Integrate Firebase", "User authentication backend", "S6", "U2", "U1")
    )



    /* ------------  Mutable data  ------------ */
    val tasks: MutableList<Task> = Collections.synchronizedList(mutableListOf())
}
