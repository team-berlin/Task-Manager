//package com.berlin.data.task
//
//import com.berlin.data.BaseDataSource
//import com.berlin.domain.exception.InvalidTaskException
//import com.berlin.domain.exception.TaskNotFoundException
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.User
//import com.berlin.domain.model.UserRole
//import com.google.common.truth.Truth.assertThat
//import com.berlin.data.DummyData
//import com.berlin.data.mapper.TaskMapper
//import com.berlin.data.repository.TaskRepositoryImpl
//import data.UserCache
//import io.mockk.mockk
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class TaskRepositoryImplTest {
//
//    private lateinit var repo: TaskRepositoryImpl
//
//    private val alice = User("U1", "alice", UserRole.MATE)
//    private val bob = User("U2", "bob", UserRole.MATE)
//
//    @BeforeEach
//    fun setUp() {
//        val taskMapper: TaskMapper = mockk()
//        val userCache: UserCache = mockk()
//        DummyData.tasks.clear()
//        repo = TaskRepositoryImpl(userCache,DummyData, taskMapper)
//    }
//
//    @Test
//    fun `create succeeds for new id`() {
//        val result = repo.createTask(task("1"))
//        assertThat(result.isSuccess).isTrue()
//        assertThat(repo.getAllTasks()).hasSize(1)
//    }
//
//    @Test
//    fun `create allows duplicate id (appends second entry)`() {
//        repo.createTask(task("1"))
//        val result2 = repo.createTask(task("1"))
//        assertThat(result2.isSuccess).isTrue()
//
//        val matches = repo.getAllTasks().filter { it.id == "1" }
//        assertThat(matches).hasSize(2)
//    }
//
//    @Test
//    fun `findById returns task when present`() {
//        val t = task("1")
//        repo.createTask(t)
//
//        val result = repo.getTaskById("1")
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(t)
//    }
//
//    @Test
//    fun `findById fails when absent`() {
//        val result = repo.getTaskById("42")
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
//    }
//
//    @Test
//    fun `update succeeds for existing task`() {
//        val original = task("1")
//        repo.createTask(original)
//
//        val changed = original.copy(title = "New title")
//        val result = repo.updateTask(changed)
//
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).isEqualTo(changed)
//    }
//
//    @Test
//    fun `update fails when task not found`() {
//        val result = repo.updateTask(task("1"))
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidTaskException::class.java)
//    }
//
//    @Test
//    fun `findTasksByProjectId returns matching list`() {
//        val t1 = task("1", projectId = "P1")
//        val t2 = task("2", projectId = "P1")
//        val t3 = task("3", projectId = "P2")
//        repo.createTask(t1); repo.createTask(t2); repo.createTask(t3)
//
//        val result = repo.getTasksByProjectId("P1")
//        assertThat(result.isSuccess).isTrue()
//        assertThat(result.getOrNull()).containsExactly(t1, t2)
//    }
//
//    @Test
//    fun `delete succeeds when task exists`() {
//        repo.createTask(task("1"))
//
//        val result = repo.deleteTask("1")
//        assertThat(result.isSuccess).isTrue()
//        assertThat(repo.getAllTasks()).isEmpty()
//    }
//
//    @Test
//    fun `delete fails when task does not exist`() {
//        val result = repo.deleteTask("46")
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
//    }
//
//    private fun task(
//        id: String,
//        projectId: String = "P1",
//        title: String = "Title $id",
//    ) = Task(
//        id = id,
//        projectId = projectId,
//        title = title,
//        description = null,
//        stateId = "TODO",
//        assignedToUserId = bob.id,
//        createByUserId = alice.id
//    )
//
//    @Test
//    fun `create fails when write returns false`() {
//        val failingDs = object : BaseDataSource<Task> {
//            override fun write(item: Task) = false
//            override fun writeAll(entities: List<Task>) = false
//            override fun getById(id: String): Task? = null
//            override fun update(id: String, item: Task) = false
//            override fun delete(id: String) = false
//            override fun getAll(): List<Task> = emptyList()
//        }
//
//        val repo = TaskRepositoryImpl(failingDs)
//        val t = Task(
//            id = "X",
//            projectId = "P1",
//            title = "won't matter",
//            description = null,
//            stateId = "TODO",
//            assignedToUserId = "U2",
//            createByUserId = "U1"
//        )
//
//        val result = repo.createTask(t)
//
//        assertThat(result.isFailure).isTrue()
//        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidTaskException::class.java)
//    }
//
//    @Test
//    fun `writeAll on data source adds all tasks and is reflected in repo`() {
//        DummyData.tasks.clear()
//
//        val t1 = task("bulk1")
//        val t2 = task("bulk2")
//
//        val wrote = DummyData.writeAll(listOf(t1, t2))
//        assertThat(wrote).isTrue()
//
//        val all = repo.getAllTasks()
//        assertThat(all).containsExactly(t1, t2)
//    }
//}
