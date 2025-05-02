package domain.usecase.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.permission.assignPermissions
import com.berlin.domain.repository.AuthenticationRepository
import data.UserCache

class AuthenticateUserUseCase(
    private val repository: AuthenticationRepository,
    private val hashingPassword: HashingPassword
) {
    fun login(userName: String, password: String): Result<com.berlin.domain.model.User> {
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
                val updateUser = user.copy(permission = assignPermissions(user.role))
                UserCache.currentUser = updateUser
                Result.success(user)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}
