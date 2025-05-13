package com.berlin.data

import com.berlin.domain.model.*
import com.berlin.domain.model.user.User
import java.util.*

object DummyData : BaseDataSource<Task> {

    /* ------------  Static demo data  ------------ */
    val users = mutableListOf(
        User("U1", "alice", User.UserRole.ADMIN),
        User("U2", "bob", User.UserRole.MATE),
        User("U3", "carol",  User.UserRole.MATE)
    )

    val projects = mutableListOf(
        Project("P1", "Berlin Core", "The back-end", listOf("S1", "S2", "S3", "S4"), mutableListOf()),
        Project("P2", "Android App", "Customer mobile app", listOf("S5", "S6", "S7"), mutableListOf()),
        Project("P3", "Web Admin", null, emptyList(), mutableListOf())
    )

    val states = mutableListOf(
        TaskState("Q1","Menna","P5"),
        TaskState("S1", "TODO", "P1"),
        TaskState("S2", "IN_PROGRESS", "P1"),
        TaskState("S3", "REVIEW", "P1"),
        TaskState("S4", "DONE", "P1"),
        TaskState("S5", "IDEA", "P2"),
        TaskState("S6", "DEV", "P2"),
        TaskState("S7", "QA", "P2")
    )

    val initialDemoTasks = mutableListOf(
        Task("T1", "P1", "Implement API Layer", "Set up all base services", "S1", "U2", "U1"),
        Task("T2", "P2", "Design Login UI", "Simple and clean login screen", "S5", "U3", "U1"),
        Task("T3", "P2", "Integrate Firebase", "User authentication backend", "S6", "U2", "U1")
    )

    /* ------------  Mutable in-memory store  ------------ */
    // tests do `DummyData.tasks.clear()` in @BeforeEach
    val tasks: MutableList<Task> = Collections.synchronizedList(mutableListOf())

    /* ------------  BaseDataSource<Task> implementation  ------------ */

    override fun write(item: Task): Boolean =
        tasks.add(item)

    override fun writeAll(entities: List<Task>): Boolean =
        tasks.addAll(entities)

    override fun getById(id: String): Task? =
        tasks.firstOrNull { it.id == id }

    override fun update(id: String, item: Task): Boolean {
        val idx = tasks.indexOfFirst { it.id == id }
        return if (idx >= 0) {
            tasks[idx] = item
            true
        } else {
            false
        }
    }

    override fun delete(id: String): Boolean =
        tasks.removeIf { it.id == id }

    override fun getAll(): List<Task> =
        // return a snapshot so callers can’t mutate directly
        tasks.toList()
}
