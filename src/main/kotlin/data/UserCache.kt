package data

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.UserDto
import com.berlin.data.mapper.UserMapper
import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.permission.assignPermissions

class UserCache(
    user: User,
    var currentPermission: Permission = assignPermissions(user.role)
) {
    var currentUser: User = user
}

class AdminUserProvider(
    private val userDtoSource: BaseDataSource<UserDto>,
    private val mapper: UserMapper
) {
    fun load(): User {
        val dto = userDtoSource
            .getAll()
            .firstOrNull { it.role == User.UserRole.ADMIN }
            ?: throw IllegalStateException("No ADMIN user found")
        return mapper.mapToDomainModel(dto)
    }
}
