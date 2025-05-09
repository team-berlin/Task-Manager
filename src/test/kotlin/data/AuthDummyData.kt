package com.berlin.data

import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import java.util.*

object AuthDummyData: BaseDataSource<User> {
    val users: MutableList<User> = Collections.synchronizedList(mutableListOf(User("U1", "alice", "secret", UserRole.ADMIN)))

    /* ------------  BaseDataSource<Task> implementation  ------------ */

    override fun write(entity: User): Boolean =
        users.add(entity)

    override fun writeAll(entities: List<User>): Boolean =
        users.addAll(entities)

    override fun getById(id: String): User? =
        users.firstOrNull { it.id == id }

    override fun update(id: String, entity: User): Boolean {
        val idx = users.indexOfFirst { it.id == id }
        return if (idx >= 0) {
            users[idx] = entity
            true
        } else {
            false
        }
    }

    override fun delete(id: String): Boolean =
        users.removeIf { it.id == id }

    override fun getAll(): List<User> =
        users.toList()
}