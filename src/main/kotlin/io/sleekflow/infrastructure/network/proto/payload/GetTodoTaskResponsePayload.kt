package io.sleekflow.infrastructure.network.proto.payload

import io.sleekflow.infrastructure.network.proto.payload.messages.Status
import kotlinx.serialization.Serializable

@Serializable
data class TodoProtoMessage(
    val id: Int?,
    val name: String?,
    val description: String?,
    val dueDate: Long?,
    val status: Status?
)

@Serializable
class GetTodoTaskResponsePayload(
    val todos: List<TodoProtoMessage>,
) : Payload() {
}