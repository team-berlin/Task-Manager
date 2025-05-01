package logic.usecase.authService
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.berlin.domain.model.User

class FetchAllUsersUseCase(
    private val repository: AuthenticationRepository
) {
    fun getAllUsers():List<User>{
        return repository.getAllUsers()
    }
}