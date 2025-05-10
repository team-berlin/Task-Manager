package data.schema

import com.berlin.data.schema.TaskSchema
import com.berlin.domain.model.Task
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskSchemaTest {

    private lateinit var taskSchema: TaskSchema

    @BeforeEach
    fun setup() {
        taskSchema = fakeTaskSchema()
    }

    //region create object

    @Test
    fun `should throw IllegalArgumentException when try to create object with blank file name`() = runTest {
        //when //then
        assertThrows<IllegalArgumentException> {
            taskSchema = TaskSchema("", listOf("a", "b", "c", "d", "e", "f", "g", "h"))
        }
    }

    @Test
    fun `should throw IllegalArgumentException when try to create object with invalid size header`() = runTest {
        //when //then
        assertThrows<IllegalArgumentException> {
            taskSchema = TaskSchema("test.csv", listOf("a", "b"))
        }
    }

    //endregion

    //region toRow

    @Test
    fun `toRow should return list of valid task attributes when valid task passed`() = runTest {
        //when
        val result = taskSchema.toRow(validTask)
        //then
        assertThat(result).isEqualTo(validRow)
    }

    @Test
    fun `toRow should return list of valid task attributes when task with empty description passed`() = runTest {
        //when
        val result = taskSchema.toRow(validTaskEmptyDescription)
        //then
        assertThat(result).isEqualTo(validRowEmptyDescription)
    }

    @Test
    fun `toRow should return empty list when invalid task passed miss id attribute`() = runTest {
        //when
        val result = taskSchema.toRow(invalidTaskEmptyId)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid task passed miss projectId attribute`() = runTest {
        //when
        val result = taskSchema.toRow(invalidTaskEmptyProjectId)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid task passed miss title attribute`() = runTest {
        //when
        val result = taskSchema.toRow(invalidTaskEmptyTitle)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid task passed miss stateId attribute`() = runTest {
        //when
        val result = taskSchema.toRow(invalidTaskEmptyStateId)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid task passed miss assign to attribute`() = runTest {
        //when
        val result = taskSchema.toRow(invalidTaskEmptyAssignTo)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid task passed miss create by attribute`() = runTest {
        //when
        val result = taskSchema.toRow(invalidTaskEmptycreateByUserId)
        //then
        assertThat(result).isEmpty()
    }

    //endregion

    //region fromRow

    @Test
    fun `fromRow should return task when valid row full passed`() = runTest {
        //when
        val result = taskSchema.fromRow(validRow)
        //then
        assertThat(result).isEqualTo(validTask)
    }

    @Test
    fun `fromRow should return task when valid row empty description passed`() = runTest {
        //when
        val result = taskSchema.fromRow(validRowEmptyDescription)
        //then
        assertThat(result).isEqualTo(validTaskEmptyDescription)
    }

    @Test
    fun `fromRow should return null when invalid row passed miss id column`() = runTest {
        //when
        val result = taskSchema.fromRow(invalidRowEmptyId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss projectId column`() = runTest {
        //when
        val result = taskSchema.fromRow(invalidRowEmptyProjectId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss title column`() = runTest {
        //when
        val result = taskSchema.fromRow(invalidRowEmptyTitle)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss stateId column`() = runTest {
        //when
        val result = taskSchema.fromRow(invalidRowEmptyStateId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss assign to column`() = runTest {
        //when
        val result = taskSchema.fromRow(invalidRowEmptyAssignToUserId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss create by column`() = runTest {
        //when
        val result = taskSchema.fromRow(invalidRowEmptyCreateByUserId)
        //then
        assertThat(result).isNull()
    }

    //endregion

    //region getId

    @Test
    fun `getId should return id of task passed`() = runTest {
        //when
        val result = taskSchema.getId(validTask)
        //then
        assertThat(result).isEqualTo(validTask.id)
    }

    @Test
    fun `getId should return null when task passed have empty id`()= runTest {
        //when
        val result = taskSchema.getId(invalidTaskEmptyId)
        //then
        assertThat(result).isNull()
    }

    //endregion

    private fun fakeTaskSchema() = TaskSchema("test.csv", listOf("a", "b", "c", "d", "e", "f", "g"))

    private companion object {

        //region Some Users id
        val testUserId ="u1"

        val testUserId2 ="u2"

        //endregion

        //region Some Tasks
        val validTask = Task(
            id = "t1",
            projectId = "p1",
            title = "task1",
            description = "desc",
            stateId = "s1",
            assignedToUserId = testUserId,
            createByUserId = testUserId2
        )
        val validTaskEmptyDescription = Task(
            id = "t1",
            projectId = "p1",
            title = "task1",
            description = null,
            stateId = "s1",
            assignedToUserId = testUserId,
            createByUserId = testUserId2,
        )
        val invalidTaskEmptyId = Task(
            id = "",
            projectId = "p1",
            title = "task1",
            description = "desc",
            stateId = "s1",
            assignedToUserId = testUserId,
            createByUserId = testUserId2,
        )
        val invalidTaskEmptyProjectId = Task(
            id = "t1",
            projectId = "",
            title = "task1",
            description = "desc",
            stateId = "s1",
            assignedToUserId = testUserId,
            createByUserId = testUserId2,
        )
        val invalidTaskEmptyTitle = Task(
            id = "t1",
            projectId = "p1",
            title = "",
            description = "desc",
            stateId = "s1",
            assignedToUserId = testUserId,
            createByUserId = testUserId2,
        )
        val invalidTaskEmptyStateId = Task(
            id = "t1",
            projectId = "p1",
            title = "task1",
            description = "desc",
            stateId = "",
            assignedToUserId = testUserId,
            createByUserId = testUserId2,
        )
        val invalidTaskEmptyAssignTo = Task(
            id = "t1",
            projectId = "p1",
            title = "task1",
            description = "desc",
            stateId = "s1",
            assignedToUserId = "",
            createByUserId = testUserId2,
        )
        val invalidTaskEmptycreateByUserId = Task(
            id = "t1",
            projectId = "p1",
            title = "task1",
            description = "desc",
            stateId = "s1",
            assignedToUserId = testUserId,
            createByUserId = "",
        )

        //endregion

        //region Some Rows

        val validRow = listOf(
            "t1", "p1", "task1", "desc", "s1", "u1", "u2"
        )
        val validRowEmptyDescription = listOf(
            "t1", "p1", "task1", "", "s1", "u1", "u2"
        )
        val invalidRowEmptyId = listOf(
            "", "p1", "task1", "desc", "s1", "u1", "u2"
        )
        val invalidRowEmptyProjectId = listOf(
            "t1", "", "task1", "desc", "s1", "u1", "u2"
        )
        val invalidRowEmptyTitle = listOf(
            "t1", "p1", "", "desc", "s1", "u1", "u2"
        )
        val invalidRowEmptyStateId = listOf(
            "t1", "p1", "task1", "desc", "", "u1", "u2"
        )
        val invalidRowEmptyAssignToUserId = listOf(
            "t1", "p1", "task1", "desc", "s1", "", "u2"
        )
        val invalidRowEmptyCreateByUserId = listOf(
            "t1", "p1", "task1", "desc", "s1", "u1", ""
        )

        //endregion
    }
}