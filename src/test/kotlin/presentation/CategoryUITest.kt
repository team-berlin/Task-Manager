package com.berlin.presentation

import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.permission.assignPermissions
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CategoryUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var userCache: UserCache
    private lateinit var categoryUI: CategoryUI

    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        userCache = UserCache(User("U1", "alice", "pw", UserRole.ADMIN))
        userCache.currentUser = userCache.currentUser
        userCache.currentPermission = assignPermissions(userCache.currentUser.role)
    }

    @Test
    fun `when no allowed children shows message and exits`() {
        val child = mockk<PermissionedUiRunner> {
            every { isAllowed(any()) } returns false
            // stub run() so mockk won't complain, though it should never be called
            every { run() } just Runs
        }

        categoryUI = CategoryUI(
            id = 42, label = "Foo", children = listOf(child), viewer = viewer, reader = reader, userCache = userCache
        )

        categoryUI.run()

        assertThat(printed).containsExactly("No available actions in Foo.")
    }

    @Test
    fun `with one allowed child shows menu and exits on X`() {
        val child = mockk<PermissionedUiRunner> {
            every { id } returns 2
            every { label } returns "Bar"
            every { isAllowed(any()) } returns true
            every { run() } just Runs
        }
        every { reader.read() } returns "X"

        categoryUI = CategoryUI(
            id = 5, label = "Foo", children = listOf(child), viewer = viewer, reader = reader, userCache = userCache
        )

        categoryUI.run()

        assertThat(printed).containsExactly(
            "=== Foo ===", "2 – Bar", "X – Back"
        ).inOrder()
    }

    @Test
    fun `runs allowed child then exits`() {
        val child = mockk<PermissionedUiRunner> {
            every { id } returns 3
            every { label } returns "Baz"
            every { isAllowed(any()) } returns true
            every { run() } just Runs
        }
        every { reader.read() } returnsMany listOf("3", "X")

        categoryUI = CategoryUI(
            id = 7, label = "Qux", children = listOf(child), viewer = viewer, reader = reader, userCache = userCache
        )

        categoryUI.run()

        verify(exactly = 1) { child.run() }
    }

    @Test
    fun `invalid choice prints error then exits`() {
        val child = mockk<PermissionedUiRunner> {
            every { id } returns 1
            every { label } returns "One"
            every { isAllowed(any()) } returns true
            every { run() } just Runs
        }
        every { reader.read() } returnsMany listOf("99", "X")

        categoryUI = CategoryUI(
            id = 8, label = "Test", children = listOf(child), viewer = viewer, reader = reader, userCache = userCache
        )

        categoryUI.run()

        assertThat(printed).contains("Invalid choice")
        verify(exactly = 0) { child.run() }
    }

    @Test
    fun `exit immediately on null input`() {
        val child = mockk<PermissionedUiRunner> {
            every { id } returns 4
            every { label } returns "Four"
            every { isAllowed(any()) } returns true
            every { run() } just Runs
        }
        every { reader.read() } returns null

        categoryUI = CategoryUI(
            id = 9,
            label = "NullTest",
            children = listOf(child),
            viewer = viewer,
            reader = reader,
            userCache = userCache
        )

        categoryUI.run()

        // The menu is printed once (header + child + back), then we exit
        assertThat(printed).containsExactly(
            "=== NullTest ===", "4 – Four", "X – Back"
        ).inOrder()
        verify(exactly = 0) { child.run() }
    }

    @Test
    fun `exit immediately on blank input`() {
        val child = mockk<PermissionedUiRunner> {
            every { id } returns 6
            every { label } returns "Six"
            every { isAllowed(any()) } returns true
            every { run() } just Runs
        }
        every { reader.read() } returns ""

        categoryUI = CategoryUI(
            id = 11,
            label = "BlankTest",
            children = listOf(child),
            viewer = viewer,
            reader = reader,
            userCache = userCache
        )

        categoryUI.run()

        // Same behavior: header + child + back, then exit on blank
        assertThat(printed).containsExactly(
            "=== BlankTest ===", "6 – Six", "X – Back"
        ).inOrder()
        verify(exactly = 0) { child.run() }
    }
}
