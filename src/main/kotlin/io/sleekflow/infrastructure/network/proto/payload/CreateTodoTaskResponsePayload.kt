package io.sleekflow.infrastructure.network.proto.payload

import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.serialization.Serializable

@Serializable
class CreateTodoTaskResponsePayload(
    val errorCode: ErrorCode?,
    val id: Int?
) : Payload()
