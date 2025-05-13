package com.berlin.data.schema

import com.berlin.data.csv_data_source.schema.StateSchema
import com.berlin.data.dto.TaskStateDto
import com.berlin.domain.model.TaskState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskStateSchemaTest {

 private lateinit var stateSchema: StateSchema

 @BeforeEach
 fun setup() {
  stateSchema = fakeStateSchema()
 }

 //region create object

 @Test
 fun `should throw IllegalArgumentException when try to create object with blank file name`() {
  //when //then
  assertThrows<IllegalArgumentException> {
   stateSchema = StateSchema("", listOf("a", "b", "c"))
  }
 }

 @Test
 fun `should throw IllegalArgumentException when try to create object with invalid size header`() {
  //when //then
  assertThrows<IllegalArgumentException> {
   stateSchema = StateSchema("test.csv", listOf("a", "b"))
  }
 }

 //endregion

 //region toRow

 @Test
 fun `toRow should return list of valid state attributes when valid state passed`() {
  //when
  val result = stateSchema.toRow(validStateDto)
  //then
  assertThat(result).isEqualTo(validRow)
 }

 @Test
 fun `toRow should return empty list when invalid state passed miss id attribute`() {
  //when
  val result = stateSchema.toRow(invalidStateDto)
  //then
  assertThat(result).isEmpty()
 }

 @Test
 fun `toRow should return empty list when invalid state passed miss name attribute`() {
  //when
  val result = stateSchema.toRow(invalidStateDtonName)
  //then
  assertThat(result).isEmpty()
 }

 @Test
 fun `toRow should return empty list when invalid state passed miss projectId attribute`() {
  //when
  val result = stateSchema.toRow(invalidStateDto)
  //then
  assertThat(result).isEmpty()
 }

 //endregion

 //region fromRow

 @Test
 fun `fromRow should return state when valid row passed`() {
  //when
  val result = stateSchema.fromRow(validRow)
  //then
  assertThat(result).isEqualTo(validStateDto)
 }

 @Test
 fun `fromRow should return null when invalid row passed miss id column`() {
  //when
  val result = stateSchema.fromRow(invalidRowEmptyId)
  //then
  assertThat(result).isNull()
 }

 @Test
 fun `fromRow should return null when invalid row passed miss name column`() {
  //when
  val result = stateSchema.fromRow(invalidRowEmptyName)
  //then
  assertThat(result).isNull()
 }

 @Test
 fun `fromRow should return null when invalid row passed miss projectId column`() {
  //when
  val result = stateSchema.fromRow(invalidRowEmptyProjectId)
  //then
  assertThat(result).isNull()
 }

 //endregion

 //region getId

 @Test
 fun `getId should return id of state passed`() {
  //when
  val result = stateSchema.getId(validStateDto)
  //then
  assertThat(result).isEqualTo(validState.id)
 }

 @Test
 fun `getId should return null when state passed have empty id`() {
  //when
  val result = stateSchema.getId(invalidStateDto)
  //then
  assertThat(result).isNull()
 }

 //endregion

 private fun fakeStateSchema() = StateSchema("test.csv", listOf("a", "b", "c"))

 private companion object {
  val invalidStateDto = TaskStateDto(
  id = "",
  name = "n1",
  projectId = "p1"
  )
  val invalidStateDtonName = TaskStateDto(
   id = "5t",
   name = "",
   projectId = "p1"
  )
  val invalidStateDtoProjectId = TaskStateDto(
   id = "5t",
   name = "uu",
   projectId = ""
  )
  val validStateDto = TaskStateDto(
   id = "s1",
   name = "n1",
   projectId = "p1"
  )

  //region Some States
  val validState = TaskState(
   id = "s1",
   name = "n1",
   projectId = "p1"
  )
  val invalidStateEmptyId = TaskState(
   id = "",
   name = "n1",
   projectId = "p1"
  )
  val invalidStateEmptyName = TaskState(
   id = "s1",
   name = "",
   projectId = "p1"
  )
  val invalidStateEmptyProjectId = TaskState(
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