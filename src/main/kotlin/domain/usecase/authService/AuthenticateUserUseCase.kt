package domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.model.User
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache

class AuthenticateUserUseCase(
    private val repository: AuthenticationRepository,
    private val hashingPassword: HashingPassword
) {
    fun login(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("No user found"))
        }

        val cachedUser = UserCache.currentUser
        if (cachedUser != null && cachedUser.userName == userName)
            return Result.success(cachedUser)

        val hashedPassword=hashingPassword.hashPassword(password)
        return  repository.login(userName, hashedPassword).fold(
            onSuccess = { user ->
                UserCache.currentUser = user
                Result.success(user)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}
