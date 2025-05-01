package logic.permission

import com.berlin.domain.model.UserRole
import com.berlin.logic.permission.assignPermissions
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class AssignPermissionsKtTest {

    @Test
    fun `assignPermissions should return correct permissions for MATE role`() {
        val permissions = assignPermissions(UserRole.MATE)

        assertThat(permissions.createTask).isTrue()
        assertThat(permissions.editTask).isTrue()
        assertThat(permissions.deleteTask).isTrue()
        assertThat(permissions.viewAuditLogs).isTrue()

        // Permissions that should be false for MATE
        assertThat(permissions.createProject).isFalse()
        assertThat(permissions.editProject).isFalse()
        assertThat(permissions.deleteProject).isFalse()
        assertThat(permissions.assignTask).isFalse()
    }
    @Test
    fun `assignPermissions should return correct permissions for ADMIN role`() {
        val permissions = assignPermissions(UserRole.ADMIN)

        assertThat(permissions.createProject).isTrue()
        assertThat(permissions.editProject).isTrue()
        assertThat(permissions.deleteProject).isTrue()
        assertThat(permissions.createTask).isTrue()
        assertThat(permissions.editTask).isTrue()
        assertThat(permissions.deleteTask).isTrue()
        assertThat(permissions.assignTask).isTrue()
        assertThat(permissions.viewAuditLogs).isTrue()
    }
}

