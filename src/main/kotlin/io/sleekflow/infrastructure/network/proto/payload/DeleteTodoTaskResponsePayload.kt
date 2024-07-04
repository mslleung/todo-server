package io.sleekflow.infrastructure.network.proto.payload

import io.sleekflow.infrastructure.network.proto.payload.messages.ErrorCode
import kotlinx.serialization.Serializable

@Serializable
class DeleteTodoTaskResponsePayload(
    val errorCode: ErrorCode?,
) : Payload()
