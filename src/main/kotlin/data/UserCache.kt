package data

import com.berlin.domain.model.User

class UserCache(
    admin: User
) {
    var currentUser: User = admin
}