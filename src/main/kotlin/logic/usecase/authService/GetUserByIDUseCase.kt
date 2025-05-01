package logic.usecase.authService

import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.berlin.domain.model.User

class GetUserByIDUseCase(
    private val repository: AuthenticationRepository
) {
    fun getUserById(id:String): User? {
        if (id.isEmpty())
            throw IllegalArgumentException("User ID must not be blank.")

        return repository.getUserById(id)
    }
}