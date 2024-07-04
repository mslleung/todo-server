package io.sleekflow.infrastructure.network.proto

import io.sleekflow.infrastructure.network.proto.payload.Payload
import kotlinx.serialization.Serializable

@Serializable
data class NetMessage(
    val header: Header?,
    val payload: Payload?
)

enum class MessageType {
    Unspecified,
    UserInitiatedRequest,
    UserInitiatedRequestResponse,
    ForwardedRequestResponse
}

enum class RequestType {
    CreateAccount,
    Login,
    CreateTodoTask,
    GetTodoTask,
    UpdateTodoTask,
    DeleteTodoTask
}

@Serializable
data class Header(
    val messageType: MessageType?,
    val requestType: RequestType?,
    val requestUuid: String?,
)
