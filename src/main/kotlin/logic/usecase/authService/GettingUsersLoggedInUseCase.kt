package logic.usecase.authService
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.berlin.domain.model.User

class GettingUsersLoggedInUseCase(
    private val repository: AuthenticationRepository
) {
    fun getCurrentUser(): User?{
        return repository.getCurrentUser()
    }
}