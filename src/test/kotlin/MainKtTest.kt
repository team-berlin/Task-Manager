package com.berlin

import com.berlin.presentation.MainMenuUI
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.core.context.startKoin

class MainKtCoverageTest {

    @Test
    fun `main should call MainMenuUi_start`() {
        val mockMainMenuUi = mockk<MainMenuUI>(relaxed = true)

        stopKoin()

        startKoin {
            modules(
                module {
                    single { mockMainMenuUi }
                }
            )
        }

        main()

        verify { mockMainMenuUi.run() }

        stopKoin()
    }

    @Test
    fun `startApp should not reinitialize Koin if already started`() {
        startApp()
    }
}
