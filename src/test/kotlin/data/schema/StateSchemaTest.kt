package data.schema

import com.berlin.data.schema.StateSchema
import com.berlin.domain.model.State
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StateSchemaTest {

 private lateinit var stateSchema: StateSchema

 @BeforeEach
 fun setup() {
  stateSchema = fakeStateSchema()
 }

 //region create object

 @Test
 fun `should throw IllegalArgumentException when try to create object with blank file name`() = runTest {
  //when //then
  assertThrows<IllegalArgumentException> {
   stateSchema = StateSchema("", listOf("a", "b", "c"))
  }
 }

 @Test
 fun `should throw IllegalArgumentException when try to create object with invalid size header`() = runTest {
  //when //then
  assertThrows<IllegalArgumentException> {
   stateSchema = StateSchema("test.csv", listOf("a", "b"))
  }
 }

 //endregion

 //region toRow

 @Test
 fun `toRow should return list of valid state attributes when valid state passed`() = runTest {
  //when
  val result = stateSchema.toRow(validState)
  //then
  assertThat(result).isEqualTo(validRow)
 }

 @Test
 fun `toRow should return empty list when invalid state passed miss id attribute`() = runTest {
  //when
  val result = stateSchema.toRow(invalidStateEmptyId)
  //then
  assertThat(result).isEmpty()
 }

 @Test
 fun `toRow should return empty list when invalid state passed miss name attribute`() = runTest {
  //when
  val result = stateSchema.toRow(invalidStateEmptyName)
  //then
  assertThat(result).isEmpty()
 }

 @Test
 fun `toRow should return empty list when invalid state passed miss projectId attribute`() = runTest {
  //when
  val result = stateSchema.toRow(invalidStateEmptyProjectId)
  //then
  assertThat(result).isEmpty()
 }

 //endregion

 //region fromRow

 @Test
 fun `fromRow should return state when valid row passed`() = runTest {
  //when
  val result = stateSchema.fromRow(validRow)
  //then
  assertThat(result).isEqualTo(validState)
 }

 @Test
 fun `fromRow should return null when invalid row passed miss id column`() = runTest {
  //when
  val result = stateSchema.fromRow(invalidRowEmptyId)
  //then
  assertThat(result).isNull()
 }

 @Test
 fun `fromRow should return null when invalid row passed miss name column`() = runTest {
  //when
  val result = stateSchema.fromRow(invalidRowEmptyName)
  //then
  assertThat(result).isNull()
 }

 @Test
 fun `fromRow should return null when invalid row passed miss projectId column`() = runTest {
  //when
  val result = stateSchema.fromRow(invalidRowEmptyProjectId)
  //then
  assertThat(result).isNull()
 }

 //endregion

 //region getId

 @Test
 fun `getId should return id of state passed`() = runTest {
  //when
  val result = stateSchema.getId(validState)
  //then
  assertThat(result).isEqualTo(validState.id)
 }

 @Test
 fun `getId should return null when state passed have empty id`() = runTest {
  //when
  val result = stateSchema.getId(invalidStateEmptyId)
  //then
  assertThat(result).isNull()
 }

 //endregion

 private fun fakeStateSchema() = StateSchema("test.csv", listOf("a", "b", "c"))

 private companion object {
  //region Some States
  val validState = State(
   id = "s1",
   name = "n1",
   projectId = "p1"
  )
  val invalidStateEmptyId = State(
   id = "",
   name = "n1",
   projectId = "p1"
  )
  val invalidStateEmptyName = State(
   id = "s1",
   name = "",
   projectId = "p1"
  )
  val invalidStateEmptyProjectId = State(
   id = "s1",
   name = "n1",
   projectId = ""
  )

  //endregion

  //region Some Rows
  val validRow = listOf(
   "s1",
   "n1",
   "p1"
  )
  val invalidRowEmptyId = listOf(
   "",
   "n1",
   "p1"
  )
  val invalidRowEmptyName = listOf(
   "s1",
   "",
   "p1"
  )
  val invalidRowEmptyProjectId = listOf(
   "s1",
   "n1",
   ""
  )

  //endregion
 }
}