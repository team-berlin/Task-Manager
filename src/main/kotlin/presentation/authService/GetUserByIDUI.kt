package com.berlin.presentation.authService

import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.UiRunner
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

class GetUserByIDUI(
    private val getUserByIDUseCase: GetUserByIDUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) : UiRunner {
    override val id: Int = 900
    override val label: String = "get user by id"
    override fun run() {
        try {
            viewer.show("Enter the user id: ")
            val id=(reader.read()).toString()
            getUserByIDUseCase.getUserById(id)
        }catch (_:Exception){

        }

    }
}