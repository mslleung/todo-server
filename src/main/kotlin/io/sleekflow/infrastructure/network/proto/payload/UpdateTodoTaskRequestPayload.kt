package io.sleekflow.infrastructure.network.proto.payload

import io.sleekflow.infrastructure.network.proto.payload.messages.Status
import kotlinx.serialization.Serializable

@Serializable
class UpdateTodoTaskRequestPayload(
    val id: Int?,
    val name: String?,
    val description: String?,
    val dueDate: Long?,
    val status: Status?,
) : Payload()
