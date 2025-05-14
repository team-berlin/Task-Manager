package domain.usecase.auth_service

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import data.UserCache

class LoginUserUseCase(
    private val userCache: UserCache,
    private val repository: AuthenticationRepository,
    private val hashingString: HashingString
) {
    operator fun invoke(userName: String, password: String): User {
        if (userName.isEmpty() || password.isEmpty()) {
            throw InvalidCredentialsException("No user found")
        }

        val hashedPassword = hashingString.hashPassword(password)
        val user = repository.login(userName, hashedPassword)
        userCache.currentUser = user
        return user
    }
}
