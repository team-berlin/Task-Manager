package domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache

class AuthenticateUserUseCase(
    private val userCache: UserCache,
    private val repository: AuthenticationRepository,
    private val hashingString: HashingString
) {
    fun login(userName: String, password: String): User {
        if (userName.isEmpty() || password.isEmpty()) {
            throw InvalidCredentialsException("No user found")
        }

        val cachedUser = userCache.currentUser
        if (cachedUser.userName == userName)
            return cachedUser

        val hashedPassword=hashingString.hashPassword(password)
        val user = repository.login(userName, hashedPassword)
        userCache.currentUser = user
        return user
    }
}
