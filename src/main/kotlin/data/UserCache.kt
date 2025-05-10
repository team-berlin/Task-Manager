package data

import com.berlin.domain.model.Permission
import com.berlin.domain.model.User
import com.berlin.domain.permission.assignPermissions

class UserCache(
    user: User,
    var currentPermission: Permission = assignPermissions(user.role)
) {
    var currentUser: User = user
}