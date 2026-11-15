package com.pararam2006.tseletrack.data.demo

import com.pararam2006.tseletrack.core.Role

class DemoModeStore {
    var role: Role? = null
        private set

    val isDemo: Boolean
        get() = role != null

    fun startDemo(role: Role) {
        this.role = role
    }

    fun stopDemo() {
        role = null
    }
}
