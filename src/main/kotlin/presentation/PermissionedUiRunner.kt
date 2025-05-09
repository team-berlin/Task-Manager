package com.berlin.presentation

import com.berlin.domain.model.Permission

interface PermissionedUiRunner : UiRunner {
    fun isAllowed(permission: Permission): Boolean
}
