package com.pararam2006.tseletrack.data.session

import com.pararam2006.tseletrack.core.Role

data class SessionIdentity(
    val id: String,
    val displayName: String,
    val role: Role,
)

class SessionIdentityStore {
    var current: SessionIdentity? = null
        private set

    fun set(identity: SessionIdentity) {
        current = identity
    }

    fun clear() {
        current = null
    }
}
