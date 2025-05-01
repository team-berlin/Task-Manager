package domain.logic.usecase.authService
import com.berlin.domain.logic.repositories.AuthenticationRepository
import com.google.common.truth.Truth.assertThat
import domain.usecase.authService.GettingUsersLoggedInUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GettingUsersLoggedInUseCaseTest{
 private lateinit var repository: AuthenticationRepository
 private lateinit var gettingUsersLoggedInUseCase: GettingUsersLoggedInUseCase
 @BeforeEach
 fun setup(){
  repository= mockk()
  gettingUsersLoggedInUseCase= GettingUsersLoggedInUseCase(repository)
 }
  @Test
  fun `getCurrentUser should return null when there is no one log in the system`() {
   //Given
   every { repository.getCurrentUser() } returns null

   //when
   val result = gettingUsersLoggedInUseCase.getCurrentUser()
   println(result)
   //Then
   assertThat(result).isNull()
  }

  @Test
  fun `getCurrentUser should return users when they logged in the system`() {
   //Given
   every { repository.getCurrentUser() } returns AuthServiceTestData.adminIsFirstUser

   //when
   val result = gettingUsersLoggedInUseCase.getCurrentUser()

   //Then
   assertThat(result).isEqualTo(listOf(AuthServiceTestData.adminIsFirstUser))
  }

 }