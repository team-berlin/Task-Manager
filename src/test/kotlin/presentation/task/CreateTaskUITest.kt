package presentation.task

import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.usecase.task.CreateTaskUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.berlin.data.DummyData
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer
import org.berlin.presentation.task.CreateTaskUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class CreateTaskUITest {

    private val printed = mutableListOf<String>()

    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()

    private val createUC: CreateTaskUseCase = mockk()

    private val currentUser = DummyData.users.first()

    private lateinit var ui: CreateTaskUI

    @BeforeEach
    fun reset() {
        DummyData.tasks.clear()
        printed.clear()
        ui = CreateTaskUI(createUC, currentUser, viewer, reader)
    }


    @Test
    fun `creates task and prints success`() {
        every { reader.read() } returnsMany listOf("1", "1", "1", "Feature X", "")

        every { createUC.invoke(any(), any(), any(), any(), any(), any()) } answers {
            // arguments = 0:projectId 1:title 2:desc 3:stateId 4:creator 5:assignee
            val projectId = firstArg<String>()
            val title = secondArg<String>()
            val desc = arg<String?>(2)
            val stateId = arg<String>(3)
            val creator = arg<User>(4)
            val assignee = arg<User>(5)

            Result.success(
                Task(
                    id = "T42",
                    projectId = projectId,
                    title = title,
                    description = desc,
                    stateId = stateId,
                    assignedToUserId = assignee.id,
                    createByUserId = creator.id,
                )
            )
        }

        ui.run()

        verify(exactly = 1) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last()).contains("Task created")
    }

    @Test
    fun `prints Cancelled when user aborts`() {
        every { reader.read() } returns "X"

        ui.run()

        verify { createUC wasNot Called }
        assertThat(printed.last()).contains("Cancelled")
    }

    @Test
    fun `empty title shows validation message`() {
        every { reader.read() } returnsMany listOf("1", "1", "1", "")
        ui.run()

        verify { createUC wasNot Called }
        assertThat(printed.last()).contains("Title cannot be empty")
    }

    @Test
    fun `failure from use case is printed`() {
        every { reader.read() } returnsMany listOf("1", "1", "1", "Bug fix", "")
        every { createUC.invoke(any(), any(), any(), any(), any(), any()) } returns
                Result.failure(IllegalStateException("db down"))

        ui.run()

        verify { createUC.invoke(any(), any(), any(), any(), any(), any()) }
        assertThat(printed.last()).contains("{it.message}")
    }

    @Test
    fun `invalid user index prints error message`() {
        every { reader.read() } returnsMany listOf("1", "1", "99")

        ui.run()

        verify { createUC wasNot Called }
        assertThat(printed.last().lowercase()).contains("out of range")
    }

    @Test
    fun `invalid state index prints error message`() {
        every { reader.read() } returnsMany listOf("1", "57")

        ui.run()

        verify { createUC wasNot Called }
        assertThat(printed.last().lowercase()).contains("out of range")
    }

    @Test
    fun `invalid project index prints error message`() {
        every { reader.read() } returns "79"

        ui.run()

        verify { createUC wasNot Called }
        assertThat(printed.last().lowercase()).contains("out of range")
    }


    @Test
    fun `null description covers last missing branches`() {
        every { reader.read() } returnsMany listOf("1", "1", "1", "Doc", null)

        every { createUC.invoke(any(), any(), null, any(), any(), any()) } returns Result.success(
            Task(
                id = "T99",
                projectId = "P1",
                title = "Doc",
                description = null,
                stateId = "S1",
                assignedToUserId = DummyData.users[1].id,
                createByUserId = currentUser.id
            )
        )

        ui.run()

        verify { createUC.invoke(any(), any(), null, any(), any(), any()) }
        assertThat(printed.last()).contains("Task created")
    }

    @Test
    fun `null description still creates task`() {
        every { reader.read() } returnsMany listOf("1", "1", "1", "Doc title", null)

        every { createUC.invoke(any(), any(), (null), any(), any(), any()) } returns Result.success(
            Task(
                id = "T99",
                projectId = "P1",
                title = "Doc title",
                description = null,
                stateId = "S1",
                assignedToUserId = DummyData.users[1].id,
                createByUserId = currentUser.id
            )
        )

        ui.run()

        verify { createUC.invoke(any(), any(), (null), any(), any(), any()) }
        assertThat(printed.last()).contains("Task created")
    }

}
