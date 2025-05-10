package com.berlin.domain.permission

import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class AssignPermissionsKtTest {

    @Test
    fun `assignPermissions should return correct permissions for MATE role`() {
        val permissions = assignPermissions(UserRole.MATE)

        assertThat(permissions.createTask).isTrue()
        assertThat(permissions.updateTask).isTrue()
        assertThat(permissions.deleteTask).isTrue()
        assertThat(permissions.viewAuditLogs).isTrue()
        assertThat(permissions.createProject).isFalse()
        assertThat(permissions.updateProject).isFalse()
        assertThat(permissions.deleteProject).isFalse()
        assertThat(permissions.assignTask).isFalse()
    }

    @Test
    fun `assignPermissions should return correct permissions for ADMIN role`() {
        val permissions = assignPermissions(UserRole.ADMIN)

        assertThat(permissions.createProject).isTrue()
        assertThat(permissions.updateProject).isTrue()
        assertThat(permissions.deleteProject).isTrue()
        assertThat(permissions.createTask).isTrue()
        assertThat(permissions.updateTask).isTrue()
        assertThat(permissions.deleteTask).isTrue()
        assertThat(permissions.assignTask).isTrue()
        assertThat(permissions.viewAuditLogs).isTrue()
    }

}

