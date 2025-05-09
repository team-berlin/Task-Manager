package domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache

class AuthenticateUserUseCase(
    private val userCache: UserCache,
    private val repository: AuthenticationRepository,
    private val hashingString: HashingString
) {
    fun login(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("No user found"))
        }

        val cachedUser = userCache.currentUser
        if (cachedUser != null && cachedUser.userName == userName)
            return Result.success(cachedUser)

        val hashedPassword=hashingString.hashPassword(password)
        return  repository.login(userName, hashedPassword).fold(
            onSuccess = { user ->
                userCache.currentUser = user
                Result.success(user)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}
