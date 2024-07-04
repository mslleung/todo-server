package io.sleekflow.infrastructure.network

import io.ktor.server.websocket.*
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

private val lastConnectionId = AtomicInteger(0)

class ConnectionManager {
    private val connections = Collections.synchronizedMap<Int, DefaultWebSocketServerSession>(HashMap())

    fun addConnection(session: DefaultWebSocketServerSession): Int {
        val connectionId = lastConnectionId.incrementAndGet()
        connections[connectionId] = session
        return connectionId
    }

    fun removeConnection(connectionId: Int) {
        connections.remove(connectionId)
    }
}