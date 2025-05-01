package domain.usecase.authService

import com.berlin.domain.logic.InvalidCredentialsException
import com.berlin.domain.logic.repositories.AuthenticationRepository
import domain.helper.HashingPassword
import com.berlin.domain.model.User
import data.UserCache

class AuthenticateUserUseCase(
    private val repository: AuthenticationRepository,
    private val hashingPassword: HashingPassword
) {
    fun login(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty())
            return Result.failure(InvalidCredentialsException("No user found"))

        if (repository.getAllUsers().isEmpty())
            return Result.failure(InvalidCredentialsException("No account"))

        val cachedUser = UserCache.currentUser
        if (cachedUser != null && cachedUser.userName == userName)
            return Result.success(cachedUser)

        val hashedPassword=hashingPassword.hashPassword(password)
        val userResult = repository.login(userName, hashedPassword)

        return userResult.fold(
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
