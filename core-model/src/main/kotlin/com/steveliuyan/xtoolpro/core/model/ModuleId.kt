package com.steveliuyan.xtoolpro.core.model

@JvmInline
value class ModuleId private constructor(val value: String) {
    companion object {
        val Proxy = ModuleId("proxy")
        val Cleaner = ModuleId("cleaner")
        val Media = ModuleId("media")
        val Image = ModuleId("image")
    }
}
