package com.steveliuyan.xtoolpro.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ModuleIdTest {
    @Test
    fun `module identifiers are stable for cross-module contracts`() {
        assertEquals("proxy", ModuleId.Proxy.value)
        assertEquals("cleaner", ModuleId.Cleaner.value)
        assertEquals("media", ModuleId.Media.value)
        assertEquals("image", ModuleId.Image.value)
    }
}
