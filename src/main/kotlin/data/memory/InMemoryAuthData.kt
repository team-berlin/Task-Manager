package com.berlin.data.memory
import com.berlin.domain.model.UserRole
import com.berlin.domain.permission.assignPermissions
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.logic.generateIdHelper.IdGenerator
import com.berlin.logic.generateIdHelper.IdGeneratorImplementation
import com.berlin.model.User
import data.UserCache

class InMemoryAuthRepositoryImpl : AuthenticationRepository {
    private val listOfUser = mutableListOf<User>()
    private val userId: IdGenerator = IdGeneratorImplementation()

    override fun login(userName: String, password: String): Result<User> {
        val user = listOfUser.find { it.userName == userName && it.password == password }
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override fun createMate(userName: String, password: String): Result<User> {
        val newUser = User(
          id = userId.generateId(""),
            userName = userName,
            password =  password,
            permission = assignPermissions(UserRole.MATE),
            role = UserRole.MATE
        )
        listOfUser.add(newUser)
        return Result.success(newUser)
    }

    override fun getUserById(userId: String): User? {
        if (userId.isEmpty()) return null
        val user = listOfUser.find { it.id == userId }
        return user
    }

    override fun getAllUsers(): List<User> {
        return listOfUser
    }

    override fun getCurrentUser(): User? {
        return UserCache.currentUser
    }
}