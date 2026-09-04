package com.steveliuyan.xtoolpro.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppShellStateTest {
    @Test
    fun `initial application state is a neutral foundation state`() {
        assertEquals(AppShellState.Foundation, AppShellState.initial())
    }
}
