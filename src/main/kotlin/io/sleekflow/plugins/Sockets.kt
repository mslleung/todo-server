package io.sleekflow.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.logging.*
import io.sleekflow.application.CreateTodoTaskRequestHandler
import io.sleekflow.application.DeleteTodoTaskRequestHandler
import io.sleekflow.application.GetTodoTaskRequestHandler
import io.sleekflow.application.UpdateTodoTaskRequestHandler
import io.sleekflow.infrastructure.network.ConnectionManager
import io.sleekflow.infrastructure.network.proto.MessageType
import io.sleekflow.infrastructure.network.proto.NetMessage
import io.sleekflow.infrastructure.network.proto.RequestType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.ktor.ext.inject
import java.time.Duration

private val LOGGER = KtorSimpleLogger("Sockets.kt")

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(ProtoBuf)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/") {
            val connectionManager by inject<ConnectionManager>()
            val connectionId = connectionManager.addConnection(this)
            LOGGER.trace("New connection: {}", connectionId)

            try {
                while (true) {
                    val request = receiveDeserialized<NetMessage>()
                    assert(request.header!!.messageType === MessageType.UserInitiatedRequest)

                    LOGGER.trace("Received request: {}", request.header!!.requestType.toString())
                    when (request.header.requestType!!) {
                        RequestType.CreateAccount -> TODO()
                        RequestType.Login -> TODO()
                        RequestType.CreateTodoTask -> {
                            val requestHandler by inject<CreateTodoTaskRequestHandler>()
                            sendSerialized(requestHandler.process(request))
                        }

                        RequestType.UpdateTodoTask -> {
                            val requestHandler by inject<UpdateTodoTaskRequestHandler>()
                            sendSerialized(requestHandler.process(request))
                        }

                        RequestType.DeleteTodoTask -> {
                            val requestHandler by inject<DeleteTodoTaskRequestHandler>()
                            sendSerialized(requestHandler.process(request))
                        }

                        RequestType.GetTodoTask -> {
                            val requestHandler by inject<GetTodoTaskRequestHandler>()
                            sendSerialized(requestHandler.process(request))
                        }
                    }
                }
            } finally {
                LOGGER.trace("Remove connection: {}", connectionId)
                connectionManager.removeConnection(connectionId)
            }
        }
    }
}
