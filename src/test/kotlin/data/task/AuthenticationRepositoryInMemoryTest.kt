//package com.berlin.data.task
//
//import com.berlin.data.AuthDummyData
//import com.berlin.data.BaseDataSource
//import com.berlin.data.dto.UserDto
//import com.berlin.data.mapper.UserMapper
//import com.berlin.data.repository.AuthenticationRepositoryImpl
//import com.berlin.domain.exception.UserNotFoundException
//import com.berlin.domain.helper.AuthServiceTestData
//import com.berlin.domain.model.user.User
//import com.berlin.domain.model.UserRole
//import data.UserCache
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import com.google.common.truth.Truth.assertThat
//import io.mockk.mockk
//
//class AuthenticationRepositoryInMemoryTest {
//
//    private val cachedUser = User("user1234", "admin", UserRole.ADMIN)
//
//    private lateinit var repoWithCache: AuthenticationRepositoryImpl
//    private lateinit var repoWithoutCache: AuthenticationRepositoryImpl
//    private val userDataSource: BaseDataSource<UserDto> = mockk()
//
//    @BeforeEach
//    fun setup() {
//        val userMapper: UserMapper = mockk()
//        AuthDummyData.users.clear()
//
//        repoWithCache = AuthenticationRepositoryImpl(
//            userCache      = UserCache(cachedUser),
//            userDataSource,
//            userMapper
//        )
//
//        repoWithoutCache = AuthenticationRepositoryImpl(
//            userCache      = UserCache(cachedUser),
//            userDataSource = userDataSource,
//            userMapper
//        )
//    }
//
//    @Test
//    fun `login succeeds with exactly matching credentials`() {
//        val expected = AuthServiceTestData.expectedUser
//        repoWithoutCache.createMate(expected)
//
//        val result = repoWithoutCache.login(expected.userName, expected.password)
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(expected)
//    }
//
//    @Test
//    fun `login fails with wrong credentials`() {
//        repoWithoutCache.createMate(AuthServiceTestData.expectedUser)
//
//        val result = repoWithoutCache.login("wrongUser", "wrongPass")
//
//        assertThat(result.isFailure).isTrue()
//    }
//
//    @Test
//    fun `createMate persists and returns the new user`() {
//        val newUser = User("id-foo", "bar", "baz", UserRole.MATE)
//
//        val result = repoWithoutCache.createMate(newUser)
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(newUser)
//        assertThat(AuthDummyData.users).contains(newUser)
//    }
//
//    @Test
//    fun `getUserById returns user when exists`() {
//        val u = AuthServiceTestData.expectedUser
//        repoWithoutCache.createMate(u)
//
//        val result = repoWithoutCache.getUserById(u.id)
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(u)
//    }
//
//    @Test
//    fun `getUserById fails when user does not exist`() {
//        val missingId = "does-not-exist"
//
//        val result = repoWithoutCache.getUserById(missingId)
//
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull())
//            .isInstanceOf(UserNotFoundException::class.java)
//        assertThat(result.exceptionOrNull()?.message)
//            .isEqualTo(missingId)
//    }
//
//    @Test
//    fun `getAllUsers returns empty list when none exist`() {
//        val result = repoWithoutCache.getAllUsers()
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEmpty()
//    }
//
//    @Test
//    fun `getAllUsers returns all users when some exist`() {
//        val u1 = AuthServiceTestData.expectedUser
//        val u2 = User("id2", "alice", "pw", UserRole.MATE)
//        repoWithoutCache.createMate(u1)
//        repoWithoutCache.createMate(u2)
//
//        val result = repoWithoutCache.getAllUsers()
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).containsExactly(u1, u2)
//    }
//
//    @Test
//    fun `getCurrentUser returns the cached user when present`() {
//        val result = repoWithCache.getCurrentUser()
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(cachedUser)
//    }
//}
