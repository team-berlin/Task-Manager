package domain.usecase.authService
import com.berlin.domain.logic.InvalidCredentialsException
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.berlin.domain.model.User
import domain.helper.HashingPassword

class CreationOfMateUseCase(
    private val repository: AuthenticationRepository,
    private val hashingPassword: HashingPassword
) {
    fun createMate(userName: String, password: String): Result<User> {
        if (userName.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException("Username and password must not be empty"))
        }
        if (password.length < 8) {
            return Result.failure(InvalidCredentialsException("Password less than 8 characters"))
        }
        if (repository.getAllUsers().any { it.userName == userName }) {
            return Result.failure(InvalidCredentialsException("Username already exists"))
        }
        val hashedPassword=hashingPassword.hashPassword(password)
        return repository.createMate(userName, hashedPassword)
    }
}
