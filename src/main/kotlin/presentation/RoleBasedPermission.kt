package com.berlin.presentation

import com.berlin.domain.model.UserRole

interface RoleBasedPermission {

    val allowedRoles: List<UserRole>
}
