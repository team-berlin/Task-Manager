package data
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole

 class InMemoryAuthData : AuthenticationRepository {
    private val listOfUser = mutableListOf<User>()

    override fun login(userName: String, password: String): Result<User> {
        val user = listOfUser.find { it.userName == userName && it.password == password }
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override fun createMate(userName: String, password: String): Result<User> {
        val newUser = User((listOfUser.size + 1).toString(), userName, password, UserRole.MATE)
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